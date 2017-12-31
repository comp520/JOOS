import joos.lib.*;

public class StrCat {

// The rule for exp1 + exp2, where the + is string concatenation is:
//
// new String(MakeAString(exp1).concat(MakeAString(exp2));
//
// where MakeAString should be defined as:
//
//       MakeAString(x)
//         { if (type(x) == int)
//             new Integer(x).toString();
//           else if (type(x) == bool)
//             new Boolean(x).toString();
//           else 
//             x.toString();
//         }

  public StrCat() { super(); } 

  public static void main(String argv[])
    { String s1,s2,s3,s4,s5;
      JoosIO f;

      f = new JoosIO();
      s1 = " Hello World ";

      // original Java code 
      s2 = s1 + 10;
      f.println(s2); 

      s3 = f + s1;
      f.println(s3);

      // the Joos way 
      s4 = new String(s1.toString()).concat(new Integer(10).toString());
      f.println(s4);

      s5 = new String(f.toString()).concat(s1.toString());
      f.println(s5);

    }
} 
