package org.ejml.alg.dense.linsol.qr;
import org.ejml.alg.dense.decomposition.TriangularSolver;
import org.ejml.alg.dense.decomposition.qr.QRDecompositionHouseholderColumn;
import org.ejml.alg.dense.decomposition.qr.QrHelperFunctions;
import org.ejml.alg.dense.linsol.LinearSolverAbstract;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.SpecializedOps;
/** 
 * <p>
 * QR decomposition can be used to solve for systems.  However, this is not as computationally efficient
 * as LU decomposition and costs about 3n<sup>2</sup> flops.
 * </p>
 * <p>
 * It solve for x by first multiplying b by the transpose of Q then solving for the result.
 * <br>
 * QRx=b<br>
 * Rx=Q^T b<br>
 * </p>
 * <p>
 * A column major decomposition is used in this solver.
 * <p>
 * @author Peter Abeles
 */
public class LinearSolverQrHouseCol extends LinearSolverAbstract {
  private QRDecompositionHouseholderColumn decomposer;
  private DenseMatrix64F a=new DenseMatrix64F(1,1);
  private DenseMatrix64F temp=new DenseMatrix64F(1,1);
  protected int maxRows=-1;
  protected int maxCols=-1;
  private double[][] QR;
  private DenseMatrix64F R=new DenseMatrix64F(1,1);
  private double gammas[];
  /** 
 * Creates a linear solver that uses QR decomposition.
 */
  public LinearSolverQrHouseCol(){
    decomposer=new QRDecompositionHouseholderColumn();
  }
  public void setMaxSize(  int maxRows,  int maxCols){
    this.maxRows=maxRows;
    this.maxCols=maxCols;
  }
  /** 
 * Performs QR decomposition on A
 * @param A not modified.
 */
  @Override public boolean setA(  DenseMatrix64F A){
    if (A.numRows > maxRows || A.numCols > maxCols)     setMaxSize(A.numRows,A.numCols);
    R.reshape(A.numCols,A.numCols);
    a.reshape(A.numRows,1);
    temp.reshape(A.numRows,1);
    _setA(A);
    if (!decomposer.decompose(A))     return false;
    gammas=decomposer.getGammas();
    QR=decomposer.getQR();
    decomposer.getR(R,true);
    return true;
  }
  @Override public double quality(){
    return SpecializedOps.qualityTriangular(true,R);
  }
  /** 
 * Solves for X using the QR decomposition.
 * @param B A matrix that is n by m.  Not modified.
 * @param X An n by m matrix where the solution is written to.  Modified.
 */
  @Override public void solve(  DenseMatrix64F B,  DenseMatrix64F X){
    if (X.numRows != numCols)     throw new IllegalArgumentException("Unexpected dimensions for X: X rows = " + X.numRows + " expected = "+ numCols);
 else     if (B.numRows != numRows || B.numCols != X.numCols)     throw new IllegalArgumentException("Unexpected dimensions for B");
    int BnumCols=B.numCols;
    for (int colB=0; colB < BnumCols; colB++) {
      for (int i=0; i < numRows; i++) {
        a.data[i]=B.data[i * BnumCols + colB];
      }
      for (int n=0; n < numCols; n++) {
        double[] u=QR[n];
        double vv=u[n];
        u[n]=1;
        QrHelperFunctions.rank1UpdateMultR(a,u,gammas[n],0,n,numRows,temp.data);
        u[n]=vv;
      }
      TriangularSolver.solveU(R.data,a.data,numCols);
      for (int i=0; i < numCols; i++) {
        X.data[i * X.numCols + colB]=a.data[i];
      }
    }
  }
  @Override public boolean modifiesA(){
    return false;
  }
  @Override public boolean modifiesB(){
    return false;
  }
}
