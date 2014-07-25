package org.ejml.alg.block.decomposition.chol;
import org.ejml.alg.block.BlockInnerRankUpdate;
import org.ejml.alg.block.BlockMatrixOps;
import org.ejml.alg.block.BlockTriangularSolver;
import org.ejml.data.BlockMatrix64F;
import org.ejml.data.D1Submatrix64F;
import org.ejml.factory.CholeskyDecomposition;
/** 
 * <p>
 * Block Cholesky using outer product form.  The original matrix is stored and modified.
 * </p>
 * <p>
 * Based on the description provided in "Fundamentals of Matrix Computations" 2nd Ed. by David S. Watkins.
 * </p>
 * @author Peter Abeles
 */
public class BlockCholeskyOuterForm implements CholeskyDecomposition<BlockMatrix64F> {
  private boolean lower=false;
  private BlockMatrix64F T;
  /** 
 * Creates a new BlockCholeskyOuterForm
 * @param lower Should it decompose it into a lower triangular matrix or not.
 */
  public BlockCholeskyOuterForm(  boolean lower){
    this.lower=lower;
  }
  /** 
 * Decomposes the provided matrix and stores the result in the same matrix.
 * @param A Matrix that is to be decomposed.  Modified.
 * @return If it succeeded or not.
 */
  @Override public boolean decompose(  BlockMatrix64F A){
    if (A.numCols != A.numRows)     throw new IllegalArgumentException("A must be square");
    this.T=A;
    if (lower)     return decomposeLower();
 else     return decomposeUpper();
  }
  private boolean decomposeLower(){
    int blockLength=T.blockLength;
    D1Submatrix64F subA=new D1Submatrix64F(T);
    D1Submatrix64F subB=new D1Submatrix64F(T);
    D1Submatrix64F subC=new D1Submatrix64F(T);
    for (int i=0; i < T.numCols; i+=blockLength) {
      int widthA=Math.min(blockLength,T.numCols - i);
      subA.col0=i;
      subA.col1=i + widthA;
      subA.row0=subA.col0;
      subA.row1=subA.col1;
      subB.col0=i;
      subB.col1=i + widthA;
      subB.row0=i + widthA;
      subB.row1=T.numRows;
      subC.col0=i + widthA;
      subC.col1=T.numRows;
      subC.row0=i + widthA;
      subC.row1=T.numRows;
      if (!BlockInnerCholesky.lower(subA))       return false;
      if (widthA == blockLength) {
        BlockTriangularSolver.solveBlock(blockLength,false,subA,subB,false,true);
        BlockInnerRankUpdate.symmRankNMinus_L(blockLength,subC,subB);
      }
    }
    BlockMatrixOps.zeroTriangle(true,T);
    return true;
  }
  private boolean decomposeUpper(){
    int blockLength=T.blockLength;
    D1Submatrix64F subA=new D1Submatrix64F(T);
    D1Submatrix64F subB=new D1Submatrix64F(T);
    D1Submatrix64F subC=new D1Submatrix64F(T);
    for (int i=0; i < T.numCols; i+=blockLength) {
      int widthA=Math.min(blockLength,T.numCols - i);
      subA.col0=i;
      subA.col1=i + widthA;
      subA.row0=subA.col0;
      subA.row1=subA.col1;
      subB.col0=i + widthA;
      subB.col1=T.numCols;
      subB.row0=i;
      subB.row1=i + widthA;
      subC.col0=i + widthA;
      subC.col1=T.numCols;
      subC.row0=i + widthA;
      subC.row1=T.numCols;
      if (!BlockInnerCholesky.upper(subA))       return false;
      if (widthA == blockLength) {
        BlockTriangularSolver.solveBlock(blockLength,true,subA,subB,true,false);
        BlockInnerRankUpdate.symmRankNMinus_U(blockLength,subC,subB);
      }
    }
    BlockMatrixOps.zeroTriangle(false,T);
    return true;
  }
  @Override public boolean isLower(){
    return lower;
  }
  @Override public BlockMatrix64F getT(  BlockMatrix64F T){
    if (T == null)     return this.T;
    T.set(this.T);
    return T;
  }
  @Override public boolean inputModified(){
    return true;
  }
}
