package org.apache.commons.math3.linear;
import org.apache.commons.math3.FieldElement;
/** 
 * Interface handling decomposition algorithms that can solve A &times; X = B.
 * <p>Decomposition algorithms decompose an A matrix has a product of several specific
 * matrices from which they can solve A &times; X = B in least squares sense: they find X
 * such that ||A &times; X - B|| is minimal.</p>
 * <p>Some solvers like {@link FieldLUDecomposition} can only find the solution for
 * square matrices and when the solution is an exact linear solution, i.e. when
 * ||A &times; X - B|| is exactly 0. Other solvers can also find solutions
 * with non-square matrix A and with non-null minimal norm. If an exact linear
 * solution exists it is also the minimal norm solution.</p>
 * @param<T>
 *  the type of the field elements
 * @version $Id: FieldDecompositionSolver.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public interface FieldDecompositionSolver<T extends FieldElement<T>> {
  /** 
 * Solve the linear equation A &times; X = B for matrices A.
 * <p>The A matrix is implicit, it is provided by the underlying
 * decomposition algorithm.</p>
 * @param b right-hand side of the equation A &times; X = B
 * @return a vector X that minimizes the two norm of A &times; X - B
 * @throws org.apache.commons.math3.exception.DimensionMismatchExceptionif the matrices dimensions do not match.
 * @throws SingularMatrixExceptionif the decomposed matrix is singular.
 */
  FieldVector<T> solve(  final FieldVector<T> b);
  /** 
 * Solve the linear equation A &times; X = B for matrices A.
 * <p>The A matrix is implicit, it is provided by the underlying
 * decomposition algorithm.</p>
 * @param b right-hand side of the equation A &times; X = B
 * @return a matrix X that minimizes the two norm of A &times; X - B
 * @throws org.apache.commons.math3.exception.DimensionMismatchExceptionif the matrices dimensions do not match.
 * @throws SingularMatrixExceptionif the decomposed matrix is singular.
 */
  FieldMatrix<T> solve(  final FieldMatrix<T> b);
  /** 
 * Check if the decomposed matrix is non-singular.
 * @return true if the decomposed matrix is non-singular
 */
  boolean isNonSingular();
  /** 
 * Get the inverse (or pseudo-inverse) of the decomposed matrix.
 * @return inverse matrix
 * @throws SingularMatrixExceptionif the decomposed matrix is singular.
 */
  FieldMatrix<T> getInverse();
}
