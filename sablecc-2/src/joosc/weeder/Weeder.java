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


package joosc.weeder;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import java.util.*;

/*
 * Weeder
 *
 * History:
 *   7 May 2000 - slight change to accomodate transformed ADeclStm nodes
 *   4 May 2000 - weeding complete! (added this, super, locals, returns
 *                and abstract weedings)
 *   2 May 2000 - merged the two into one post-AST-fix Weeder class
 *   2 Apr 2000 - created PostAstFixWeeder; non-id caster weed and 
 *                transformation added
 *   7 Mar 2000 - created PreAstFixWeeder; prototype weeding 
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

	/* Weed non-String main method argument type */
	
	private void weedMainargvType(TIdentifier typeId) {
		if (! typeId.getText().equals("String")) {
			MyError.error(filename,"type String expected",
			              typeId.getLine(), typeId.getPos());
		}
	}
	public void outAFirstMainargv(AFirstMainargv node) {
		TIdentifier typeId = node.getType();
		weedMainargvType(typeId);
	}
	public void outASecondMainargv(ASecondMainargv node) {
		TIdentifier typeId = node.getType();
		weedMainargvType(typeId);
	}

	/* 
	 * Weed non-identifier caster element in tmpcast exp,
	 * and transform it to a cast exp if passes.
	 * (caster exp becomes an identifier)
	 */
	
	public void outATmpcastExp(ATmpcastExp tmpNode) {
		PExp caster = tmpNode.getCaster();
		
		// also transform tmpcast exp to cast exp
		if (caster instanceof AIdExp) {
			tmpNode.replaceBy(new ACastExp(
			                     tmpNode.getLPar(),
			                     ((AIdExp)caster).getIdentifier(),
			                     tmpNode.getRPar(),
			                     tmpNode.getCastee()));
		}
		else {
			MyError.error(filename,"identifier expected",
			              tmpNode.getLPar().getLine(),
			              tmpNode.getLPar().getPos());
		}
	}
	
	/* weed abstract methods inside non-abstract classes */
	
	public void inAClass(AClass node) {
		// only interested in non-abstract classes
		if (node.getClassmods() instanceof AAbstractClassmods)
			return;
		
		Iterator methods = node.getMethod().iterator();
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
				              token.getLine());
			}
		}
	}
	public void inAExternClass(AExternClass node) {
		// only interested in non-abstract classes
		if (node.getClassmods() instanceof AAbstractClassmods)
			return;
		
		Iterator methods = node.getExternMethod().iterator();
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
				              token.getLine());
			}
		}
	}
	
	/* weed 'this' exp and 'super' receiver inside main method */
	
	public void inAThisExp(AThisExp node) {
		Token token;
		
		if (insideMainMethod) {
			token = node.getThis();
			MyError.error(filename,"this not allowed in main method",
			              token.getLine(),token.getPos());
		}
	}
	public void inASuperReceiver(ASuperReceiver node) {
		Token token;
		
		if (insideMainMethod) {
			token = node.getSuper();
			MyError.error(filename,"super not allowed in main method",
			              token.getLine(),token.getPos());
		}
	}
	public void outAMainMethod(AMainMethod node) {
		insideMainMethod = false;
	}
	
	/* 
	 * Weed local declarations not at beginning of a method
	 * or of a constructor or of a block stm.
	 */
	
	public void weedStmSeqLocals(LinkedList stms) {
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
			weedStmSeqLocals(((ABlockStm)stm).getStm());
			return false;
		}
		else if (stm instanceof ADeclStm) {
			if (!localsAllowed)
				MyError.error(filename,"illegally placed local declaration",
				              ((AOnelocal)((ADeclStm)stm)
				                 .getOnelocal().getFirst()).
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
	
	/* 
	 * weed non-void non-abstract methods in non-extern classes 
	 * that do not return.
	 */
	
	private boolean weedStmReturns(PStm stm) {
		// base case
		if (stm == null)
			return false;
		
		if (stm instanceof ABlockStm) {
			// return, if present, must be last statement of block
			LinkedList bstm = ((ABlockStm)stm).getStm();
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

	/* weed all method alternatives, and constructor */
	
	public void inAMainMethod(AMainMethod node) {
		insideMainMethod = true;
		// weed locals
		weedStmSeqLocals(node.getStm()); 
	}
	public void inAModMethod(AModMethod node) {
		LinkedList stms = node.getStm();
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
	public void inANonmodMethod(ANonmodMethod node) {
		LinkedList stms = node.getStm();
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
	public void inAConstructor(AConstructor node) {
		// weed locals
		weedStmSeqLocals(node.getStm());
	}
	
}