import java.util.*;
import joos.lib.*;

public class TestIOlist {

  public TestIOlist() { super(); }

  public static void main(String argv[])
    { JoosIO f;
      int i, sum, j;
      String line, next;
      StringTokenizer tokens;

      f = new JoosIO();
      f.println("Enter a list of integers, all on one line, ");
      f.println("   separated by commas or spaces ... "); 

      // read the whole line
      line = f.readLine();
      // tokenize, tokens separated by , or blanks, don't return delimiters
      tokens = new StringTokenizer(line,", ",false);

      // go through tokens, converting into integers and summing
      sum = 0;
      while (tokens.hasMoreElements()){
	j = 0;
        sum = sum + (new JoosString(tokens.nextToken(", "))).string2Int();
      }
      // print out the sum
      f.println("The sum is: " + sum);
    }
} 
