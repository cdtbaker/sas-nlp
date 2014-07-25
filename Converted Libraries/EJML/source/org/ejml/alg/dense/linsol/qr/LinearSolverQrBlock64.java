package org.ejml.alg.dense.linsol.qr;
import org.ejml.alg.block.linsol.qr.BlockQrHouseHolderSolver;
import org.ejml.alg.dense.linsol.WrapLinearSolverBlock64;
/** 
 * Wrapper around {@link BlockQrHouseHolderSolver} that allows it to process{@link org.ejml.data.DenseMatrix64F}.
 * @author Peter Abeles
 */
public class LinearSolverQrBlock64 extends WrapLinearSolverBlock64 {
  public LinearSolverQrBlock64(){
    super(new BlockQrHouseHolderSolver());
  }
}
