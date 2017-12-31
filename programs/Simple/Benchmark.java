import joos.lib.*;

public abstract class Benchmark {

  protected JoosSystem s;

  public Benchmark() { 
    super(); 
    s = new JoosSystem(); 
  } 

  public abstract void benchmark();

  public int myrepeat(int count)
  { int start; 
    int i;

    start = s.currentTimeMillis();
    i = 0;
    while (i < count)
      { this.benchmark();
        i = i + 1;
      }
    return(s.currentTimeMillis() - start);
  }

}
