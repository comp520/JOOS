// Adapted from Java: How to Program, by Deitel and Deitel,
// Fig. 9.11: DemoFont.java
// Demonstrating the Font constants, the Font constructor
// and the setFont method

import joos.lib.*;
import java.awt.*;
import java.applet.Applet;

public class DemoFont extends Applet {  
   protected Font font1, font2, font3; 

   public DemoFont() { super(); }

   public void init()
   { JoosConstants c;

      c = new JoosConstants(); 
      // create a font object: 12-point bold times roman
      font1 = new Font( "TimesRoman", c.BOLD(), 12 );

      // create a font object: 24-point italic courier
      font2 = new Font( "Courier", c.ITALIC(), 24 );

      // create a font object: 14-point plain helvetica
      font3 = new Font( "Helvetica", c.PLAIN(), 14 );
   }

   public void paint( Graphics g )
   {
      // set the current font to font1
      g.setFont( font1 );

      // draw a string in font font1
      g.drawString( "TimesRoman 12 point bold.", 20, 20 );

      // change the current font to font2
      g.setFont( font2 );

      // draw a string in font font2
      g.drawString( "Courier 24 point italic.", 20, 40 );

      // change the current font to font3
      g.setFont( font3 );

      // draw a string in font font3
      g.drawString( "Helvetica 14 point plain.", 20, 60 );
  }
}
