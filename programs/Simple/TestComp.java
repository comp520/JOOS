import joos.lib.*;

public class TestComp {

  public TestComp() { super(); }

  public static void main(String argv[])
    { JoosIO f;
      Cons c,d;
      Tree t;
      ExtCons e,ec;
      String s;
      int i;

      f = new JoosIO();

      // no ordering on bools, only on ints
      //f.println("ordering on bools: " + (true < false));

      // can't compare bool and int,  only int and int
      //f.println("comparison int,bool:" + (true < 3));

      // These are ok,  can't mix int and bool
      f.println("(true) equality on bools:" + (true == true));
      f.println("(true) equality on ints:" + (3 == 3));

      // can't test for equality on int, bool  only bool,bool and int,int
      // f.println("equality on int,bool: " + (true == 1));

      c = new Cons("cons",null);
      d = new Cons("cons",null);
      // These are ok,  first one true, second false
      f.println("(true) equality of refs to same ref: " + (c == c));
      f.println("(false)equality of refs to same type: " + (c == d));
      
      e = new ExtCons("cons",null,0);
      // This one is ok
      f.println("(false )equality of refs to subclass: " + (c == e));

      s = new String("I am a string");
      // This one is a compile-time error 
      // f.println("equality of refs to disjoint classes: ", (s == c)); 

      i = 3;
      t = new Tree(new Integer(1),null,null);
      // all of the following get compile-time errors using Sun's javac,
      // stating that it is impossible for them to be instanceof.  It seems
      // that javac looks at the class heirarchy and determines if the
      //   instanceof is impossible
      // f.println("instance on int: " +(i instanceof String));
      // f.println("instance on disjoint types: " +
      //                ((new Integer(i)) instanceof String));
      // f.println("instance on disjoint types: " + (c instanceof String));
      // f.println("instance on disjoint types: " + (t instanceof Cons));

      // This one is okay, should return true
      f.println("(true) instanceof on subtypes: " + 
                 (e.testReturn() instanceof ExtCons));

      // needs the cast because testReturn creates an ExtCons, but has
      //    a return type of Cons (return is assignment compatible rule)
      ec = (ExtCons) e.testReturn(); 

      // spurious casts are ok,  ec is a subclass of c,  c and d are both
      //    refs to Cons
      c = (Cons) ec;
      c = (Cons) d; 
      

      // impossible cast, syntax error,  javac also looks at class heirarchy
      //    to determine if a cast is possible
      // t = (Tree) c; 

    }
} 
