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

import soot.util.*; // for Chain and HashChain
import java.util.*;
import joosc.code.bytecode.*;

/*
 * CodeChain
 *
 * Forward chain of unique instructions (different Instr instances).
 * Represents an alterable finite code stream.
 *
 * History:
 *   29 May 2000 - feature-complete
 *   26 May 2000 - created
 */
public class CodeChain {
	private Chain chain;
	
	public CodeChain() {
		chain = new HashChain();
	}
	
	/* Both methods have the same semantics: appending an Instr */
	public void addLast(Instr instr) { chain.addLast(instr); }
	public void add(Instr instr) { addLast(instr); }
	
	public Instr getFirst() {
		try {
			return (Instr)chain.getFirst();
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}
	
	/* Returns successor of instr, or null if instr has none */
	public Instr getSuccOf(Instr instr) {
		try {
			return (Instr)chain.getSuccOf(instr);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	/* 
	 * Returns successor of instr by howMany instructions, 
	 * or null if less than howMany instructions follow instr. 
	 */
	public Instr getSuccOfBy(Instr instr, int howMany) {
		int i = 0;
		Object cur = null;
		
		for (Iterator iter = chain.iterator(instr); i <= howMany; i++) {
			if (! iter.hasNext())  {
				cur = null;
				break;
			}
			cur = iter.next();
		}
		return (Instr)cur;
	}
	
	/* Iterators returned by these methods implement the remove method */
	public Iterator iterator() { return chain.iterator(); }
	public Iterator iterator(Instr start) { 
		return chain.iterator(start); 
	}

	/* 
	 * Replaces howMany instructions (within code stream boundaries) 
	 * starting at from by the sequence of instructions pointed to by by.
	 * params: by must be a list of Instr objects (possibly empty/null).
	 * return: boolean indicating success of replacement.
	 * WARNING: one should never replace a LabelInstr not 
	 * representing a dead label. Behavior undefined otherwise!
	 */
	public boolean replace(InstrBox from, int howMany, List by) {
		Instr fromInstr = from.getInstr();
		int i = 0;
		
		chain.insertBefore(by,fromInstr);
		// set 'from' instrBox
		if (by == null || by.isEmpty())
			from.setInstr(getSuccOfBy(fromInstr,howMany));
		else
			from.setInstr((Instr)by.get(0));
		// remove replaced instructions
		for (Iterator iter = chain.iterator(fromInstr);
		     iter.hasNext() && i < howMany; i++)  {
			iter.next();
			iter.remove();
		}
		
		return true; // indicate change
	}
	/* 
	 * Replaces howMany instructions (within code stream boundaries) 
	 * starting at from by the sequence of instructions pointed to by by,
	 * updating indegree of labels in the case of replacing a BranchInstr.
	 * params: by must be a list of Instr objects (possibly empty/null).
	 * return: boolean indicating success of replacement.
	 * WARNING: one should never replace a LabelInstr not 
	 * representing a dead label. Behavior undefined otherwise!
	 */
	public boolean smartReplace(InstrBox from, int howMany, List by) {
		Instr fromInstr = from.getInstr();
		int i = 0;
		Instr instr;
		
		chain.insertBefore(by,fromInstr);
		// set 'from' instrBox
		if (by == null || by.isEmpty())
			from.setInstr(getSuccOfBy(fromInstr,howMany));
		else
			from.setInstr((Instr)by.get(0));
		// remove replaced instructions
		for (Iterator iter = chain.iterator(fromInstr); 
		     iter.hasNext() && i < howMany; i++)  {
			instr = (Instr)iter.next();
			// decrement indegree of target label instr if applicable
			if ((instr instanceof BranchInstr) &&
			     ! ((BranchInstr)instr).getTarget().isDead()) {
				((BranchInstr)instr).getTarget().dropSource();
			}
			iter.remove();
		}
		
		return true; // indicate change
	}
	
	public boolean remove(InstrBox instrBox) {
		return replace(instrBox,1,null);
	}
	/* updates indegree of label targeted by instrBox if a BranchInstr */
	public boolean smartRemove(InstrBox instrBox) {
		return smartReplace(instrBox,1,null);
	}
	
	/* code chain emitter */
	public void printTo(java.io.PrintWriter out) {
		for (Iterator iter = iterator(); iter.hasNext();)
			out.println("  " + iter.next().toString());
	}	
}