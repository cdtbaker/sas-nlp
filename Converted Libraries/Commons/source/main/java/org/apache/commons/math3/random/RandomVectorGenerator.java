package org.apache.commons.math3.random;
/** 
 * This interface represents a random generator for whole vectors.
 * @since 1.2
 * @version $Id: RandomVectorGenerator.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface RandomVectorGenerator {
  /** 
 * Generate a random vector.
 * @return a random vector as an array of double.
 */
  double[] nextVector();
}
