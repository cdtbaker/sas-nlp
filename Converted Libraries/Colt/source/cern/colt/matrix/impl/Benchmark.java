package cern.colt.matrix.impl;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
/** 
 * Benchmarks the performance of matrix algorithms.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
class Benchmark {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected Benchmark(){
    throw new RuntimeException("Non instantiable");
  }
  /** 
 * Runs a bench on matrices holding double elements.
 */
  public static void benchmark(  int runs,  int size,  String kind,  boolean print,  int initialCapacity,  double minLoadFactor,  double maxLoadFactor,  double percentNonZero){
    cern.colt.Timer timer1=new cern.colt.Timer();
    cern.colt.Timer timer2=new cern.colt.Timer();
    cern.colt.Timer timer3=new cern.colt.Timer();
    cern.colt.Timer timer4=new cern.colt.Timer();
    cern.colt.Timer timer5=new cern.colt.Timer();
    cern.colt.Timer timer6=new cern.colt.Timer();
    DoubleMatrix2D matrix=null;
    if (kind.equals("sparse"))     matrix=new SparseDoubleMatrix2D(size,size,initialCapacity,minLoadFactor,maxLoadFactor);
 else     if (kind.equals("dense"))     matrix=cern.colt.matrix.DoubleFactory2D.dense.make(size,size);
 else     throw new RuntimeException("unknown kind");
    System.out.println("\nNow initializing...");
    double value=2;
    DoubleMatrix2D tmp=DoubleFactory2D.dense.sample(matrix.rows(),matrix.columns(),value,percentNonZero);
    matrix.assign(tmp);
    tmp=null;
    System.out.println("\ntesting...");
    if (print)     System.out.println(matrix);
    DoubleMatrix2D dense=DoubleFactory2D.dense.make(size,size);
    dense.assign(matrix);
    if (!dense.equals(matrix))     throw new InternalError();
    DoubleMatrix2D ADense=dense.copy();
    DoubleMatrix2D BDense=dense.copy();
    DoubleMatrix2D CDense=dense.copy();
    ADense.zMult(BDense,CDense);
    System.out.println("\nNext testing...");
{
      DoubleMatrix2D A=matrix.copy();
      DoubleMatrix2D B=matrix.copy();
      DoubleMatrix2D C=matrix.copy();
      A.zMult(B,C);
      if (!(C.equals(CDense)))       throw new InternalError();
      C.assign(matrix);
      System.out.println("\nNow benchmarking...");
      timer3.start();
      for (int i=0; i < runs; i++) {
        A.zMult(B,C);
      }
      timer3.stop();
      timer3.display();
      int m=A.rows();
      int n=A.columns();
      int p=B.rows();
      int reps=runs;
      double mflops=1.0e-3 * (2.0 * m * n* p* reps) / timer3.millis();
      System.out.println("mflops: " + mflops);
    }
    if (print)     System.out.println(matrix);
    System.out.println("bye bye.");
  }
  /** 
 */
  protected static double cubicLoop(  int runs,  int size){
    double a=1.123;
    double b=1.000000000012345;
    for (int r=0; r < runs; r++) {
      for (int i=size; --i >= 0; ) {
        for (int j=size; --j >= 0; ) {
          for (int k=size; --k >= 0; ) {
            a*=b;
          }
        }
      }
    }
    return a;
  }
  /** 
 * Benchmarks various matrix methods.
 */
  public static void main(  String args[]){
    int runs=Integer.parseInt(args[0]);
    int rows=Integer.parseInt(args[1]);
    int columns=Integer.parseInt(args[2]);
    String kind=args[3];
    int initialCapacity=Integer.parseInt(args[4]);
    double minLoadFactor=new Double(args[5]).doubleValue();
    double maxLoadFactor=new Double(args[6]).doubleValue();
    boolean print=args[7].equals("print");
    double initialValue=new Double(args[8]).doubleValue();
    int size=rows;
    benchmark(runs,size,kind,print,initialCapacity,minLoadFactor,maxLoadFactor,initialValue);
  }
}
