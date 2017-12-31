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


package joosc.abstracter;

import joosc.node.*;
import java.util.LinkedList;
/*
 * GenericConstructor
 *
 * Factors out commonality between AConstructor and AExternConstructor.
 * Intended for use by entities that need to handle constructors generically.
 *
 * History:
 *  20 May 2000 - created; feature-complete
 */
public class GenericConstructor {
	private TIdentifier id;
	private LinkedList formals;
	
	/* public constructor */
	public GenericConstructor(TIdentifier id, LinkedList formals) {
		this.id = id;
		this.formals = formals;
	}

	/* accessors */
	public String getName() { return id.getText(); }
	public LinkedList getFormals() { return formals; }
	// special purpose accessor
	public TIdentifier getIdentifier() { return id; }
}
