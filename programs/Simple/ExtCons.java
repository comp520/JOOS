
import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class ExtCons extends Cons {
  // Fields 
 protected int intField;
 
  // Constructor 
  public ExtCons(Object f, Cons r, int i)
    { super(f,r); 
      intField = i;
    }

  public void setIntField(int i)
   { intField = i;
   }

  public void setFirst(Object o)
   { Object o2;
     
     o2 = new Object();
     super.setFirst(o2);
   }

  public Cons testReturn()
    { // We can return a subclass of Cons 
      return (new ExtCons("abc",null,3));
    } 

  public void testLookup(Cons c, Object o)
    { c.setFirst(o);
    }

  public void testSuper(Object o)
    { super.setFirst(o);
    }

  public void printFirst()
    { (new JoosIO()).println("first is: " + first); 
    }
     
}
