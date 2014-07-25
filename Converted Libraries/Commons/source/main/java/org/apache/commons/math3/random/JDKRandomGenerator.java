package org.apache.commons.math3.random;
import java.util.Random;
/** 
 * Extension of <code>java.util.Random</code> to implement{@link RandomGenerator}.
 * @since 1.1
 * @version $Id: JDKRandomGenerator.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class JDKRandomGenerator extends Random implements RandomGenerator {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=-7745277476784028798L;
  /** 
 * {@inheritDoc} 
 */
  public void setSeed(  int seed){
    setSeed((long)seed);
  }
  /** 
 * {@inheritDoc} 
 */
  public void setSeed(  int[] seed){
    final long prime=4294967291l;
    long combined=0l;
    for (    int s : seed) {
      combined=combined * prime + s;
    }
    setSeed(combined);
  }
}
