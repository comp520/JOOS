import joos.lib.*;

public class UseCons {

  public UseCons() { super(); }

  public static void main(String argv[])
    { Cons l; 
      JoosIO f;

      l = new Cons("a",new Cons("b",new Cons("c",null))); 
      f = new JoosIO();
      f.println(l.toString());
      f.println("first is " + l.getFirst());
      f.println("second is " + l.getRest().getFirst());
      f.println("a member? " + l.member("a"));
      f.println("z member? " + l.member("z"));
    }
} 
