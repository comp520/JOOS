
import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public final class TestEqual {

  public TestEqual() { super(); }

  public static void main(String argv[])
   { Object o1, o2;
     int i1;
     boolean b1, b2, b3;
     String s;
     JoosIO f;

     f = new JoosIO();
     o1 = new Object();
     o2 = new Object();

     b1 = o1 == o2;
     f.println("b1 should be false: " + b1);

     b3 = b2 = !(b1);
     f.println("b2 and b3 should be true: " + b2 + " "  + b3); 
     s = "hi";
     i1 = s.length();
     b3 = i1 == s.length(); 
     f.println("b3 should be true: " + b3);
    }
} 
