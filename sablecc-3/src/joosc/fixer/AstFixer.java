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


package joosc.fixer;

import joosc.*;
import joosc.node.*;
import joosc.analysis.*;
import java.util.*;

/**
 * AstFixer<p>
 * 
 * Notes:<br>
 *  noshortif 'for' statements are eliminated like all other noshortif
 *  statements, rather than being converted to the equivalent 'while'
 *  loops, because this would have the side-effect of introducing 
 *  another entry point for noshortif statements, not desirable.<p>
 *
 * History:
 * <ul>
 *   <li>19 Aug 2003 - use SableCC 3 AST grammar, fix documentation,
 *                     remove all short_if_xxx and A+ code.
 *   <li>21 May 2000 - corrected bug: had forgotten formal_list tranformation!
 *   <li>10 May 2000 - made extension of "Object" class explicit in the AST
 *   <li> 7 May 2000 - added new list flattening transformations that
 *                tightly couple name and type for field and decl stm
 *   <li>4 May 2000 - driver (fix) now performs AstFixerSimplestm pass as well
 *   <li>2 Apr 2000 - A+ additions as pseudo-syntactic sugar completed
 *                expression cascade collapsing completed
 *   <li>1 Apr 2000 - stm_no_short_if transformations completed
 *                constructor streamlining transformation completed
 *   <li>6 Mar 2000 - list flattening transformations completed
 *                prototype list transformation: identifier_list
 *                created
 * </ul>
 */
public class AstFixer extends DepthFirstAdapter {
  protected String filename;

  public AstFixer(String fn) { filename = fn; }

  public static void fix(List theProgram) {

    ClassFile[] programs = (ClassFile[])theProgram.toArray(new ClassFile[0]);

    Node ast;
    String cname;
    ClassFile classfile;
    
    for(int i = 0; i < programs.length; i++) {
      classfile = programs[i];
      ast = classfile.getAst();
      cname = classfile.getName();
      ast.apply(new AstFixer(cname));
      //ast.apply(new AstFixerSimplestm(cname));
    }
  }

  /******************************************************************
   *              "Object" extension made explicit                  *
   ******************************************************************/
   
  /**
   * Makes a default class (no super class) as a subclass of 
   * the "java.lang.Object" class.
   */
  public void inAClass(AClass node) {
    TIdentifier id = node.getIdentifier();
    
    if (node.getExtension() == null && 
        ! id.getText().equals("Object")) {
      node.setExtension(
         new AExtension(new TIdentifier("Object",id.getLine(),id.getPos())));
    }
  }

  /**
   * Makes a default class (no super class) as a subclass of 
   * the "java.lang.Object" class.
   */
  public void inAExternClass(AExternClass node) {
    TIdentifier id = node.getIdentifier();
    
    if (node.getExtension() == null && 
        ! id.getText().equals("Object")) {
      node.setExtension(
        new AExtension(new TIdentifier("Object",id.getLine(),id.getPos())));
    }
  }

  /**
   * Unifies field declarations to onefield* format.
   */
  public void outAFirstField(AFirstField tmpNode) {
    LinkedList newList = new LinkedList();

    Object[] ids = tmpNode.getIdentifiers().toArray();
    
    // pair field name and type
    for (int i=0, n=ids.length; i<n; i++) {
        newList.add(new AOnefield((PType)tmpNode.getType().clone(),
                                  (TIdentifier)ids[i]));
    }
    tmpNode.replaceBy(new AField(newList));
  }
        
  /**
   * Unifies local declarations to one local* format.
   */
  public void outADeclFirstStm(ADeclFirstStm tmpNode) {
    LinkedList newList = new LinkedList();
    
    Object[] ids = tmpNode.getIdentifiers().toArray();
  
    for (int i=0, n=ids.length; i<n; i++) {
      newList.add(new AOnelocal((PType)tmpNode.getType().clone(),
                                (TIdentifier)ids[i]));
    }
    tmpNode.replaceBy(new ADeclStm(newList));
  }
}
