// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.
// Modified to work with joos.

import java.awt.*;

public class Main {

    public Main() { super(); }

    public static void main(String argv[]) 
    {
        Frame f;
        f = new AllEvents("AWT Demo");
        //f.pack();
        f.resize(450, 475);
        f.show();
    }
}
