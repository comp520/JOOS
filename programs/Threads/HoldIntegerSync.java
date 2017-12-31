import joos.lib.*;

public class HoldIntegerSync {
   protected int sharedInt;
   protected boolean writeable;
   protected JoosIO f;

   public HoldIntegerSync() 
     { super(); 
       writeable = true; 
       f = new JoosIO();
     }

   public synchronized void setSharedInt( int val )
   {
      while ( !writeable ) {
         if (new JoosObject(this).wait(0))
           f.println( "Interruption of setSharedInt");
      }

      sharedInt = val;
      writeable = false;
      this.notify();
   }

   public synchronized int getSharedInt()
   {
      while ( writeable ) {
        if (new JoosObject(this).wait(0))
            f.println("Interruption of getSharedInt");
      }

      writeable = true;
      this.notify();
      return sharedInt;
   }
}
