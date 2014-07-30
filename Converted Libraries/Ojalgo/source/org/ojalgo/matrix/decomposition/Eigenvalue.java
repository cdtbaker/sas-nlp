package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.decomposition.task.DeterminantTask;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;
/** 
 * [A] = [V][D][V]<sup>-1</sup> ([A][V] = [V][D])
 * <ul>
 * <li>[A] = any square matrix.</li>
 * <li>[V] = contains the eigenvectors as columns.</li>
 * <li>[D] = a diagonal matrix with the eigenvalues on the diagonal (possibly in blocks).</li>
 * </ul>
 * <p>
 * [A] is normal if [A][A]<sup>H</sup> = [A]<sup>H</sup>[A], and [A] is normal if and only if there exists a unitary
 * matrix [Q] such that [A] = [Q][D][Q]<sup>H</sup>. Hermitian matrices are normal.
 * </p>
 * <p>
 * [V] and [D] can always be calculated in the sense that they will satisfy [A][V] = [V][D], but it is not always
 * possible to calculate [V]<sup>-1</sup>. (Check the rank and/or the condition number of [V] to determine the validity
 * of [V][D][V]<sup>-1</sup>.)
 * </p>
 * @author apete
 */
public interface Eigenvalue<N extends Number> extends MatrixDecomposition<N>, DeterminantTask<N> {
  /** 
 * @param matrix A matrix to decompose
 * @param eigenvaluesOnly No need to calculate eigenvectors
 * @return true/false if the computation succeeded or not
 */
  boolean compute(  Access2D<?> matrix,  boolean eigenvaluesOnly);
  /** 
 * The only requirements on [D] are that it should contain the eigenvalues and that [A][V] = [V][D]. The ordering of
 * the eigenvalues is not specified.
 * <ul>
 * <li>If [A] is real and symmetric then [D] is (purely) diagonal with real eigenvalues.</li>
 * <li>If [A] is real but not symmetric then [D] is block-diagonal with real eigenvalues in 1-by-1 blocks and
 * complex eigenvalues in 2-by-2 blocks.</li>
 * <li>If [A] is complex then [D] is (purely) diagonal with complex eigenvalues.</li>
 * </ul>
 * @return The (block) diagonal eigenvalue matrix.
 */
  MatrixStore<N> getD();
  /** 
 * <p>
 * A matrix' determinant is the product of its eigenvalues.
 * </p>
 * @return The matrix' determinant
 */
  N getDeterminant();
  /** 
 * <p>
 * Even for real matrices the eigenvalues are potentially complex numbers. Typically they need to be expressed as
 * complex numbers when [A] is not symmetric.
 * </p>
 * <p>
 * The eigenvalues in this array should be ordered in descending order - largest (modulus) first.
 * </p>
 * @return The eigenvalues in an ordered array.
 */
  Array1D<ComplexNumber> getEigenvalues();
  /** 
 * A matrix' trace is the sum of the diagonal elements. It is also the sum of the eigenvalues. This method should
 * return the sum of the eigenvalues.
 * @return The matrix' trace
 */
  ComplexNumber getTrace();
  /** 
 * The columns of [V] represent the eigenvectors of [A] in the sense that [A][V] = [V][D].
 * @return The eigenvector matrix.
 */
  MatrixStore<N> getV();
  /** 
 * If [A] is hermitian then [V][D][V]<sup>-1</sup> becomes [Q][D][Q]<sup>H</sup>...
 */
  boolean isHermitian();
  /** 
 * The eigenvalues in D (and the eigenvectors in V) are not necessarily ordered. This is a property of the
 * algorithm/implementation, not the data.
 * @return true if they are ordered
 */
  boolean isOrdered();
}