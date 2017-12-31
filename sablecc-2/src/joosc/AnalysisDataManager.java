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


package joosc;

import java.util.*;
import joosc.symbol.*;

/*
 * AnalysisDataManager
 *
 * Centralizes access to all the data collected by the various analyses
 * for the purpose of dissemination.
 *
 * Put here all compiler resources that needs to be shared between passes.
 *
 * History:
 *   7 Jun 2000 - created
 */
 
public class AnalysisDataManager {
	
	/* abstracter-generated data */
	public void initAbstracter() {
		astToGenericMap = new Hashtable();
	}
	public Hashtable astToGenericMap;
	
	/* symbol-table-generated data */
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
	
	/* type-checking-generated data */
	public void initTypeChecking() {
		typeAnnotations = new Hashtable();
		coerceToStringMap = new Hashtable();
		invokeBindingsMap = new Hashtable();
	}
	public Hashtable typeAnnotations;
	public Hashtable coerceToStringMap;
	public Hashtable invokeBindingsMap;
	
	/* resources-generated data */
	public void initResources() {
		resourcesMap = new Hashtable();
		toStringResourcesMap = new Hashtable();
	}
	public Hashtable resourcesMap;
	public Hashtable toStringResourcesMap;
	
	/* code-generation-generated data ;) */
	public void initCodeGenerator() {
		signaturesMap = new Hashtable();
		astToCodeMap = new Hashtable();
	}
	public Hashtable signaturesMap;
	public Hashtable astToCodeMap;
}
