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

import joosc.node.*;
import java.util.LinkedList;
/*
 * GenericMethod
 *
 * Factors out commonality between the various PMethod and 
 * PExternMethod alternatives.
 * Intended for use by entities that need to handle methods generically.
 *
 * History:
 *   31 May 2000 - added a toString method to Modifier class
 *   20 May 2000 - created; feature-complete
 */
public class GenericMethod {
	private Token name;
	private PReturntype returntype;
	private LinkedList formals;
	private Modifier modifier;

	/* set of possible method modifiers: choose one among these */
	public static final Modifier 
	                       STATIC = new Modifier("static"),
	                       FINAL = new Modifier("final"),
	                       ABSTRACT = new Modifier("abstract"),
	                       SYNCHRONIZED = new Modifier("synchronized");

	/* public inner class representing a method modifier */
	public static final class Modifier {
		private String id;
		private Modifier(String mod) { id = mod; }
		public String toString() { return id; }
	}
	
	/* public constructor */
	public GenericMethod(Token name, PReturntype returntype, 
	                     LinkedList formals, Modifier modifier) {
		this.name = name;
		this.returntype = returntype;
		this.formals = formals;
		this.modifier = modifier;
	}

	/* accessors */
	public String getName() { return name.getText(); }
	public PReturntype getReturntype() { return returntype; }
	public LinkedList getFormals() { return formals; }
	public Modifier getModifier() { return modifier; }
	// special purpose accessor
	public Token getNameToken() { return name; }
}
