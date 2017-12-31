// From Java How to Program, by Deitel and Deitel
// Modified to work with JOOS.
// Fig. 13.5: SharedCell.java
// Show multiple threads modifying shared object.
// Use synchronization to ensure that both threads
// access the shared cell properly.

public class SharedCellSync {
   public SharedCellSync() { super(); }

   public static void main( String argv[] )
   {
      HoldIntegerSync h;
      ProduceIntegerSync p;
      ConsumeIntegerSync c;

      h = new HoldIntegerSync();
      p = new ProduceIntegerSync( h );
      c = new ConsumeIntegerSync( h );

      p.start();
      c.start();
   }
}
