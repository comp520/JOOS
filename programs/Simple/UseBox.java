import joos.lib.*;

public class UseBox {

  public UseBox() { super(); }

  public static void main(String argv[])
    { SyncBox b; 
      JoosIO f;

      f = new JoosIO();
      b = new SyncBox();
      b.put("abc");
      f.println(b.get().toString()); 
    }
} 
