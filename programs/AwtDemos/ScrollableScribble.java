// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Modified to work with Joos.

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class ScrollableScribble extends Panel {
    protected Canvas canvas;
    protected Scrollbar hbar, vbar;
    protected Vector lines;
    protected int last_x, last_y;
    protected int offset_x, offset_y;
    protected int canvas_width, canvas_height;
    protected JoosConstants c;
    
    // Create a canvas and two scrollbars and lay them out in the panel.
    // Use a BorderLayout to get the scrollbars flush against the 
    // right and bottom sides of the canvas.  When the panel grows,
    // the canvas and scrollbars will also grow appropriately.
    public ScrollableScribble() {
        super();  // Create the Panel 
        c = new JoosConstants();
        lines = new Vector(100, 100);
        canvas = (Canvas) new BlankCanvas();
        hbar = new Scrollbar(c.SCROLLBAR_HORIZONTAL());
        vbar = new Scrollbar(c.SCROLLBAR_VERTICAL());
        this.setLayout(new BorderLayout(0, 0));
        new JoosContainer(this).addString("Center", canvas);
        new JoosContainer(this).addString("South", hbar);
        new JoosContainer(this).addString("East", vbar);
    }
    
    // Draw the scribbles that we've saved in the Vector.
    // This method will only ever be invoked when the BlankCanvas
    // class (below) calls it.  It uses the Graphics object from the 
    // BlankCanvas object, and the Vector of lines from this class
    // to redraw everything.  
    // The offset_x and offset_y variables specify which portion of
    // the larger (1000x1000) scribble is to be displayed in the 
    // relatively small canvas.  Moving the scrollbars changes these
    // variables, and thus scrolls the picture.  Note that the Graphics
    // object automatically does clipping; we can't accidentally 
    // draw outside of the borders of the BlankCanvas.
    public void paint(Graphics g) {
        Line l;
        int i;

        i = 0;
        while( i < lines.size() )
          { l = (Line)lines.elementAt(i);
            g.drawLine(l.getx1() - offset_x, l.gety1() - offset_y, 
                      l.getx2() - offset_x, l.gety2() - offset_y);
            i = i + 1;
          }
    }
    
    // Handle user's mouse scribbles.  Draw the scribbles
    // and save them in the vector for later redrawing.
    // Note that to draw these scribbles, we have to look up the
    // Graphics object of the canvas.
    public boolean mouseDown(Event e, int x, int y)
    {
        last_x = x; last_y = y;
        return true;
    }
    public boolean mouseDrag(Event e, int x, int y)
    {
        Graphics g;
        g = canvas.getGraphics();
        g.drawLine(last_x, last_y, x, y);
        lines.addElement(new Line(last_x + offset_x, last_y + offset_y, 
                      x + offset_x, y + offset_y));
        last_x = x;
        last_y = y;
        return true;
    }
    // handle mouse up, too,, just for symmetry.
    public boolean mouseUp(Event e, int x, int y) {  return true; }
    
    // This method handles the scrollbar events.  It updates the
    // offset_x and offset_y variables that are used by the paint()
    // method, and then calls update(), which clears the canvas and
    // invokes the paint() method to redraw the scribbles.
    public boolean handleEvent(Event e) {
        JoosEvent je;
        Object e_target;
        int e_id;
        Object e_arg;
 
        // get appropriate values out of e
        je = new JoosEvent(e);
        e_target = je.target();
        e_id = je.id();
        e_arg = je.arg();

        if (e_target == hbar) {
           if (e_id == c.SCROLL_LINE_UP() ||
               e_id == c.SCROLL_LINE_DOWN() ||
               e_id == c.SCROLL_PAGE_UP() ||
               e_id == c.SCROLL_PAGE_DOWN() ||
               e_id == c.SCROLL_ABSOLUTE()
              ) 
              offset_x = ((Integer)e_arg).intValue();   
            this.update(canvas.getGraphics());
            return (true);
        }
        else if (e_target == vbar) {
           if (e_id == c.SCROLL_LINE_UP() ||
               e_id == c.SCROLL_LINE_DOWN() ||
               e_id == c.SCROLL_PAGE_UP() ||
               e_id == c.SCROLL_PAGE_DOWN() ||
               e_id == c.SCROLL_ABSOLUTE()
              )
              offset_y = ((Integer)e_arg).intValue();  
            this.update(canvas.getGraphics());
            return (true);
        }
        
        // If we didn't handle it above, pass it on to the superclass
        // handleEvent routine, which will check its type and call
        // the mouseDown(), mouseDrag(), and other methods.
        return (super.handleEvent(e));
    }

    // This method is called when our size is changed.  We need to
    // know this so we can update the scrollbars
    public synchronized void reshape(int x, int y, int width, int height) {
        JoosDimension hbar_size; 
        JoosDimension vbar_size; 
        // do the real stuff
        super.reshape(x, y, width, height);
        // Update our scrollbar page size
        hbar_size = new JoosDimension(hbar.size());
        vbar_size = new JoosDimension(vbar.size());
        canvas_width = width - vbar_size.width();
        canvas_height = height - hbar_size.height();
        hbar.setValues(offset_x, canvas_width, 0, 1000-canvas_width);
        vbar.setValues(offset_y, canvas_height, 0, 1000-canvas_height);
        hbar.setPageIncrement(canvas_width/2);
        vbar.setPageIncrement(canvas_height/2);
        this.update(canvas.getGraphics());
    }
}
