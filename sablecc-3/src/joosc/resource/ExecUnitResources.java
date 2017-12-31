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


package joosc.resource;

/*
 * ExecUnitResources
 *
 * Represents a set of resources used to generate code 
 * for a given execution unit (method or constructor).
 *
 * History:
 *    5 Jun 2000 - legacy labelCount field phased out
 *   25 May 2000 - created
 */
public  class ExecUnitResources extends Resources {
	private int localsLimit;
	
	public ExecUnitResources(int localsLimit) {
		this.localsLimit = localsLimit;
	}
	
	public int getLocalsLimit() { return localsLimit; }
}