package org.apache.commons.math3.linear;
/** 
 * This is an implementation of {@link UnmodifiableRealVectorAbstractTest} for
 * unmodifiable views of {@link ArrayRealVectorTest}.
 * @version $Id$
 */
public class UnmodifiableArrayRealVectorTest extends UnmodifiableRealVectorAbstractTest {
  /** 
 * Returns a random vector of type {@link ArrayRealVector}.
 * @return a new random {@link ArrayRealVector}.
 */
  @Override public RealVector createVector(){
    ArrayRealVector v=new ArrayRealVector(DIM);
    for (int i=0; i < DIM; i++) {
      v.setEntry(i,RANDOM.nextDouble());
    }
    return v;
  }
}
