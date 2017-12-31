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

public abstract class InstanceInvokeInstr implements Instr {
	private String nameAndSignature;
	private int delta, used, affected;
	/* Computes the effects of invoke on the stack from the signature */
	private void setStackEffect() {
		int pos = 0; 
		char[] a = getNameAndSignature().toCharArray();
		
		delta = 0;
		while (a[pos] != '(') pos++;
		pos++;
		while (a[pos] != ')') {
			delta--;
			if (a[pos] == 'L')
				while (a[pos] != ';') pos++;
			pos++;
		}
		pos++;
		// height lowered by number of args (+1 for receiver) to invoke
		used = affected = delta - 1;
		if (a[pos] == 'V')
			delta--; // void; receiver not made up for by return
	}
	public InstanceInvokeInstr(String nameAndSignature) {
		this.nameAndSignature = nameAndSignature;
		setStackEffect();
	}
	public String getNameAndSignature() { return nameAndSignature; }
	public int getHeightDelta() { return delta; }
	public int getLowestUsed() { return used; }
	public int getLowestAffected() { return affected; }
	protected abstract String getName();
	public String toString() {
		return getName()+" "+getNameAndSignature();
	}
}
