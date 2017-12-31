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

/*
 * ClassFile - Represents a given classfile. 
 *             Used for multifile program compilation.
 *
 * History:
 *   2 Apr 2000 - created
 */
public class ClassFile {
	private Node ast;
	private String name;
	
	public ClassFile(String name, Node ast) {
		this.name = name;
		this.ast = ast;
	}
	
	public Node getAst() { return ast; }
	public String getName() { return name; }
}
