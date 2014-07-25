package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.ArithmeticUtils;
/** 
 * This class implements inverses of Hilbert Matrices as{@link RealLinearOperator}.
 */
public class InverseHilbertMatrix extends RealLinearOperator {
  /** 
 * The size of the matrix. 
 */
  private final int n;
  /** 
 * Creates a new instance of this class.
 * @param n Size of the matrix to be created.
 */
  public InverseHilbertMatrix(  final int n){
    this.n=n;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public int getColumnDimension(){
    return n;
  }
  /** 
 * Returns the {@code (i, j)} entry of the inverse Hilbert matrix. Exact
 * arithmetic is used; in case of overflow, an exception is thrown.
 * @param i Row index (starts at 0).
 * @param j Column index (starts at 0).
 * @return The coefficient of the inverse Hilbert matrix.
 */
  public long getEntry(  final int i,  final int j){
    long val=i + j + 1;
    long aux=ArithmeticUtils.binomialCoefficient(n + i,n - j - 1);
    val=ArithmeticUtils.mulAndCheck(val,aux);
    aux=ArithmeticUtils.binomialCoefficient(n + j,n - i - 1);
    val=ArithmeticUtils.mulAndCheck(val,aux);
    aux=ArithmeticUtils.binomialCoefficient(i + j,i);
    val=ArithmeticUtils.mulAndCheck(val,aux);
    val=ArithmeticUtils.mulAndCheck(val,aux);
    return ((i + j) & 1) == 0 ? val : -val;
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
        final long coeff=getEntry(i,j);
        final double daux=coeff * xj;
        if (daux > 0.) {
          pos+=daux;
        }
 else {
          neg+=daux;
        }
      }
      y[i]=pos + neg;
    }
    return new ArrayRealVector(y,false);
  }
}
