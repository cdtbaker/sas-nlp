package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.decomposition.task.DeterminantTask;
import org.ojalgo.matrix.store.MatrixStore;
/** 
 * Cholesky: [A] = [L][L]<sup>T</sup>
 * <p>
 * If [A] is symmetric and positive definite then the general LU decomposition - [P][L][D][U] - becomes
 * [I][L][D][L]<sup>T</sup> (or [I][U]<sup>T</sup>[D][U]). [I] can be left out and [D] is normally split in halves and
 * merged with [L] (and/or [U]). We'll express it as [A] = [R]<sup>T</sup>[R].
 * </p>
 * <p>
 * A cholesky decomposition is still/also an LU decomposition where [P][L][D][U] => [R]<sup>T</sup>[R].
 * </p>
 * @author apete
 */
public interface Cholesky<N extends Number> extends MatrixDecomposition<N>, DeterminantTask<N> {
  /** 
 * To use the Cholesky decomposition rather than the LU decomposition the matrix must be symmetric and positive
 * definite. It is recommended that the decomposition algorithm checks for this during calculation. Possibly the
 * matrix could be assumed to be symmetric (to improve performance) but tests should be made to assure the matrix is
 * positive definite.
 * @return true if the tests did not fail.
 */
  public boolean isSPD();
  boolean compute(  final Access2D<?> matrix,  final boolean checkHermitian);
  N getDeterminant();
  MatrixStore<N> getL();
}
