// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Adapted to work with joos.

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class MultiLineLabel extends Canvas {
    protected StringVector lines;     // The lines of text to display
    protected int num_lines;          // The number of lines
    protected int margin_width;       // Left and right margins
    protected int margin_height;      // Top and bottom margins
    protected int line_height;        // Total height of the font
    protected int line_ascent;        // Font height above baseline
    protected IntVector line_widths;  // How wide each line is
    protected int max_width;          // The width of the widest line
    protected int alignment;
    
    // Break the label up into separate lines, and save the other info.
    public MultiLineLabel(String lab, int m_width, int m_height) {
        super();
        this.newLabel(lab);
        margin_width = m_width;
        margin_height = m_height;
        alignment = this.CENTER(); 
    }

    // Alignment constants
    public int LEFT()   { return(0);  } 
    public int CENTER() { return(1); }
    public int RIGHT()  { return(2); }

    // This method breaks a specified label up into an array of lines.
    // It uses the StringTokenizer utility class.
    public void newLabel(String label) {
        StringTokenizer t;
        int i;
        t = new StringTokenizer(label, "\n", false);
        num_lines = t.countTokens();
        lines = new StringVector(num_lines,1);
        line_widths = new IntVector(num_lines,1);
        for(i = 0; i < num_lines; i++) 
          lines.add(t.nextToken("\n"));
    }
    
    // This method figures out how the font is, and how wide each
    // line of the label is, and how wide the widest line is.
   public void measure() {
        FontMetrics fm;
        int i;
        fm  = this.getFontMetrics(this.getFont());
        // If we don't have font metrics yet, just return.
        if (fm == null) return;
        
        line_height = fm.getHeight();
        line_ascent = fm.getAscent();
        max_width = 0;
        for(i=0; i < num_lines; i++) 
          { line_widths.add(fm.stringWidth(lines.sub(i)));
            if (line_widths.sub(i) > max_width) 
              max_width = line_widths.sub(i);
          }
    }
    
    // Methods to set the various attributes of the component
    public void setLabel(String label) {
        this.newLabel(label);
        this.measure();
        this.repaint();
    }
    public void setFont(Font f) {
        super.setFont(f);
        this.measure();
        this.repaint();
    }
    public void setForeground(Color c) { 
        super.setForeground(c); 
        this.repaint(); 
    }
    public void setAlignment(int a) 
      { alignment = a; this.repaint(); }
    public void setMarginWidth(int mw) 
      { margin_width = mw; this.repaint(); }
    public void setMarginHeight(int mh) 
      { margin_height = mh; this.repaint(); }
    public int getAlignment() 
      { return (alignment); }
    public int getMarginWidth() 
      { return (margin_width); }
    public int getMarginHeight() 
      { return (margin_height); }
    
    // This method is invoked after our Canvas is first created
    // but before it can actually be displayed.  After we've
    // invoked our superclass's addNotify() method, we have font
    // metrics and can successfully call measure() to figure out
    // how big the label is.
    public void addNotify() { super.addNotify(); this.measure(); }
    
    // This method is called by a layout manager when it wants to
    // know how big we'd like to be.  
    public Dimension preferredSize() {
        return new Dimension(max_width + 2*margin_width, 
                     num_lines * line_height + 2*margin_height);
    }
    
    // This method is called when the layout manager wants to know
    // the bare minimum amount of space we need to get by.
    public Dimension minimumSize() {
        return new Dimension(max_width, num_lines * line_height);
    }
    
    // This method draws the label (applets use the same method).
    // Note that it handles the margins and the alignment, but that
    // it doesn't have to worry about the color or font--the superclass
    // takes care of setting those in the Graphics object we're passed.
    public void paint(Graphics g) {
        int x, y, i;
        JoosDimension d;
       
        d = new JoosDimension(this.size());
        y = line_ascent + (d.height() - num_lines * line_height)/2;
        for(i = 0; i < num_lines; i++)
          { if (alignment == this.LEFT()) 
                x = margin_width; 
            else if (alignment == this.RIGHT())
                x = d.width() - margin_width -  line_widths.sub(i); 
            else // CENTER or other
                x = (d.width() - line_widths.sub(i))/2; 
            y = y + line_height; 
            g.drawString(lines.sub(i), x, y);
          }
    }
}
