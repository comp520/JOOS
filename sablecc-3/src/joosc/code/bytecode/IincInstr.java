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

public class IincInstr extends StacklessInstr {
	private int offset, inc;
	public IincInstr(int offset, int increment) {
		this.offset = offset;
		inc = increment;
	}
	public int getOffset() { return offset; }
	public int getIncrement() { return inc; }
	protected String getName() { return "iinc"; }
	public String toString() { 
		return getName()+" "+getOffset()+" "+getIncrement();
	}
}
