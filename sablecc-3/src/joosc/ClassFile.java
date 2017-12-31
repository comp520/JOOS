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

/**
 * ClassFile - Represents a given classfile. Used for multifile program compilation.<p>
 *
 * History:
 * <ul>
 *   <li>Aug 18, 2003 - fix documentation
 *   <li>2 Apr 2000 - created
 * </ul>
 */
public class ClassFile {
	private Node ast;
	private String name;

  /**
   * Defines a ClassFile with a specific name and an AST tree.
   */
	public ClassFile(String name, Node ast) {
		this.name = name;
		this.ast = ast;
	}
	
	public Node getAst() { return ast; }
	public String getName() { return name; }
}
