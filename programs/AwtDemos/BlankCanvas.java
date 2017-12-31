// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Modified for joos.

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

// This class is a trivial subclass of Canvas.  All it knows how to do
// is ask its parent (the ScrollableScribble object) for a redraw.
public class BlankCanvas extends Canvas {
    public BlankCanvas() { super(); }

    public void paint(Graphics g) {
        this.getParent().paint(g);
    }
}
