package cern.jet.random;
/** 
 * Abstract base class for all discrete distributions.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public abstract class AbstractDiscreteDistribution extends AbstractDistribution {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected AbstractDiscreteDistribution(){
  }
  /** 
 * Returns a random number from the distribution; returns <tt>(double) nextInt()</tt>.
 */
  public double nextDouble(){
    return (double)nextInt();
  }
  /** 
 * Returns a random number from the distribution.
 */
  public abstract int nextInt();
}
