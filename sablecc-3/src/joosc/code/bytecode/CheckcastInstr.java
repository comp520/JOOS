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

public class CheckcastInstr extends ClassInstr { 
	public CheckcastInstr(String className) { super(className); }
	public int getHeightDelta() { return 0; }
	public int getLowestUsed() { return -1; }
	public int getLowestAffected() { return 0; }
	protected String getName() { return "checkcast"; }
}
