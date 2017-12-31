/*
 * JOOS is Copyright (C) 1997 Laurie Hendren & Michael I. Schwartzbach
 *
 * Reproduction of all or part of this software is permitted for
 * educational or research use on condition that this copyright notice is
 * included in any copy. This software comes with no warranty of any
 * kind. In no event will the authors be liable for any damages resulting from
 * use of this software.
 *
 * email: hendren@cs.mcgill.ca, mis@brics.dk
 */

#include <stdio.h>
#include <string.h>
#include "memory.h"
#include "emit.h"

FILE *emitFILE;

LABEL *emitlabels;

char *emitname(char *name)
{ int i,j;
  char *e;
  i = strlen(name);
  for (i=strlen(name); i>0 && name[i-1]!='.'; i--);
  e = (char *)Malloc(i+2);
  for (j=0; j<=i; j++) e[j]=name[j];
  e[i] = 'j';
  e[i+1] = '\0';
  return e;
}

void emitLABEL(int label)
{ fprintf(emitFILE,"%s_%i",emitlabels[label].name,label);
}

void localmem(char *opcode, int offset)
{ if (offset >=0 && offset <=3) {
     fprintf(emitFILE,"%s_%i",opcode,offset);
  } else {
     fprintf(emitFILE,"%s %i",opcode,offset);
  }
}

int limitCODE(CODE *c)
{ return 25;
}

void emitCODE(CODE *c)
{ if (c!=NULL) {
     fprintf(emitFILE,"  ");
     switch(c->kind) {
       case nopCK:
            fprintf(emitFILE,"nop");
            break;
       case i2cCK:
            fprintf(emitFILE,"i2c");
            break;
       case newCK:
            fprintf(emitFILE,"new %s",c->val.newC);
            break;
       case instanceofCK:
            fprintf(emitFILE,"instanceof %s",c->val.instanceofC);
            break;
       case checkcastCK:
            fprintf(emitFILE,"checkcast %s",c->val.checkcastC);
            break;
       case imulCK:
            fprintf(emitFILE,"imul");
            break;
       case inegCK:
            fprintf(emitFILE,"ineg");
            break;
       case iremCK:
            fprintf(emitFILE,"irem");
            break;
       case isubCK:
            fprintf(emitFILE,"isub");
            break;
       case idivCK:
            fprintf(emitFILE,"idiv");
            break;
       case iaddCK:
            fprintf(emitFILE,"iadd");
            break;
       case iincCK:
            fprintf(emitFILE,"iinc %i %i",
                             c->val.iincC.offset,c->val.iincC.amount);
            break;
       case labelCK:
            emitLABEL(c->val.labelC);
            fprintf(emitFILE,":");
            break;
       case gotoCK:
            fprintf(emitFILE,"goto ");
            emitLABEL(c->val.gotoC);
            break;
       case ifeqCK:
            fprintf(emitFILE,"ifeq ");
            emitLABEL(c->val.ifeqC);
            break;
       case ifneCK:
            fprintf(emitFILE,"ifne ");
            emitLABEL(c->val.ifneC);
            break;
       case if_acmpeqCK:
            fprintf(emitFILE,"if_acmpeq ");
            emitLABEL(c->val.if_acmpeqC);
            break;
       case if_acmpneCK:
            fprintf(emitFILE,"if_acmpne ");
            emitLABEL(c->val.if_acmpneC);
            break;
       case ifnullCK:
            fprintf(emitFILE,"ifnull ");
            emitLABEL(c->val.ifnullC);
            break;
       case ifnonnullCK:
            fprintf(emitFILE,"ifnonnull ");
            emitLABEL(c->val.ifnonnullC);
            break;
       case if_icmpeqCK:
            fprintf(emitFILE,"if_icmpeq ");
            emitLABEL(c->val.if_icmpeqC);
            break;
       case if_icmpgtCK:
            fprintf(emitFILE,"if_icmpgt ");
            emitLABEL(c->val.if_icmpgtC);
            break;
       case if_icmpltCK:
            fprintf(emitFILE,"if_icmplt ");
            emitLABEL(c->val.if_icmpltC);
            break;
       case if_icmpleCK:
            fprintf(emitFILE,"if_icmple ");
            emitLABEL(c->val.if_icmpleC);
            break;
       case if_icmpgeCK:
            fprintf(emitFILE,"if_icmpge ");
            emitLABEL(c->val.if_icmpgeC);
            break;
       case if_icmpneCK:
            fprintf(emitFILE,"if_icmpne ");
            emitLABEL(c->val.if_icmpneC);
            break;
       case ireturnCK:
            fprintf(emitFILE,"ireturn");
            break;
       case areturnCK:
            fprintf(emitFILE,"areturn");
            break;
       case returnCK:
            fprintf(emitFILE,"return");
            break;
       case aloadCK:
            localmem("aload",c->val.aloadC);
            break;
       case astoreCK:
            localmem("astore",c->val.astoreC);
            break;
       case iloadCK:
            localmem("iload",c->val.iloadC);
            break;
       case istoreCK:
            localmem("istore",c->val.istoreC);
            break;
       case dupCK:
            fprintf(emitFILE,"dup");
            break;
       case popCK:
            fprintf(emitFILE,"pop");
            break;
       case swapCK:
            fprintf(emitFILE,"swap");
            break;
       case ldc_intCK:
            if (c->val.ldc_intC >= 0 && c->val.ldc_intC <= 5) {
               fprintf(emitFILE,"iconst_%i",c->val.ldc_intC);
            } else {
               fprintf(emitFILE,"ldc %i",c->val.ldc_intC);
            }
            break;
       case ldc_stringCK:
            fprintf(emitFILE,"ldc \"%s\"",c->val.ldc_stringC);
            break;
       case aconst_nullCK:
            fprintf(emitFILE,"aconst_null");
            break;
       case getfieldCK:
            fprintf(emitFILE,"getfield %s",c->val.getfieldC);
            break;
       case putfieldCK:
            fprintf(emitFILE,"putfield %s",c->val.putfieldC);
            break;
       case invokevirtualCK:
            fprintf(emitFILE,"invokevirtual %s",c->val.invokevirtualC);
            break;
       case invokenonvirtualCK:
            fprintf(emitFILE,"invokenonvirtual %s",c->val.invokenonvirtualC);
            break;
     }
     fprintf(emitFILE,"\n");
     emitCODE(c->next);
  }
}

void emitPROGRAM(PROGRAM *p)
{ if (p!=NULL) {
     emitPROGRAM(p->next);
     emitCLASSFILE(p->classfile,p->name);
  } 
}

void emitCLASSFILE(CLASSFILE *c, char *name)
{ if (c!=NULL) {
     emitCLASSFILE(c->next,name);
     emitCLASS(c->class,name);
  }
}

void emitCLASS(CLASS *c, char *name)
{ if (!c->external) {
     emitFILE = fopen(emitname(name),"w");
     fprintf(emitFILE,".class public ");
     emitMODIFIER(c->modifier);
     fprintf(emitFILE,"%s\n\n",c->name);
     fprintf(emitFILE,".super %s\n\n",c->parent->signature);
     emitFIELD(c->fields);
     if (c->fields!=NULL) fprintf(emitFILE,"\n");
     emitCONSTRUCTOR(c->constructors);
     emitMETHOD(c->methods);
     fclose(emitFILE);
  }
}

void emitTYPE(TYPE *t)
{ switch (t->kind) {
    case intK:
         fprintf(emitFILE,"I");
         break;
    case boolK:
         fprintf(emitFILE,"Z");
         break;
    case charK:
         fprintf(emitFILE,"C");
         break;
    case voidK:
         fprintf(emitFILE,"V");
         break;
    case refK:
         fprintf(emitFILE,"L%s;",t->class->signature);
         break;
    case polynullK:
         break;
  }
}

void emitFIELD(FIELD *f)
{ if (f!=NULL) {
     emitFIELD(f->next);
     fprintf(emitFILE,".field protected %s ",f->name);
     emitTYPE(f->type);
     fprintf(emitFILE,"\n");
  }
}

void emitCONSTRUCTOR(CONSTRUCTOR *c)
{ if (c!=NULL) {
     emitCONSTRUCTOR(c->next);
     fprintf(emitFILE,".method public <init>%s\n",c->signature);
     fprintf(emitFILE,"  .limit locals %i\n",c->localslimit);
     emitlabels = c->labels;
     fprintf(emitFILE,"  .limit stack %i\n",limitCODE(c->opcodes));
     emitCODE(c->opcodes);
     fprintf(emitFILE,".end method\n\n");
  }
}

void emitMETHOD(METHOD *m)
{ if (m!=NULL) {
     emitMETHOD(m->next);
     if (m->modifier==staticMod) {
        fprintf(emitFILE,".method public static main([Ljava/lang/String;)V\n");
     } 
     else 
     { fprintf(emitFILE,".method public ");
        emitMODIFIER(m->modifier);
        fprintf(emitFILE,"%s%s\n",m->name,m->signature);
     }
     /* LJH - added check for abstract */
     if (m->modifier!=abstractMod)
       { fprintf(emitFILE,"  .limit locals %i\n",m->localslimit);
         emitlabels = m->labels;
         fprintf(emitFILE,"  .limit stack %i\n",limitCODE(m->opcodes));
         emitCODE(m->opcodes);
       }
     fprintf(emitFILE,".end method\n\n");
  }
}

void emitMODIFIER(ModifierKind modifier)
{ switch (modifier)
    { case noneMod:          
           break;
      case finalMod:         
           fprintf(emitFILE,"final "); 
           break;
      case abstractMod:      
           fprintf(emitFILE,"abstract "); 
           break;
      case synchronizedMod:  
           fprintf(emitFILE,"synchronized "); 
           break;
      case staticMod:  
           fprintf(emitFILE,"static "); 
           break;
    }
}

