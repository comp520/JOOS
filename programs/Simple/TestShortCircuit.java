import joos.lib.*;

public class TestShortCircuit {

  public TestShortCircuit() { super(); }

  public static void main(String argv[])
    {  int a,b,c,d; 
       JoosIO f;

       f = new JoosIO();
       a = 1;
       b = 2;
       c = 3;
       d = 3;

       if ( (a == b)  || (b == c) )
         { f.println("in the first");
         }
       else if ( (a == b) || (c == d) )
         { f.println("in the second");
         }
 
       f.println("at the end");

    }
} 
