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


package joosc.code.bytecode;

public interface Instr {
	/* Returns line of Jasmin code to be emitted for this bytecode */
	public abstract String toString();
	/* Returns height difference of the stack after this operation */
	public abstract int getHeightDelta();
	/* Returns lowest element on the stack used by this bytecode 
	   (as negative offset relative to the starting point) */
	public abstract int getLowestUsed();
	/* Returns lowest element on the stack affected by this bytecode 
	   (as negative offset relative to the starting point) */
	public abstract int getLowestAffected();
}

