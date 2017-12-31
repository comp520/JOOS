// Adapted from Java: How to Program, by Deitel and Deitel,
// Fig. 9.36: DrawPoly.java
// Drawing polygons

import java.awt.*;
import java.applet.Applet;

public class DrawPoly extends Applet {

   // create references to polygons
   protected Polygon p1, p2;

   public DrawPoly() { super(); }

   public void init()
   {
      // instantiate polygon objects
      p1 = new Polygon();
      p2 = new Polygon();

      // add points to p1
      p1.addPoint( 10, 10 );
      p1.addPoint( 10, 100 );
      p1.addPoint( 50, 10 );

      // add points to p2
      p2.addPoint( 240, 50 );
      p2.addPoint( 260, 70 );
      p2.addPoint( 250, 90 );
      p2.addPoint( 240, 50 );
   }

   public void paint( Graphics g )
   {
      // draw a filled polygon object
      g.fillPolygon( p1 );

      // draw a polygon object
      g.drawPolygon( p2 );


      // now try out copying an area
      g.copyArea(0,0,100,100,0,110);

   }
}
