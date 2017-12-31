// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Modified to work with JOOS.

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;


public class YesNoDialog extends Dialog {

    protected Button yesButton,noButton,cancelButton; 
    protected MultiLineLabel label;
    protected JoosConstants c;
    protected Panel p; 
 
    public YesNoDialog(Frame parent, String title, String message,
               String yes_label, String no_label, String cancel_label)
    {   // Create the window.
        super(parent, title, true);

        // Make instance for JoosConstants
        c = new JoosConstants();

        // Specify a LayoutManager for it
        this.setLayout(new BorderLayout(15, 15));
        
        // Put the message label in the middle of the window.
        label = new MultiLineLabel(message, 20, 20);
        new JoosContainer(this).addString("Center", label);
        
        // Create a panel of buttons, center the row of buttons in
        // the panel, and put the pane at the bottom of the window.
        p = new Panel();
        p.setLayout(new FlowLayout(c.FLOWLAYOUT_CENTER(), 15, 15));
        if (yes_label != null) 
          p.add(yesButton = new Button(yes_label));
        if (no_label != null)  
          p.add(noButton = new Button(no_label));
        if (cancel_label != null) 
          p.add(cancelButton = new Button(cancel_label));
        new JoosContainer(this).addString("South", p);

        // Set the window to its preferred size.
        this.pack();
    }

    public int NO()  { return(0); }
    public int YES() { return(1); }
    public int CANCEL() { return(-1); }
    
    // Handle button events by calling the answer() method.
    // Pass the appropriate constant value, depending on the button.
    public boolean action(Event e, Object arg)
    { JoosEvent je;
      Object target;
      je = new JoosEvent(e);
      target = je.target();
      if (target instanceof Button) 
        { this.hide();
          this.dispose();
          if (target == yesButton) this.answer(this.YES());
          else if (target == noButton) this.answer(this.NO());
          else this.answer(this.CANCEL());
          return(true);
        }
        else return(false);
    }
    
    // Call yes(), no(), and cancel() methods depending on the button the
    // user clicked.  Subclasses define how the answer is processed by
    // overriding this method or the  yes(), no(), and cancel() methods.
    public void answer(int ans) 
     { if (ans == this.YES()) 
         this.yes();
       else if (ans == this.NO())
         this.no();
       else if (ans == this.CANCEL())
         this.cancel();
      }

    public void yes() {}
    public void no() {}
    public void cancel() {}
}
