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

/*
 * Symbol
 * 
 * Represents a symbol as hosted by a symbol table.
 *
 * History:
 *   8 May 2000 - total redesign
 *   3 Apr 2000 - created
 */
public class Symbol /* LJH implements Symbol.Constants */ {
	private String name;
	private Node value;
	private Constants.SymbolKind kind;

	public Symbol(String n, Constants.SymbolKind k, Node v) {
		name = n;
		kind = k;
		value = v;
	}
	
	public String name() { return name; }
	public Node value() { return value; }
	public Constants.SymbolKind kind() { return kind; }
	
	// implement this interface for constant access w/o full qualification
	public interface Constants {
		// constants: implicitely public, static and final
		SymbolKind classSymK = new SymbolKind("class"); // value: AClass or AExternClass
		SymbolKind methodSymK  = new SymbolKind("method"); // value: PMethod
		SymbolKind fieldSymK = new SymbolKind("field"); // value AOnefield
		SymbolKind localSymK = new SymbolKind("local"); // value AOnelocal
		SymbolKind formalSymK = new SymbolKind("formal"); // value AFormal
		// inner class: final for type-safety
		final class SymbolKind {
			private String kId;
			private SymbolKind(String id) {
			   kId = id;
			}
			public String toString() { return kId; }
		}
	}
	
	// for debugging
	public String toString() { 
		return kind+": "+name;
	}
	
	// unit testing
        /* LJH
	public static void main(String args[]) {
		System.out.println(new Symbol("test",localSymK,null));
	}
        */ 
}
