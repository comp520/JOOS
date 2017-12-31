// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Modified to work with JOOS.

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class ReallyQuitDialog extends YesNoDialog {
    protected TextComponent status;

    // Create the kind of YesNoDialog we want
    // And store away a piece of information we need later.
    public ReallyQuitDialog(Frame parent, TextComponent sstatus) 
    {  super(parent, "Really Quit?", "Really Quit?", "Yes", "No", null);
       status = sstatus;
    }
    // Define these methods to handle the user's answer
    public void yes() { new JoosSystem().exit(0); }
    public void no() 
    { if (status != null) status.setText("Quit cancelled."); 
    }
}
