// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Adapted to work with joos.

import joos.lib.*;
import java.awt.*;
import java.util.Vector;

public class AllComponents extends Frame {

    protected MenuBar menubar;            // the menubar
    protected Menu file, help;            // menu panes
    protected Button okay, cancel;        // buttons
    protected List mylist;                // A list of choices
    protected Choice choice;              // A menu of choices
    protected CheckboxGroup checkbox_group; // A group of button choices
    protected Vector checkboxes;          // the buttons to choose from
    protected TextField textfield;        // One line of text input
    protected TextArea  textarea;         // A text window
    protected ScrollableScribble scribble;// An area to draw in.
    protected FileDialog file_dialog;

    protected Panel panel1, panel2; // Sub-containers for this stuff.
    protected Panel buttonpanel; 

    // The layout manager for each of the containers.
    protected GridBagLayout gridbag; 
    
    // JoosConstants
    protected JoosConstants c;

    public AllComponents(String title) {
        super(title);
        
        c = new JoosConstants();
        gridbag = new GridBagLayout();
        // Create the menubar.  Tell the frame about it.
        menubar = new MenuBar();
        this.setMenuBar(menubar);
        // Create the file menu.  Add two items to it.  Add to menubar.
        file = new Menu("File",false);
        file.add(new MenuItem("Open"));
        file.add(new MenuItem("Quit"));
        menubar.add(file);
        // Create Help menu; add an item; add to menubar
        help = new Menu("Help",true);
        help.add(new MenuItem("About"));
        menubar.add(help);
        // Display the help menu in a special reserved place.
        menubar.setHelpMenu(help);
        
        // Create pushbuttons
        okay = new Button("Okay");
        cancel = new Button("Cancel");
        
        // Create a menu of choices
        choice = new Choice();
        choice.addItem("red");
        choice.addItem("green");
        choice.addItem("blue");
        
        // Create checkboxes, and group them.
        checkbox_group = new CheckboxGroup();
        checkboxes = new Vector(3,3);
        checkboxes.addElement(new Checkbox("vanilla", checkbox_group, false));
        checkboxes.addElement(new Checkbox("chocolate", checkbox_group, true));
        checkboxes.addElement(new Checkbox("strawberry", checkbox_group, false));
        
        // Create a list of choices.
        mylist = new List(4, true);
        mylist.addItem("JOOS"); mylist.addItem("Java"); 
        mylist.addItem("C"); mylist.addItem("C++");
        mylist.addItem("Smalltalk"); mylist.addItem("Lisp");
        mylist.addItem("Modula-3"); mylist.addItem("Forth");
        
        // Create a one-line text field, and multi-line text area.
        textfield = new TextField("Laurie",15);
        textfield.setEditable(true);
        textarea = new TextArea("",6, 40);
        textarea.setEditable(false);
        
        // Create a scrolling canvas to scribble in. 
        scribble = new ScrollableScribble();
        
        // Create a file selection dialog box
        file_dialog = new FileDialog(this, "Open File", c.LOAD());
        
        // Create a Panel to contain all the components along the
        // left hand side of the window.  Use a GridBagLayout for it.
        panel1 = new Panel();
        panel1.setLayout(gridbag);
        
        // Use several versions of the constrain() convenience method
        // to add components to the panel and to specify their 
        //GridBagConstraints values.
        this.constrainShort(panel1, new Label("Name:",c.LABEL_LEFT()),
                            0, 0, 1, 1);
        this.constrainShort(panel1, textfield, 0, 1, 1, 1);
        this.constrainMed(panel1, new Label("Favorite color:",c.LABEL_LEFT()),
                          0, 2, 1, 1, 10, 0, 0, 0);
        this.constrainShort(panel1, choice, 0, 3, 1, 1);
        this.constrainMed(panel1, new Label("Favorite flavor:",c.LABEL_LEFT()),
                          0, 4, 1, 1, 10, 0, 0, 0);
        this.constrainShort(panel1, (Checkbox) checkboxes.elementAt(0),
                            0, 5, 1, 1);
        this.constrainShort(panel1, (Checkbox) checkboxes.elementAt(1), 
                            0, 6, 1, 1);
        this.constrainShort(panel1, (Checkbox) checkboxes.elementAt(2),
                            0, 7, 1, 1);
        this.constrainMed(panel1, 
                          new Label("Favorite languages:",c.LABEL_LEFT()), 
                          0, 8, 1, 1, 10, 0, 0, 0);
        this.constrainAll(panel1, mylist, 0, 9, 1, 3, 
              c.GRIDBAGCONSTRAINTS_VERTICAL(),
              c.NORTHWEST(), 
              new JoosFraction(0,1), new JoosFraction(1,1), 
              0, 0, 0, 0);
        
        // Create a panel for the items along the right side.
        // Use a GridBagLayout, and arrange items with constrain(), as above.
        panel2 = new Panel();
        panel2.setLayout(gridbag);
        
        this.constrainShort(panel2, new Label("Messages",c.LABEL_LEFT()), 
                            0, 0, 1, 1);
        this.constrainAll(panel2, textarea, 0, 1, 1, 3, 
              c.GRIDBAGCONSTRAINTS_HORIZONTAL(),
              c.NORTH(), 
              new JoosFraction(1,1), new JoosFraction(0,1), 
              0, 0, 0, 0);
        this.constrainMed(panel2, new Label("Diagram",c.LABEL_LEFT()), 
                          0, 4, 1, 1, 10, 0, 0, 0);
        this.constrainAll(panel2, scribble, 0, 5, 1, 5, c.BOTH(),
              c.GRIDBAGCONSTRAINTS_CENTER(), 
              new JoosFraction(1,1), new JoosFraction(1,1), 
              0, 0, 0, 0);
        
        // Do the same for the buttons along the bottom.
        buttonpanel = new Panel();
        buttonpanel.setLayout(gridbag);
        this.constrainAll(buttonpanel, okay, 0, 0, 1, 1, c.NONE(),
              c.GRIDBAGCONSTRAINTS_CENTER(), 
              new JoosFraction(3,10), new JoosFraction(0,1), 
              0, 0, 0, 0);
        this.constrainAll(buttonpanel, cancel, 1, 0, 1, 1, c.NONE(),
              c.GRIDBAGCONSTRAINTS_CENTER(), 
              new JoosFraction(3,10), new JoosFraction(0,1), 
              0, 0, 0, 0);
        
        // Finally, use a GridBagLayout to arrange the panels themselves
        this.setLayout(gridbag);
        // And add the panels to the toplevel window
        this.constrainAll(this, panel1, 0, 0, 1, 1, 
              c.GRIDBAGCONSTRAINTS_VERTICAL(), 
              c.NORTHWEST(), 
              new JoosFraction(0,1), new JoosFraction(1,1), 
              10, 10, 5, 5);
        this.constrainAll(this, panel2, 1, 0, 1, 1, c.BOTH(),
              c.GRIDBAGCONSTRAINTS_CENTER(), 
              new JoosFraction(1,1), new JoosFraction(1,1),
              10, 10, 5, 10);
        this.constrainAll(this, buttonpanel, 0, 1, 2, 1, 
              c.GRIDBAGCONSTRAINTS_HORIZONTAL(),
              c.GRIDBAGCONSTRAINTS_CENTER(), 
              new JoosFraction(1,1), new JoosFraction(0,1),
              5, 0, 0, 0);
    }
    
    public void constrainAll(Container container, Component component, 
                  int grid_x, int grid_y, int grid_width, int grid_height,
                  int fill, int anchor, 
                  JoosFraction  weight_x,  JoosFraction weight_y,
                  int top, int left, int bottom, int right)
    {
        JoosGridBagConstraints g;
        
        g = new JoosGridBagConstraints(new GridBagConstraints());
        g.setGridX(grid_x);
        g.setGridY(grid_y); 
        g.setGridWidth(grid_width); 
        g.setGridHeight(grid_height);
        g.setFill(fill); 
        g.setAnchor(anchor);
        g.setWeightX(weight_x);
        g.setWeightY(weight_y);
        if (top+bottom+left+right > 0)
            g.setInsets(new Insets(top, left, bottom, right));
        
        ((GridBagLayout)container.getLayout()).setConstraints(component, 
                                    (GridBagConstraints) g.getConstraints());
        container.add(component);
    }
    
    public void constrainShort(
             Container container, Component component, 
             int grid_x, int grid_y, int grid_width, int grid_height) 
    { this.constrainAll(container, component, grid_x, grid_y, 
              grid_width, grid_height, c.NONE(), 
              c.NORTHWEST(), 
              new JoosFraction(0,1), new JoosFraction(0,1), 
              0, 0, 0, 0);
    }
    
    public void constrainMed(Container container, Component component, 
                  int grid_x, int grid_y, int grid_width, int grid_height,
                  int top, int left, int bottom, int right) {
        this.constrainAll(container, component, grid_x, grid_y, 
              grid_width, grid_height, c.NONE(), 
              c.NORTHWEST(), 
              new JoosFraction(0,1),  new JoosFraction(0,1),
              top, left, bottom, right);
    }
    

    public static void main(String argv[]) 
    { Frame f;
        f = new AllComponents("AWT Demo - no event handler");
        //f.pack();
        f.resize(450, 475);
        f.show();
    }
}
