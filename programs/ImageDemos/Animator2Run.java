import joos.lib.*;
import java.awt.*;
import java.util.*;
import java.applet.*;

// note:  You must extend JoosRunnable, not Runnable which is an interface
public class Animator2Run extends JoosRunnable {

  // the Animator applet in which I am running
  protected Animator2 myApplet;

  // the vector of images to display
  protected Vector myImages;

  // the tracker used for the images
  protected MediaTracker myTracker;

  // the off-screen Graphics context
  protected Graphics myContext;

  public Animator2Run(Animator2 app, Vector images, 
                     MediaTracker tracker, Graphics context)
    { super();
      myApplet = app;
      myImages = images;
      myTracker = tracker;
      myContext = context;
    }

   public void run()
   {  int currentImage;
      JoosConstants c;
      JoosThread t;

      c = new JoosConstants();
      t = new JoosThread(null);

      // start at 1, 0 already displayed by applet
      currentImage = 1;
      while ( true ) {
         if ( myTracker.checkID( currentImage, true ) ) {
            // clear previous image from buffer
            myContext.fillRect( 0, 0, 160, 80 );
          
            // draw new image in buffer
            myContext.drawImage((Image) myImages.elementAt(currentImage),
                                0, 0, myApplet );
   
            currentImage++;
            currentImage = currentImage % myImages.size();
         }
         else   // browser fix: help load images 
            myApplet.postEvent( new Event(myApplet, c.MOUSE_ENTER(), "" ) );

         if (t.sleep(myApplet.getSleepTime()))
            myApplet.showStatus("Animation interrupted.");

         myApplet.repaint();  // display buffered image
      }
   }
}
