package org.ejml.alg.dense.linsol.chol;
import org.ejml.alg.block.BlockMatrixOps;
import org.ejml.alg.block.linsol.chol.BlockCholeskyOuterSolver;
import org.ejml.alg.dense.linsol.WrapLinearSolverBlock64;
import org.ejml.data.DenseMatrix64F;
/** 
 * A wrapper around {@link org.ejml.factory.CholeskyDecomposition}(BlockMatrix64F) that allows
 * it to be easily used with {@link org.ejml.data.DenseMatrix64F}.
 * @author Peter Abeles
 */
public class LinearSolverCholBlock64 extends WrapLinearSolverBlock64 {
  public LinearSolverCholBlock64(){
    super(new BlockCholeskyOuterSolver());
  }
  /** 
 * Only converts the B matrix and passes that onto solve.  Te result is then copied into
 * the input 'X' matrix.
 * @param B A matrix &real; <sup>m &times; p</sup>.  Not modified.
 * @param X A matrix &real; <sup>n &times; p</sup>, where the solution is written to.  Modified.
 */
  @Override public void solve(  DenseMatrix64F B,  DenseMatrix64F X){
    blockB.reshape(B.numRows,B.numCols,false);
    BlockMatrixOps.convert(B,blockB);
    alg.solve(blockB,null);
    BlockMatrixOps.convert(blockB,X);
  }
}
