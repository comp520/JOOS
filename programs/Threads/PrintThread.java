// From Java How to Program, by Deitel and Deitel
// Modified to work with JOOS.
// Fig. 13.3: PrintTest.java
// Show multiple threads printing at different intervals.

import joos.lib.*;
import java.util.*;

public class PrintThread extends Thread {
   protected int sleepTime;
   protected JoosIO f;
   protected int r;

   // PrintThread constructor assigns name to thread 
   // by calling Thread constructor
   public PrintThread()
   {
      super((Thread) null);
      f = new JoosIO();
      r = new Random().nextInt();

      // sleep between 0 and 5 seconds
      if (r > 0)
         sleepTime = r % 5000;
      else
         sleepTime = (-r) % 5000;

      f.println( "System Thread Name: " + this.getName() +
                          ";  sleep: " + sleepTime + " created.");
   }

   // execute the thread
   public void run()
   {
      // put thread to sleep for a random interval
      if (new JoosThread(null).sleep(sleepTime))
        f.println("Interruption of thread " + this.getName());

      // print thread name
      f.println( "Thread " + this.getName() + " ran.");                             
   }
}
