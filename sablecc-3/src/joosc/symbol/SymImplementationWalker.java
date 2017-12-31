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

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;

/*
 * SymImplementationWalker
 *
 * Defines fields, locals and formals and analyzes methods 
 * (and constructors) bodies, checking and linking variable uses 
 * to their defining symbols, and class uses to corresponding
 * class nodes. Also checks reference types of fields and locals 
 * (if applicable) and links them to the corresponding class.
 * For the benefit of the symbol pretty printer pass, it also
 * maps each scope to the AST node of its corresponding construct
 * (class, constructor, method, block statement).
 *
 * History:
 *   20 May 2000 - design alteration: hierarchy lookups use hierarchy object
 *   13 May 2000 - implementation reference type node check moved here
 *   12 May 2000 - feature-complete
 *   11 May 2000 - created
 */
public class SymImplementationWalker extends DepthFirstAdapter
                                     implements Symbol.Constants {
	private String filename;
	private SymbolTable curScope; // current scope
	private ClassHierarchy hierarchy;
	private Hashtable annotations;
	private Hashtable astToScopeMap;
	private Node curClass; // current containing class
	private boolean insideStaticMethod; // can't use fields in main
	
 	public static void walk(List theProgram, AnalysisDataManager proxy) {
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		String cname;
		
		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			cname = classfile.getName();
			ast.apply(new SymImplementationWalker(cname,proxy));
			MyError.noErrors();
		}
 	}
	
	public SymImplementationWalker(String fn,
	                               AnalysisDataManager proxy) {
		this.filename = fn;
		this.curScope = proxy.classlib; // starting at global scope
		this.hierarchy = proxy.hierarchy;
		this.annotations = proxy.symAnnotations;
		this.astToScopeMap = proxy.symAstToScopeMap;
		curClass = null;
		insideStaticMethod = false;
	}
	
	/********************************************************
	 *                 AST Node Visitors                    *
	 ********************************************************/

	/*
	 * Symbol definition verification and scope management
	 */
	 
	/* class and extern_class productions */
	
	public void inAClass(AClass node) {
		curScope = curScope.scope(); // new scope for class body
		curClass = node;
		astToScopeMap.put(node,curScope);
	}
	public void outAClass(AClass node) {
		curScope = curScope.unscope(); // leave class body scope
		curClass = null;
	}
	public void inAExternClass(AExternClass node) {
		curScope = curScope.scope(); // new scope for class body
		astToScopeMap.put(node,curScope);
	}
	public void outAExternClass(AExternClass node) {
		curScope = curScope.unscope(); // leave class body scope
	}

	/* onefield production */

	public void inAOnefield(AOnefield node) {
		TIdentifier id = node.getIdentifier();
		String name = id.getText();
		
		checkType(node.getType());
		if (curScope.defSymbol(name)) {
			MyError.error(filename,
			              "field name "+name+" already declared",
			              id.getLine());
		}
		else {
			curScope.putSymbol(name,fieldSymK,node);
		}
	}
	
	/* constructor and extern_constructor productions */

	public void inAConstructor(AConstructor node) {
		inAnyConstructor(node);
	}
	public void inAExternConstructor(AExternConstructor node) {
		inAnyConstructor(node);
	}
	public void inAnyConstructor(Node node) {
		curScope = curScope.scope(); // new scope for constructor body
		astToScopeMap.put(node,curScope); // for debugging
	}
	public void outAConstructor(AConstructor node) {
		outAnyConstructor(node);
	}
	public void outAExternConstructor(AExternConstructor node) {
		outAnyConstructor(node);
	}
	// worker method
	public void outAnyConstructor(Node node) {
		curScope = curScope.unscope(); // leave constructor body scope
	}

	/* method and extern_method productions */
	
	public void inAModMethod(AModMethod node) {
		inAnyMethod(node);
	}
	public void inANonmodMethod(ANonmodMethod node) {
		inAnyMethod(node);
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		inAnyMethod(node);
	}
	public void inAMainMethod(AMainMethod node) {
		inAnyMethod(node);
		insideStaticMethod = true;
	}
	public void inAModExternMethod(AModExternMethod node) {
		inAnyMethod(node);
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		inAnyMethod(node);
	}
	// worker method
	public void inAnyMethod(Node node) {
		curScope = curScope.scope(); // new scope for method body
		astToScopeMap.put(node,curScope); // for debugging
	}
	public void outAModMethod(AModMethod node) {
		outAnyMethod(node);
	}
	public void outANonmodMethod(ANonmodMethod node) {
		outAnyMethod(node);
	}
	public void outAAbstractMethod(AAbstractMethod node) {
		outAnyMethod(node);
	}
	public void outAMainMethod(AMainMethod node) {
		outAnyMethod(node);
		insideStaticMethod = false;
	}
	public void outAModExternMethod(AModExternMethod node) {
		outAnyMethod(node);
	}
	public void outANonmodExternMethod(ANonmodExternMethod node) {
		outAnyMethod(node);
	}
	// worker method
	public void outAnyMethod(Node node) {
		curScope = curScope.unscope(); // leave method body scope
	}
	
	/* block alternative of stm production */
	
	public void inABlockStm(ABlockStm node) {
		curScope = curScope.scope(); // new scope for block body
		astToScopeMap.put(node,curScope); // for debugging
	}
	public void outABlockStm(ABlockStm node) {
		curScope = curScope.unscope(); // leave block body scope
	}
	
	/* formal production */

	public void inAFormal(AFormal node) {
		TIdentifier id = node.getIdentifier();
		String name = id.getText();
		
		if (curScope.defSymbol(name)) {
			MyError.error(filename,
			              "formal "+name+" already declared",
			              id.getLine());
		}
		else {
			curScope.putSymbol(name,formalSymK,node);
		}
	}

	/* onelocal production */

	public void inAOnelocal(AOnelocal node) {
		TIdentifier id = node.getIdentifier();
		String name = id.getText();

		checkType(node.getType());
		if (curScope.defSymbol(name)) {
			MyError.error(filename,
			              "local "+name+" already declared",
			              id.getLine());
		}
		else {
			curScope.putSymbol(name,localSymK,node);
		}
	}
			
	/*
	 * Variable symbol use verification
	 */
	
	public void inAIdExp(AIdExp node) {
		Symbol s = checkVariableUse(node.getIdentifier());
		if (s != null)
			annotations.put(node,s);
	}
	public void inAAssignExp(AAssignExp node) {
		Symbol s = checkVariableUse(node.getIdentifier());
		if (s != null)
			annotations.put(node,s);
	}
	// returns corresponding symbol if found, null otherwise.
	public Symbol checkVariableUse(TIdentifier varId) {
		String name = varId.getText();
		int line = varId.getLine();
		Symbol s = curScope.getSymbol(name);
		
		if (s == null) {
			s = hierarchy.lookupHierarchy(name,curClass);
			if (s == null) {
				MyError.error(filename,
				              "identifier "+name+" not declared",
				              line);
			}
			else if (s.kind() != fieldSymK) {
				MyError.error(filename,
				              name+" is not a variable as expected",
				              line);
			}
		}
		else if ((s.kind() != fieldSymK) && 
		         (s.kind() != formalSymK) && 
		         (s.kind() != localSymK)) {
			MyError.error(filename,
			              name+" is not a variable as expected",
			              line);
		}
		else if ((s.kind() == fieldSymK) && insideStaticMethod) {
			MyError.error(filename,
			              "illegal static reference to "+name,
			              line);
		}
		
		return s;
	}
	
	/*
	 * Class symbol use verification
	 */
	
	public void inAInstanceofExp(AInstanceofExp node) {
		Node c = checkClassUse(node.getIdentifier());
		if (c != null)
			annotations.put(node,c);
	}
	public void inACastExp(ACastExp node) {
		Node c = checkClassUse(node.getIdentifier());
		if (c != null)
			annotations.put(node,c);
	}
	public void inANewExp(ANewExp node) {
		Node c = checkClassUse(node.getIdentifier());
		if (c != null)
			annotations.put(node,c);
	}
	// returns corresponding class node if found, null otherwise.
	public Node checkClassUse(TIdentifier classId) {
		String name = classId.getText();
		int line = classId.getLine();
		Symbol s = curScope.getSymbol(name);
		
		if (s == null) {
			MyError.error(filename,"identifier "+name+" not declared",line);
			return null;
		}
		else if (s.kind() != classSymK) {
			MyError.error(filename,"class with name "+name+" expected",line);
		}
		
		return s.value();
	}	
	
	/*
	 * implementation reference type node analysis:
	 * setting up links between reference types and type-defining classes
	 */
	 
	public void checkType(PType node) {
		if (! (node instanceof AReferenceType))
			return;
		
		TIdentifier id = ((AReferenceType)node).getIdentifier();
		Symbol s = curScope.getSymbol(id.getText());
		
		if (s == null) {
			MyError.error(filename,
			              "type identifier "+id.getText()+" not declared",
			              id.getLine());
		}
		else if (s.kind() != classSymK) {
			MyError.error(filename,
			              "type "+id.getText()+" must be a class",
			              id.getLine());
		}
		else { // annotate type node with a link to the defining class node
			annotations.put(node,s.value());
		}
	}	
}
