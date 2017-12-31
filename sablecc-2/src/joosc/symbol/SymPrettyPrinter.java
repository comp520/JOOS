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


package joosc.symbol;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;

/*
 * SymPrettyPrinter
 * 
 * Prints the symbol table corresponding to each input program scope.
 *
 * History:
 *   22 May 2000 - corrected bug: was not printing appropriate class scope
 *   21 may 2000 - integrated and expanded classlib pretty-printing
 *   12 May 2000 - feature-complete
 *   10 May 2000 - created
 */
public class SymPrettyPrinter extends DepthFirstAdapter {
	protected String filename;
	protected Hashtable astToScopeMap;
	
	public SymPrettyPrinter(String fn, AnalysisDataManager proxy) {
		filename = fn;
		this.astToScopeMap = proxy.symAstToScopeMap;
	}

	public static void print(List theProgram, AnalysisDataManager proxy) { 
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		String cname;
		
		System.out.println("CLASS LIBRARY START");
		printClassLibrary(proxy.classlib,proxy.hierarchy);
		System.out.println("CLASS LIBRARY END");
		System.out.println();

		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			cname = classfile.getName();
			ast.apply(new SymPrettyPrinter(cname, proxy));
		}
	}
	
	/* 
	 * Prints the class library.
	 * This includes the class symbols but also the symbols constituting 
	 * the interface of each class (collected in symInterfaceWalker pass).
	 */
	private static void printClassLibrary(SymbolTable classlib, 
	                                      ClassHierarchy hierarchy) {
		Enumeration e = classlib.elements();
		Symbol classSym;
		
		while(e.hasMoreElements()) {
			classSym = (Symbol)e.nextElement();
			System.out.println(classSym+" -- contains symbols:");
			System.out.println(
		           hierarchy.get(classSym.value()).getLocalsym());
		}
	}
		

	/********************************************************
	 *                 AST Node Visitors                    *
	 ********************************************************/

	/* class and extern class productions */
	
	public void inAClass(AClass node) {
		inAnyClass(node.getIdentifier(),node);
	}
	public void outAClass(AClass node) {
		outAnyClass(node);
	}
	public void inAExternClass(AExternClass node) {
		inAnyClass(node.getIdentifier(),node);
	}
	public void outAExternClass(AExternClass node) {
		outAnyClass(node);
	}
	public void inAnyClass(Token name, Node node) {
		System.out.println("ENTERING CLASS "+name.getText());
		System.out.print((SymbolTable)astToScopeMap.get(node));
	}
	public void outAnyClass(Node node) {
		System.out.println("LEAVING CLASS");
	}
	
	/* method and extern method productions */
	
	public void inAModMethod(AModMethod node) {
		inAnyMethod(node.getIdentifier(),node);
	}
	public void inANonmodMethod(ANonmodMethod node) {
		inAnyMethod(node.getIdentifier(),node);
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		inAnyMethod(node.getIdentifier(),node);
	}
	public void inAMainMethod(AMainMethod node) {
		inAnyMethod(node.getMain(),node);
	}
	public void inAModExternMethod(AModExternMethod node) {
		inAnyMethod(node.getIdentifier(),node);
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		inAnyMethod(node.getIdentifier(),node);
	}
	public void inAnyMethod(Token name, Node node) {
		System.out.println("ENTERING METHOD "+name.getText());
		System.out.print((SymbolTable)astToScopeMap.get(node));
	}
	public void outAModMethod(AModMethod node) {
		outAnyMethod(node.getIdentifier(),node);
	}
	public void outANonmodMethod(ANonmodMethod node) {
		outAnyMethod(node.getIdentifier(),node);
	}
	public void outAAbstractMethod(AAbstractMethod node) {
		outAnyMethod(node.getIdentifier(),node);
	}
	public void outAMainMethod(AMainMethod node) {
		outAnyMethod(node.getMain(),node);
	}
	public void outAModExternMethod(AModExternMethod node) {
		outAnyMethod(node.getIdentifier(),node);
	}
	public void outANonmodExternMethod(ANonmodExternMethod node) {
		outAnyMethod(node.getIdentifier(),node);
	}
	public void outAnyMethod(Token name, Node node) {
		System.out.println("LEAVING METHOD");
	}
	
	/* constructor and extern constructor productions */
	
	public void inAConstructor(AConstructor node) {
		inAnyConstructor(node.getIdentifier(),node);
	}
	public void inAExternConstructor(AExternConstructor node) {
		inAnyConstructor(node.getIdentifier(),node);
	}
	public void inAnyConstructor(Token name, Node node) {
		System.out.println("ENTERING CONSTRUCTOR "+name.getText());
		System.out.print((SymbolTable)astToScopeMap.get(node));
	}
	public void outAConstructor(AConstructor node) {
		outAnyConstructor(node.getIdentifier(),node);
	}
	public void outAExternConstructor(AExternConstructor node) {
		outAnyConstructor(node.getIdentifier(),node);
	}
	public void outAnyConstructor(Token name, Node node) {
		System.out.println("LEAVING CONSTRUCTOR");
	}
	
	/* block alternative of stm production */
	
	public void inABlockStm(ABlockStm node) {
		System.out.println("ENTERING BLOCK");
		System.out.print((SymbolTable)astToScopeMap.get(node));
	}
	public void outABlockStm(ABlockStm node) {
		System.out.println("LEAVING BLOCK");
	}

}	
