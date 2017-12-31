import joos.lib.*;

public class UseExtCons {

  public UseExtCons() { super(); }

  public static void main(String argv[])
    { ExtCons l; 
      Cons r;
      JoosIO f;

      l = new ExtCons("a",new ExtCons("b",new ExtCons("c",null,3),2),1); 
      f = new JoosIO();
      f.println("a member? " + l.member("a"));
      f.println("z member? " + l.member("z"));
      l.printFirst();
      r = l.testReturn();
    }
} 
