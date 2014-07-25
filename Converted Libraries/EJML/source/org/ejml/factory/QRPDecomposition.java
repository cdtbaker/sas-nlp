package org.ejml.factory;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
/** 
 * <p>
 * Similar to {@link QRDecomposition} but it can handle the rank deficient case by
 * performing column pivots during the decomposition. The final decomposition has the
 * following structure:<br>
 * A*P=Q*R<br>
 * where A is the original matrix, P is a pivot matrix, Q is an orthogonal matrix, and R is
 * upper triangular.
 * </p>
 * <p>
 * WARNING: You should always call {@link #setSingularThreshold(double)} before {@link #decompose(org.ejml.data.Matrix64F)}.
 * </p>
 * @author Peter Abeles
 */
public interface QRPDecomposition<T extends Matrix64F> extends QRDecomposition<T> {
  /** 
 * <p>
 * Specifies the threshold used to flag a column as being singular.  The optimal threshold (if one exists)
 * varies by the matrix being processed.  A reasonable value would be the maximum absolute value of the
 * matrix's elements multiplied by EPS:<br>
 * decomposition.setSingularThreshold(CommonOps.elementMaxAbs(A)*UtilEjml.EPS)
 * </p>
 * @param threshold Singular threshold.
 */
  public void setSingularThreshold(  double threshold);
  /** 
 * Returns the rank as determined by the algorithm.  This is dependent upon a fixed threshold
 * and might not be appropriate for some applications.
 * @return Matrix's rank
 */
  public int getRank();
  /** 
 * Ordering of each column after pivoting.   The current column i was original at column pivot[i].
 * @return Order of columns.
 */
  public int[] getPivots();
  /** 
 * Creates the pivot matrix.
 * @param P Optional storage for pivot matrix.  If null a new matrix will be created.
 * @return The pivot matrix.
 */
  public DenseMatrix64F getPivotMatrix(  DenseMatrix64F P);
}
