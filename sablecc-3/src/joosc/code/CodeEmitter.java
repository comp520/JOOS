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


package joosc.code;

import java.io.*;
import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import joosc.abstracter.*;
import joosc.symbol.*;
import joosc.type.*;
import joosc.resource.*;

/* 
 * CodeEmitter - emits Jasmin bytecode files for input program
 *
 * History:
 *    2 Jun 2000 - thorough code review and bug fix
 *   31 may 2000 - created; feature-complete
 */
 
public class CodeEmitter extends DepthFirstAdapter {
	private PrintWriter out;
	private String filename;
	private Hashtable astToGenericMap;
	private Hashtable symAnnotations;
	private ClassHierarchy hierarchy;
	private Hashtable resourcesMap;
	private Hashtable signaturesMap;
	private Hashtable astToCodeMap;
	
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
			ast.apply(new CodeEmitter(cname,proxy));
		}
 	}
 	
	public CodeEmitter(String filename, 
	                   AnalysisDataManager proxy) {
		this.filename = filename;
		this.astToGenericMap = proxy.astToGenericMap;
		this.symAnnotations = proxy.symAnnotations;
		this.hierarchy = proxy.hierarchy;
		this.resourcesMap = proxy.resourcesMap;
		this.signaturesMap = proxy.signaturesMap;
		this.astToCodeMap = proxy.astToCodeMap;
	}
	
	/************************************************************
	 *                         Helpers                          *
	 ************************************************************/
	 
	private String makeEmittableFileName(String filename) {
		int dotPos = filename.lastIndexOf('.');
		// jasmin file extension = ".j"
		return filename.substring(0,dotPos+1) + "j";
	}
	
	private void emitType(PType node) {
		if (node instanceof AIntType)
			out.print("I");
		else if (node instanceof ABooleanType)
			out.print("Z");
		else if (node instanceof ACharType)
			out.print("C");
		else if (node instanceof AVoidType)
			out.print("V");
		else if (node instanceof AReferenceType) {
			out.print(
			   "L"+
			   (String)signaturesMap.get(
			      symAnnotations.get(node))+
			   ";");
		}
	}

	private int stackLimit(CodeChain code) {
		return 25; // dummy unsafe answer (A-)
	}

	/************************************************************
	 *                      AST Visitors                        *
	 ************************************************************/
	 
	/* class production */
	
	public void caseAClass(AClass node) {
		Iterator iter;

		try {
			// open output stream
			out = new PrintWriter(
			         new FileWriter(
				    makeEmittableFileName(filename)));
			// emit class
			out.print(".class public ");
			if (node.getClassmods() != null)  {
				node.getClassmods().apply(this);
				out.print(" ");
			}
			out.println(node.getIdentifier().getText());
			out.println();
			// every emittable class has a parent
			out.println(".super "+
			            (String)signaturesMap.get(
			               hierarchy.get(node).getParent()));
			out.println();
      
			for (iter = node.getFields().iterator(); iter.hasNext();)
				((PField)iter.next()).apply(this);
			if (! node.getFields().isEmpty())
				out.println();
			for (iter = node.getConstructors().iterator(); iter.hasNext();)
				((PConstructor)iter.next()).apply(this);
			for (iter = node.getMethods().iterator(); iter.hasNext();)
				((PMethod)iter.next()).apply(this);
			// flush and close output stream
			out.flush(); out.close();
		} catch (IOException e) {
			MyError.fatalError("emission",
			   "IOException while emitting code for "+filename+
			   ": " + e.getMessage());
		}
	}
	public void caseAExternClass(AExternClass node) {
		// do nothing: no code emitted for extern classes
	}

	/* classmods production */
	
	public void caseAFinalClassmods(AFinalClassmods node) {
		out.print(node.getFinal().getText());
	}
	public void caseAAbstractClassmods(AAbstractClassmods node) {
		out.print(node.getAbstract().getText());
	}

	/* onefield production */
	
	public void caseAOnefield(AOnefield node) {
                /* LJH - removed private from field decl */
		out.print(".field protected " + 
		          node.getIdentifier().getText() + " ");
		emitType(node.getType());
		out.println();
	}

	/* constructor production */
	
	public void caseAConstructor(AConstructor node) {
		CodeChain code = (CodeChain)astToCodeMap.get(node);
		ExecUnitResources res =	(ExecUnitResources)resourcesMap.get(node);
		
		out.println(".method public <init>" +
		            (String)signaturesMap.get(node));
		out.println("  .limit locals " + res.getLocalsLimit());
		out.println("  .limit stack " + stackLimit(code));
		code.printTo(out);
		out.println(".end method");
		out.println();
	}

	/* method production */
	
	public void caseAModMethod(AModMethod node) {
		caseAnyMethod(node);
	}
	public void caseANonmodMethod(ANonmodMethod node) {
		caseAnyMethod(node);
	}
	public void caseAAbstractMethod(AAbstractMethod node) {
		caseAnyMethod(node);
	}
	public void caseAMainMethod(AMainMethod node) {
		caseAnyMethod(node);
	}
	private void caseAnyMethod(Node node) {
		GenericMethod gMethod = (GenericMethod)astToGenericMap.get(node);
		GenericMethod.Modifier gMod = gMethod.getModifier();
		CodeChain code = (CodeChain)astToCodeMap.get(node);
		ExecUnitResources res =	(ExecUnitResources)resourcesMap.get(node);
		
		if (gMod == GenericMethod.STATIC) {
			out.println(
			   ".method public static main([Ljava/lang/String;)V");
		}
		else {
			out.print(".method public ");
			if (gMod != null)
				out.print(gMod + " ");
			out.println(gMethod.getName() + 
			            (String)signaturesMap.get(node));
		}
                /* LJH - added check for abstract methods */
                if (gMod != GenericMethod.ABSTRACT) {
		  out.println("  .limit locals " + res.getLocalsLimit());
		  out.println("  .limit stack " + stackLimit(code));
		  code.printTo(out);
                }
		out.println(".end method");
		out.println();
	}
}
