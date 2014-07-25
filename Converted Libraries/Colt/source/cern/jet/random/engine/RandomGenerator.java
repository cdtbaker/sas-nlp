package cern.jet.random.engine;
/** 
 * Interface for uniform pseudo-random number generators.
 */
public interface RandomGenerator {
  /** 
 * Returns a 32 bit uniformly distributed random number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
 */
  public double raw();
  /** 
 * Returns a 64 bit uniformly distributed random number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
 */
  public double nextDouble();
  /** 
 * Returns a 32 bit uniformly distributed random number in the closed interval <tt>[Integer.MIN_VALUE,Integer.MAX_VALUE]</tt> (including <tt>Integer.MIN_VALUE</tt> and <tt>Integer.MAX_VALUE</tt>);
 */
  public int nextInt();
  /** 
 * Returns a 64 bit uniformly distributed random number in the closed interval <tt>[Long.MIN_VALUE,Long.MAX_VALUE]</tt> (including <tt>Long.MIN_VALUE</tt> and <tt>Long.MAX_VALUE</tt>).
 */
  public long nextLong();
  /** 
 * Returns a 32 bit uniformly distributed random number in the open unit interval <code>(0.0f,1.0f)</code> (excluding 0.0f and 1.0f).
 */
  public float nextFloat();
}
