// From Java How to Program, by Deitel and Deitel
// Modified to work with JOOS.

// Load an array of images, loop through the array,
// and display each image. This version is customizeable.
//
// HTML parameter "sleeptime" is an integer indicating the
// number of milliseconds to sleep between images.
//
// HTML parameter "imagename" is the base name of the images
// that will be displayed (i.e., "deitel" is the base name
// for images "deitel0.gif," "deitel1.gif," etc.). The applet
// assumes that images are in an "images" subdirectory of
// the directory in which the applet resides.
//
// HTML parameter "totalimages" is an integer representing the
// total number of images in the animation. The applet assumes
// images are numbered from 0 to totalimages - 1.

import joos.lib.*;
import java.applet.Applet;
import java.awt.*;
import java.util.*;

public class Animator2 extends Applet {
   protected Vector images;
   protected int totalImages;    // total number of images
   protected int sleepTime;      // milliseconds to sleep
   protected String imageName;  // base name of images

   // The next two objects are for double-buffering
   protected Graphics gContext; // off-screen graphics context 
   protected Image buffer;      // buffer in which to draw image

   protected MediaTracker imageTracker;  // used to track images

   protected Thread animate;      // animation thread
   protected boolean suspended;   // toggle on/off

   protected int width, height;

   // GUI Components to allow dynamic speed changing
   protected Label sleepLabel;
   protected TextField sleepDisplay;
   protected Panel sleepStuff;

   // Joos Constants and File IO
   protected JoosConstants c;
   protected JoosIO f;

   // Constructor
   public Animator2() 
     { super();
       c = new JoosConstants();
       f = new JoosIO();
     }

   // Get value of sleep
   public int getSleepTime() { return (sleepTime); }

   // load the images when the applet begins executing
   public void init()
   {  int i;
      
      this.processHTMLParameters();

      if ( totalImages == 0 || imageName == null ) {
         this.showStatus( "Invalid parameters" );
         this.destroy();
      }

      images = new Vector(totalImages,1);

      imageTracker = new MediaTracker( this );

      for (i = 0; i < totalImages; i++ ) {
         images.addElement(this.getImage( this.getDocumentBase(),
                           "images/" + imageName + i + ".gif" ));
         // track loading image
         imageTracker.addImage( (Image) images.elementAt(i), i );
      }

      // wait for the first image,
      //   must use a Joos version of tracker to avoid catching
      //   interruped exception
      new JoosMediaTracker(imageTracker).waitForID(0);

      width = ((Image) images.elementAt(0)).getWidth( this );      
      height = ((Image) images.elementAt(0)).getHeight( this );
      this.resize( width, height + 30 );

      buffer = this.createImage( width, height ); 
      gContext = buffer.getGraphics(); 

      // set background of buffer to white
      gContext.setColor( c.white() );
      gContext.fillRect( 0, 0, 160, 80 );

      this.setLayout( new BorderLayout(10,10) );
      sleepLabel = new Label( "Sleep time",c.LABEL_CENTER() );
      sleepDisplay = new TextField("",5);
      sleepDisplay.setText( new Integer( sleepTime ).toString() );
      sleepStuff = new Panel();
      sleepStuff.add( sleepLabel );
      sleepStuff.add( sleepDisplay );
      new JoosContainer(this).addString( "South", sleepStuff );
   }

   // start the applet
   public void start()
   {
      // always start with 1st image
      gContext.drawImage((Image) images.elementAt(0), 0, 0, this );

      // create a new animation thread when user visits page
      if ( animate == null ) {
         animate = new Thread 
           (new Animator2Run(this,images,imageTracker,gContext));
         animate.start();
      }
   }

   // terminate animation thread when user leaves page
   public void stop()
   {
      if ( animate != null ) {
         animate.stop();
         animate = null;
      }
   }

   // display the image in the Applet's Graphics context
   public void paint( Graphics g )
   {
      g.drawImage( buffer, 0, 0, this );
   }

   // override update to eliminate flicker
   public void update( Graphics g )
   {
      this.paint( g );
   }

   public boolean action( Event e, Object o )
   {
      sleepTime = (new JoosString(o.toString())).string2Int();
      return true;
   }

   public boolean mouseDown( Event e, int x, int y )
   {
      if ( suspended ) {
         animate.resume();
         suspended = false;
      }
      else {
         animate.suspend();
         suspended = true;
      }

      return true;
   }

   public void processHTMLParameters()
   {
      String parameter;

      parameter = this.getParameter( "sleeptime" );
      if (parameter == null)
        sleepTime = 50;
      else
        sleepTime = (new JoosString(parameter)).string2Int();

      imageName = this.getParameter( "imagename" );

      parameter = this.getParameter( "totalimages" );
      
      if (parameter == null)
        totalImages = 0;
      else
        totalImages = (new JoosString(parameter)).string2Int();

   }
}
                                        
