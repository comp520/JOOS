import joos.lib.*;

public class ReverseURL {

  public ReverseURL() { super(); }

  public static void main(String argv[])
    { JoosIO f;
      JoosURL url;
      String toReverse,inputLine;

      f = new JoosIO();
      f.print("Please enter a string you want reversed: ");
      toReverse = f.readLine();

      // make the url 
      url = new JoosURL(null,"http://java.sun.com/cgi-bin/backwards");
      // try to open a connection for the url
      if (url.openConnection())
        { // connection opened ok, now try to open the output stream
          // send into to the cgi form
          if (url.openOutputStream())
            { // output stream ok, send the string to reverse
              url.println("string=" + toReverse);
              // close output stream
              url.closeOutputStream();
            } 

          //  now try to open the input stream, get text back from cgi form 
          if (url.openInputStream())
            { // input stream opened ok, now read each line and echo 
              while((inputLine = url.readLine()) != null)
                f.println(inputLine);
              // close the input stream
              url.closeInputStream();
            }
         }

       // if the error log is null, then no errors occurred
       if (url.getErrorLog() != null)
         { f.println("***** Errors in this session were: ");
           f.println(url.getErrorLog());
         }
    }
} 
