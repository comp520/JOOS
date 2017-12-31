// Adapted from Java: How to Program, by Deitel and Deitel,
// Fig. 9.7: ShowColors.java
// Demonstrating setting and getting a Color 

import java.awt.*;
import java.applet.Applet;

public class ShowColors extends Applet {
   // need protected vars for Applet
   protected int red, green, blue; 

   public ShowColors() { super(); }

   public void init()
   { // set some values
      red = 100;
      blue = 255;
      green = 125;
   }

   public void paint ( Graphics g )
   {  Color c;

      c = g.getColor(); 

      g.setColor( new Color( red, green, blue ) );
      g.drawString( "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 50, 33 );
      this.showStatus( "Current RGB: " + g.getColor().toString() );
   } 
}
