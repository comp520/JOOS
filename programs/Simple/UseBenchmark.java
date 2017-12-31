import joos.lib.*;

public class UseBenchmark {

  public UseBenchmark() { super(); }

  public static void main(String argv[])
    { ExtBenchmark b; 
      JoosIO f;
      int reps;
      int time;

      // correct one
      b = new ExtBenchmark();
      // error, can't call constructor of abstract class
      //b = new Benchmark();
      f = new JoosIO();

      f.print("Enter number of repetitions: ");
      reps = f.readInt();
      time = b.myrepeat(reps);
      f.println("time is " + time + " millisecs"); 
    }
} 
