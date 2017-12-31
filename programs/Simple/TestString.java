import joos.lib.*;

public class TestString {

  public TestString() { super(); }

  public static void main(String argv[])
    { JoosString s1,s2,s3;
      JoosIO f;

      f = new JoosIO();

      s1 = new JoosString("true");
      if (s1.string2Bool()) 
        f.println("string2Bool is ok");

      s2 = new JoosString("10");
      if (s2.string2Int() == 10)
        f.println("string2Int is ok"); 

      s3 = new JoosString("I am a string");

      if (s3.valueOf() == "I am a string")
        f.println("valueOf is ok");
    }
} 
