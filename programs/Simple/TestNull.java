import joos.lib.*;

public class TestNull {

  public TestNull() { super(); }

  public static void main(String argv[])
    { String s1;
      JoosIO f;

      f = new JoosIO();
      f.println("printing literal null: " + null);

      if (true)
        s1 = null;
      else
        s1 = new String("I am not null!");

      f.println("printing value null: " + s1);

    }
} 
