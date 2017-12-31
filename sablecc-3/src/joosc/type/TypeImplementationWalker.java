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


package joosc.type;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import joosc.abstracter.*;
import joosc.symbol.*;

/**
 * TypeImplementationWalker, second pass of type-checking module.
 * Concurrently performs bottom-up type inference and type checking
 * on expression trees and statements containing expressions.
 * Also marks every exp node with a flag indicating whether
 * it should be coerced to a string (in code generation),
 * and binds selected constructors and methods to invoking sites.<p>
 *
 * History:
 * <ul>
 *   <li>19 Aug 2003 - change to sablecc 3 ast grammar, add documentation
 *   <li>23 May 2000 - fixed bug in outANotExp (correct type not int but bool)
 *                 fixed bug in assignCompatible (subClass args reversed)
 *                 feature-complete
 *   <li>14 May 2000 - created
 * </ul>
 */
public class TypeImplementationWalker extends AbstractTypeWalker {
	private Hashtable typeTree;
	private Hashtable coerceToStringMap;
	private Hashtable invokeBindingsMap;
	private PType returnType; // to be used when inside a method
	private Node curClass; // current containing class

	// "constant" type node singletons (initialized in walk method below)
	private static AIntType intType;
	private static ACharType charType;
	private static ABooleanType booleanType;
	private static AVoidType voidType;
	private static APolynullType polynullType;
	private static AReferenceType stringType;

	/* MANDATORY entry point */
 	public static void walk(List theProgram,
  	                        AnalysisDataManager proxy) {
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		String cname;
		Symbol s;

		/* set the singleton type nodes */
		intType = new AIntType(new TInt());
		charType = new ACharType(new TChar());
		booleanType = new ABooleanType(new TBoolean());
		voidType = new AVoidType();
		polynullType = new APolynullType();
		// special String type treatment
		s = proxy.classlib.getSymbol("String");
		if (s == null) {
			MyError.globalError("class String not found");
			MyError.noErrors(); // interrupt execution if such an error
		}
		stringType = new AReferenceType(new TIdentifier("String"));
		proxy.symAnnotations.put(stringType,s.value()); // link type to its class

		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			cname = classfile.getName();
			ast.apply(new TypeImplementationWalker(cname,proxy));
		}
 	}

	public TypeImplementationWalker(String filename,
	                                AnalysisDataManager proxy) {
		this.filename = filename;
		this.astToGenericMap = proxy.astToGenericMap;
		this.hierarchy = proxy.hierarchy;
		this.symAnnotations = proxy.symAnnotations;
		typeTree = proxy.typeAnnotations;
		this.invokeBindingsMap = proxy.invokeBindingsMap;
		this.coerceToStringMap = proxy.coerceToStringMap;
		returnType = null;
		curClass = null;
	}

	/********************************************************
	 *               Type Utility Methods                   *
	 ********************************************************/

	/*
	 * returns type of AST node specified (null if none)
	 */

	private PType typeOfNode(Node node) {
		return (PType)typeTree.get(node);
	}

	private boolean isIntegerType(PType type) {
		return (type instanceof AIntType) || (type instanceof ACharType);
	}

	/* assignment compatiblity method */

	private boolean assignCompatible(PType t1, PType t2) {
		if ((t1 instanceof AReferenceType) && (t2 instanceof APolynullType))
			return true;
		if ((t1 instanceof AIntType) && (t2 instanceof ACharType))
			return true;
		if (t1.getClass() != t2.getClass()) // types of different kind
			return false;
		if (t1 instanceof AReferenceType) {
			// subclass ref assignable to superclass var, not vice versa
			return hierarchy.subClass((Node)symAnnotations.get(t2),
			                          (Node)symAnnotations.get(t1));
		}
		// same type kind
		return true;
	}

	/* type predicate methods */

	private boolean checkBoolean(PType type, int line) {
		if (!(type instanceof ABooleanType)) {
			MyError.error(filename,"boolean type expected",line);
			return false;
		}
		return true;
	}
	private boolean checkChar(PType type, int line) {
		if (!(type instanceof ACharType)) {
			MyError.error(filename,"char type expected",line);
			return false;
		}
		return true;
	}
	private boolean checkInt(PType type, int line) {
		if (!(type instanceof AIntType)) {
			MyError.error(filename,"int type expected",line);
			return false;
		}
		return true;
	}

	/*
	 * type axiom method
	 * params: node should be a variable use, for which a link
	 * to its defining symbol has been previously created
	 * (symbol of one of field, formal, or local symbol kinds)
	 * note: Program terminated if incorrect parameter.
	 */

	private PType variableUseType(Node node) {
		Symbol s = (Symbol)symAnnotations.get(node);

		if (s != null) {
			if (s.kind() == fieldSymK)
				return ((AOnefield)s.value()).getType();
			else if (s.kind() == formalSymK)
				return ((AFormal)s.value()).getType();
			else if (s.kind() == localSymK)
				return ((AOnelocal)s.value()).getType();
		}

		// a bug if reaches this point
		MyError.fatalError("Type Checking",
		                   "varUseType: parameter incorrect");
		return null;
	}

	/*
	 * create a new reference type based on input class.
	 * theClass and gClass must correspond to the same class.
	 */

	private AReferenceType newClassType(Node theClass) {
		GenericClass gClass = (GenericClass)astToGenericMap.get(theClass);
		AReferenceType type =
		   new AReferenceType(
		          (TIdentifier)gClass.getIdentifier().clone());

		// map type to its defining class
		symAnnotations.put(type,theClass);
		return type;
	}

	/* constructor uniqueness check */

	private void checkUniqueConstructors(LinkedList constructors) {
		Object consArray[] = constructors.toArray();
		int length = consArray.length;
		GenericConstructor gConsi,gConsj;
		int j;

		// every two constructors are compared at most once
		for (int i = 0; i < length; i++)  {
			// check that ith constructor isn't in set of subsequent ones
			for (j = i+1; j < length; j++)  {
				gConsi = (GenericConstructor)
				             astToGenericMap.get(consArray[i]);
				gConsj = (GenericConstructor)
				             astToGenericMap.get(consArray[j]);
				if (equalFormals(gConsi.getFormals(),
				                 gConsj.getFormals()))  {
					MyError.error(filename,"duplicate constructor",
					              gConsi.getIdentifier().getLine());
					break;
				} // end if
			} // end for
		} // end for
	}

	/* possibly overloaded constructor selection algorithm */

	private Node selectConstructor(LinkedList constructors,
	                               LinkedList args,
	                               int line) {
		LinkedList applicable = applicableConstructors(constructors,args);
		LinkedList maxspecific;

		if (applicable.isEmpty())  {
			MyError.error(filename,"no matching constructors",line);
			return null;
		}
		else {
			maxspecific = maxSpecificConstructors(applicable);
			if (maxspecific.size() != 1)  { // none or many maxspecific
				MyError.error(filename,"ambiguous constructor",line);
				return null;
			}
			else // exactly one constructor
				return (Node)maxspecific.getFirst();
		}
	}

	// collect set of applicable constructors

	private LinkedList applicableConstructors(LinkedList constructors,
	                                          LinkedList args) {
		LinkedList applicable = new LinkedList();
		Iterator iter = constructors.iterator();
		Node cons;
		GenericConstructor gCons;

		while (iter.hasNext())  {
			cons = (Node)iter.next();
			gCons = (GenericConstructor)astToGenericMap.get(cons);
			if (isApplicableSignature(gCons.getFormals(),args))
				applicable.add(cons);
		}
		return applicable;
	}

	private boolean isApplicableSignature(LinkedList formals,
	                                      LinkedList args) {
		Iterator iterF = formals.iterator();
		Iterator iterA = args.iterator();

		while (iterF.hasNext() && iterA.hasNext())  {
			if (!assignCompatible(((AFormal)iterF.next()).getType(),
			                      typeOfNode((PExp)iterA.next())))
				return false;
		}
		// # formals different from # args => false
		if (iterF.hasNext() || iterA.hasNext())
			return false;

		return true;
	}

	// collect set of most specific constructors (subset of applicable)

	private LinkedList maxSpecificConstructors(LinkedList applicable) {
		Iterator iter = applicable.iterator();
		Node cons;
		GenericConstructor gCons;
		LinkedList maxspecific = new LinkedList();
		LinkedList formals;

		while (iter.hasNext())  {
			cons = (Node)iter.next();
			gCons = (GenericConstructor)astToGenericMap.get(cons);
			formals = gCons.getFormals();
			if (isMaxSpecificSignature(formals,applicable))
				maxspecific.add(cons);
		}
		return maxspecific;
	}

	private boolean isMaxSpecificSignature(LinkedList formals,
	                                       LinkedList applicable) {
		Iterator iter = applicable.iterator();
		Node cons;
		GenericConstructor gCons;

		while (iter.hasNext())  {
			cons = (Node)iter.next();
			gCons = (GenericConstructor)astToGenericMap.get(cons);
			if (! isMoreSpecificSignature(formals,gCons.getFormals()))
				return false;
		}
		return true;
	}

	private boolean isMoreSpecificSignature(LinkedList f1,
	                                        LinkedList f2) {
    if (f1.size() != f2.size()) return false;
		Iterator iter1 = f1.iterator();
		Iterator iter2 = f2.iterator();
		while (iter1.hasNext() && iter2.hasNext())  {
			if (!assignCompatible(((AFormal)iter1.next()).getType(),
			                      ((AFormal)iter2.next()).getType()))
				return false;
		}

		return true;
	}

	/*
	 * Checks that input argument list and formal list match in size
	 * and that the arguments are assignment-compatible to the formals.
	 */

	private void checkArgumentsAgainstFormals(LinkedList f,
	                                          LinkedList a,
	                                          int line) {
		Iterator iterF;
		Iterator iterA;
		int index = 0;

		if (f == null && a != null) {
			MyError.error(filename,"too many arguments",line);
			return;
		}
		if (f != null && a == null) {
			MyError.error(filename,"too few arguments",line);
			return;
		}
		if (f == null && a == null) // ok
			return;

		iterF = f.iterator();
		iterA = a.iterator();
		// compare each formal/argument pair in order
		while(iterF.hasNext() && iterA.hasNext()) {
			index++;
			if (! assignCompatible(((AFormal)iterF.next()).getType(),
			                       typeOfNode((PExp)iterA.next()))) {
				MyError.error(filename,
				              "argument "+index+" has wrong type",line);
			}
		}
		if (!iterF.hasNext() && iterA.hasNext()) {
			MyError.error(filename,"too many arguments",line);
		}
		if (iterF.hasNext() && !iterA.hasNext()) {
			MyError.error(filename,"too few arguments",line);
		}
	}

	/********************************************************
	 *                 AST Node Visitors                    *
	 ********************************************************/

	/* class and extern class productions */

	public void inAClass(AClass node) {
		curClass = node;
		checkUniqueConstructors(node.getConstructors());
	}
	public void outAClass(AClass node) {
		curClass = null;
	}
	public void inAExternClass(AExternClass node) {
		curClass = node;
		checkUniqueConstructors(node.getConstructors());
	}
	public void outAExternClass(AExternClass node) {
		curClass = null;
	}

	/* constructor and extern constructor productions */

	public void inAConstructor(AConstructor node) {
		returnType = voidType;
	}
	public void inAExternConstructor(AExternConstructor node) {
		returnType = voidType;
	}

	/* method and extern method productions */

	public void inAModMethod(AModMethod node) {
		setReturntype(node.getReturntype());
	}
	public void inANonmodMethod(ANonmodMethod node) {
		setReturntype(node.getReturntype());
	}
	public void inAAbstractMethod(AAbstractMethod node) {
		setReturntype(node.getReturntype());
	}
	public void inAMainMethod(AMainMethod node) {
		returnType = voidType;
	}
	public void inAModExternMethod(AModExternMethod node) {
		setReturntype(node.getReturntype());
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		setReturntype(node.getReturntype());
	}
	public void setReturntype(PReturntype rt) {
		if (rt instanceof AVoidReturntype)
			returnType = voidType;
		else
			returnType = ((ANonvoidReturntype)rt).getType();
	}

	/* stm production */

	public void outAReturnStm(AReturnStm node) {
		PExp returnValue = node.getExp();
		PType returnValueType;

		if ((returnType instanceof AVoidType) && (returnValue != null)) {
			MyError.error(filename,"return value not allowed", Lines.getLine(node), Lines.getPos(node));
		}
		if (!(returnType instanceof AVoidType) && (returnValue == null)) {
			MyError.error(filename,"return value expected", Lines.getLine(node), Lines.getPos(node));
		}
		if (!(returnType instanceof AVoidType) && (returnValue != null)) {
			returnValueType = typeOfNode(returnValue);
			if (!assignCompatible(returnType,returnValueType)) {
				MyError.error(filename,"illegal type of expression", Lines.getLine(node), Lines.getPos(node));
			}
		}
	}

	public void outASuperconsStm(ASuperconsStm node) {
		GenericClass parentGC = (GenericClass)astToGenericMap.get(
		                           hierarchy.get(curClass).getParent());
		Node cons = selectConstructor(
		               parentGC.getConstructors(),
		               node.getArgs(),
                   Lines.getLine(node));

		if (cons != null)
			invokeBindingsMap.put(node,cons);
	}

	public void outAIfStm(AIfStm node) {
		checkBoolean(typeOfNode(node.getExp()), Lines.getLine(node));
	}

	public void outAIfelseStm(AIfelseStm node) {
		checkBoolean(typeOfNode(node.getExp()), Lines.getLine(node));
	}

	public void outAWhileStm(AWhileStm node) {
		checkBoolean(typeOfNode(node.getExp()), Lines.getLine(node));
	}

	/* stm_exp production */
	public void outAAssignExp (AAssignExp node) {
    coerceToStringMap.put(node, Boolean.FALSE);

		PType leftType = variableUseType(node);
		PType rightType = typeOfNode(node.getExp());

		// assignment exp type is that of its (left) identifier
		typeTree.put(node,leftType);
		// check assignment compatibility
		if (!assignCompatible(leftType,rightType)) {
			MyError.error(filename,"illegal assignment",
			              Lines.getLine(node));
		}
	}

	/* logical operators */
	public void outAOrExp(AOrExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		checkBoolean(typeOfNode(node.getLeft()), Lines.getLine(node));
		checkBoolean(typeOfNode(node.getRight()), Lines.getLine(node));
		typeTree.put(node,booleanType);
	}
	public void outAAndExp(AAndExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		checkBoolean(typeOfNode(node.getLeft()), Lines.getLine(node));
		checkBoolean(typeOfNode(node.getRight()), Lines.getLine(node));
		typeTree.put(node,booleanType);
	}

	/* relational equality and difference operators */
	public void outAEqExp(AEqExp node) {
		PType leftType = typeOfNode(node.getLeft());
		PType rightType = typeOfNode(node.getRight());

		coerceToStringMap.put(node,Boolean.FALSE);
		if (!assignCompatible(leftType,rightType) &&
		    !assignCompatible(rightType,leftType)) {
			MyError.error(filename,"arguments for = have wrong types",
			              Lines.getLine(node));
		}
		typeTree.put(node,booleanType);
	}
	public void outANeqExp(ANeqExp node) {
		PType leftType = typeOfNode(node.getLeft());
		PType rightType = typeOfNode(node.getRight());

		coerceToStringMap.put(node,Boolean.FALSE);
		if (!assignCompatible(leftType,rightType) &&
		    !assignCompatible(rightType,leftType)) {
			MyError.error(filename,"arguments for != have wrong types",
			              Lines.getLine(node));
		}
		typeTree.put(node,booleanType);
	}

	/* relational inequality operators */
	public void outALtExp(ALtExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,booleanType);
	}
	public void outAGtExp(AGtExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,booleanType);
	}
	public void outALeqExp(ALeqExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,booleanType);
	}
	public void outAGeqExp(AGeqExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,booleanType);
	}

	/* instanceof operator */
	public void outAInstanceofExp(AInstanceofExp node) {
		PType leftType = typeOfNode(node.getExp());
		Node leftClass, rightClass;
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		if (!(leftType instanceof AReferenceType)) {
			MyError.error(filename,"class reference expected",line);
		}
		else {
			// class associated to reference type node
			leftClass = (Node)symAnnotations.get(leftType);
			// class associated to instanceof exp node
			rightClass = (Node)symAnnotations.get(node);
			if (! hierarchy.subClass(leftClass,rightClass) &&
			    ! hierarchy.subClass(rightClass,leftClass)) {
				MyError.error(filename,"instanceof will always fail",line);
			}
		}
		typeTree.put(node,booleanType);
	}

	/* overloaded plus operator */
	public void outAPlusExp(APlusExp node) {
		PType leftType = typeOfNode(node.getLeft());
		PType rightType = typeOfNode(node.getRight());
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		// + as addition operator
		if (isIntegerType(leftType) && isIntegerType(rightType)) {
			typeTree.put(node,intType);
			return;
		}
		// illegal attempt to use plus on non-integer non-string operands
		if (!equalTypes(leftType,stringType) &&
		    !equalTypes(rightType,stringType)) {
			MyError.error(filename,"arguments for + have wrong types",line);
		}
		// + as string concatenation operator
		// request tostring coercion for both operands
		coerceToStringMap.put(node.getLeft(),Boolean.TRUE);
		coerceToStringMap.put(node.getRight(),Boolean.TRUE);
		typeTree.put(node,stringType);
	}

	/* binary arithmetic operators */
	public void outAMinusExp(AMinusExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,intType);
	}
	public void outAMultExp(AMultExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,intType);
	}
	public void outADivExp(ADivExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,intType);
	}
	public void outAModExp(AModExp node) {
		int line = Lines.getLine(node);

		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getLeft()),line);
		checkInt(typeOfNode(node.getRight()),line);
		typeTree.put(node,intType);
	}

	/* unary minus arithmetic operator */
	public void outAUminusExp(AUminusExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getExp()),Lines.getLine(node));
		typeTree.put(node,intType);
	}

	/* logical not operator */
	public void outANotExp(ANotExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		checkBoolean(typeOfNode(node.getExp()),Lines.getLine(node));
		typeTree.put(node,booleanType);
	}

	public void outACastExp(ACastExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);

    int line = Lines.getLine(node);
		Node leftC = (Node)symAnnotations.get(node); // caster class
		PType rightT = typeOfNode(node.getExp()); // castee type
		Node rightC = (Node)symAnnotations.get(rightT); // castee class

		// type inference
		typeTree.put(node,newClassType((Node)symAnnotations.get(node)));
		// type checking
		if (!(rightT instanceof AReferenceType) &&
		    !(rightT instanceof APolynullType)) {
			MyError.error(filename,"class reference expected",line);
		}
		else {
			// ref type but neither class is subclass of the other: fails
			if ((rightT instanceof AReferenceType) &&
			    !hierarchy.subClass(leftC,rightC) &&
			    !hierarchy.subClass(rightC,leftC)) {
				MyError.error(filename,"cast will always fail",line);
			}
		}
	}

	public void outACasttocharExp(ACasttocharExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		checkInt(typeOfNode(node.getExp()),Lines.getLine(node));
		typeTree.put(node,charType);
	}

	public void outAIdExp(AIdExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,variableUseType(node));
	}

	public void outAThisExp(AThisExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,newClassType(curClass));
	}

	/* constant expressions */
	public void outAIntconstExp(AIntconstExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,intType);
	}
	public void outATrueExp(ATrueExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,booleanType);
	}
	public void outAFalseExp(AFalseExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,booleanType);
	}
	public void outACharconstExp(ACharconstExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,charType);
	}
	public void outAStringconstExp(AStringconstExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,stringType);
	}
	public void outANullExp(ANullExp node) {
		coerceToStringMap.put(node,Boolean.FALSE);
		typeTree.put(node,polynullType);
	}

	public void outANewExp(ANewExp node) {
    coerceToStringMap.put(node, Boolean.FALSE);
		Node theClass = (Node)symAnnotations.get(node); // mapped class
		GenericClass gClass = (GenericClass)astToGenericMap.get(theClass);
		int line = Lines.getLine(node);
		Node cons;

		if (gClass.getModifier() instanceof AAbstractClassmods) {
			MyError.error(filename,"illegal abstract constructor "+
			              gClass.getName(),line);
		}
		// select and bind constructor to instance creation (for codegen)
		cons = selectConstructor(
		          gClass.getConstructors(),
		          node.getArgs(),
		          line);
		if (cons != null)
			invokeBindingsMap.put(node,cons);
		typeTree.put(node,newClassType(theClass));
	}

	/* methodinvocation production */

	public void outACallExp(ACallExp node) {
    coerceToStringMap.put(node, Boolean.FALSE);

		int line = Lines.getLine(node);
		String name = node.getIdentifier().getText();
		GenericMethod gMethod;
		PType receiverT = typeOfNode(node.getReceiver());
		Node receiverC; // class of receiver
		Symbol s;
		PReturntype rt;

		if (!(receiverT instanceof AReferenceType)) {
			MyError.error(filename,"receiver must be an object",line);
			typeTree.put(node,polynullType);
		}
		else {
			receiverC = (Node)symAnnotations.get(receiverT);
			s = hierarchy.lookupHierarchy(name,receiverC);
			// method invoked not found
			if ((s == null) || (s.kind() != methodSymK)) {
				MyError.error(filename,"no such method called "+name,line);
				typeTree.put(node,polynullType);
			}
			else {
				// bind method to invocation (for codegen)
				invokeBindingsMap.put(node,s.value());
				gMethod = (GenericMethod)astToGenericMap.get(s.value());
				if (gMethod.getModifier() == GenericMethod.STATIC) {
					MyError.error(filename,"static method "+name+
					              " may not be invoked",line);
				}
				checkArgumentsAgainstFormals(
				   gMethod.getFormals(),
				   node.getArgs(),
				   line);
				rt = gMethod.getReturntype();
				if (rt instanceof AVoidReturntype)
					typeTree.put(node, voidType);
				else
					typeTree.put(node,((ANonvoidReturntype)rt).getType());
			}
		}
	}

	/* receiver production */

	public void outAObjectReceiver(AObjectReceiver node) {
		typeTree.put(node,typeTree.get(node.getExp()));
	}
	public void outASuperReceiver(ASuperReceiver node) {
		int line = Lines.getLine(node);
		Node parentC = hierarchy.get(curClass).getParent();

		if (parentC == null) {
			MyError.error(filename,"super not allowed here",line);
			typeTree.put(node,polynullType);
		}
		else {
			typeTree.put(node,newClassType(parentC));
		}
	}
}

