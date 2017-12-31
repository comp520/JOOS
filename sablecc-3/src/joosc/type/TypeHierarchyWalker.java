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

package joosc.type;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import joosc.abstracter.*;
import joosc.symbol.*;

/**
 * TypeHierarchyWalker, first pass of type-checking module. 
 * Statically checks the class hierarchy, and in particular:
 * <ul>
 *   <li> inheritance validity
 *   <li> field hiding validity
 *   <li> method overriding validity
 * </ul>
 * 
 * History:
 * <ul>
 *   <li> 19 Aug 2003 - change to sablecc 3 ast grammar, add documentation
 *   <li>23 May 2000 - corrected bug in inAnyMethod (wrong method node used)
 *                 feature-complete
 *   <li>21 May 2000 - updated to take advantage of astToGenericMap
 *   <li>17 May 2000 - created
 * </ul>
 */
public class TypeHierarchyWalker extends AbstractTypeWalker {
	private Node curClass;
	
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
			ast.apply(new TypeHierarchyWalker(cname,proxy));
		}
 	}

	public TypeHierarchyWalker(String filename,
	                           AnalysisDataManager proxy) {
		this.filename = filename;
		this.astToGenericMap = proxy.astToGenericMap;
		this.hierarchy = proxy.hierarchy;
		this.symAnnotations = proxy.symAnnotations;
		curClass = null;
	}

	/********************************************************
	 *               Type Utility Methods                   *
	 ********************************************************/
	
	/* return type equivalence method */
	
	private boolean equalReturntypes(PReturntype rt1, PReturntype rt2) {
		PType t1,t2;
		
		// different returntypes (one void and the other non-void)
		if (rt1.getClass() != rt2.getClass())
			return false;
		
		// case both non-void types
		if (rt1 instanceof ANonvoidReturntype) {
			t1 = ((ANonvoidReturntype)rt1).getType();
			t2 = ((ANonvoidReturntype)rt2).getType();
			// different types
			if (t1.getClass() != t2.getClass())
				return false;
			// both class types
			if (t1 instanceof AReferenceType) {
				// name equivalence for reference/class types
				return ((AReferenceType)t1).getIdentifier().getText().equals(
				         ((AReferenceType)t2).getIdentifier().getText());
			}
		}
		
		// same void returntypes or non-void types
		return true;
	}
	
	/********************************************************
	 *                  AST Node Visitors                   *
	 ********************************************************/

	/* class and extern production */
	
	public void inAClass(AClass node) {
		inAnyClass(node.getIdentifier(),node);
	}
	public void inAExternClass(AExternClass node) {
		inAnyClass(node.getIdentifier(),node);
	}
	public void inAnyClass(TIdentifier id, Node node) {
		Node parentC = hierarchy.get(node).getParent(); // parent class
		int line = id.getLine();
		GenericClass parentGC;
		PClassmods parMod;
		
		if (hierarchy.subClass(parentC,node)) {
			MyError.error(filename,"cyclic inheritance",line);
		}
		if (parentC != null) { // extension
			parentGC = (GenericClass)astToGenericMap.get(parentC);
			parMod = parentGC.getModifier();
			if ((parMod != null) && (parMod instanceof AFinalClassmods)) {
				MyError.error(filename,"extension of final "+
				              parentGC.getName()+" class",line);
			}
		}
		curClass = node;
	}
	public void outAClass(AClass node) {
		curClass = null;
	}
	public void outAExternClass(AExternClass node) {
		curClass = null;
	}
	
	/* onefield production */
	
	public void inAOnefield(AOnefield node) {
		String name = node.getIdentifier().getText();
		int line = node.getIdentifier().getLine();
		// lookup field name in hierarchy starting at parent of current class
		Node parentC = hierarchy.get(curClass).getParent();
		Symbol s = hierarchy.lookupHierarchy(name,parentC);
		
		if (s != null) {
			MyError.error(filename,"illegal overriding of field "+name,line);
		}
	}
	
	/* method production */
	
	public void inAModMethod(AModMethod node) {
		inAnyMethod(node.getIdentifier(),node.getFormals(),
		            node.getReturntype(),node);
	}
	public void inANonmodMethod(ANonmodMethod node) {
		inAnyMethod(node.getIdentifier(),node.getFormals(),
		            node.getReturntype(),node);
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		inAnyMethod(node.getIdentifier(),node.getFormals(),
		            node.getReturntype(),node);
	}
	// no handling needed for AMainMethod //
	public void inAModExternMethod(AModExternMethod node) {
		inAnyMethod(node.getIdentifier(),node.getFormals(),
		            node.getReturntype(),node);
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		inAnyMethod(node.getIdentifier(),node.getFormals(),
		            node.getReturntype(),node);
	}
	public void inAnyMethod(Token id, LinkedList formals, 
	                        PReturntype returntype,  Node node) {
		String name = id.getText();
		int line = id.getLine();
		// lookup method name in hierarchy starting at parent of current class
		Node parentC = hierarchy.get(curClass).getParent();
		Symbol s = hierarchy.lookupHierarchy(name,parentC);
		GenericMethod gMethod;
		
		if (s != null) {
			// overriden symbol not a method
			if (s.kind() != methodSymK) {
				MyError.error(filename,"illegal overriding of method "+name,
				              line);
			}
			else {
				gMethod = (GenericMethod)astToGenericMap.get(s.value());
				if (gMethod.getModifier() == GenericMethod.FINAL) {
					MyError.error(filename,"illegal overriding of final method "+
					              name,line);
				}
				else {
					// overriding requires same return type and signature
					if (! equalFormals(formals, gMethod.getFormals()) ||
					    ! equalReturntypes(returntype,gMethod.getReturntype())) {
						MyError.error(filename,"overriden method"+name+
						   "must have same signature",line);
					}
				}
			}
		}
	}
}
