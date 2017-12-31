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

import java.util.*;
import joosc.node.*;

/*
 * GenericClass
 *
 * Factors out commonality between AExternClass and AClass AST nodes.
 * Intended for use by entities that need to handle classes generically.
 *
 * History:
 *  23 May 2000 - added constructors and methods fields
 *  21 May 2000 - corrected bug: what if no extension?
 *  20 May 2000 - (re)created; feature-complete
 */
public class GenericClass {
	private TIdentifier id;
	private PClassmods modifier;
	private TIdentifier parentId;
	private LinkedList constructors; // possibly extern constructors
	private LinkedList methods; // possibly extern methods
	
	/* public constructor (mod and ext can be null) */
	public GenericClass(TIdentifier id, PClassmods mod, AExtension ext,
	                    LinkedList constructors, LinkedList methods) {
		this.id = id;
		modifier = mod;
		if (ext == null)
			parentId = null;
		else
			parentId = ext.getIdentifier();
		this.constructors = constructors;
		this.methods = methods;	
	}
	
	/* accessors */
	public String getName() { return id.getText(); }
	public PClassmods getModifier() { return modifier; }
	public String getParentName() { 
		if (parentId == null)
			return null;
		else
			return parentId.getText(); 
	}
	public LinkedList getConstructors() { return constructors; }
	public LinkedList getMethods() { return methods; }
	// special purpose accessors (e.g. to access line number)
	public TIdentifier getIdentifier() { return id; }
	public TIdentifier getParentIdentifier() { return parentId; }
}
