import joos.lib.*;

public class TestForInc {

  public TestForInc() { super(); }

  public static void main(String argv[])
  {  int i;
     JoosIO f;

     f = new JoosIO();
     for (i=0; i<10; i++)
       f.println("i is now ..." + i);
     f.println("is ends at ..." + i);
  }
} 
