// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Modified to work with Joos

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.net.*;
import java.applet.Applet;

public class Imagemap extends Applet {
    protected Image image;      // image to display.
    protected Vector rects;     // list of rectangles in it.
    protected ImagemapRectangle lastrect;
    protected JoosConstants c;
    
    public Imagemap() { super(); c = new JoosConstants();}

    public void init() 
    { int i;
      ImagemapRectangle r;
 
      // load the image to be displayed.
      image = this.getImage(this.getDocumentBase(), 
                            this.getParameter("image"));
      // lookup a list of rectangular areas and the URLs they map to.
      rects = new Vector(10,10);
      i = 0;
      while ((r = this.getRectangleParameter("rect" + i)) != null) 
        { rects.addElement(r);
          i=i+1;
        }
    }
    
    // Called when the applet is being unloaded from the system.
    // We use it here to "flush" the image. This may result in memory 
    // and other resources being freed quicker than they otherwise would.

    public void destroy() { image.flush(); }
    
    // Display the image.
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
    
    // We override this method so that it doesn't clear the background
    // before calling paint().  Makes for less flickering in some situations.
    public void update(Graphics g) { this.paint(g); }
    
    // find the rectangle we're inside
    public ImagemapRectangle findrect(int x, int y) {
        int i;
        ImagemapRectangle r;
        boolean found;

        r = null;
        found = false;
        i = 0;
        while ( (!found) &&  (i < rects.size()) )
          {  r = (ImagemapRectangle) rects.elementAt(i);
             if (r.inside(x, y)) found = true; else i++;
          }
        if (i < rects.size()) return(r);
        else return(null);
    }
    
    
    // On button down, highlight the rectangle, and display a message
    public boolean mouseDown(Event e, int x, int y) {
        ImagemapRectangle r;
        Graphics g;
        r = this.findrect(x, y); 
        if (r == null) return false;
        g = this.getGraphics();
        g.setXORMode(c.red());
        g.drawRect(r.x(), r.y(), r.width(), r.height());
        lastrect = r;
        this.showStatus("To: " + r.url());
        return true;
    }
    
    // On button up, unhighlight the rectangle. 
    // If still inside the rectangle go to the URL
    public boolean mouseUp(Event e, int x, int y) {
        ImagemapRectangle r;
        Graphics g;
      
        if (lastrect != null) {
            g = this.getGraphics();
            g.setXORMode(c.red());
            g.drawRect(lastrect.x(), lastrect.y(), 
                       lastrect.width(), lastrect.height());
            this.showStatus("");
            r = this.findrect(x,y);
            if ((r != null) && (r == lastrect))
                this.getAppletContext().showDocument(r.url());
            lastrect = null;
        }
        return true;
    }    

    // Parse a comma-separated list of rectangle coordinates and a URL.
    public ImagemapRectangle getRectangleParameter(String name) {
        int x, y, w, h;
        URL url;
        String value;
        StringTokenizer st;

        value  = this.getParameter(name);
        if (value == null) return null;
        st = new StringTokenizer(value, ",",false);
        
        x = new JoosString(st.nextToken(",")).string2Int();
        y = new JoosString(st.nextToken(",")).string2Int();
        w = new JoosString(st.nextToken(",")).string2Int();
        h = new JoosString(st.nextToken(",")).string2Int();
        url = new JoosURL(this.getDocumentBase(), st.nextToken(",")).getURL();
        
        return new ImagemapRectangle(x, y, w, h, url);
    }
}

