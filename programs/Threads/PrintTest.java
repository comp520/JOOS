// From Java How to Program, by Deitel and Deitel
// Modified to work with JOOS.
// Fig. 13.3: PrintTest.java
// Show multiple threads printing at different intervals.

public class PrintTest {
   public PrintTest() { super(); }

   public static void main( String argv[] )
   {
      PrintThread thread1, thread2, thread3, thread4;

      thread1 = new PrintThread();
      thread2 = new PrintThread();
      thread3 = new PrintThread();
      thread4 = new PrintThread();

      thread1.start();
      thread2.start();
      thread3.start();
      thread4.start();
   }
}
