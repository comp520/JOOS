import joos.lib.*;
import java.awt.*;
import java.net.*;
import java.applet.Applet;

public class TicTacToe extends WigApplet {
    protected String s1,s2,s3,s4,s5,s6,s7,s8,s9; 
    protected String mark; 
    protected Image notImage,crossImage;
    protected JoosIO f;

    public TicTacToe() {
      super();
      f = new JoosIO();
    }

    public void init() {
        notImage = this.getImage(this.getCodeBase(), "images/not.gif");
        crossImage = this.getImage(this.getCodeBase(), "images/cross.gif");
        s1 = this.getParameter("i1");
        s2 = this.getParameter("i2");
        s3 = this.getParameter("i3");
        s4 = this.getParameter("i4");
        s5 = this.getParameter("i5");
        s6 = this.getParameter("i6");
        s7 = this.getParameter("i7");
        s8 = this.getParameter("i8");
        s9 = this.getParameter("i9");
        
        f.println("s1 is [" + s1 + "]");
        f.println("equal? " + new Boolean(s1.equals("*")));
        mark = this.getParameter("mark");
        this.repaint();
    }

    public void paintSquare(Graphics g, int x, int y, String mark) {
      if (mark.equals("X")) g.drawImage(crossImage, x, y, this);
      if (mark.equals("O")) g.drawImage(notImage, x, y, this);
    }

    public void paint(Graphics g) {
        int xoff, yoff;
        JoosDimension d;
        d = new JoosDimension(this.size());
        g.setColor(new JoosConstants().black());
        xoff = d.width() / 3;
        yoff = d.height() / 3;
        g.drawLine(xoff, 0, xoff, d.height());
        g.drawLine(2*xoff, 0, 2*xoff, d.height());
        g.drawLine(0, yoff, d.width(), yoff);
        g.drawLine(0, 2*yoff, d.width(), 2*yoff);
        this.paintSquare(g,0*xoff+1,0*yoff+1,s1);
        this.paintSquare(g,1*xoff+1,0*yoff+1,s2);
        this.paintSquare(g,2*xoff+1,0*yoff+1,s3);
        this.paintSquare(g,0*xoff+1,1*yoff+1,s4);
        this.paintSquare(g,1*xoff+1,1*yoff+1,s5);
        this.paintSquare(g,2*xoff+1,1*yoff+1,s6);
        this.paintSquare(g,0*xoff+1,2*yoff+1,s7);
        this.paintSquare(g,1*xoff+1,2*yoff+1,s8);
        this.paintSquare(g,2*xoff+1,2*yoff+1,s9);
    }

    public boolean legalMove(int i) {
      f.println("In legalMove is is " + new Integer(i));
      if ((i==1 && !s1.equals("*")) ||
          (i==2 && !s2.equals("*")) ||
          (i==3 && !s3.equals("*")) ||
          (i==4 && !s4.equals("*")) ||
          (i==5 && !s5.equals("*")) ||
          (i==6 && !s6.equals("*")) ||
          (i==7 && !s7.equals("*")) ||
          (i==8 && !s8.equals("*")) ||
          (i==9 && !s9.equals("*"))) {
         return false;
      } else {
          if (i==1) s1=mark;
          if (i==2) s2=mark;
          if (i==3) s3=mark;
          if (i==4) s4=mark;
          if (i==5) s5=mark;
          if (i==6) s6=mark;
          if (i==7) s7=mark;
          if (i==8) s8=mark;
          if (i==9) s9=mark;
          return true;
      }
    }

    public boolean win(String m) {
      if (s1.equals(m) && s2.equals(m) && s3.equals(m)) return true;
      if (s4.equals(m) && s5.equals(m) && s6.equals(m)) return true;
      if (s7.equals(m) && s8.equals(m) && s9.equals(m)) return true;
      if (s1.equals(m) && s4.equals(m) && s7.equals(m)) return true;
      if (s2.equals(m) && s5.equals(m) && s8.equals(m)) return true;
      if (s3.equals(m) && s6.equals(m) && s9.equals(m)) return true;
      if (s1.equals(m) && s5.equals(m) && s9.equals(m)) return true;
      if (s3.equals(m) && s5.equals(m) && s7.equals(m)) return true;
      return false;
    }

    public boolean draw() {
      return !s1.equals("*") && 
             !s2.equals("*") && 
             !s3.equals("*") && 
             !s4.equals("*") && 
             !s5.equals("*") && 
             !s6.equals("*") && 
             !s7.equals("*") && 
             !s8.equals("*") && 
             !s9.equals("*");
    }

    public String done() {
      if (this.win("X")) return "X";
      if (this.win("O")) return "O";
      if (this.draw()) return "D";
      return "";
    }

    public boolean mouseDown(Event evt, int x, int y) {
      JoosDimension d;
      JoosIO f;
      int c,r;

      f = new JoosIO();
      d = new JoosDimension(this.size());
      c = (x * 3) / d.width();
      r = (y * 3) / d.height();
      f.println("x = " + new Integer(x) + "y = " + new Integer(y) + "c = " + new Integer(c) + "r = " + new Integer(r));
      if (this.legalMove(c + r*3 + 1)) {
          this.repaint();
          this.addResult("o1",s1);
          this.addResult("o2",s2);
          this.addResult("o3",s3);
          this.addResult("o4",s4);
          this.addResult("o5",s5);
          this.addResult("o6",s6);
          this.addResult("o7",s7);
          this.addResult("o8",s8);
          this.addResult("o9",s9);
          this.addResult("done",this.done());
          if (this.openURL()) f.println("URL opened");
          if (this.returnResults()) f.println("results returned");
          this.getResponse();
      } 
      return true;
    }
}
