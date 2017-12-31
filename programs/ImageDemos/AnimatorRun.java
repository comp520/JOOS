// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Adpated to work with JOOS.

import joos.lib.*;
import java.awt.*;
import java.util.*;
import java.applet.*;

// note:  You must extend JoosRunnable, not Runnable which is an interface
public class AnimatorRun extends JoosRunnable {

  // the Animator applet in which I am running 
  protected Animator myApplet;

  // the vector of images to display
  protected Vector myImages;

  // the tracker used for the images
  protected MediaTracker myTracker;

  public AnimatorRun(Animator app, Vector images, MediaTracker tracker)
    { super();
      myApplet = app;
      myImages = images;
      myTracker = tracker;
    }

  public void waitForLoading() 
    { int i,n;
      JoosMediaTracker m;  // need this guy to call waitForID
      m = new JoosMediaTracker(myTracker);
      n = myImages.size();
      for (i = 0; i < n; i++)
        { myApplet.showStatus("Loading image: " + i + "  ");
          m.waitForID(i);
          if (myTracker.isErrorID(i))
            { myApplet.showStatus("Error loading image " + i + " .. quitting");
              return;
            }
         } 
      myApplet.showStatus(n + " images loaded.");
     }
      
  // This is the body of the thread--the method that does the animation.
  public void run() {
    int current_image;
    this.waitForLoading();
    current_image = 0;
    while(true) 
      { current_image++;
        if (current_image >= myImages.size()) 
          current_image = 0;
        myApplet.getGraphics().drawImage(
                 (Image) myImages.elementAt(current_image),
                 0,0,myApplet);
        myApplet.getToolkit().sync();  // Force it to be drawn *now*.
        new JoosThread(null).sleep(200);
      }
  }
}
