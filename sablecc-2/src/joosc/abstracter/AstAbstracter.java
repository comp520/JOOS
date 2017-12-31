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


package joosc.abstracter;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;

/*
 * AstAbstracter
 *
 * Abstracts class, constructor and method AST nodes
 * by factoring out the commonality between the various
 * possible forms of each of these node kinds into GenericClass, 
 * GenericConstructor and GenericMethod objects respectively.
 * Maps AST nodes to corresponding Generic objects.
 *
 * History:
 *   23 May 2000 - complete
 *   20 May 2000 - created
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
		         node.getConstructor(),
		         node.getMethod()));
	}
	public void inAExternClass(AExternClass node) {
		astToGenericMap.put(node,
		      new GenericClass(
		         node.getIdentifier(),
		         node.getClassmods(),
		         (AExtension)node.getExtension(),
		         node.getExternConstructor(),
		         node.getExternMethod()));
	}
	
	/* constructor and extern_constructor productions */
	
	public void inAConstructor(AConstructor node) {
		astToGenericMap.put(node,
		      new GenericConstructor(
		         node.getIdentifier(),
		         (AFormalList)node.getFormalList()));
	}
	public void inAExternConstructor(AExternConstructor node) {
		astToGenericMap.put(node,
		      new GenericConstructor(
		         node.getIdentifier(),
		         (AFormalList)node.getFormalList()));
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
		         (AFormalList)node.getFormalList(),
		         gmod));
	}
	public void inANonmodMethod(ANonmodMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         (AFormalList)node.getFormalList(),
		         null));
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         (AFormalList)node.getFormalList(),
		         GenericMethod.ABSTRACT));
	}
	public void inAMainMethod(AMainMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getMain(),
		         new AVoidReturntype((TVoid)node.getVoid().clone()),
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
		         (AFormalList)node.getFormalList(),
		         gmod));
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		astToGenericMap.put(node,
		      new GenericMethod(
		         node.getIdentifier(),
		         node.getReturntype(),
		         (AFormalList)node.getFormalList(),
		         null));
	}
}
