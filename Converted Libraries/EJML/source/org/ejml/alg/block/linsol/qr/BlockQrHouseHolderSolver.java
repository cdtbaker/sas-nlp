package org.ejml.alg.block.linsol.qr;
import org.ejml.alg.block.BlockMatrixOps;
import org.ejml.alg.block.BlockTriangularSolver;
import org.ejml.alg.block.decomposition.qr.BlockMatrix64HouseholderQR;
import org.ejml.data.BlockMatrix64F;
import org.ejml.data.D1Submatrix64F;
import org.ejml.factory.LinearSolver;
import org.ejml.ops.SpecializedOps;
/** 
 * <p>
 * A solver for {@link BlockMatrix64HouseholderQR}.  Systems are solved for using the standard
 * QR decomposition method, sketched below.
 * </p>
 * <p>
 * A = Q*R<br>
 * A*x = b<br>
 * Q*R*x = b <br>
 * R*x = y = Q<sup>T</sup>b<br>
 * x = R<sup>-1</sup>y<br>
 * <br>
 * Where A is the m by n matrix being decomposed. Q is an orthogonal matrix. R is upper triangular matrix.
 * </p>
 * @author Peter Abeles
 */
public class BlockQrHouseHolderSolver implements LinearSolver<BlockMatrix64F> {
  protected BlockMatrix64HouseholderQR decomp=new BlockMatrix64HouseholderQR();
  protected BlockMatrix64F QR;
  public BlockQrHouseHolderSolver(){
    decomp.setSaveW(false);
  }
  /** 
 * Computes the QR decomposition of A and store the results in A.
 * @param A The A matrix in the linear equation. Modified. Reference saved.
 * @return true if the decomposition was successful.
 */
  @Override public boolean setA(  BlockMatrix64F A){
    if (A.numRows < A.numCols)     throw new IllegalArgumentException("Number of rows must be more than or equal to the number of columns.  " + "Can't solve an underdetermined system.");
    if (!decomp.decompose(A))     return false;
    this.QR=decomp.getQR();
    return true;
  }
  /** 
 * Computes the quality using diagonal elements the triangular R matrix in the QR decomposition.
 * @return Solutions quality.
 */
  @Override public double quality(){
    return SpecializedOps.qualityTriangular(true,decomp.getQR());
  }
  @Override public void solve(  BlockMatrix64F B,  BlockMatrix64F X){
    if (B.numCols != X.numCols)     throw new IllegalArgumentException("Columns of B and X do not match");
    if (QR.numCols != X.numRows)     throw new IllegalArgumentException("Rows in X do not match the columns in A");
    if (QR.numRows != B.numRows)     throw new IllegalArgumentException("Rows in B do not match the rows in A.");
    if (B.blockLength != QR.blockLength || X.blockLength != QR.blockLength)     throw new IllegalArgumentException("All matrices must have the same block length.");
    decomp.applyQTran(B);
    BlockMatrixOps.extractAligned(B,X);
    int M=Math.min(QR.numRows,QR.numCols);
    BlockTriangularSolver.solve(QR.blockLength,true,new D1Submatrix64F(QR,0,M,0,M),new D1Submatrix64F(X),false);
  }
  /** 
 * Invert by solving for against an identity matrix.
 * @param A_inv Where the inverted matrix saved. Modified.
 */
  @Override public void invert(  BlockMatrix64F A_inv){
    int M=Math.min(QR.numRows,QR.numCols);
    if (A_inv.numRows != M || A_inv.numCols != M)     throw new IllegalArgumentException("A_inv must be square an have dimension " + M);
    BlockMatrixOps.setIdentity(A_inv);
    decomp.applyQTran(A_inv);
    BlockTriangularSolver.solve(QR.blockLength,true,new D1Submatrix64F(QR,0,M,0,M),new D1Submatrix64F(A_inv),false);
  }
  @Override public boolean modifiesA(){
    return decomp.inputModified();
  }
  @Override public boolean modifiesB(){
    return true;
  }
}
