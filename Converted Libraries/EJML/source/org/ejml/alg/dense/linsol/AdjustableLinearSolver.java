package org.ejml.alg.dense.linsol;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolver;
/** 
 * In many situations solutions to linear systems that share many of the same data points are needed.
 * This can happen when solving using the most recent data or when rejecting outliers.  In these situations
 * it is possible to solve these related systems much faster than solving the entire data set again.
 * @see org.ejml.factory.LinearSolver
 * @author Peter Abeles
 */
public interface AdjustableLinearSolver extends LinearSolver<DenseMatrix64F> {
  /** 
 * Adds a row to A.  This has the same effect as creating a new A and calling {@link #setA}.
 * @param A_row The row in A.
 * @param rowIndex Where the row appears in A.
 * @return if it succeeded or not.
 */
  public boolean addRowToA(  double[] A_row,  int rowIndex);
  /** 
 * Removes a row from A.  This has the same effect as creating a new A and calling {@link #setA}.
 * @param index which row is removed from A.
 * @return If it succeeded or not.
 */
  public boolean removeRowFromA(  int index);
}
