// Adapted from Java: How to Program, by Deitel and Deitel,
// Fig. 9.25: Rectangle2.java
// Drawing rounded rectangles

import java.awt.*;
import java.applet.Applet;

public class Rectangle2 extends Applet {
   public Rectangle2() { super(); }

   public void paint( Graphics g )
   {
      // draw a rounded rectangle at (10, 35)
      g.drawRoundRect( 10, 35, 50, 50, 10, 20 );

      // draw a filled rounded rectangle at (80, 15)  
      g.fillRoundRect( 80, 15, 60, 80, 50, 10 );

      // draw a rounded rectangle at (150, 55)
      g.drawRoundRect( 150, 55, 80, 20, 70, 70 );

      // draw a filled square at (240, 15)  
      g.fillRoundRect( 240, 15, 80, 80, 0, 0 );

      // draw a circle at (330, 15)
      g.drawRoundRect( 330, 15, 80, 80, 80, 80 );      
   }
}
