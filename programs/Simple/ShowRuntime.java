import java.util.*;

public class ShowRuntime {

   public ShowRuntime() { super(); }

   public void Doit()
     { Vector v;
       Object item;

       v = new Vector(10,1);
       item = v.elementAt(0);    // raises an exception
     }

   public static void main(String argv[])
     { new ShowRuntime().Doit();
     }
}
