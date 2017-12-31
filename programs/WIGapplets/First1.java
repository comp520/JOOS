import joos.lib.*;
import java.awt.*;
import java.applet.Applet;

public class First extends WigApplet {
    
   public First() 
   { super(); 
   }

   public void init()
   {  String ppp, qqq, rrr;
      JoosIO f;
   

      f = new JoosIO();

      // get the parameters, in this case there are two, ppp and qqq
      ppp = this.getParameter("ppp");
      f.println("ppp is: " + ppp);
      qqq = this.getParameter("qqq");
      f.println("qqq is: " + qqq);

      // do the computation
      rrr = ppp + qqq;
      f.println("rrr is: " + rrr);

      // collect all results by calling addResult for each result, in
      //   this case there is only one result rrr
      this.addResult("rrr",rrr);
     
      // open the URL for the WIG applet expecting the return value(s) 
      if (this.openURL()) f.println("URL opened ok");

      // return the results
      if (this.returnResults()) f.println("results returned ok");

      // now see if the form returns something
      this.getResponse();

   }

   public void paint (Graphics g)
     { g.drawString("Applet done.",0,50);
     }
}
