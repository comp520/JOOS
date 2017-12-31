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


package joosc.resource;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;

/*
 * ResourceGenerator
 *
 * Builds the resources necessary for generating Java bytecode
 * for the program represented by the AST under consideration.
 * These are:
 * - offsets for locals and formals
 * - labels for control structures
 * - localslimit (max offset + 1)
 * - labels for expressions to be tostring-coerced
 *
 * History:
 *    5 Jun 2000 - legacy labelcount in UnitExecResources phased out
 *   25 May 2000 - created; feature-complete
 */
public class ResourceGenerator extends DepthFirstAdapter {
	private String filename;
	private int offset, // offset for locals and formals
	            label, // label for control structures
	            localsLimit; // locals limit for exec. unit stack frame
	private Hashtable toStringResourcesMap, resourcesMap;
	private Hashtable coerceToStringMap;
	
 	public static void walk(List theProgram, 
	                        AnalysisDataManager proxy) {
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		String cname;
		
		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			cname = classfile.getName();
			ast.apply(new ResourceGenerator(cname,proxy));
		}
 	}
 	
	public ResourceGenerator(String filename, 
	                         AnalysisDataManager proxy) {
		this.filename = filename;
		this.coerceToStringMap = proxy.coerceToStringMap;
		this.resourcesMap = proxy.resourcesMap;
		this.toStringResourcesMap = proxy.toStringResourcesMap;
	}

	/********************************************************
	 *                   Utility Methods                    *
	 ********************************************************/
	 
	private int nextOffset() {
		offset++;
		if (offset > localsLimit) localsLimit = offset;
		return offset; // starts at 1
	}
	
	private int nextLabel() { 
		return label++; // starts at 0
	}
	
	/********************************************************
	 *                 AST Node Visitors                    *
	 ********************************************************/
	
	/* constructor and extern constructor productions */
	
	public void inAConstructor(AConstructor node) {
		inAnyConstructor(node);
	}
	public void inAExternConstructor(AExternConstructor node) {
		inAnyConstructor(node);
	}
	// worker method
	private void inAnyConstructor(Node node) {
		offset = 0;
		localsLimit = 0;
		label = 0;
	}
	public void outAConstructor(AConstructor node) {
		outAnyConstructor(node);
	}
	public void outAExternConstructor(AExternConstructor node) {
		outAnyConstructor(node);
	}
	// worker method
	private void outAnyConstructor(Node node) {
		resourcesMap.put(node,new ExecUnitResources(localsLimit+1));
	}
	
	/* formal production */
	
	public void inAFormal(AFormal node) {
		resourcesMap.put(node,new LocalResources(nextOffset()));
	}
	
	/* method and extern method productions */
	
	public void inAModMethod(AModMethod node) {
		inAnyMethod(node);
	}
	public void inANonmodMethod(ANonmodMethod node) {
		inAnyMethod(node);
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		inAnyMethod(node);
	}
	public void inAMainMethod(AMainMethod node) {
		inAnyMethod(node);
	}
	public void inAModExternMethod(AModExternMethod node) {
		inAnyMethod(node);
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		inAnyMethod(node);
	}
	// worker method
	private void inAnyMethod(Node node) {
		offset = 0;
		localsLimit = 0;
		label = 0;
	}
	public void outAModMethod(AModMethod node) {
		outAnyMethod(node);
	}
	public void outANonmodMethod(ANonmodMethod node) {
		outAnyMethod(node);
	}
	public void outAAbstractMethod(AAbstractMethod node) {
		outAnyMethod(node);
	}
	public void outAMainMethod(AMainMethod node) {
		outAnyMethod(node);
	}
	public void outAModExternMethod(AModExternMethod node) {
		outAnyMethod(node);
	}
	public void outANonmodExternMethod(ANonmodExternMethod node) {
		outAnyMethod(node);
	}
	// worker method
	private void outAnyMethod(Node node) {
		resourcesMap.put(node,new ExecUnitResources(localsLimit+1));
	}
	
	/* stm production */
	
	// override caseXXX to avoid using a stack of base offsets
	public void caseABlockStm(ABlockStm node) {
		Iterator iter = node.getStmts().iterator();
		int baseOffset;
				
		baseOffset = offset; // save offset
		while (iter.hasNext())
			((PStm)iter.next()).apply(this); // visit block body
		offset = baseOffset; // restore offset
	}
	
	public void inAIfStm(AIfStm node) {
		resourcesMap.put(node,new IfStmLabels(nextLabel()));
	}
	public void inAIfelseStm(AIfelseStm node) {
		resourcesMap.put(node,
		   new IfelseStmLabels(nextLabel(),nextLabel()));
	}
	public void inAWhileStm(AWhileStm node) {
		resourcesMap.put(node,
		   new WhileStmLabels(nextLabel(),nextLabel()));
	}

	/* onelocal production */
	
	public void inAOnelocal(AOnelocal node) {
		resourcesMap.put(node,new LocalResources(nextOffset()));
	}
	
	/* exp production */
	
	public void inAAssignExp(AAssignExp node) {
		inAnyExp(node);
	}
	// or expression
	public void inAOrExp(AOrExp node) {
		inAnyExp(node);
		resourcesMap.put(node,new OrExpLabels(nextLabel()));
	}
	// and exp
	public void inAAndExp(AAndExp node) {
		inAnyExp(node);
		resourcesMap.put(node,new AndExpLabels(nextLabel()));
	}
	// relational expressions
	public void inAEqExp(AEqExp node) {
		inAnyExp(node);
		resourcesMap.put(node,
		   new RelExpLabels(nextLabel(),nextLabel()));
	}
	public void inANeqExp(ANeqExp node) {
		inAnyExp(node);
		resourcesMap.put(node,
		   new RelExpLabels(nextLabel(),nextLabel()));
	}
	public void inALtExp(ALtExp node) {
		inAnyExp(node);
		resourcesMap.put(node,
		   new RelExpLabels(nextLabel(),nextLabel()));
	}
	public void inAGtExp(AGtExp node) {
		inAnyExp(node);
		resourcesMap.put(node,
		   new RelExpLabels(nextLabel(),nextLabel()));
	}
	public void inALeqExp(ALeqExp node) {
		inAnyExp(node);
		resourcesMap.put(node,
		   new RelExpLabels(nextLabel(),nextLabel()));
	}
	public void inAGeqExp(AGeqExp node) {
		inAnyExp(node);
		resourcesMap.put(node,
		   new RelExpLabels(nextLabel(),nextLabel()));
	}
	// rel-exp end
	public void inAInstanceofExp(AInstanceofExp node) {
		inAnyExp(node);
	}
	public void inAPlusExp(APlusExp node) {
		inAnyExp(node);
	}
	public void inAMinusExp(AMinusExp node) {
		inAnyExp(node);
	}
	public void inAMultExp(AMultExp node) {
		inAnyExp(node);
	}
	public void inADivExp(ADivExp node) {
		inAnyExp(node);
	}
	public void inAModExp(AModExp node) {
		inAnyExp(node);
	}
	public void inAUminusExp(AUminusExp node) {
		inAnyExp(node);
	}
	// not expression
	public void inANotExp(ANotExp node) {
		inAnyExp(node);
		resourcesMap.put(node,
		   new NotExpLabels(nextLabel(),nextLabel()));
	}
	public void inACastExp(ACastExp node) {
		inAnyExp(node);
	}
	public void inACasttocharExp(ACasttocharExp node) {
		inAnyExp(node);
	}
	public void inAIdExp(AIdExp node) {
		inAnyExp(node);
	}
	public void inAThisExp(AThisExp node) {
		inAnyExp(node);
	}
	public void inANewExp(ANewExp node) {
		inAnyExp(node);
	}
	public void inACallExp(ACallExp node) {
		inAnyExp(node);
	}
	public void inAIntconstExp(AIntconstExp node) {
		inAnyExp(node);
	}
	public void inATrueExp(ATrueExp node) {
		inAnyExp(node);
	}
	public void inAFalseExp(AFalseExp node) {
		inAnyExp(node);
	}
	public void inACharconstExp(ACharconstExp node) {
		inAnyExp(node);
	}
	public void inAStringconstExp(AStringconstExp node) {
		inAnyExp(node);
	}
	public void inANullExp(ANullExp node) {
		inAnyExp(node);
	}
	// sets tostring label resources if applicable
	private void inAnyExp(Node node) {
		if (((Boolean)coerceToStringMap.get(node)).booleanValue())
			toStringResourcesMap.put(node,
			   new ToStringLabels(nextLabel(),nextLabel()));
	}	   
}
