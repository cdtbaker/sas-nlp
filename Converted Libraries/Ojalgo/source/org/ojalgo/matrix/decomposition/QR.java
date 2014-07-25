package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.decomposition.task.DeterminantTask;
import org.ojalgo.matrix.store.MatrixStore;
/** 
 * QR: [A] = [Q][R] Decomposes [this] into [Q] and [R] where:
 * <ul>
 * <li>[Q] is an orthogonal matrix (orthonormal columns). It has the same number of rows as [this].</li>
 * <li>[R] is a right (upper) triangular matrix. It has the same number of columns as [this].</li>
 * <li>[this] = [Q][R]</li>
 * </ul>
 * Note: Either Q or R will be square. The interface does not specify which.
 * @author apete
 */
public interface QR<N extends Number> extends MatrixDecomposition<N>, DeterminantTask<N> {
  /** 
 * @param matrix A matrix to decompose
 * @return true if the computation suceeded; false if not
 */
  boolean compute(  Access2D<?> matrix,  boolean fullSize);
  N getDeterminant();
  MatrixStore<N> getQ();
  MatrixStore<N> getR();
  int getRank();
  /** 
 * The QR decompostion always exists, even if the matrix does not have full column rank, so the compute method will
 * never fail. The primary use of the QR decomposition is in the least squares solution of overdetermined systems of
 * simultaneous linear equations. This will fail if the matrix does not have full column rank. The rank must be
 * equal to the number of columns.
 */
  boolean isFullColumnRank();
}
