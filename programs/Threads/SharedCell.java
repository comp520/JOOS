// From Java How to Program, by Deitel and Deitel
// Modified to work with JOOS.
// Fig. 13.4: SharedCell.java
// Show multiple threads modifying shared object.  HoldObject is
//    not synchronized.

public class SharedCell {
   public SharedCell() { super(); }

   public static void main( String argv[] )
   {  HoldInteger h; 
      ProduceInteger p; 
      ConsumeInteger c; 

      h = new HoldInteger();
      p = new ProduceInteger( h );
      c = new ConsumeInteger( h );

      p.start();
      c.start();
   }
}
