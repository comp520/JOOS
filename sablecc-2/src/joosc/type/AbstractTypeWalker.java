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


package joosc.type;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import joosc.abstracter.*;
import joosc.symbol.*;

/*
 * AbstractTypeWalker
 *
 * Abstract type checking pass.
 *
 * History:
 *   23 May 2000 - feature-complete
 *   21 May 2000 - made equalFormals more robust (to null arguments)
 *   18 May 2000 - created
 */
public abstract class AbstractTypeWalker extends DepthFirstAdapter 
                                      implements Symbol.Constants {
	protected String filename;
	protected Hashtable astToGenericMap;
	protected Hashtable symAnnotations;
	protected ClassHierarchy hierarchy;
	
	/********************************************************
	 *               Type Utility Methods                   *
	 ********************************************************/
	
	/* type equivalence method */
	
	protected boolean equalTypes(PType t1, PType t2) {
		if (t1.getClass() != t2.getClass())
			return false;
		if (t1 instanceof AReferenceType) {
			// name equivalence for reference/class types
			return ((AReferenceType)t1).getIdentifier().getText().equals(
			         ((AReferenceType)t2).getIdentifier().getText());
		}
		return true;
	}
	
	/* formal list comparison method */
	
	protected boolean equalFormals(AFormalList formals1, AFormalList formals2) {
		Iterator iter1;
		Iterator iter2;
		
		if (formals1 == null || formals2 == null)
			return (formals1 == null && formals2 == null);
		
		iter1 = formals1.getFormal().iterator();
		iter2 = formals2.getFormal().iterator();

		// compare each pair of formals in order
		while(iter1.hasNext() && iter2.hasNext()) {
			// mismatching pair
			if (! equalTypes(((AFormal)iter1.next()).getType(),
			                 ((AFormal)iter2.next()).getType())) {
				return false;
			}
		}
		// different number of formals
		if (iter1.hasNext() || iter2.hasNext())
			return false;
		
		return true;
	}
}