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


package joosc;

import joosc.node.*;
import joosc.analysis.*;
import java.util.*;

/**
 * PrettyPrinter - for ast correctness verification.<p>
 *
 * History:
 * <ul>
 *   <li>Aug 18, 2003 - fixed documentation.
 *   <li>7 May 2000 - marked identifier list un-flattening as obsolete
 *   <li>6 Mar 2000 - ast de-transformation: list un-flattening completed
 *                added prototype transformed ast pretty-printing (idlist)
 *   <li>5 Mar 2000 - created
 * </ul>
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
}
