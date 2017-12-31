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

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import joosc.abstracter.*;
import joosc.symbol.*;
import joosc.type.*;
import joosc.resource.*;
import joosc.code.*;
import joosc.code.bytecode.*;

public class PeepholeOptimizer extends DepthFirstAdapter {
	List peepholes; // say dynamically loaded
	Hashtable codeAnnotations;
	
	public PeepholeOptimizer(Hashtable codeAnnotations) {
		this.codeAnnotations = codeAnnotations;
	}
	
	public void caseAConstructor(AConstructor node) {
	/*
		ExecUnitCode notes = (ExecUnitCode)codeAnnotations.get(node);
		optimize(notes.getCodeStream());
	*/
	}
	// *** idem for non-external methods
	
	private void optimize(CodeChain code) {
	/*
		boolean globalChange, change, optimized;
		Iterator peepIter;
		Peephole peep;
		Instr instr;
		InstrBox instrBox;

		globalChange = true;
		while (globalChange) { // do another round of optimization
			globalChange = false;
			instr = code.getFirst();
			while(instr != null) { // more instructions
				instrBox = new InstrBox(instr);
				change = true;
				while (change) { // another round on same window of code
					change = false;
					for (peepIter = peepholes.iterator(); peepIter.hasNext();) {
						peep = (Peephole)((Class)peepIter.next()).newInstance();
						optimized = peep.apply(code,instrBox);
						change = change || optimized;
					}
					globalChange = globalChange || change;
				}
				if (instrBox.getInstr() == null) break;// stream cut-off
				instr = code.getSuccOf(instrBox.getInstr());
			}
		}
    */
	}
	
	public interface Peephole {
		boolean apply(CodeChain code, InstrBox start);
	}
	
	public class SimplifyMultiplicationRightPeephole implements Peephole {
		public boolean apply(CodeChain code,InstrBox start) {
			Object i1,i2,i3;
			List by;
			int x,k;

		/*			
			// bail out if any instruction is null before checking it against pattern			if ((((i1 = start.getInstr()) != null) && (i1 instanceof IloadInstr)) &&
			    (((i2 = code.getSuccOf(i1)) != null) && (i2 instanceof LdcIntInstr)) &&
			    (((i3 = code.getSuccOf(i2)) != null) && (i3 instanceof ImulInstr))) {
				k = ((LdcIntInstr)i2).getConstant();
				x = ((IloadInstr)i1).getOffset();
				if (k == 0) {
					by = new ArrayList(1);
					by.add(new LdcIntInstr(0));
					return code.replace(start,3,by);
				}
				else if (k == 1) {
					by = new ArrayList(1);
					by.add(new IloadInstr(x));
					return code.replace(start,3,by);
				}
				else if (k == 2) {
					by = new ArrayList(3);
					by.add(new IloadInstr(x));
					by.add(new DupInstr());
					by.add(new IaddInstr());
					return code.replace(start,3,by);
				}
				return false;
			}
         */
			return false;
		}
	}
}
			    
