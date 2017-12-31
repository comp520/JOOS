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
 * NotExpLabels
 *
 * Provides the labels necessary to generate code 
 * for a not expression.
 *
 * History:
 *   25 May 2000 - created
 */
public class NotExpLabels extends Labels {
	private int trueLabel, stopLabel;
	
	public NotExpLabels(int trueLabel, int stopLabel) {
		this.trueLabel = trueLabel;
		this.stopLabel = stopLabel;
	}
	
	public int getTrueLabel() { return trueLabel; }
	public int getStopLabel() { return stopLabel; }
}