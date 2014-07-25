package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.decomposition.task.DeterminantTask;
import org.ojalgo.matrix.store.ColumnsStore;
import org.ojalgo.matrix.store.IdentityStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.RowsStore;
/** 
 * LU: [A] = [L][U]
 * <p>
 * Decomposes [this] into [L] and [U] (with pivot order information in an int[]) where:
 * </p>
 * <ul>
 * <li>[L] is a unit lower (left) triangular matrix. It has the same number of rows as [this], and ones on the diagonal.
 * </li>
 * <li>[U] is an upper (right) triangular matrix. It has the same number of columns as [this].</li>
 * <li>[this] = [L][U] (with reordered rows according to the pivot order)</li>
 * </ul>
 * <p>
 * Note: The number of columns in [L] and the number of rows in [U] is not specified by this interface.
 * </p>
 * <p>
 * The LU decomposition always exists - the compute method should always succeed - even for non-square and/or singular
 * matrices. The primary use of the LU decomposition is in the solution of systems of simultaneous linear equations.
 * That will, however, only work for square non-singular matrices.
 * </p>
 * @author apete
 */
public interface LU<N extends Number> extends MatrixDecomposition<N>, DeterminantTask<N> {
  /** 
 * The normal {@link #compute(Access2D)} method must handle cases where pivoting is required. If you know that
 * pivoting is not needed you may call this method instead - it's faster.
 */
  boolean computeWithoutPivoting(  MatrixStore<?> matrix);
  N getDeterminant();
  MatrixStore<N> getL();
  /** 
 * This can be used to create a [P] matrix using {@linkplain IdentityStore} in combination with{@linkplain RowsStore} or {@linkplain ColumnsStore}.
 */
  int[] getPivotOrder();
  int getRank();
  int[] getReducedPivots();
  /** 
 * http://en.wikipedia.org/wiki/Row_echelon_form <br>
 * <br>
 * This is the same as [D][U]. Together with the pivotOrder and [L] this constitutes an alternative, more compact,
 * way to express the decomposition.
 * @see #getPivotOrder()
 * @see #getL()
 */
  MatrixStore<N> getU();
  boolean isSquareAndNotSingular();
}
