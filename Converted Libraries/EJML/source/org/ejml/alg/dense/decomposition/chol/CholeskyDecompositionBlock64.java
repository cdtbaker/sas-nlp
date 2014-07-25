package org.ejml.alg.dense.decomposition.chol;
import org.ejml.EjmlParameters;
import org.ejml.alg.block.BlockMatrixOps;
import org.ejml.alg.block.decomposition.chol.BlockCholeskyOuterForm;
import org.ejml.alg.dense.decomposition.BaseDecompositionBlock64;
import org.ejml.data.BlockMatrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.CholeskyDecomposition;
/** 
 * Wrapper around {@link org.ejml.alg.block.decomposition.chol.BlockCholeskyOuterForm} that allows
 * it to process DenseMatrix64F.
 * @author Peter Abeles
 */
public class CholeskyDecompositionBlock64 extends BaseDecompositionBlock64 implements CholeskyDecomposition<DenseMatrix64F> {
  public CholeskyDecompositionBlock64(  boolean lower){
    super(new BlockCholeskyOuterForm(lower),EjmlParameters.BLOCK_WIDTH);
  }
  @Override public boolean isLower(){
    return ((BlockCholeskyOuterForm)alg).isLower();
  }
  @Override public DenseMatrix64F getT(  DenseMatrix64F T){
    BlockMatrix64F T_block=((BlockCholeskyOuterForm)alg).getT(null);
    if (T == null) {
      T=new DenseMatrix64F(T_block.numRows,T_block.numCols);
    }
    BlockMatrixOps.convert(T_block,T);
    return T;
  }
}
