package cern.jet.random.engine;
/** 
 * Abstract base class for uniform pseudo-random number generating engines.
 * <p>
 * Most probability distributions are obtained by using a <b>uniform</b> pseudo-random number generation engine 
 * followed by a transformation to the desired distribution.
 * Thus, subclasses of this class are at the core of computational statistics, simulations, Monte Carlo methods, etc.
 * <p>
 * Subclasses produce uniformly distributed <tt>int</tt>'s and <tt>long</tt>'s in the closed intervals <tt>[Integer.MIN_VALUE,Integer.MAX_VALUE]</tt> and <tt>[Long.MIN_VALUE,Long.MAX_VALUE]</tt>, respectively, 
 * as well as <tt>float</tt>'s and <tt>double</tt>'s in the open unit intervals <tt>(0.0f,1.0f)</tt> and <tt>(0.0,1.0)</tt>, respectively.
 * <p>
 * Subclasses need to override one single method only: <tt>nextInt()</tt>.
 * All other methods generating different data types or ranges are usually layered upon <tt>nextInt()</tt>.
 * <tt>long</tt>'s are formed by concatenating two 32 bit <tt>int</tt>'s.
 * <tt>float</tt>'s are formed by dividing the interval <tt>[0.0f,1.0f]</tt> into 2<sup>32</sup> sub intervals, then randomly choosing one subinterval.
 * <tt>double</tt>'s are formed by dividing the interval <tt>[0.0,1.0]</tt> into 2<sup>64</sup> sub intervals, then randomly choosing one subinterval.
 * <p>
 * Note that this implementation is <b>not synchronized</b>.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see MersenneTwister
 * @see MersenneTwister64
 * @see java.util.Random
 */
public abstract class RandomEngine extends cern.colt.PersistentObject implements cern.colt.function.DoubleFunction, cern.colt.function.IntFunction {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected RandomEngine(){
  }
  /** 
 * Equivalent to <tt>raw()</tt>.
 * This has the effect that random engines can now be used as function objects, returning a random number upon function evaluation.
 */
  public double apply(  double dummy){
    return raw();
  }
  /** 
 * Equivalent to <tt>nextInt()</tt>.
 * This has the effect that random engines can now be used as function objects, returning a random number upon function evaluation.
 */
  public int apply(  int dummy){
    return nextInt();
  }
  /** 
 * Constructs and returns a new uniform random number engine seeded with the current time.
 * Currently this is {@link cern.jet.random.engine.MersenneTwister}.
 */
  public static RandomEngine makeDefault(){
    return new cern.jet.random.engine.MersenneTwister((int)System.currentTimeMillis());
  }
  /** 
 * Returns a 64 bit uniformly distributed random number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
 */
  public double nextDouble(){
    double nextDouble;
    do {
      nextDouble=((double)nextLong() - -9.223372036854776E18) * 5.421010862427522E-20;
    }
 while (!(nextDouble > 0.0 && nextDouble < 1.0));
    return nextDouble;
  }
  /** 
 * Returns a 32 bit uniformly distributed random number in the open unit interval <code>(0.0f,1.0f)</code> (excluding 0.0f and 1.0f).
 */
  public float nextFloat(){
    float nextFloat;
    do {
      nextFloat=(float)raw();
    }
 while (nextFloat >= 1.0f);
    return nextFloat;
  }
  /** 
 * Returns a 32 bit uniformly distributed random number in the closed interval <tt>[Integer.MIN_VALUE,Integer.MAX_VALUE]</tt> (including <tt>Integer.MIN_VALUE</tt> and <tt>Integer.MAX_VALUE</tt>);
 */
  public abstract int nextInt();
  /** 
 * Returns a 64 bit uniformly distributed random number in the closed interval <tt>[Long.MIN_VALUE,Long.MAX_VALUE]</tt> (including <tt>Long.MIN_VALUE</tt> and <tt>Long.MAX_VALUE</tt>).
 */
  public long nextLong(){
    return ((nextInt() & 0xFFFFFFFFL) << 32) | ((nextInt() & 0xFFFFFFFFL));
  }
  /** 
 * Returns a 32 bit uniformly distributed random number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
 */
  public double raw(){
    int nextInt;
    do {
      nextInt=nextInt();
    }
 while (nextInt == 0);
    return (double)(nextInt & 0xFFFFFFFFL) * 2.3283064365386963E-10;
  }
}
