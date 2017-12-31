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


package joosc.symbol;

import joosc.node.*;
import joosc.*;

/*
 * HClass - Hierarchical Class
 *
 * History:
 *   20 may 2000 - created
 */
public class HClass {
	private String name;
	private Node parent;
	private SymbolTable localsym;
	private boolean parentHasValue;
	private boolean localsymHasValue;
	
	public HClass(String name) {
		this.name = name;
		parentHasValue = false;
		localsymHasValue = false;
	}
	
	/* parent link setters */
	public void setParent(AClass parent) { setParent((Node)parent); }
	public void setParent(AExternClass parent) { setParent((Node)parent); }
	// implementation (least type-safe)
	public void setParent(Node parent) {
		if (!parentHasValue) {
			this.parent = parent; 
			parentHasValue = true;
		}
		else {
			MyError.fatalError("symtable",
			                   "HClass: attempt to set parent twice");
		}
	}
	
	/* localsym field setter */
	public void setLocalsym(SymbolTable localsym) {
		if (!localsymHasValue) {
			this.localsym = localsym;
			localsymHasValue = true;
		}
		else {
			MyError.fatalError("symtable",
			                   "HClass: attempt to set parent twice");
		}
	}
	
	/* accessors */
	public String getName() { return name; }
	public Node getParent() {
		if (parentHasValue)
			return parent;
		else {
			MyError.fatalError("symtable",
			                   "HClass: attempt to get unset parent");
			return null;
		}
	}
	public SymbolTable getLocalsym() {
		if (localsymHasValue)
			return localsym;
		else {
			MyError.fatalError("symtable",
			                   "HClass: attempt to get unset localsym");
			return null;
		}
	}
}
