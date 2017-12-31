import joos.lib.*;
import java.util.*;

public class ConsumeIntegerSync extends Thread {
   protected HoldIntegerSync cHold;
   protected JoosIO f;

   public ConsumeIntegerSync( HoldIntegerSync h )
   {  super((Thread) null);
      cHold = h;
      f = new JoosIO();
   }

   public void run()
   { int val, r;
     val = cHold.getSharedInt();
     f.println( "Consumer retrieved " + val );
     while ( val != 9 ) 
       { // sleep for a random interval
         r = new Random().nextInt();  if (r < 0) r = -r; 
         if (new JoosThread(null).sleep(r%3000))
            f.println( "Interruption of thread " + this.getName() + ".");
         val = cHold.getSharedInt();
         f.println( "Consumer retrieved " + val );
      }
   }
}
