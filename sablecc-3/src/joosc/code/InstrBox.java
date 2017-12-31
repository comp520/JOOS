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

import joosc.code.bytecode.*; // for Instr

public class InstrBox {
	private Instr instr;
	
	public InstrBox(Instr instr) { 
		setInstr(instr);
	}
	public Instr getInstr() { return instr; }
	public void setInstr(Instr instr) { this.instr = instr; }
}