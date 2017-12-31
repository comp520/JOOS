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

public class LabelInstr implements Instr {
	private int label;
	private String name;
	private int sourceCount; // in-degree
	public LabelInstr(String name, int label) {
		this.name = name;
		this.label = label;
		sourceCount = 1;
	}
	public int getHeightDelta() { return 0; }
	public int getLowestUsed() { return 0; }
	public int getLowestAffected() { return 0; }
	// label methods
	public boolean isDead() { return sourceCount == 0; }
	public boolean hasUniqueSource() { return sourceCount == 1; }
	public void addSource() { sourceCount++; }
	public void dropSource() { sourceCount--; }
	public String getCompleteLabel() { 
		return name+"_"+label;
	}
	public String toString() {
		return getCompleteLabel()+":";
	}
}
