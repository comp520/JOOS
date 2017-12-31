import java.awt.*;
import java.net.*;
import joos.lib.*;

// A helper class.  Just like java.awt.Rectangle, but with a new URL field.
// The constructor lets us create them from parameter specifications.
public class ImagemapRectangle extends JoosRectangle {
    
    protected URL myurl;

    public ImagemapRectangle(int x, int y, int w, int h, URL url) {
        super(x, y, w, h);
        myurl = url;
    }

    public URL url() { return(myurl); }

    public void setURL(URL u) { myurl = u; }
}
