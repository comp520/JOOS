// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.

import joos.lib.*;
import java.util.*;
import java.awt.*;
import java.applet.Applet;

public class AllEvents extends AllComponents {
  public AllEvents(String title) {
    super(title);
  }

  public boolean handleEvent(Event event) 
  { JoosEvent je;
    JoosConstants c;
    Object target;
    int id;
    Object arg;

    c = new JoosConstants();
    je = new JoosEvent(event);
    target = je.target();
    id = je.id();
    arg = je.arg();

    if (id == c.ACTION_EVENT())
      // Most components generate ACTION_EVENT
      // We test the target field to find out which component.
      if (target == textfield) 
        textarea.setText("Your name is: " + arg + "\n");
      else if (target == choice) 
        textarea.setText("Your favorite color is: " + arg + "\n"); 
      else if ((target == checkboxes.elementAt(0)) ||
               (target == checkboxes.elementAt(1)) ||
               (target == checkboxes.elementAt(2))
              ) 
        textarea.setText("Your favorite flavor is: " +
                         checkbox_group.getCurrent().getLabel() + "\n");
      else if (target == mylist)  
        textarea.setText("You double-clicked on: " + arg + "\n");
      else if (target == okay)  
        textarea.setText("Okay button clicked.\n");
      else if (target == cancel)  
        textarea.setText("Cancel button clicked.\n");
      else if (target instanceof MenuItem) 
        { // Since we didn't save references to each of the menu objects,
          // we check which one was pressed by comparing labels.
          // Note that we respond to these menu items by
          // popping up dialog boxes.
          if (((String)arg).equals("Quit")) 
            { YesNoDialog d;
              d = new ReallyQuitDialog(this, textarea);
              d.show();
              textarea.setText("You selected Quit\n");
            }
          else if (((String)arg).equals("Open")) 
            { textarea.setText("You selected Open.\n");
              // Use the dialog box 
              file_dialog.pack();  // bug workaround
              file_dialog.show();  // blocks until user selects a file
              textarea.setText("You selected file: " +  file_dialog.getFile());
            }
          else if (((String)arg).equals("About")) 
            { InfoDialog d;
              d = new InfoDialog(this, "About AWT Demo",
                             "This demo was written by David Flanagan\n" +
                             "Copyright (c) 1996 O'Reilly & Associates\n" +
                             "Modified by Laurie Hendren to work with JOOS");
              d.show();
              textarea.setText("You selected About.\n");
            }
         }
      else 
        textarea.setText("Unknown action event.");
    else if (id == c.LIST_SELECT())
      // Double-clicking on a list generates an action event.
      // But list selection and deselection are separate event types.
      textarea.setText("You selected: " + 
                       mylist.getItem(((Integer)arg).intValue()) + "\n");
    else if (id == c.LIST_DESELECT())
      textarea.setText("You deselected: " + 
                       mylist.getItem(((Integer)arg).intValue()) + "\n");
            
    // These are some events pertaining to the window itself.
    else if (id == c.WINDOW_DESTROY())
      textarea.setText("Window Destroy\n");
    else if (id == c.WINDOW_ICONIFY())
      textarea.setText("Window iconify\n");
    else if (id == c.WINDOW_DEICONIFY())
      textarea.setText("Window deiconify\n");
    else if (id == c.WINDOW_MOVED())
      textarea.setText("Window moved\n");
    // We print a message about each of these mouse and key events,
    // but return false after so that they can still
    // be properly handled by their correct recipient.
    else if (id == c.MOUSE_DOWN())
      { textarea.setText("Mouse down: [" + je.x() + "," + je.y() + "]\n");
        return(false);
      }
    else if (id == c.MOUSE_UP())
      { textarea.setText("Mouse up: [" + je.x() + "," + je.y() + "]\n");
        return(false);
      }
    else if (id == c.MOUSE_DRAG())
      { textarea.setText("Mouse drag: [" + je.x() + "," + je.y() + "]\n");
        return(false);
      }
    else if ((id == c.KEY_PRESS()) || (id == c.KEY_ACTION()))
      { textarea.setText("Key press\n");
        return(false);
      }
    else if ((id == c.KEY_RELEASE()) || (id == c.KEY_ACTION_RELEASE()))
      { textarea.setText("Key release\n");
        return(false);
      }
   // We ignore these event types.
   else if ((id == c.GOT_FOCUS()) || (id == c.LOST_FOCUS()) ||
            (id == c.MOUSE_ENTER()) || (id == c.MOUSE_EXIT()) ||
            (id == c.MOUSE_MOVE())) 
      return(false);
   else     
   // We shouldn't ever get this...
     textarea.setText("Unexpected Event type: " + event + "\n");
   return(true);
 }

 // Here's the method that lets us run and test this class.
 public static void main(String argv[]) {
   Frame f;
   f = new AllEvents("AWT Demo - with event handler");
   // f.pack();
   f.resize(450, 475);
   f.show();
 }
} 
