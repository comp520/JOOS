
import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class Tree {
  // Fields 
  protected Object value;
  protected Tree left;
  protected Tree right;
 
  // Constructor 
  public Tree(Object v, Tree l, Tree r)
    { super();
      value = v;
      left = l; 
      right = r;
    }

  // Methods
  public void setValue(Object newValue)
    { value = newValue; }

}
