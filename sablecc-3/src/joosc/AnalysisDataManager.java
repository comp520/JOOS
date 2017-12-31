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


/*
 * August 2003
 *
 * Updated JOOSJ which works with the new version of 
 * SableCC (SableCC3.0)
 *
 * Update by Ahmer Ahmedani & Feng Qian
 * email: ahmer.ahmedani@mail.mcgill.ca & fqian@cs.mcgill.ca
 */

package joosc;

import java.util.*;
import joosc.symbol.*;

/**
 * An analysis data manager centralizes access to all the data collected by 
 * the various analyses for the purpose of dissemination.<p>
 *
 * Put here all compiler resources that needs to be shared between passes.<p>
 *
 * History:
 * <ul>
 *   <li>18 Aug 2003 - organize java doc
 *   <li>7 Jun 2000 - created
 * </ul>
 */
 
public class AnalysisDataManager {
	
	/**
   * Initializes a map for abstracter-generated data. 
   */
	public void initAbstracter() {
		astToGenericMap = new Hashtable();
	}
	public Hashtable astToGenericMap;
	
	/**
   * Initializes data structures for symbol-table-generated data.
   */
	public void initSymbolTable() {
		classlib = new SymbolTable();
		hierarchy = new ClassHierarchy();
		symAstToScopeMap = new Hashtable();
		symAnnotations = new Hashtable();
	}
	public SymbolTable classlib;
	public ClassHierarchy hierarchy;
	public Hashtable symAstToScopeMap;
	public Hashtable symAnnotations;
	
	/**
   * Initializes data structures for type-checking-generated data.
   */
	public void initTypeChecking() {
		typeAnnotations = new Hashtable();
		coerceToStringMap = new Hashtable();
		invokeBindingsMap = new Hashtable();
	}
	public Hashtable typeAnnotations;
	public Hashtable coerceToStringMap;
	public Hashtable invokeBindingsMap;
	
	/**
   * Initializes data structures for resources-generated data 
   */
	public void initResources() {
		resourcesMap = new Hashtable();
		toStringResourcesMap = new Hashtable();
	}
	public Hashtable resourcesMap;
	public Hashtable toStringResourcesMap;
	
	/**
   * Initializes data structures for code-generation-generated data.
   */
	public void initCodeGenerator() {
		signaturesMap = new Hashtable();
		astToCodeMap = new Hashtable();
	}
	public Hashtable signaturesMap;
	public Hashtable astToCodeMap;
}
