// Adapted from Java: How to Program, by Deitel and Deitel,
// Fig. 10.7: MyLabel2.java
// Demonstrating Label class getText and setText methods.

import joos.lib.*;
import java.awt.*;
import java.applet.Applet;

public class MyLabel2 extends Applet {
   protected Font f;
   protected JoosConstants c;
   protected Label noLabel;
    
   public MyLabel2() 
   { super(); 
     c = new JoosConstants();
   }

   public void init()
   { Component force;
      Container force2;

      f = new Font( "Courier", c.BOLD(), 14 );

      // call label constructor with empty string, LEFT alignment 
      noLabel = new Label("",c.LABEL_LEFT());

      // set text in noLabel
      noLabel.setText( "new text!" );

      // set font for noLabel
      noLabel.setFont(f); 
      this.add( noLabel );
   }

   public void paint( Graphics g )
   { this.showStatus( "Label is displaying: " + noLabel.getText() );
   }
}
