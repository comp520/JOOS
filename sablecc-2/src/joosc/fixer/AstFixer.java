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


package joosc.fixer;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import java.util.*;

/*
 * AstFixer
 * 
 * Notes
 *  noshortif 'for' statements are eliminated like all other noshortif
 *  statements, rather than being converted to the equivalent 'while'
 *  loops, because this would have the side-effect of introducing 
 *  another entry point for noshortif statements, not desirable.
 *
 * History:
 *  21 May 2000 - corrected bug: had forgotten formal_list tranformation!
 *  10 May 2000 - made extension of "Object" class explicit in the AST
 *   7 May 2000 - added new list flattening transformations that
 *                tightly couple name and type for field and decl stm
 *   4 May 2000 - driver (fix) now performs AstFixerSimplestm pass as well
 *   2 Apr 2000 - A+ additions as pseudo-syntactic sugar completed
 *                expression cascade collapsing completed
 *   1 Apr 2000 - stm_no_short_if transformations completed
 *                constructor streamlining transformation completed
 *   6 Mar 2000 - list flattening transformations completed
 *                prototype list transformation: identifier_list
 *                created
 */
public class AstFixer extends DepthFirstAdapter {
	protected String filename;

	public AstFixer(String fn) { filename = fn; }
	
 	public static void fix(List theProgram) {
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		String cname;
		
		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			cname = classfile.getName();
			ast.apply(new AstFixer(cname));
			ast.apply(new AstFixerSimplestm(cname));
		}
 	}

	/******************************************************************
	 *              "Object" extension made explicit                  *
	 ******************************************************************/
	 
	// lines and positions for new structures are fictitious.
	public void inAClass(AClass node) {
		TIdentifier id = node.getIdentifier();
		
		if (node.getExtension() == null && 
		    ! id.getText().equals("Object")) {
			node.setExtension(
			   new AExtension(
			      new TExtends(id.getLine(),id.getPos()),
			      new TIdentifier("Object",id.getLine(),id.getPos())));
		}
	}
	// lines and positions for new structures are fictitious.
	public void inAExternClass(AExternClass node) {
		TIdentifier id = node.getIdentifier();
		
		if (node.getExtension() == null && 
		    ! id.getText().equals("Object")) {
			node.setExtension(
			   new AExtension(
			      new TExtends(id.getLine(),id.getPos()),
			      new TIdentifier("Object",id.getLine(),id.getPos())));
		}
	}
	
 	/******************************************************************
 	 *             list flattening transformations                    *
 	 ******************************************************************/
 	 
 	public void outATmpIdentifierList(ATmpIdentifierList tmpNode) {
 		LinkedList idList = new LinkedList();
 		Iterator iter = tmpNode.getIdentifierListTail().iterator();
 		TIdentifier id;
 		AIdentifierList node;
 		
 		// create new streamlined list of identifiers
 		id = tmpNode.getIdentifier();
 		idList.add(id);
 		while (iter.hasNext()) {
 			id = ((AIdentifierListTail) iter.next()).getIdentifier();
 			idList.add(id);
 		}
 		
 		// create replacement node and perform replacement
 		node = new AIdentifierList(idList);
 		tmpNode.replaceBy(node);
 	}
 	
  	public void outATmpFormalList(ATmpFormalList tmpNode) {
 		LinkedList formalList = new LinkedList();
 		Iterator iter = tmpNode.getFormalListTail().iterator();
 		PFormal formal;
 		AFormalList node;
 		
 		// create new streamlined list of formals
 		formal = tmpNode.getFormal();
 		formalList.add(formal);
 		while (iter.hasNext()) {
 			formal = ((AFormalListTail) iter.next()).getFormal();
 			formalList.add(formal);
 		}
 		
 		// create replacement node and perform replacement
 		node = new AFormalList(formalList);
 		tmpNode.replaceBy(node);
 	}
 	
 	public void outATmpArgumentList(ATmpArgumentList tmpNode) {
 		LinkedList argList = new LinkedList();
 		Iterator iter = tmpNode.getArgumentListTail().iterator();
 		PExp exp;
 		AArgumentList node;
 		
 		// create new streamlined list of arguments
 		exp = tmpNode.getExp();
 		argList.add(exp);
 		while (iter.hasNext()) {
 			exp = ((AArgumentListTail) iter.next()).getExp();
 			argList.add(exp);
 		}
 		
 		// create replacement node and perform replacement
 		node = new AArgumentList(argList);
 		tmpNode.replaceBy(node);
 	}
 	
 	public void outATmpField(ATmpField tmpNode) {
 		LinkedList newList = new LinkedList();
 		Iterator iter = ((AIdentifierList)tmpNode.getIdentifierList()).
 		                   getIdentifier().iterator();
 		
 		// pair field name and type
 		while (iter.hasNext()) {
 			newList.add(
 			   new AOnefield((TProtected)tmpNode.getProtected().clone(),
 			                 (PType)tmpNode.getType().clone(),
 			                 (TIdentifier)((TIdentifier)iter.next()).clone(),
 			                 (TSemicolon)tmpNode.getSemicolon().clone()));
 		}
 		tmpNode.replaceBy(new AField(newList));
 	}
 	
 	public void outATmpdeclStm(ATmpdeclStm tmpNode) {
 		LinkedList newList = new LinkedList();
 		Iterator iter = ((AIdentifierList)tmpNode.getIdentifierList()).
 		                   getIdentifier().iterator();
 		
 		// pair declared variable names and types
 		while (iter.hasNext()) {
 			newList.add(
 			   new AOnelocal((PType)tmpNode.getType().clone(),
 			                 (TIdentifier)((TIdentifier)iter.next()).clone(),
 			                 (TSemicolon)tmpNode.getSemicolon().clone()));
 		}
 		tmpNode.replaceBy(new ADeclStm(newList));
 	}
 	
 	/* supercons invocation stm integration into constructor stm list */
 	
 	public void outATmpConstructor(ATmpConstructor tmpNode) {
 		AConstructor node;
 		ASuperconsStm supercons;
 		LinkedList stmList;
 		
 		// create a supercons statement using hidden stm alternative
 		supercons = new ASuperconsStm(tmpNode.getSuper(),
 		                              tmpNode.getSuperLPar(),
 		                              tmpNode.getArgumentList(),
 		                              tmpNode.getSuperRPar(),
 		                              tmpNode.getSemicolon());
 		// merge supercons stm with subsequent stm list
 		stmList = new LinkedList(tmpNode.getStm());
 		stmList.addFirst(supercons);
 		// create replacement node using supercons just created
 		node = new AConstructor(tmpNode.getPublic(),
 		                        tmpNode.getIdentifier(),
 		                        tmpNode.getLPar(),
 		                        tmpNode.getFormalList(),
 		                        tmpNode.getRPar(),
 		                        tmpNode.getLBrace(),
 		                        stmList,
 		                        tmpNode.getRBrace());
 		// perform replacement
 		tmpNode.replaceBy(node);
 	}
 	
 	/*******************************************************************
 	 *          Bottom-Up Elimination of no_short_if statements        *
 	 *******************************************************************/
 	
 	/*
 	 * This assumes that all stm_no_short_if children of stm_no_short_if 
 	 * in node have been replaced by their stm equivalent.
 	 * This is a valid assumption since all the children of node have 
 	 * already been visited by this walker (and for some replaced),
 	 * this being an "out" routine.
 	 *
 	 * All what's left to do here is replace the stm_no_short_if
 	 * by the equivalent stm using the children of stm_no_short_if.
 	 * This requires a switch on the type of stm_no_short_if.
	 */
	public void outATmpIfelseStmNoShortIf(ATmpIfelseStmNoShortIf tmpNode) {
		PStmNoShortIf oldStmChild;
		
		oldStmChild = tmpNode.getThenStmNoShortIf();
		PStm newStmChild1 = null;
		
		newStmChild1 = convertNoshortifStmToStm(oldStmChild);
		
		oldStmChild = tmpNode.getElseStmNoShortIf();
		PStm newStmChild2 = null;

		newStmChild2 = convertNoshortifStmToStm(oldStmChild);
		
		tmpNode.replaceBy(new AIfelseStmNoShortIf(
			tmpNode.getIf(),
			tmpNode.getLPar(),
			tmpNode.getExp(),
			tmpNode.getRPar(),
			newStmChild1,
			tmpNode.getElse(),
			newStmChild2));
	}
 	
 	/*
 	 * This assumes that all stm_no_short_if children of stm_no_short_if 
 	 * in node have been replaced by their stm equivalent.
 	 * This is a valid assumption since all the children of node have 
 	 * already been visited by this walker (and for some replaced),
 	 * this being an "out" routine.
 	 *
 	 * All what's left to do here is replace the stm_no_short_if
 	 * by the equivalent stm using the children of stm_no_short_if.
 	 * This requires a switch on the type of stm_no_short_if.
	 */
	public void outATmpWhileStmNoShortIf(ATmpWhileStmNoShortIf tmpNode) {
		PStmNoShortIf oldStmChild = tmpNode.getStmNoShortIf();
		PStm newStmChild = null;
	
		newStmChild = convertNoshortifStmToStm(oldStmChild);
		
		tmpNode.replaceBy(new AWhileStmNoShortIf(
			tmpNode.getWhile(),
			tmpNode.getLPar(),
			tmpNode.getExp(),
			tmpNode.getRPar(),
			newStmChild));
	}

	/*@A+begin*/
 	/*
 	 * This assumes that all stm_no_short_if children of stm_no_short_if 
 	 * in node have been replaced by their stm equivalent.
 	 * This is a valid assumption since all the children of node have 
 	 * already been visited by this walker (and for some replaced),
 	 * this being an "out" routine.
 	 *
 	 * All what's left to do here is replace the stm_no_short_if
 	 * by the equivalent stm using the children of stm_no_short_if.
 	 * This requires a switch on the type of stm_no_short_if.
	 */
	public void outATmpForStmNoShortIf(ATmpForStmNoShortIf tmpNode) {
		PStmNoShortIf oldStmChild = tmpNode.getStmNoShortIf();
		PStm newStmChild = null;

		newStmChild = convertNoshortifStmToStm(oldStmChild);
		
		tmpNode.replaceBy(new AForStmNoShortIf(
			tmpNode.getFor(),
			tmpNode.getLPar(),
			tmpNode.getInitializer(),
			tmpNode.getSemicolon1(),
			tmpNode.getExp(),
			tmpNode.getSemicolon2(),
			tmpNode.getUpdater(),
			tmpNode.getRPar(),
			newStmChild));
	}
	/*@A+end*/
 	
 	/* 
 	 * This is the unique entry point to stm_no_short_if types of nodes.
 	 * Once all calls to this method have returned, stm_no_short_if
 	 * nodes in the AST become history.
 	 *
 	 * This assumes that all stm_no_short_if children of stm_no_short_if 
 	 * in node have been replaced by their stm equivalent.
 	 * This is a valid assumption since all the children of node have 
 	 * already been visited by this walker (and for some replaced),
 	 * this being an "out" routine.
 	 *
 	 * All what's left to do here is replace the stm_no_short_if
 	 * by the equivalent stm using the children of stm_no_short_if.
 	 * This requires a switch on the type of stm_no_short_if.
 	 */
	public void outATmpIfelseStm(ATmpIfelseStm tmpNode) {
		PStmNoShortIf oldStmChild = tmpNode.getStmNoShortIf();
		PStm newStmChild = null;
		
		newStmChild = convertNoshortifStmToStm(oldStmChild);
		
		tmpNode.replaceBy(new AIfelseStm(
			tmpNode.getIf(),
			tmpNode.getLPar(),
			tmpNode.getExp(),
			tmpNode.getRPar(),
			newStmChild,
			tmpNode.getElse(),
			tmpNode.getStm()));
	}	
 
 	/*
 	 * Core routine shared by all the noshortif statements transformers.
 	 */
	private PStm convertNoshortifStmToStm(PStmNoShortIf oldStmChild) {
		PStm newStmChild = null;
		
		if (oldStmChild instanceof ASimpleStmNoShortIf) {
			newStmChild = new ASimpleStm(
				((ASimpleStmNoShortIf)oldStmChild).getSimplestm());
		}		
		else if (oldStmChild instanceof AIfelseStmNoShortIf) {
			AIfelseStmNoShortIf castStmChild = 
				(AIfelseStmNoShortIf)oldStmChild;
			newStmChild = new AIfelseStm(
				castStmChild.getIf(),
				castStmChild.getLPar(),
				castStmChild.getExp(),
				castStmChild.getRPar(),
				castStmChild.getThenStm(),
				castStmChild.getElse(),
				castStmChild.getElseStm());
		}
		else if (oldStmChild instanceof AWhileStmNoShortIf) {
			AWhileStmNoShortIf castStmChild = 
				(AWhileStmNoShortIf)oldStmChild;
			newStmChild = new AWhileStm(
				castStmChild.getWhile(),
				castStmChild.getLPar(),
				castStmChild.getExp(),
				castStmChild.getRPar(),
				castStmChild.getStm());
		}
		/*@A+begin*/
		else if (oldStmChild instanceof AForStmNoShortIf) {
			AForStmNoShortIf castStmChild = 
				(AForStmNoShortIf)oldStmChild;
			newStmChild = new AForStm(
				castStmChild.getFor(),
				castStmChild.getLPar(),
				castStmChild.getInitializer(),
				castStmChild.getSemicolon1(),
				castStmChild.getExp(),
				castStmChild.getSemicolon2(),
				castStmChild.getUpdater(),
				castStmChild.getRPar(),
				castStmChild.getStm());
		}
		/*@A+end*/
		else
			MyError.fatalError("ASTFixer","unexpected AST node type");
		
		return newStmChild;
	}
 	
 	
	/*@A+begin*/
 	/*****************************************************************
 	 *           A+ Additions as "pseudo-syntactic sugar"            *
 	 *****************************************************************/
 	
 	/*
 	 * Replaces a for stm by a simple stm containing a block simplestm,
 	 * whose second statement is a while loop.
 	 * 'for (initializer; exp; updater) stm' becomes:
 	 * '{ initializer; while (exp) { stm; updater; } }'
 	 */
	public void outAForStm(AForStm tmpNode) {
		AWhileStm whileStm;
		LinkedList whileBodyStmList, stmList;
		ASimpleStm whileBody;
		
		// form stm sequence to be in while loop body
		whileBodyStmList = new LinkedList();
		whileBodyStmList.add(tmpNode.getStm());
		// stm_exp must first be made into an actual stm
		whileBodyStmList.add(new ASimpleStm(new AExpSimplestm(
		                                       tmpNode.getUpdater(),
		                                       new TSemicolon())));
		// form while loop body
		whileBody = new ASimpleStm(
		               new ABlockSimplestm(
		                  new TLBrace(),
		                  whileBodyStmList,
		                  new TRBrace()));
		// form while stm
		whileStm = new AWhileStm(new TWhile(),
		                     new TLPar(),
		                     tmpNode.getExp(),
		                     new TRPar(),
		                     whileBody);
		
		// form stm sequence made of initializer and while stm
		stmList = new LinkedList();
		// stm_exp must first be made into an actual stm
		stmList.add(new ASimpleStm(new AExpSimplestm(
		                              tmpNode.getInitializer(),
		                              new TSemicolon())));
		stmList.add(whileStm);
		
		// replace node by a simple block stm whose body is stm sequence
		tmpNode.replaceBy(new ASimpleStm(
		                     new ABlockSimplestm(
		                        new TLBrace(),
		                        stmList,
		                        new TRBrace())));
	}
	
 	/* 
 	 * Replaces an inc stm_exp i++ by an equivalent assign stm_exp 
 	 * i = i + 1.
 	 * Assumes that expression cascade collapsing (below) is performed,
 	 * since it uses hidden exp alternatives.
 	 */
	public void outAIncStmExp(AIncStmExp tmpNode) {
		TIdentifier id = tmpNode.getIdentifier(), 
		            idbis = (TIdentifier)id.clone();
		
		APlusExp plusExp = new APlusExp(
		                      new AIdExp(id),
		                      new TPlus(),
		                      new AIntconstExp(new TIntconst("1")));
		
		tmpNode.replaceBy(new AAssignStmExp(new AAssignment(
		                                      idbis,
		                                      new TAssign(),
		                                      plusExp)));
	}
			
	/*@A+end*/
	
	/**************************************************************
	 *     Bottom-up Expression Precedence Cascade Collapsing     *
	 **************************************************************/

	/*************************** exp *****************************/

	/*
	 * Once all calls to this method return, all expressions in AST
	 * will be alternatives of the exp production.
	 */
	public void outADefaultExp(ADefaultExp node) {
		node.replaceBy((PExp)getOut(node.getOrExp()));
	}

	/************************* or_exp ****************************/
	
	public void outADefaultOrExp(ADefaultOrExp node) {
		setOut(node, getOut(node.getAndExp()));
	}
	public void outAOrOrExp(AOrOrExp node) {
		setOut(node, new AOrExp((PExp)getOut(node.getLeft()),
		                        node.getOr(),
		                        (PExp)getOut(node.getRight())));
	}
	
	/************************* and_exp ****************************/
	
	public void outADefaultAndExp(ADefaultAndExp node) {
		setOut(node, getOut(node.getEqExp()));
	}
	public void outAAndAndExp(AAndAndExp node) {
		setOut(node, new AAndExp((PExp)getOut(node.getLeft()),
		                         node.getAnd(),
		                         (PExp)getOut(node.getRight())));
	}
	
	/************************** eq_exp ****************************/
	
	public void outADefaultEqExp(ADefaultEqExp node) {
		setOut(node, getOut(node.getRelExp()));
	}
	public void outAEqEqExp(AEqEqExp node) {
		setOut(node, new AEqExp((PExp)getOut(node.getLeft()),
		                        node.getEq(),
		                        (PExp)getOut(node.getRight())));
	}
	public void outANeqEqExp(ANeqEqExp node) {
		setOut(node, new ANeqExp((PExp)getOut(node.getLeft()),
		                         node.getNeq(),
		                         (PExp)getOut(node.getRight())));
	}
	
 	/************************* rel_exp ***************************/
	
	public void outADefaultRelExp(ADefaultRelExp node) {
		setOut(node, getOut(node.getAddExp()));
	}
	public void outALtRelExp(ALtRelExp node) {
		setOut(node, new ALtExp((PExp)getOut(node.getLeft()),
		                        node.getLt(),
		                        (PExp)getOut(node.getRight())));
	}
	public void outAGtRelExp(AGtRelExp node) {
		setOut(node, new AGtExp((PExp)getOut(node.getLeft()),
		                        node.getGt(),
		                        (PExp)getOut(node.getRight())));
	}
	public void outALeqRelExp(ALeqRelExp node) {
		setOut(node, new ALeqExp((PExp)getOut(node.getLeft()),
		                         node.getLeq(),
		                         (PExp)getOut(node.getRight())));
	}
	public void outAGeqRelExp(AGeqRelExp node) {
		setOut(node, new AGeqExp((PExp)getOut(node.getLeft()),
		                        node.getGeq(),
		                        (PExp)getOut(node.getRight())));
	}
	public void outAInstanceofRelExp(AInstanceofRelExp node) {
		setOut(node, new AInstanceofExp((PExp)getOut(node.getRelExp()),
		                                node.getInstanceof(),
		                                node.getIdentifier()));
	}

	/************************* add_exp ***************************/
	
	public void outADefaultAddExp(ADefaultAddExp node) {
		setOut(node, getOut(node.getMultExp()));
	}
	public void outAPlusAddExp(APlusAddExp node) {
		setOut(node, new APlusExp((PExp)getOut(node.getLeft()),
		                          node.getPlus(),
		                          (PExp)getOut(node.getRight())));
	}
	public void outAMinusAddExp(AMinusAddExp node) {
		setOut(node, new AMinusExp((PExp)getOut(node.getLeft()),
		                           node.getMinus(),
		                           (PExp)getOut(node.getRight())));
	}
	
	/************************* mult_exp ***************************/
	
	public void outADefaultMultExp(ADefaultMultExp node) {
		setOut(node, getOut(node.getUnaryExp()));
	}
	public void outAMultMultExp(AMultMultExp node) {
		setOut(node, new AMultExp((PExp)getOut(node.getLeft()),
		                          node.getMult(),
		                          (PExp)getOut(node.getRight())));
	}
	public void outADivMultExp(ADivMultExp node) {
		setOut(node, new ADivExp((PExp)getOut(node.getLeft()),
		                         node.getDiv(),
		                         (PExp)getOut(node.getRight())));
	}
	public void outAModMultExp(AModMultExp node) {
		setOut(node, new AModExp((PExp)getOut(node.getLeft()),
		                         node.getMod(),
		                         (PExp)getOut(node.getRight())));
	}

	/************************* unary_exp **************************/
	
	public void outADefaultUnaryExp(ADefaultUnaryExp node) {
		setOut(node, getOut(node.getUnaryExpNotMinus()));
	}
	public void outAMinusUnaryExp(AMinusUnaryExp node) {
		setOut(node, new AUminusExp(node.getMinus(),
		                            (PExp)getOut(node.getUnaryExp())));
	}
	
	/******************** unary_exp_not_minus *********************/
	
	public void outADefaultUnaryExpNotMinus(ADefaultUnaryExpNotMinus node) {
		setOut(node, getOut(node.getPostfixExp()));
	}
	public void outANotUnaryExpNotMinus(ANotUnaryExpNotMinus node) {
		setOut(node, new ANotExp(node.getNot(),
		                         (PExp)getOut(node.getUnaryExp())));
	}
	public void outACastUnaryExpNotMinus(ACastUnaryExpNotMinus node) {
		setOut(node, getOut(node.getCastExp()));
	}
	
	/******************** cast_exp production *********************/
	 
	public void outANoncharCastExp(ANoncharCastExp node) {
		setOut(node, new ATmpcastExp(node.getLPar(),
		                             node.getExp(),
		                             node.getRPar(),
		                             (PExp)getOut(
		                                 node.getUnaryExpNotMinus())));
	}
	public void outACharCastExp(ACharCastExp node) {
		setOut(node, new ACasttocharExp(node.getLPar(),
		                                node.getChar(),
		                                node.getRPar(),
		                                (PExp)getOut(node.getUnaryExp())));
	}

	/****************** postfix_exp production ********************/
	 
	public void outAIdPostfixExp(AIdPostfixExp node) {
		setOut(node, new AIdExp(node.getIdentifier()));
	}
	public void outAPrimaryPostfixExp(APrimaryPostfixExp node) {
		setOut(node, getOut(node.getPrimaryExp()));
	}
	
	/****************** primary_exp production ********************/

	public void outALiteralPrimaryExp(ALiteralPrimaryExp node) {
		setOut(node, getOut(node.getLiteral()));
	}
	public void outAThisPrimaryExp(AThisPrimaryExp node) {
		setOut(node, new AThisExp(node.getThis()));
	}
	public void outAParenPrimaryExp(AParenPrimaryExp node) {
		setOut(node, new AParenExp(node.getLPar(),
		                           node.getExp(),
		                           node.getRPar()));
	}
	public void outANewPrimaryExp(ANewPrimaryExp node) {
		setOut(node, new ANewExp(node.getClassinstancecreation()));
	}
	public void outACallPrimaryExp(ACallPrimaryExp node) {
		setOut(node, new ACallExp(node.getMethodinvocation()));
	}

	/******************* receiver production **********************/
	
	/* don't forget to do this as well! */
	public void outATmpobjectReceiver(ATmpobjectReceiver node) {
		node.replaceBy(new AObjectReceiver(
		                  (PExp)getOut(node.getPostfixExp())));
	}
	
	/******************** literal production **********************/
	 
	public void outAIntLiteral(AIntLiteral node) {
		setOut(node, new AIntconstExp(node.getIntconst()));
	}
	public void outATrueLiteral(ATrueLiteral node) {
		setOut(node, new ATrueExp(node.getTrue()));
	}
	public void outAFalseLiteral(AFalseLiteral node) {
		setOut(node, new AFalseExp(node.getFalse()));
	}
	public void outACharLiteral(ACharLiteral node) {
		setOut(node, new ACharconstExp(node.getCharconst()));
	}
	public void outAStringLiteral(AStringLiteral node) {
		setOut(node, new AStringconstExp(node.getStringconst()));
	}
	public void outANullLiteral(ANullLiteral node) {
		setOut(node, new ANullExp(node.getNull()));
	}	
}
