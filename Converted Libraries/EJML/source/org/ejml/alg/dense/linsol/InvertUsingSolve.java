package org.ejml.alg.dense.linsol;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.RowD1Matrix64F;
import org.ejml.factory.LinearSolver;
import org.ejml.ops.CommonOps;
/** 
 * A matrix can be easily inverted by solving a system with an identify matrix.  The only
 * disadvantage of this approach is that additional computations are required compared to
 * a specialized solution.
 * @author Peter Abeles
 */
public class InvertUsingSolve {
  public static void invert(  LinearSolver<DenseMatrix64F> solver,  RowD1Matrix64F A,  DenseMatrix64F A_inv,  DenseMatrix64F storage){
    if (A.numRows != A_inv.numRows || A.numCols != A_inv.numCols) {
      throw new IllegalArgumentException("A and A_inv must have the same dimensions");
    }
    CommonOps.setIdentity(storage);
    solver.solve(storage,A_inv);
  }
  public static void invert(  LinearSolver<DenseMatrix64F> solver,  RowD1Matrix64F A,  DenseMatrix64F A_inv){
    if (A.numRows != A_inv.numRows || A.numCols != A_inv.numCols) {
      throw new IllegalArgumentException("A and A_inv must have the same dimensions");
    }
    CommonOps.setIdentity(A_inv);
    solver.solve(A_inv,A_inv);
  }
}
