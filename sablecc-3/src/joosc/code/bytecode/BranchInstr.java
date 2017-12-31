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

import joosc.code.*;

public abstract class BranchInstr implements Instr {
	private InstrBox targetBox;
	public BranchInstr() {
		targetBox = new InstrBox(null);
	}	
	public void setTarget(LabelInstr instr) {
		targetBox.setInstr(instr);
	}
	public LabelInstr getTarget() {
		return (LabelInstr)targetBox.getInstr();
	}
	protected abstract String getName();
	public String toString() {
		return getName()+" "+getTarget().getCompleteLabel();
	}
}
