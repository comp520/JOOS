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
 * SymInterfaceTypesWalker
 *
 * Completes the class hierarchy by setting the parent links, and links 
 * reference type nodes for formals and method returns to corresponding 
 * classe nodes.
 * 
 *
 * History:
 *   21 May 2000 - design alteration: uses HClass instead of GenericClass
 *   13 May 2000 - type node analysis on interface only, here
 *   10 May 2000 - created; feature-complete
 */
public class SymInterfaceTypesWalker extends DepthFirstAdapter 
                                implements Symbol.Constants {
	private String filename;
	private SymbolTable classlib;
	private ClassHierarchy hierarchy;
	private Hashtable annotations;
	
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
			ast.apply(new SymInterfaceTypesWalker(cname,proxy));
			MyError.noErrors();
		}
 	}
	
	public SymInterfaceTypesWalker(String fn,
	                               AnalysisDataManager proxy) {
		this.filename = fn;
		this.classlib = proxy.classlib;
		this.hierarchy = proxy.hierarchy;
		this.annotations = proxy.symAnnotations;
	}
	
	/********************************************************
	 *                 AST Node Visitors                    *
	 ********************************************************/

	/* 
	 * class and extern class productions:
	 * setting up the class hierarchy
	 */
	
	public void inAClass(AClass node) {
		inAnyClass(node.getExtension(),node);
	}
	public void inAExternClass(AExternClass node) {
		inAnyClass(node.getExtension(),node);
	}
	public void inAnyClass(PExtension ext, Node node) {
		TIdentifier parentId;
		HClass hc = hierarchy.get(node);
		Symbol s;
		
		if (ext != null) { // definite extension
			parentId = ((AExtension)ext).getIdentifier();
			s = classlib.getSymbol(parentId.getText());
			if (s == null) {
				MyError.error(filename,
				              "No such parent class "+parentId.getText(),
				              parentId.getLine());
				hc.setParent((Node)null);
			}
			else { // verified definite extension
				hc.setParent(s.value()); 
			}
		}
		else { // no extension
			hc.setParent((Node)null);
		}
	}
	
	/*
	 * interface reference type node analysis:
	 * setting up links between reference types and type-defining classes
	 */
		
	public void inAFormal(AFormal node) {
		checkType(node.getType());
	}	
	public void inANonvoidReturntype(ANonvoidReturntype node) {
		checkType(node.getType());
	}
	public void checkType(PType node) {
		if (! (node instanceof AReferenceType))
			return;
		
		TIdentifier id = ((AReferenceType)node).getIdentifier();
		Symbol s = classlib.getSymbol(id.getText());
		
		if (s == null) {
			MyError.error(filename,
			              "type identifier "+id.getText()+" not declared",
			              id.getLine());
		}
		else { // annotate type node with a link to the defining class node
			annotations.put(node,s.value());
		}
	}
	
}
