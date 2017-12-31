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


package joosc.abstracter;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;

/**
 * Abstracts class, constructor and method AST nodes
 * by factoring out the commonality between the various
 * possible forms of each of these node kinds into GenericClass, 
 * GenericConstructor and GenericMethod objects respectively.
 * Maps AST nodes to corresponding Generic objects.<p>
 *
 * History:
 * <ul>
 *   <li>19 Aug 2003 - change to SableCC 3 AST grammar, fix documentation
 *   <li>23 May 2000 - complete
 *   <li>20 May 2000 - created
 * </ul>
 */
public class AstAbstracter extends DepthFirstAdapter {
	private Hashtable astToGenericMap;
	private String filename;

	public AstAbstracter(String fn, AnalysisDataManager proxy) {
		filename = fn;
		this.astToGenericMap = proxy.astToGenericMap;
	}
	
 	public static void walk(List theProgram, AnalysisDataManager proxy) {
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		String cname;
		
		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			cname = classfile.getName();
			ast.apply(new AstAbstracter(cname,proxy));
		}
 	}

	/* class and extern_class productions */
	
	public void inAClass(AClass node) {
		astToGenericMap.put(node,
		      new GenericClass(
		         node.getIdentifier(),
		         node.getClassmods(),
		         (AExtension)node.getExtension(),
		         node.getConstructors(),
		         node.getMethods()));
	}
	public void inAExternClass(AExternClass node) {
		astToGenericMap.put(node,
		      new GenericClass(
		         node.getIdentifier(),
		         node.getClassmods(),
		         (AExtension)node.getExtension(),
		         node.getConstructors(),
		         node.getMethods()));
	}
	
	/* constructor and extern_constructor productions */
	
	public void inAConstructor(AConstructor node) {
		astToGenericMap.put(node,
		      new GenericConstructor(
		         node.getIdentifier(),
		         node.getFormals()));
	}
	public void inAExternConstructor(AExternConstructor node) {
		astToGenericMap.put(node,
		      new GenericConstructor(
		         node.getIdentifier(),
		         node.getFormals()));
	}
	
	/* method and extern_method productions */
	
	public void inAModMethod(AModMethod node) {
		GenericMethod.Modifier gmod = null;
		PMethodmods mod = node.getMethodmods();
		
		if (mod instanceof AFinalMethodmods)
			gmod = GenericMethod.FINAL;
		else if (mod instanceof ASynchronizedMethodmods)
			gmod = GenericMethod.SYNCHRONIZED;
			
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         node.getFormals(),
		         gmod));
	}
	public void inANonmodMethod(ANonmodMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         node.getFormals(),
		         null));
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         node.getFormals(),
		         GenericMethod.ABSTRACT));
	}
	public void inAMainMethod(AMainMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         new TIdentifier("main"),
		         new AVoidReturntype(),
		         null,
		         GenericMethod.STATIC));
	}
	public void inAModExternMethod(AModExternMethod node) {
		GenericMethod.Modifier gmod = null;
		PExternMethodmods mod = node.getExternMethodmods();
		
		if (mod instanceof AFinalExternMethodmods)
			gmod = GenericMethod.FINAL;
		else if (mod instanceof AAbstractExternMethodmods)
			gmod = GenericMethod.ABSTRACT;
		else if (mod instanceof ASynchronizedExternMethodmods)
			gmod = GenericMethod.SYNCHRONIZED;
		
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         node.getFormals(),
		         gmod));
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         node.getFormals(),
		         null));
	}
}
