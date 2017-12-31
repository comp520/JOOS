
import joos.lib.*;

public class TestIO {

  public TestIO() { super(); }

  public static void main(String argv[])
    { JoosIO f;
      boolean b;
      int i;
      String s;

      f = new JoosIO();

      f.println("Hello World");

      f.print("Enter a string: "); 
      s = f.readLine();
      f.print("The string s is: ");
      f.println(s);

      f.print("Enter an integer: "); 
      i = f.readInt(); 
      f.print("The integer i is: ");
      f.println((new Integer(i)).toString());

      f.print("Enter a boolean: "); 
      b = f.readBoolean();
      f.print("The boolean b is: ");
      f.println((new Boolean(b)).toString());
    }
} 
