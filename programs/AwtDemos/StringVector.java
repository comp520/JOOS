import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class StringVector extends Vector {

  public StringVector(int size, int incr) 
    { super(size,incr); }

  public String sub(int i) 
    { return( (String) this.elementAt(i)); }

  public boolean add(Object x)
    { this.addElement((String) x); return true; }
}
