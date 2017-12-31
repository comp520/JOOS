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

import java.util.*;
import joosc.node.*;

/*
 * ClassHierarchy
 *
 * Encapsulates the class hierarchy of an input program,
 * and offers methods for exploring this hierarchy.
 * 
 * History:
 *   23 May 2000 - corrected bug in worker class hierarchy methods
 *                 (was not checking for null class soon enough)
 *   20 May 2000 - created
 */
public class ClassHierarchy {
	private Hashtable astToHClassMap;
	
	public ClassHierarchy() {
		astToHClassMap = new Hashtable();
	}
	
	/* get HClass object mapped to AST node 'key' */
	
	public HClass get(AClass key) { return get((Node)key); }
	public HClass get(AExternClass key) { return get((Node)key); }
	// implementation (least type-safe)
	public HClass get(Node key) {
		return (HClass)astToHClassMap.get(key);
	}	
	
	/* map an HClass object to AST node 'key' */
	
	public void put(AClass key, HClass hc) { put((Node)key,hc); }
	public void put(AExternClass key, HClass hc) { put((Node)key,hc); }
	// implementation (least type-safe)
	public void put(Node key, HClass hc) {
		astToHClassMap.put(key,hc);
	}
	
	/**************************************************************
	 *                CLASS HIERARCHY ROUTINES                    *
	 **************************************************************/
	
	/*
	 * Checks whether subC is a subclass of superC.
	 * params: each of subC and superC must be an AClass or an AExternClass
	 */
	public boolean subClass(AClass subC, AClass superC) {
		return subClass((Node)subC,(Node)superC);
	}
	public boolean subClass(AClass subC, AExternClass superC) {
		return subClass((Node)subC,(Node)superC);
	}
	public boolean subClass(AExternClass subC, AClass superC) {
		return subClass((Node)subC,(Node)superC);
	}
	public boolean subClass(AExternClass subC, AExternClass superC) {
		return subClass((Node)subC,(Node)superC);
	}
	// implementation (least type-safe)
	public boolean subClass(Node subC, Node superC) {
		HClass subHC,superHC;
		
		if (subC == null) return false;
		subHC = get(subC);
		superHC = get(superC);
		if (subHC.getName().equals(superHC.getName())) return true;
		if (subHC.getParent() == null) return false; // root reached
		return subClass(subHC.getParent(),superC);
	}
	
	/*
	 * Peforms a symbol lookup in the class hierarchy starting at
	 * startC (from a bottom-up perspective).
	 * params: startC _must_ be an AClass or an AExternClass
	 * return: null if not found
	 */
	public Symbol lookupHierarchy(String name, AClass startC) {
		return lookupHierarchy(name,(Node)startC);
	}
	public Symbol lookupHierarchy(String name, AExternClass startC) {
		return lookupHierarchy(name,(Node)startC);
	}
	// implementation (least type-safe)
	public Symbol lookupHierarchy(String name, Node startC) {
		Symbol s;
		HClass startHC;
		
		if (startC == null) return null;
		startHC = get(startC);
		s = startHC.getLocalsym().getSymbol(name);
		if (s != null) return s;
		if (startHC.getParent() == null) return null;
		return lookupHierarchy(name,startHC.getParent());
	}
	
	/*
	 * Performs a lookup of class containing symbol in the class hierarchy
	 * starting at startC (from a bottom-up perspective).
	 * params: startC _must_ be an AClass or an AExternClass
	 * return: AClass or AExternClass node, null if symbol not found
	 */
	public Node lookupHierarchyClass(String name, AClass startC) {
		return lookupHierarchyClass(name,(Node)startC);
	}
	public Node lookupHierarchyClass(String name, AExternClass startC) {
		return lookupHierarchyClass(name,(Node)startC);
	}
	// implementation (least type-safe)
	public Node lookupHierarchyClass(String name, Node startC) {
		Symbol s;
		HClass startHC;
		
		if (startC == null) return null;
		startHC = (HClass)astToHClassMap.get(startC);
		s = startHC.getLocalsym().getSymbol(name);
		if (s != null) return startC;
		if (startHC.getParent() == null) return null;
		return lookupHierarchyClass(name,startHC.getParent());
	}
}
