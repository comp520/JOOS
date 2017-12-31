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
 * SymInterfaceWalker
 *
 * Defines classes and their interfaces (fields and methods).
 * Also fills the class hierarchy with HClass objects,and 
 * assigns them the symbol table containing the interface of
 * the corresponding class (localsym field).
 *
 * History:
 *   21 May 2000 - design change: deals with HClass instead of GenericClass
 *   17 May 2000 - set modifier field of GenericClass
 *    9 May 2000 - feature-complete
 *    8 May 2000 - created
 */
public class SymInterfaceWalker extends DepthFirstAdapter 
                                implements Symbol.Constants {
	private String filename;
	private SymbolTable classlib;
	private ClassHierarchy hierarchy;
	// scope contained by current class (null if cannot be set)
	private SymbolTable localsym; 
	// name of current class
	private String classname;
	
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
			ast.apply(new SymInterfaceWalker(cname,proxy));
			MyError.noErrors();
		}
 	}
	
	public SymInterfaceWalker(String fn,
	                          AnalysisDataManager proxy) {
		this.filename = fn;
		this.classlib = proxy.classlib;
		this.hierarchy = proxy.hierarchy;
		localsym = null;
		classname = null;
	}
	
	/********************************************************
	 *                 AST Node Visitors                    *
	 ********************************************************/

	/* class and extern_class productions */
	
	public void inAClass(AClass node) {
		inAnyClass(node.getIdentifier(),node.getClassmods(),node);
	}
	public void inAExternClass(AExternClass node) {
		inAnyClass(node.getIdentifier(),node.getClassmods(),node);
	}
	public void inAnyClass(TIdentifier id, PClassmods mod, Node node) {
		String name = id.getText();
		HClass hc;
		
		// ensure class name uniqueness in global scope
		if (classlib.defSymbol(name)) {
			MyError.error(filename,
			              "class name "+name+" already defined",
			              id.getLine());
			localsym = null;
		}
		else {
			// push class symbol
			classlib.putSymbol(name,classSymK,node);
			// add class to class hierarchy
			hc = new HClass(name);
			localsym = new SymbolTable();
			hc.setLocalsym(localsym);
			hierarchy.put(node,hc);
		}

		classname = name;
	}

	/* onefield production */
	
	public void inAOnefield(AOnefield node) {
		if (localsym == null)
			return;

		localsym.putSymbol(node.getIdentifier().getText(),
		                   fieldSymK,node);
	}

	/* constructor and extern_constructor productions */
	
	public void inAConstructor(AConstructor node) {
		inAnyConstructor(node.getIdentifier(),node);
	}
	public void inAExternConstructor(AExternConstructor node) {
		inAnyConstructor(node.getIdentifier(),node);
	}
	public void inAnyConstructor(TIdentifier id, Node node) {
		String name;
		
		if (localsym == null)
			return;

		name = id.getText();
		if (! name.equals(classname)) {
			MyError.error(filename,"constructor name "+name+
			              " different from class name",
				      id.getLine());
		}
	}

	/* method and extern_method productions */
	
	public void inAModMethod(AModMethod node) {
		inAnyMethod(node.getIdentifier(), node);
	}
	public void inANonmodMethod(ANonmodMethod node) {
		inAnyMethod(node.getIdentifier(), node);
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		inAnyMethod(node.getIdentifier(), node);
	}
	public void inAMainMethod(AMainMethod node) {
		inAnyMethod(new TMain(), node);
	}
	public void inAModExternMethod(AModExternMethod node) {
		inAnyMethod(node.getIdentifier(), node);
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		inAnyMethod(node.getIdentifier(), node);
	}
	public void inAnyMethod(Token id, Node node) {
		String name;
		
		if (localsym == null)
			return;
			
		name = id.getText();
		if (localsym.defSymbol(name)) {
			MyError.error(filename,
			              "Redfinition of an identifier: "+
				      "method name "+name+" already defined",
				      id.getLine());
		}
		else {
			localsym.putSymbol(name,methodSymK,node);
		}
	}

}
