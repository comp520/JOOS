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


package joosc;

import joosc.node.*;
import joosc.analysis.*;
import java.util.*;

/**
 * PrettyPrinter - for ast correctness verification
 *
 * History:
 *   7 May 2000 - marked identifier list un-flattening as obsolete
 *   6 Mar 2000 - ast de-transformation: list un-flattening completed
 *                added prototype transformed ast pretty-printing (idlist)
 *   5 Mar 2000 - created
 */
public class PrettyPrinter extends DepthFirstAdapter {
	protected String filename;

	public PrettyPrinter(String fn) { filename = fn; }
	
	public static void print(List theProgram) {
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		
		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			ast.apply(new PrettyPrinter(classfile.getName()));
		}
	}
	
	/* insert newline at end of output */
	public void outADefaultClassfile(ADefaultClassfile node) {
		System.out.print("\n");
	}
	public void outAExternClassfile(AExternClassfile node) {
		System.out.print("\n");
	}
	
	public void defaultCase(Node node)
	{
		System.out.print(((Token) node).getText() + " ");
	}
	
	/*********************************************************************
	 *                 pretty-print transformed entities                 *
	 *                 by unraveling the transformations                 *
	 *                 that produce unparseable code                     *
	 *********************************************************************/
	
	/* flattened lists */
	 
	public void caseAFormalList(AFormalList node) {
		Iterator formalListIter = node.getFormal().iterator();
		PFormal formal;
		
		// at least one formal expected
		Assert.that(formalListIter.hasNext());
		formal = (PFormal) formalListIter.next();
		formal.apply(this);
		
		// insert separating commas
		while (formalListIter.hasNext()) {
			System.out.print(", ");
			formal = (PFormal) formalListIter.next();
			formal.apply(this);
		}
	}	
	
	public void caseAArgumentList(AArgumentList node) {
		Iterator argListIter = node.getExp().iterator();
		PExp arg;
		
		// at least one argument expected
		Assert.that(argListIter.hasNext());
		arg = (PExp) argListIter.next();
		arg.apply(this);
		
		// insert separating commas
		while (argListIter.hasNext()) {
			System.out.print(", ");
			arg = (PExp) argListIter.next();
			arg.apply(this);
		}
	}	
}