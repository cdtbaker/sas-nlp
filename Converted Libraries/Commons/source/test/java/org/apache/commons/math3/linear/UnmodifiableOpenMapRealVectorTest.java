package org.apache.commons.math3.linear;
/** 
 * This is an implementation of {@link UnmodifiableRealVectorAbstractTest} for
 * unmodifiable views of {@link OpenMapRealVector}.
 * @version $Id$
 */
public class UnmodifiableOpenMapRealVectorTest extends UnmodifiableRealVectorAbstractTest {
  /** 
 * To ensure sufficient sparsity. 
 */
  public static final double PROBABILITY_OF_ZERO=0.5;
  /** 
 * Returns a random vector of type {@link ArrayRealVector}.
 * @return a new random {@link ArrayRealVector}.
 */
  @Override public RealVector createVector(){
    OpenMapRealVector v=new OpenMapRealVector(DIM,EPS);
    for (int i=0; i < DIM; i++) {
      if (RANDOM.nextDouble() > PROBABILITY_OF_ZERO) {
        v.setEntry(i,RANDOM.nextDouble());
      }
    }
    return v;
  }
}
