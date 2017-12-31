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
 * IfelseStmLabels
 *
 * Provides the labels necessary to generate code 
 * for an ifelse statement.
 *
 * History:
 *   25 May 2000 - created
 */
public class IfelseStmLabels extends Labels {
	private int elseLabel, stopLabel;
	
	public IfelseStmLabels(int elseLabel, int stopLabel) {
		this.elseLabel = elseLabel;
		this.stopLabel = stopLabel;
	}
	
	public int getElseLabel() { return elseLabel; }
	public int getStopLabel() { return stopLabel; }
}