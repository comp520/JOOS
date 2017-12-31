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
 * SymbolTable - Stack of symbol hashtables. 
 *               Based on SymbolTable from cs520 WIG compiler.
 * History:
 *  21 May 2000 - added elements method, returning enumeration of symbols
 *  20 May 2000 - moved class hierarchy methods in own module
 *  11 May 2000 - added class hierarchy methods
 *  10 May 2000 - bug in putSymbol fixed: was not pushing the symbol!
 *   8 May 2000 - putSymbol is now responsible for Symbol creation
 *   3 Apr 2000 - created
 */
public class SymbolTable {
	private static final int hashSize = 317; // prime capacity
	private Hashtable table;
	private SymbolTable next;

	public SymbolTable() {
		table = new Hashtable(hashSize);
		next = null;
	}
	
	public SymbolTable scope() {
		SymbolTable t;
		t = new SymbolTable();
		t.next = this;
		return t;
	}   

	public SymbolTable unscope() {
		return this.next;
	}   

	/*
	 * Returns new Symbol if name not already mapped,
	 * or the mapped-to symbol otherwise.
	 */
	public Symbol putSymbol(String name, 
	                        Symbol.Constants.SymbolKind kind,
	                        Node value) {
		Symbol s = (Symbol)table.get(name);
		if (s == null) {
			s = new Symbol(name, kind, value);
			table.put(name, s);
		}
		return s;
	}
	
	/*
	 * Seek the most closely nested symbol with name key.
	 * Returns null if not found.
	 */
	public Symbol getSymbol(String key) {
   	Symbol s = null;
   	s = (Symbol) (table.get(key));

   	if ((s == null) && (next != null))
			s = next.getSymbol(key);

		return s;
   }

	/*
	 * Is symbol defined in current scope?
	 */
	public boolean defSymbol(String key) {
		return ((table.get(key)) != null);
	}

	// for debugging
	public String toString() {
		StringBuffer display = new StringBuffer();
		Enumeration e = elements();
		while(e.hasMoreElements()) {
			display.append(((Symbol)(e.nextElement())).toString());
			display.append(System.getProperty("line.separator"));
		}
		return display.toString();
	}
	public Enumeration elements() {
		return table.elements();
	}
}	
