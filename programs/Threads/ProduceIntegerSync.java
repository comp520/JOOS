import joos.lib.*;
import java.util.*;

public class ProduceIntegerSync extends Thread {

   protected HoldIntegerSync pHold;
   protected JoosIO f;

   public ProduceIntegerSync( HoldIntegerSync h )
   {  super((Thread) null);
      f = new JoosIO();
      pHold = h;
   }

   public void run()
   { int count; int r;
      for (count = 0; count < 10; count++ ) 
        { pHold.setSharedInt( count );
          f.println( "Producer set sharedInt to " + count );
          // sleep for a random interval
          r = new Random().nextInt(); if (r < 0) r = -r;
          if ( new JoosThread(null).sleep(r%3000) )
            f.println( "Thread "+ this.getName() + "interrupted.");
        }
   }
}
