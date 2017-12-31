// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Adpated to work with JOOS.

/**
 * This applet displays an animation.   It uses the MediaTracker to
 * load the images and verify that there are no errors. 
 **/
import java.applet.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import joos.lib.*;

public class Animator extends Applet {
    protected Vector images;
    protected Thread animator_thread;
    protected MediaTracker tracker;

    public Animator() { super(); animator_thread = null; }


    // Read the basename and num_images parameters.
    // Then read in the images, using the specified base name.
    // For example, if basename is images/anim, read images/anim0, 
    // images/anim1, etc.  These are relative to the current document URL.
    public void init() {
        String basename;
        String num;
        int i, num_images;
        basename = this.getParameter("basename");
        num = this.getParameter("numimages");
        new JoosIO().println("num is " + num + "basename is " + basename);
        num_images = new JoosString(num).string2Int();

        // getImage() creates an Image object from a URL specification,
        //   but doesn't actually load the images; that is done
        //   asynchronously.  Store all the images in a MediaTracker
        //   so we can wait until they have all loaded.
        tracker = new MediaTracker(this);
        images = new Vector(num_images,1);
        for(i = 0; i < num_images; i++) {
          images.addElement(this.getImage(this.getDocumentBase(), 
                                          basename + i));
          tracker.addImage((Image) images.elementAt(i),i);
        }
    }

    // This is the thread that runs the animation, and the methods
    // that start it and stop it.
    public void start() {
        if (animator_thread == null) {
            animator_thread = new Thread(new AnimatorRun(this,images,tracker));
            animator_thread.start();
        }
    }

    public void stop() {
        if ((animator_thread != null) && animator_thread.isAlive()) 
            animator_thread.stop();
        // We do this so the garbage collector can reclaim the Thread object.
        // Otherwise it might sit around in the Web browser for a long time.
        animator_thread = null;
    }

}
