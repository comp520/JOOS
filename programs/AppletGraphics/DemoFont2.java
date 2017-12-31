// Adapted from Java: How to Program, by Deitel and Deitel,
// Fig. 9.13: DemoFont2.java
// Demonstrating the Font methods for
// retrieving font information

import joos.lib.*;
import java.awt.*;
import java.applet.Applet;

public class DemoFont2 extends Applet {
   protected Font f;
   protected JoosConstants c;

   public DemoFont2() 
     { super(); 
       c = new JoosConstants(); 
     }

   public void init()
   { // create a font object: 24-point bold italic courier
      f = new Font( "Courier", c.ITALIC() + c.BOLD(), 24 );
   }

   public void paint( Graphics g )
   {  int style, size;
      String s, name;

      g.setFont( f );          // set the current font to f
      style = f.getStyle();    // determine current font style
      
      if ( style == c.PLAIN() )
         s = "Plain ";
      else if ( style == c.BOLD() )
         s = "Bold ";
      else if ( style == c.ITALIC() )
         s = "Italic ";
      else    // bold + italic
         s = "Bold italic ";

      size = f.getSize();    // determine current font size
      s = s + size + " point "; 
      name = f.getName();    // determine current font name
      s = s + name;
      g.drawString( s, 20, 40 );

      // display font family
      g.drawString( "Font family is " + f.getFamily(), 20, 60 );
  }
}
