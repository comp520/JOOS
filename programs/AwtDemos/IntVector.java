import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class IntVector extends Vector {

  public IntVector(int size, int incr) 
    { super(size,incr); }

  public int sub(int i) 
    { return( ((Integer) this.elementAt(i)).intValue()); }

  public void add(int x)
    { this.addElement(new Integer(x)); }
}
