package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.DimensionMismatchException;
/** 
 * This class implements Hilbert Matrices as {@link RealLinearOperator}. 
 */
public class HilbertMatrix extends RealLinearOperator {
  /** 
 * The size of the matrix. 
 */
  private final int n;
  /** 
 * Creates a new instance of this class.
 * @param n Size of the matrix to be created..
 */
  public HilbertMatrix(  final int n){
    this.n=n;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public int getColumnDimension(){
    return n;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public int getRowDimension(){
    return n;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public RealVector operate(  final RealVector x){
    if (x.getDimension() != n) {
      throw new DimensionMismatchException(x.getDimension(),n);
    }
    final double[] y=new double[n];
    for (int i=0; i < n; i++) {
      double pos=0.;
      double neg=0.;
      for (int j=0; j < n; j++) {
        final double xj=x.getEntry(j);
        final double coeff=1. / (i + j + 1.);
        if (xj > 0.) {
          pos+=coeff * xj;
        }
 else {
          neg+=coeff * xj;
        }
      }
      y[i]=pos + neg;
    }
    return new ArrayRealVector(y,false);
  }
}
