// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Modified to work with JOOS.

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

// This class stores the coordinates of one line of the scribble.
public class Line {
    protected int x1, y1, x2, y2;

    public Line(int X1, int Y1, int X2, int Y2) {
        super(); 
        x1 = X1; y1 = Y1; x2 = X2; y2 = Y2;
    }
    public int getx1() { return x1; }
    public int gety1() { return y1; }
    public int getx2() { return x2; }
    public int gety2() { return y2; }
}
