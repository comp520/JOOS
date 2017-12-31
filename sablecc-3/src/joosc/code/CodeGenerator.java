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


package joosc.code;

import java.util.*;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import joosc.abstracter.*;
import joosc.symbol.*;
import joosc.type.*;
import joosc.resource.*;
import joosc.code.bytecode.*;

/**
 * CodeGenerator - generates bytecode IR for the input program.<p>
 *
 * History:
 * <ul>
 *   <li>19 Aug 2003 - change to SableCC 3 AST grammar, add documentation
 *   <li> 4 Jun 2000 - bug in caseACastcharExp fixed
 *   <li> 2 Jun 2000 - thorough code review (5 bugs fixed); AExpStm bug fixed
 *   <li>31 May 2000 - feature-complete
 *   <li>29 May 2000 - created
 */

public class CodeGenerator extends DepthFirstAdapter 
                           implements Symbol.Constants {
	private String filename;
	private Hashtable astToGenericMap;
	private Hashtable symAnnotations;
	private Hashtable typeAnnotations;
	private ClassHierarchy hierarchy;
	private Hashtable invokeBindingsMap;
	private Hashtable coerceToStringMap;
	private Hashtable toStringResourcesMap, resourcesMap;
	private Hashtable signaturesMap;
	private Hashtable astToCodeMap;
	private CodeChain currentCode; // current code chain
	private Node currentClass;
	
 	public static void walk(List theProgram,
	                        AnalysisDataManager proxy) {
		Iterator iter = theProgram.iterator();
		ClassFile classfile;
		Node ast;
		String cname;
		
		while (iter.hasNext()) {
			classfile = (ClassFile)iter.next();
			ast = classfile.getAst();
			cname = classfile.getName();
			ast.apply(new CodeGenerator(cname,proxy));
		}
 	}
 	
	public CodeGenerator(String filename, 
	                     AnalysisDataManager proxy) {
		this.filename = filename;
		this.astToGenericMap = proxy.astToGenericMap;
		this.symAnnotations = proxy.symAnnotations;
		this.typeAnnotations = proxy.typeAnnotations;
		this.hierarchy = proxy.hierarchy;
		this.invokeBindingsMap = proxy.invokeBindingsMap;
		this.coerceToStringMap = proxy.coerceToStringMap;
		this.resourcesMap = proxy.resourcesMap;
		this.toStringResourcesMap = proxy.toStringResourcesMap;
		this.signaturesMap = proxy.signaturesMap;
		this.astToCodeMap = proxy.astToCodeMap;
	}
	
	/* convenience method: chains an instruction into the code stream */
	private void chainInstr(Instr instr) {
		currentCode.add(instr);
	}
	
	/*****************************************************************
	 *             CODE SNIPPET CONSTRUCTION UTILITIES               *
	 *****************************************************************/
	
	private String makePackageCode(TStringconst packageName) {
		String text = packageName.getText();
		
		// get rid of quotes
		text = text.substring(1,text.length()-1);
		return text.replace('.','/') + "/";
	}
	
	private String makeClassNameCode(Node theClass) {
		GenericClass gClass = (GenericClass)astToGenericMap.get(theClass);
		String packageCode = "";
		
		if (theClass instanceof PExternClass) {
			packageCode = 
			   makePackageCode(((AExternClass)theClass).getStringconst());
		}
		return packageCode + gClass.getName();
	}
	
	private String makeReturntypeCode(PReturntype type) {
		if (type instanceof AVoidReturntype)
			return "V";
		else
			return makeTypeCode(((ANonvoidReturntype)type).getType());
	}
		
	private String makeTypeCode(PType type) {
		if (type instanceof AIntType)
			return "I";
		else if (type instanceof ABooleanType)
			return "Z";
		else if (type instanceof ACharType)
			return "C";
		else if (type instanceof AReferenceType) {
			return "L" + 
			       makeClassNameCode((Node)symAnnotations.get(type)) +
			       ";";
		}
		return null;
	}
	
	private String makeFormalsCode(LinkedList formals) {
		Iterator iter = formals.iterator();
		StringBuffer buf = new StringBuffer();
		
		while (iter.hasNext())
			buf.append(makeTypeCode(((AFormal)iter.next()).getType()));
		return buf.toString();
	}
	
	private String makeSignature(LinkedList f, PReturntype rt) {
		return "(" + makeFormalsCode(f) + ")" + makeReturntypeCode(rt);
	}
	
	private String makeConstructorSignature(LinkedList formals) {
		return "(" + makeFormalsCode(formals) + ")V";
	}
	
	private String makeConstructorCode(Node theClass, Node cons) {
		GenericConstructor gCons = 
		   (GenericConstructor)astToGenericMap.get(cons);

		return makeClassNameCode(theClass) + "/<init>" +
		       makeConstructorSignature(gCons.getFormals());
	}
	
	private String makeMethodCode(Node theClass, Node method) {
		GenericMethod gMethod = 
		   (GenericMethod)astToGenericMap.get(method);
	
		return makeClassNameCode(theClass) + 
		       "/" + gMethod.getName() +
		       makeSignature(gMethod.getFormals(),gMethod.getReturntype());
	} 
	
	/*****************************************************************
	 *                         AST VISITORS                          *
	 *****************************************************************/
	
	/* class and extern class productions */
	
	public void inAClass(AClass node) {
		inAnyClass(node);
	}
	public void inAExternClass(AExternClass node) {
		inAnyClass(node);
	}
	// worker method
	private void inAnyClass(Node node) {
		currentClass = node;
	}
	
	public void outAClass(AClass node) {
		outAnyClass(node);
	}
	public void outAExternClass(AExternClass node) {
		outAnyClass(node);
	}
	// worker method
	private void outAnyClass(Node node) {
		signaturesMap.put(node,makeClassNameCode(node));
		currentClass = null;
	}
	
	/* constructor and extern constructor productions */
	
	public void inAConstructor(AConstructor node) {
		inAnyConstructor(node);
	}
	public void inAExternConstructor(AExternConstructor node) {
		inAnyConstructor(node);
	}
	// worker method
	private void inAnyConstructor(Node node) {
		currentCode = new CodeChain();
	}
	
	public void outAConstructor(AConstructor node) {
		outAnyConstructor(node.getFormals(),node);
	}
	public void outAExternConstructor(AExternConstructor node) {
		outAnyConstructor(node.getFormals(),node);
	}
	// worker method
	private void outAnyConstructor(LinkedList formals, Node node) {
		chainInstr(new VoidReturnInstr());
		astToCodeMap.put(node,currentCode);
		signaturesMap.put(node,
		                  makeConstructorSignature(formals));
	}
	
	/* method and extern method productions */
	
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
	}
	public void inAModExternMethod(AModExternMethod node) {
		inAnyMethod(node);
	}
	public void inANonmodExternMethod(ANonmodExternMethod node) {
		inAnyMethod(node);
	}
	// worker method
	private void inAnyMethod(Node node) {
		currentCode = new CodeChain();
	}
	
	public void outAModMethod(AModMethod node) {
		outAnyMethod(node.getReturntype(),node.getFormals(),node);
	}
	public void outANonmodMethod(ANonmodMethod node) {
		outAnyMethod(node.getReturntype(),node.getFormals(),node);
	}
	public void outAAbstractMethod(AAbstractMethod node) {
		outAnyMethod(node.getReturntype(),node.getFormals(),node);
	}
	public void outAMainMethod(AMainMethod node) {
		outAnyMethod(new AVoidReturntype(), new LinkedList(), node);
	}
	public void outAModExternMethod(AModExternMethod node) {
		outAnyMethod(node.getReturntype(),node.getFormals(),node);
	}
	public void outANonmodExternMethod(ANonmodExternMethod node) {
		outAnyMethod(node.getReturntype(),node.getFormals(),node);
	}
	// worker method
	private void outAnyMethod(PReturntype rt, LinkedList f, Node node) {
		if (rt instanceof AVoidReturntype)
			chainInstr(new VoidReturnInstr());
		else
			chainInstr(new NopInstr());
		astToCodeMap.put(node,currentCode);
		signaturesMap.put(node,makeSignature(f,rt));
	}
	
  /* stm production */
  public void caseAExpStm(AExpStm node) {
    node.getExp().apply(this);
    if (! (typeAnnotations.get(node.getExp()) instanceof AVoidType))
      chainInstr(new PopInstr());
  }
	
  public void caseAReturnStm(AReturnStm node) {
		PType type;
		
		if (node.getExp() == null)
			chainInstr(new VoidReturnInstr());
		else {
			node.getExp().apply(this);
			type = (PType)typeAnnotations.get(node.getExp());
			if (type instanceof AReferenceType ||
			    type instanceof APolynullType) {
				chainInstr(new AreturnInstr());
			}
			else {
				chainInstr(new IreturnInstr());
			}
		}
	}
	
	public void caseASuperconsStm(ASuperconsStm node) {
		Node parentClass = hierarchy.get(currentClass).getParent();
		Node superCons = (Node)invokeBindingsMap.get(node);
		
		chainInstr(new AloadInstr(0));
    Iterator argsIt = node.getArgs().iterator();
    while (argsIt.hasNext()) 
      ((PExp)argsIt.next()).apply(this);
		chainInstr(
		   new InvokenonvirtualInstr(
		      makeConstructorCode(parentClass,superCons)));
	}

	public void caseAIfStm(AIfStm node) {
		int stopLabel;
		BranchInstr stopBranch;
		LabelInstr stopTarget;
		IfStmLabels labels = (IfStmLabels)resourcesMap.get(node);
		
		// ifstm -> EXP; ifeq stopL; STM; stopL:
		node.getExp().apply(this);
		stopLabel = labels.getStopLabel();
		chainInstr(stopBranch = new IfeqInstr());
		node.getStm().apply(this);
		chainInstr(stopTarget = new LabelInstr("stop",stopLabel));
		stopBranch.setTarget(stopTarget);
	}

	public void caseAIfelseStm(AIfelseStm node) {
		int elseLabel,stopLabel; 
		BranchInstr elseBranch,stopBranch; 
		LabelInstr elseTarget,stopTarget;
		IfelseStmLabels labels = (IfelseStmLabels)resourcesMap.get(node);
		
		// ifelsestm -> EXP; ifeq elseL; THENSTM; goto stopL; 
		//              elseL: ELSESTM; stop:
		node.getExp().apply(this);
		elseLabel = labels.getElseLabel();
		chainInstr(elseBranch = new IfeqInstr());
		node.getThenStm().apply(this);
		stopLabel = labels.getStopLabel();
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(elseTarget = new LabelInstr("else",elseLabel));
		elseBranch.setTarget(elseTarget);
		node.getElseStm().apply(this);
		chainInstr(stopTarget = new LabelInstr("stop",stopLabel));
		stopBranch.setTarget(stopTarget);
	}
	
	public void caseAWhileStm(AWhileStm node) {
		int startLabel,stopLabel; 
		BranchInstr startBranch,stopBranch; 
		LabelInstr startTarget,stopTarget;
		WhileStmLabels labels = (WhileStmLabels)resourcesMap.get(node);
		
		// whilestm -> startL: EXP; ifeq stopL; STM; goto startL; stopL:
		startLabel = labels.getStartLabel();
		chainInstr(startTarget = new LabelInstr("start",startLabel));
		node.getExp().apply(this);
		stopLabel = labels.getStopLabel();
		chainInstr(stopBranch = new IfeqInstr());
		node.getStm().apply(this);
		chainInstr(startBranch = new GotoInstr());
		startBranch.setTarget(startTarget);
		chainInstr(stopTarget = new LabelInstr("stop",stopLabel));
		stopBranch.setTarget(stopTarget);
	}

  /** 
   * assignment production 
   */
  public void caseAAssignExp(AAssignExp node) {
    // get Symbol associated with identifier on the left
    Symbol s = (Symbol)symAnnotations.get(node);
    LocalResources localRes;
    String assignee;
		
    node.getExp().apply(this);
    chainInstr(new DupInstr());
    if (s.kind() == localSymK)  {
      localRes = (LocalResources)resourcesMap.get(s.value());
      if (((AOnelocal)s.value()).getType() instanceof AReferenceType)
        chainInstr(new AstoreInstr(localRes.getOffset()));
      else 
        chainInstr(new IstoreInstr(localRes.getOffset()));
    }
    else if (s.kind() == formalSymK) {
      localRes = (LocalResources)resourcesMap.get(s.value());
      if (((AFormal)s.value()).getType() instanceof AReferenceType)
        chainInstr(new AstoreInstr(localRes.getOffset()));
      else 
        chainInstr(new IstoreInstr(localRes.getOffset()));
    }
    else if (s.kind() == fieldSymK) {
      chainInstr(new AloadInstr(0));
      chainInstr(new SwapInstr());
      assignee = node.getIdentifier().getText();
      chainInstr(new PutfieldInstr(
                    makeClassNameCode(
                      hierarchy.lookupHierarchyClass(assignee,currentClass)) +
                    "/" +
                    assignee +
                    " " +
                    makeTypeCode(((AOnefield)s.value()).getType())));
    }
  }
	
  /* exp production */
  private void inAnyExp(PExp node) {
    PType type;
		
    // wrapping primitive types into objects for toString coercion
    if (((Boolean)coerceToStringMap.get(node)).booleanValue()) {
      type = (PType)typeAnnotations.get(node);
      if (type instanceof AIntType) {
        chainInstr(new NewInstr("java/lang/Integer"));
        chainInstr(new DupInstr());
      }
      else if (type instanceof ABooleanType) {
        chainInstr(new NewInstr("java/lang/Boolean"));
        chainInstr(new DupInstr());
      }
      else if (type instanceof ACharType) {
        chainInstr(new NewInstr("java/lang/Character"));
        chainInstr(new DupInstr());
      }
    }
  }
	
	// boolean exp
	public void caseAOrExp(AOrExp node) {
		OrExpLabels labels;
		BranchInstr trueBranch;
		LabelInstr trueTarget;
		
		inAnyExp(node);
		// or exp -> LEFTEXP; dup; ifne trueL; pop; RIGHTEXP; trueL:
		labels = (OrExpLabels)resourcesMap.get(node);
		node.getLeft().apply(this);
		chainInstr(new DupInstr());
		chainInstr(trueBranch = new IfneInstr());
		chainInstr(new PopInstr());
		node.getRight().apply(this);
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		outAnyExp(node);
	}
	public void caseAAndExp(AAndExp node) {
		AndExpLabels labels;
		BranchInstr falseBranch;
		LabelInstr falseTarget;
		
		inAnyExp(node);
		// and exp -> LEFTEXP; dup; ifeq falseL; pop; RIGHTEXP; falseL:
		labels = (AndExpLabels)resourcesMap.get(node);
		node.getLeft().apply(this);
		chainInstr(new DupInstr());
		chainInstr(falseBranch = new IfeqInstr());
		chainInstr(new PopInstr());
		node.getRight().apply(this);
		chainInstr(falseTarget = new LabelInstr("false",labels.getFalseLabel()));
		falseBranch.setTarget(falseTarget);
		outAnyExp(node);
	}
	
	public void caseAEqExp(AEqExp node) {
		PType leftType = (PType)typeAnnotations.get(node.getLeft());
		BranchInstr stopBranch, trueBranch;
		LabelInstr stopTarget, trueTarget;
		RelExpLabels labels = (RelExpLabels)resourcesMap.get(node);
		
		inAnyExp(node);
		// eq exp -> LEFTEXP; RIGHTEXP; if_(i|a)cmpeq trueL; ldc 0; goto stopL;
		//           trueL: ldc 1; stopL:
		node.getLeft().apply(this);
		node.getRight().apply(this);
		if (leftType instanceof AReferenceType ||
		    leftType instanceof APolynullType)  {
			chainInstr(trueBranch = new IfAcmpeqInstr());
		}
		else {
			chainInstr(trueBranch = new IfIcmpeqInstr());
		}
		chainInstr(new LdcIntInstr(0));
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		chainInstr(new LdcIntInstr(1));
		chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
		stopBranch.setTarget(stopTarget);
		outAnyExp(node);
	}
	public void caseANeqExp(ANeqExp node) {
		PType leftType = (PType)typeAnnotations.get(node.getLeft());
		BranchInstr stopBranch, trueBranch;
		LabelInstr stopTarget, trueTarget;
		RelExpLabels labels = (RelExpLabels)resourcesMap.get(node);
		
		inAnyExp(node);
		// neq exp -> LEFTEXP; RIGHTEXP; if_(i|a)cmpne trueL; ldc 0; goto stopL;
		//            trueL: ldc 1; stopL:
		node.getLeft().apply(this);
		node.getRight().apply(this);
		if (leftType instanceof AReferenceType ||
		    leftType instanceof APolynullType)  {
			chainInstr(trueBranch = new IfAcmpneInstr());
		}
		else {
			chainInstr(trueBranch = new IfIcmpneInstr());
		}
		chainInstr(new LdcIntInstr(0));
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		chainInstr(new LdcIntInstr(1));
		chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
		stopBranch.setTarget(stopTarget);
		outAnyExp(node);
	}
	public void caseALtExp(ALtExp node) {
		BranchInstr stopBranch, trueBranch;
		LabelInstr stopTarget, trueTarget;
		RelExpLabels labels = (RelExpLabels)resourcesMap.get(node);
		
		inAnyExp(node);
		// lt exp -> LEFTEXP; RIGHTEXP; if_icmplt trueL; ldc 0; goto stopL;
		//           trueL: ldc 1; stopL:
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(trueBranch = new IfIcmpltInstr());
		chainInstr(new LdcIntInstr(0));
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		chainInstr(new LdcIntInstr(1));
		chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
		stopBranch.setTarget(stopTarget);
		outAnyExp(node);
	}
	public void caseAGtExp(AGtExp node) {
		BranchInstr stopBranch, trueBranch;
		LabelInstr stopTarget, trueTarget;
		RelExpLabels labels = (RelExpLabels)resourcesMap.get(node);
		
		inAnyExp(node);
		// gt exp -> LEFTEXP; RIGHTEXP; if_icmpgt trueL; ldc 0; goto stopL;
		//           trueL: ldc 1; stopL:
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(trueBranch = new IfIcmpgtInstr());
		chainInstr(new LdcIntInstr(0));
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		chainInstr(new LdcIntInstr(1));
		chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
		stopBranch.setTarget(stopTarget);
		outAnyExp(node);
	}
	public void caseALeqExp(ALeqExp node) {
		BranchInstr stopBranch, trueBranch;
		LabelInstr stopTarget, trueTarget;
		RelExpLabels labels = (RelExpLabels)resourcesMap.get(node);
		
		inAnyExp(node);
		// leq exp -> LEFTEXP; RIGHTEXP; if_icmple trueL; ldc 0; goto stopL;
		//            trueL: ldc 1; stopL:
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(trueBranch = new IfIcmpleInstr());
		chainInstr(new LdcIntInstr(0));
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		chainInstr(new LdcIntInstr(1));
		chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
		stopBranch.setTarget(stopTarget);
		outAnyExp(node);
	}
	public void caseAGeqExp(AGeqExp node) {
		BranchInstr stopBranch, trueBranch;
		LabelInstr stopTarget, trueTarget;
		RelExpLabels labels = (RelExpLabels)resourcesMap.get(node);
		
		inAnyExp(node);
		// geq exp -> LEFTEXP; RIGHTEXP; if_icmpge trueL; ldc 0; goto stopL;
		//            trueL: ldc 1; stopL:
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(trueBranch = new IfIcmpgeInstr());
		chainInstr(new LdcIntInstr(0));
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		chainInstr(new LdcIntInstr(1));
		chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
		stopBranch.setTarget(stopTarget);
		outAnyExp(node);
	}
		    
	public void caseAInstanceofExp(AInstanceofExp node) {
		inAnyExp(node);
		node.getExp().apply(this);
		chainInstr(new InstanceofInstr(
		   makeClassNameCode((Node)symAnnotations.get(node))));
		outAnyExp(node);
	}
	
	// arithmetic exp
	public void caseAPlusExp(APlusExp node) {
		inAnyExp(node);
		node.getLeft().apply(this);
		node.getRight().apply(this);
		if (typeAnnotations.get(node) instanceof AIntType) {
			chainInstr(new IaddInstr());
		}
		else {
			chainInstr(new InvokevirtualInstr(
			   "java/lang/String/concat(Ljava/lang/String;)" +
			   "Ljava/lang/String;"));
		}
		outAnyExp(node);
	}
	public void caseAMinusExp(AMinusExp node) {
		inAnyExp(node);
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(new IsubInstr());
		outAnyExp(node);
	}
	public void caseAMultExp(AMultExp node) {
		inAnyExp(node);
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(new ImulInstr());
		outAnyExp(node);
	}
	public void caseADivExp(ADivExp node) {
		inAnyExp(node);
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(new IdivInstr());
		outAnyExp(node);
	}
	public void caseAModExp(AModExp node) {
		inAnyExp(node);
		node.getLeft().apply(this);
		node.getRight().apply(this);
		chainInstr(new IremInstr());
		outAnyExp(node);
	}
	public void caseAUminusExp(AUminusExp node) {
		inAnyExp(node);
		node.getExp().apply(this);
		chainInstr(new InegInstr());
		outAnyExp(node);
	}
	
	public void caseANotExp(ANotExp node) {
		BranchInstr trueBranch, stopBranch;
		LabelInstr trueTarget, stopTarget;
		NotExpLabels labels = (NotExpLabels)resourcesMap.get(node);
		
		inAnyExp(node);
		// not exp -> EXP; ifeq trueL; ldc 0; goto stopL; trueL: ldc 1; stopL:
		node.getExp().apply(this);
		chainInstr(trueBranch = new IfeqInstr());
		chainInstr(new LdcIntInstr(0));
		chainInstr(stopBranch = new GotoInstr());
		chainInstr(trueTarget = new LabelInstr("true",labels.getTrueLabel()));
		trueBranch.setTarget(trueTarget);
		chainInstr(new LdcIntInstr(1));
		chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
		stopBranch.setTarget(stopTarget);
		outAnyExp(node);
	}
	
	// cast exp
	public void caseACastExp(ACastExp node) {
		inAnyExp(node);
		node.getExp().apply(this);
		chainInstr(new CheckcastInstr(
		   makeClassNameCode((Node)symAnnotations.get(node))));
		outAnyExp(node);
	}
	public void caseACasttocharExp(ACasttocharExp node) {
		inAnyExp(node);
		node.getExp().apply(this);
		if (! (((PType)typeAnnotations.get(node.getExp())) instanceof ACharType))
			chainInstr(new I2cInstr());
		outAnyExp(node);
	}	
	
	public void caseAIdExp(AIdExp node) {
		// get Symbol associated with identifier
		Symbol s = (Symbol)symAnnotations.get(node);
		LocalResources localRes;
		String idText;
		
		inAnyExp(node);
		if (s.kind() == localSymK)  {
			localRes = (LocalResources)resourcesMap.get(s.value());
			if (((AOnelocal)s.value()).getType() instanceof AReferenceType)
				chainInstr(new AloadInstr(localRes.getOffset()));
			else 
				chainInstr(new IloadInstr(localRes.getOffset()));
		}
		else if (s.kind() == formalSymK) {
			localRes = (LocalResources)resourcesMap.get(s.value());
			if (((AFormal)s.value()).getType() instanceof AReferenceType)
				chainInstr(new AloadInstr(localRes.getOffset()));
			else 
				chainInstr(new IloadInstr(localRes.getOffset()));
		}
		else if (s.kind() == fieldSymK) {
			chainInstr(new AloadInstr(0));
			idText = node.getIdentifier().getText();
			chainInstr(new GetfieldInstr(
			   makeClassNameCode(
			      hierarchy.lookupHierarchyClass(idText,currentClass)) +
			   "/" +
			   idText +
			   " " +
			   makeTypeCode(((AOnefield)s.value()).getType())));
		}
		outAnyExp(node);
	}
	
	public void caseAThisExp(AThisExp node) {
		inAnyExp(node);
		chainInstr(new AloadInstr(0));
		outAnyExp(node);
	}
	
  public void caseANewExp(ANewExp node) {
    Node theClass = (Node)symAnnotations.get(node);
    Node cons = (Node)invokeBindingsMap.get(node);
		
    chainInstr(new NewInstr(makeClassNameCode(theClass)));
    chainInstr(new DupInstr());
    Iterator argsIt = node.getArgs().iterator();
    while (argsIt.hasNext()) 
      ((PExp)argsIt.next()).apply(this);
    chainInstr(new InvokenonvirtualInstr(makeConstructorCode(theClass,cons)));
  }
	
  public void caseACallExp(ACallExp node) {
    PReceiver receiver = node.getReceiver();
    PType receiverType = (PType)typeAnnotations.get(receiver);
    Node receiverClass = (Node)symAnnotations.get(receiverType);
    Node method = (Node)invokeBindingsMap.get(node);
    GenericMethod gMethod = (GenericMethod)astToGenericMap.get(method);
    Node theClass;
    
    receiver.apply(this);
    Iterator argsIt = node.getArgs().iterator();
    while (argsIt.hasNext()) 
      ((PExp)argsIt.next()).apply(this);
    
    if (receiver instanceof AObjectReceiver) {
      theClass = hierarchy.lookupHierarchyClass(
                       gMethod.getName(),receiverClass);
      chainInstr(new InvokevirtualInstr(makeMethodCode(theClass,method)));
    }
    else if (receiver instanceof ASuperReceiver) {
      theClass = hierarchy.lookupHierarchyClass(
                       gMethod.getName(),
                       hierarchy.get(currentClass).getParent());
      chainInstr(new InvokenonvirtualInstr(makeMethodCode(theClass,method)));
    }
  }
	
  // constant exp
  public void caseAIntconstExp(AIntconstExp node) {
    inAnyExp(node);
    chainInstr(new LdcIntInstr(
                Integer.parseInt(node.getIntconst().getText())));
		outAnyExp(node);
  }
	public void caseATrueExp(ATrueExp node) {
		inAnyExp(node);
		chainInstr(new LdcIntInstr(1)); // true = 1 in bytecode
		outAnyExp(node);
	}
	public void caseAFalseExp(AFalseExp node) {
		inAnyExp(node);
		chainInstr(new LdcIntInstr(0)); // false = 0 in bytecode
		outAnyExp(node);
	}
	// remember to update this if charconst token specs change!
	public void caseACharconstExp(ACharconstExp node) {
		String text = node.getCharconst().getText();
		text = text.substring(1,text.length()-1); // drop quotes
		int value;
		
		inAnyExp(node);
		// interpret text String as a character
		if (text.startsWith("\\"))  { // escape sequence
			if (Character.isDigit(text.charAt(1)))  { // octal esc-seq
				value = Integer.parseInt(text.substring(1),8);
			}
			else {
				switch (text.charAt(1))  {
					case 'b': value = '\b'; break;
					case 't': value = '\t'; break;
					case 'n': value = '\n'; break;
					case 'f': value = '\f'; break;
					case 'r': value = '\r'; break;
					case '"': value = '\"'; break;
					case '\'': value = '\''; break;
					case '\\': value = '\\'; break;
					default: MyError.fatalError(
					            "code-gen",
					            "no such escape sequence: \\"+text.charAt(1));
					         value = -1; // bogus value
				}
			}
		}
		else { // regular char
			value = text.charAt(0);
		}
		chainInstr(new LdcIntInstr(value));
		outAnyExp(node);
	}
	public void caseAStringconstExp(AStringconstExp node) {
		String text = node.getStringconst().getText();

		inAnyExp(node);
		// drop quotes
		chainInstr(new LdcStringInstr(text.substring(1,text.length()-1)));
		outAnyExp(node);
	}
	public void caseANullExp(ANullExp node) {
		inAnyExp(node);
		if (((Boolean)coerceToStringMap.get(node)).booleanValue())
			chainInstr(new LdcStringInstr("null")); // coerce null to "null"
		else
			chainInstr(new AconstNullInstr()); // no toString coercion
		outAnyExp(node);
	}
	
	private void outAnyExp(PExp node) {
		PType type;
		
		// toString coercion -> invoke primitive type wrapper constructor and toString method
		if (((Boolean)coerceToStringMap.get(node)).booleanValue()) {
			type = (PType)typeAnnotations.get(node);
			if (type instanceof AIntType) {
				chainInstr(new InvokenonvirtualInstr("java/lang/Integer/<init>(I)V"));
				chainInstr(new InvokevirtualInstr("java/lang/Integer/toString()Ljava/lang/String;"));
			}
			else if (type instanceof ABooleanType) {
				chainInstr(new InvokenonvirtualInstr("java/lang/Boolean/<init>(Z)V"));
				chainInstr(new InvokevirtualInstr("java/lang/Boolean/toString()Ljava/lang/String;"));
			}
			else if (type instanceof ACharType) {
				chainInstr(new InvokenonvirtualInstr("java/lang/Character/<init>(C)V"));
				chainInstr(new InvokevirtualInstr("java/lang/Character/toString()Ljava/lang/String;"));
			}
			else if (type instanceof AReferenceType) { // special treatment for reference types
				ToStringLabels labels;
				BranchInstr nullBranch, stopBranch;
				LabelInstr nullTarget, stopTarget;
				Node theClass;
				
				// coerce ref ->  dup; ifnull nullL; [!String] goto stopL; null: pop; ldc "null"; stop:
				// [!String] -> invokevirtual <toString-capable-superclass>/toString()Ljava/lang/String
				labels = (ToStringLabels)toStringResourcesMap.get(node);
				chainInstr(new DupInstr());
				chainInstr(nullBranch = new IfnullInstr());
				if (! ((AReferenceType)type).getIdentifier().getText().equals("String")) {
					theClass = hierarchy.lookupHierarchyClass("toString",
					                                          (Node)symAnnotations.get(type));
					chainInstr(new InvokevirtualInstr(makeClassNameCode(theClass) +
					                                  "/toString()Ljava/lang/String;"));
				}
				chainInstr(stopBranch = new GotoInstr());
				chainInstr(nullTarget = new LabelInstr("null",labels.getNullLabel()));
				nullBranch.setTarget(nullTarget);
				chainInstr(new PopInstr());
				chainInstr(new LdcStringInstr("null"));
				chainInstr(stopTarget = new LabelInstr("stop",labels.getStopLabel()));
				stopBranch.setTarget(stopTarget);
			}
		}
	}
	
  /* receiver production */
  public void caseASuperReceiver(ASuperReceiver node) {
    chainInstr(new AloadInstr(0));
  }
}
