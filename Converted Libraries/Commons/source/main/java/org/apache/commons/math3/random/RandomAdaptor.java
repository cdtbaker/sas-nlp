package org.apache.commons.math3.random;
import java.util.Random;
/** 
 * Extension of <code>java.util.Random</code> wrapping a{@link RandomGenerator}.
 * @since 1.1
 * @version $Id: RandomAdaptor.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class RandomAdaptor extends Random implements RandomGenerator {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=2306581345647615033L;
  /** 
 * Wrapped randomGenerator instance 
 */
  private final RandomGenerator randomGenerator;
  /** 
 * Prevent instantiation without a generator argument
 */
  @SuppressWarnings("unused") private RandomAdaptor(){
    randomGenerator=null;
  }
  /** 
 * Construct a RandomAdaptor wrapping the supplied RandomGenerator.
 * @param randomGenerator  the wrapped generator
 */
  public RandomAdaptor(  RandomGenerator randomGenerator){
    this.randomGenerator=randomGenerator;
  }
  /** 
 * Factory method to create a <code>Random</code> using the supplied
 * <code>RandomGenerator</code>.
 * @param randomGenerator  wrapped RandomGenerator instance
 * @return a Random instance wrapping the RandomGenerator
 */
  public static Random createAdaptor(  RandomGenerator randomGenerator){
    return new RandomAdaptor(randomGenerator);
  }
  /** 
 * Returns the next pseudorandom, uniformly distributed
 * <code>boolean</code> value from this random number generator's
 * sequence.
 * @return  the next pseudorandom, uniformly distributed
 * <code>boolean</code> value from this random number generator's
 * sequence
 */
  @Override public boolean nextBoolean(){
    return randomGenerator.nextBoolean();
  }
  /** 
 * Generates random bytes and places them into a user-supplied
 * byte array.  The number of random bytes produced is equal to
 * the length of the byte array.
 * @param bytes the non-null byte array in which to put the
 * random bytes
 */
  @Override public void nextBytes(  byte[] bytes){
    randomGenerator.nextBytes(bytes);
  }
  /** 
 * Returns the next pseudorandom, uniformly distributed
 * <code>double</code> value between <code>0.0</code> and
 * <code>1.0</code> from this random number generator's sequence.
 * @return  the next pseudorandom, uniformly distributed
 * <code>double</code> value between <code>0.0</code> and
 * <code>1.0</code> from this random number generator's sequence
 */
  @Override public double nextDouble(){
    return randomGenerator.nextDouble();
  }
  /** 
 * Returns the next pseudorandom, uniformly distributed <code>float</code>
 * value between <code>0.0</code> and <code>1.0</code> from this random
 * number generator's sequence.
 * @return  the next pseudorandom, uniformly distributed <code>float</code>
 * value between <code>0.0</code> and <code>1.0</code> from this
 * random number generator's sequence
 */
  @Override public float nextFloat(){
    return randomGenerator.nextFloat();
  }
  /** 
 * Returns the next pseudorandom, Gaussian ("normally") distributed
 * <code>double</code> value with mean <code>0.0</code> and standard
 * deviation <code>1.0</code> from this random number generator's sequence.
 * @return  the next pseudorandom, Gaussian ("normally") distributed
 * <code>double</code> value with mean <code>0.0</code> and
 * standard deviation <code>1.0</code> from this random number
 * generator's sequence
 */
  @Override public double nextGaussian(){
    return randomGenerator.nextGaussian();
  }
  /** 
 * Returns the next pseudorandom, uniformly distributed <code>int</code>
 * value from this random number generator's sequence.
 * All 2<font size="-1"><sup>32</sup></font> possible <tt>int</tt> values
 * should be produced with  (approximately) equal probability.
 * @return the next pseudorandom, uniformly distributed <code>int</code>
 * value from this random number generator's sequence
 */
  @Override public int nextInt(){
    return randomGenerator.nextInt();
  }
  /** 
 * Returns a pseudorandom, uniformly distributed <tt>int</tt> value
 * between 0 (inclusive) and the specified value (exclusive), drawn from
 * this random number generator's sequence.
 * @param n the bound on the random number to be returned.  Must be
 * positive.
 * @return  a pseudorandom, uniformly distributed <tt>int</tt>
 * value between 0 (inclusive) and n (exclusive).
 * @throws IllegalArgumentException  if n is not positive.
 */
  @Override public int nextInt(  int n){
    return randomGenerator.nextInt(n);
  }
  /** 
 * Returns the next pseudorandom, uniformly distributed <code>long</code>
 * value from this random number generator's sequence.  All
 * 2<font size="-1"><sup>64</sup></font> possible <tt>long</tt> values
 * should be produced with (approximately) equal probability.
 * @return  the next pseudorandom, uniformly distributed <code>long</code>
 * value from this random number generator's sequence
 */
  @Override public long nextLong(){
    return randomGenerator.nextLong();
  }
  /** 
 * {@inheritDoc} 
 */
  public void setSeed(  int seed){
    if (randomGenerator != null) {
      randomGenerator.setSeed(seed);
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public void setSeed(  int[] seed){
    if (randomGenerator != null) {
      randomGenerator.setSeed(seed);
    }
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void setSeed(  long seed){
    if (randomGenerator != null) {
      randomGenerator.setSeed(seed);
    }
  }
}
