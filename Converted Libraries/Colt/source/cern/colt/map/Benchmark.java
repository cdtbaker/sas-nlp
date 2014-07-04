package cern.colt.map;
import cern.colt.Timer;
/** 
 * Benchmarks the classes of this package.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class Benchmark extends Object {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected Benchmark(){
  }
  /** 
 */
  public static void benchmark(  int runs,  int size,  String kind){
    QuickOpenIntIntHashMap map;
    System.out.println("initializing...");
    map=new QuickOpenIntIntHashMap();
    for (int i=0; i < size; i++) {
      map.put(i,i);
    }
    Runtime.getRuntime().gc();
    try {
      Thread.currentThread().sleep(1000);
    }
 catch (    InterruptedException exc) {
    }
    ;
    System.out.println("Now benchmarking...");
    int s=0;
    Timer timer0=new Timer();
    Timer timer1=new Timer();
    Timer timer2=new Timer();
    for (int run=runs; --run >= 0; ) {
      if (kind.equals("add")) {
        map.clear();
        timer0.start();
        for (int i=size; --i >= 0; ) {
          map.put(i,i);
        }
        timer0.stop();
      }
      if (kind.equals("get")) {
        timer0.start();
        for (int i=size; --i >= 0; ) {
          s+=map.get(i);
        }
        timer0.stop();
      }
 else {
        timer1.start();
        map.rehash(PrimeFinder.nextPrime(size * 2));
        timer1.stop();
        timer2.start();
        map.rehash(PrimeFinder.nextPrime((int)(size * 1.5)));
        timer2.stop();
      }
    }
    System.out.println("adding: " + timer0);
    System.out.println("growing: " + timer1);
    System.out.println("shrinking: " + timer2);
    System.out.println("total: " + (timer1.plus(timer2)));
    System.out.print(s);
  }
  /** 
 * Tests various methods of this class.
 */
  public static void main(  String args[]){
    int runs=Integer.parseInt(args[0]);
    int size=Integer.parseInt(args[1]);
    String kind=args[2];
    benchmark(runs,size,kind);
  }
  /** 
 */
  public static void test2(  int length){
    cern.jet.random.Uniform uniform=new cern.jet.random.Uniform(new cern.jet.random.engine.MersenneTwister());
    int[] keys=new int[length];
    int to=10000000;
    for (int i=0; i < length; i++) {
      keys[i]=uniform.nextIntFromTo(0,to);
    }
    int[] values=(int[])keys.clone();
    int size=keys.length;
    AbstractIntIntMap map=new OpenIntIntHashMap();
    for (int i=0; i < keys.length; i++) {
      map.put(keys[i],(int)values[i]);
    }
    int sum=0;
    for (int i=0; i < keys.length; i++) {
      sum+=map.get(keys[i]);
    }
    System.out.println(map);
    System.out.println(sum);
    System.out.println("\n\n");
  }
}
