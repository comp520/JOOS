/*
 * JOOSJ is Copyright (C) 2000 Othman Alaoui
 *
 * Reproduction of all or part of this software is permitted for
 * educational or research use on condition that this copyright notice is
 * included in any copy. This software comes with no warranty of any
 * kind. In no event will the author be liable for any damages resulting from
 * use of this software.
 *
 * email: oalaou@po-box.mcgill.ca
 */

/*
 * August 2003
 *
 * Updated JOOSJ which works with the new version of
 * SableCC (SableCC3.0)
 *
 * Update by Ahmer Ahmedani & Feng Qian
 * email: ahmer.ahmedani@mail.mcgill.ca & fqian@cs.mcgill.ca
 */


package joosc.weeder;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import java.util.*;

/**
 * Weeder<p>
 *
 * History:
 * <ul>
 *   <li> 19 Aug 2003 - change to use AST grammar, fixup documentation
 *   <li> 7 May 2000 - slight change to accomodate transformed ADeclStm nodes
 *   <li> 4 May 2000 - weeding complete! (added this, super, locals, returns
 *                and abstract weedings)
 *   <li> 2 May 2000 - merged the two into one post-AST-fix Weeder class
 *   <li> 2 Apr 2000 - created PostAstFixWeeder; non-id caster weed and
 *                transformation added
 *   <li> 7 Mar 2000 - created PreAstFixWeeder; prototype weeding
 *                (mainargv type)
 */
public class Weeder extends DepthFirstAdapter {
	protected String filename;
	private boolean insideMainMethod = false;

	public Weeder(String fn) { filename = fn; }

	public static void weed(List theProgram) {
		ClassFile classfile;
		Iterator iter = theProgram.iterator();
		Node ast;

		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			ast.apply(new Weeder(classfile.getName()));
		}
 	}

	private void weedMainargvType(TIdentifier typeId) {
		if (! typeId.getText().equals("String")) {
			MyError.error(filename,"type String expected",
			              Lines.getLine(typeId), Lines.getPos(typeId));
		}
  }
	/**
   * Weeds non-String main method argument type.
   */
	public void outAMainargv(AMainargv node) {
		TIdentifier typeId = node.getType();
		weedMainargvType(typeId);
	}

	/**
	 * Weeds non-identifier caster element in tmpcast exp,
	 * and transform it to a cast exp if passes.
	 * (caster exp becomes an identifier)
	 */
	public void outATmpcastExp(ATmpcastExp tmpNode) {
		PExp caster = tmpNode.getCaster();

		// also transform tmpcast exp to cast exp
		if (caster instanceof AIdExp) {
			tmpNode.replaceBy(new ACastExp(
			                     ((AIdExp)caster).getIdentifier(),
			                     tmpNode.getCastee()));
		}
		else {
			MyError.error(filename,"identifier expected", Lines.getLine(tmpNode), Lines.getPos(tmpNode));
      // right now, an ast node doesnot have ability to keep line, pos info
		}
	}

	/**
   * Weeds abstract methods inside non-abstract classes.
   */
	public void inAClass(AClass node) {
		// only interested in non-abstract classes
		if (node.getClassmods() instanceof AAbstractClassmods)
			return;

		Iterator methods = node.getMethods().iterator();
		PMethod method;
		Token token;

		// iterate over all methods
		while (methods.hasNext()) {
			method = (PMethod)methods.next();
			if (method instanceof AAbstractMethod) {
				token = ((AAbstractMethod)method).getIdentifier();
				MyError.error(filename,
				              "abstract method "+
				              token.getText()+
				              " requires an abstract class",
				              Lines.getLine(token));
			}
		}
	}

  /**
   * Weeds abstract methods inside non-abstract extern classes.
   */
	public void inAExternClass(AExternClass node) {
		// only interested in non-abstract classes
		if (node.getClassmods() instanceof AAbstractClassmods)
			return;

		Iterator methods = node.getMethods().iterator();
		PExternMethod method;
		Token token;

		// iterate over all methods
		while (methods.hasNext()) {
			method = (PExternMethod)methods.next();
			// if method is modified...
			if ((method instanceof AModExternMethod) &&
			    // and modifier is 'abstract'
			    (((AModExternMethod)method).getExternMethodmods()
			     instanceof AAbstractExternMethodmods)) {
				token = ((AModExternMethod)method).getIdentifier();
				MyError.error(filename,
				              "abstract method "+
				              token.getText()+
				              " requires an abstract class",
				              Lines.getLine(token));
			}
		}
	}

	/**
   * Weeds 'this' exp inside a main method.
   */
	public void inAThisExp(AThisExp node) {
		Token token;

		if (insideMainMethod) {
			MyError.error(filename,"this not allowed in main method", Lines.getLine(node), Lines.getPos(node));
		}
	}
  /**
   * Weeds 'super' receiver inside a main method.
   */
	public void inASuperReceiver(ASuperReceiver node) {
		Token token;

		if (insideMainMethod) {
			MyError.error(filename,"super not allowed in main method", Lines.getLine(node), Lines.getPos(node));
		}
	}
	public void outAMainMethod(AMainMethod node) {
		insideMainMethod = false;
	}

	/* Weeds local declarations not at beginning of a method
	 * or of a constructor or of a block stm.
	 */
	private void weedStmSeqLocals(LinkedList stms) {
		Iterator seq = stms.iterator();
		boolean moreLocalsAllowed;

		if (seq.hasNext()) {
			moreLocalsAllowed = weedStmLocals((PStm)seq.next(), true);
			while(seq.hasNext()) {
				moreLocalsAllowed = // weedStmLocals _must_ be executed (1st)
				   weedStmLocals((PStm)seq.next(), moreLocalsAllowed) &&
				   moreLocalsAllowed;
			}
		}
	}
	private boolean weedStmLocals(PStm stm, boolean localsAllowed) {

		if (stm instanceof ABlockStm) {
			weedStmSeqLocals(((ABlockStm)stm).getStmts());
			return false;
		}
		else if (stm instanceof ADeclStm) {
			if (!localsAllowed)
				MyError.error(filename,"illegally placed local declaration",
				              ((AOnelocal)((ADeclStm)stm)
				                 .getLocals().getFirst()).
				                    getIdentifier().getLine());
			// locals allowed after a local (duh!)
			return true;
		}
		else if (stm instanceof ASuperconsStm) {
			// locals allowed after superconstructor invocation
			return true;
		}
		else if (stm instanceof AIfStm) {
			weedStmLocals(((AIfStm)stm).getStm(), false);
		}
		else if (stm instanceof AIfelseStm) {
			weedStmLocals(((AIfelseStm)stm).getThenStm(), false);
			weedStmLocals(((AIfelseStm)stm).getElseStm(), false);
		}
		else if (stm instanceof AWhileStm) {
			weedStmLocals(((AWhileStm)stm).getStm(), false);
		}

		// no locals allowed _after_ input statement stm
		return false;
	}

	/* weed non-void non-abstract methods in non-extern classes
	 * that do not return.
	 */
	private boolean weedStmReturns(PStm stm) {
		// base case
		if (stm == null)
			return false;

		if (stm instanceof ABlockStm) {
			// return, if present, must be last statement of block
			LinkedList bstm = ((ABlockStm)stm).getStmts();
			if (bstm.isEmpty())
				return false;
			else
				return weedStmReturns((PStm)bstm.getLast());
		}
		else if (stm instanceof AReturnStm) {
			return true;
		}
		else if (stm instanceof AIfelseStm) {
			// check that _both_ paths return
			return weedStmReturns(((AIfelseStm)stm).getThenStm()) &&
			       weedStmReturns(((AIfelseStm)stm).getElseStm());
		}

		// case non return statements
		return false;
	}

	/**
   * Weeds all method alternatives, and constructors.
   */
	public void inAMainMethod(AMainMethod node) {
		insideMainMethod = true;
		// weed locals
		weedStmSeqLocals(node.getStmts());
	}

  /**
   * Weeds a method and check the consistency between the return
   * statement and the method signature.
   */
	public void inAModMethod(AModMethod node) {
		LinkedList stms = node.getStmts();
		// weed locals
		weedStmSeqLocals(stms);
		// weed returns for nonvoid modmethod
		if (node.getReturntype() instanceof ANonvoidReturntype)
			if (stms.isEmpty() || !weedStmReturns((PStm)stms.getLast()))
				MyError.error(filename,
				              "method "+node.getIdentifier().getText()+
				              " must return a value",
				              node.getIdentifier().getLine());
	}

  /**
   * Weeds a method and check the consistency between the return
   * statement and the method signature.
   */
	public void inANonmodMethod(ANonmodMethod node) {
		LinkedList stms = node.getStmts();
		// weed locals
		weedStmSeqLocals(stms);
		// weed returns for nonvoid modmethod
		if (node.getReturntype() instanceof ANonvoidReturntype)
			if (stms.isEmpty() || !weedStmReturns((PStm)stms.getLast()))
				MyError.error(filename,
				              "method "+node.getIdentifier().getText()+
				              " must return a value",
				              node.getIdentifier().getLine());
	}

  /**
   * Weeds a constructor.
   */
	public void inAConstructor(AConstructor node) {
		// weed locals
		weedStmSeqLocals(node.getStmts());
	}

}
