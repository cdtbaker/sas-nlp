package org.ejml.alg.dense.linsol.qr;
import org.ejml.alg.dense.decomposition.TriangularSolver;
import org.ejml.alg.dense.decomposition.qr.QRDecompositionHouseholder;
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
 * @author Peter Abeles
 */
public class LinearSolverQrHouse extends LinearSolverAbstract {
  private QRDecompositionHouseholder decomposer;
  private double[] a, u;
  private int maxRows=-1;
  private DenseMatrix64F QR;
  private double gammas[];
  /** 
 * Creates a linear solver that uses QR decomposition.
 */
  public LinearSolverQrHouse(){
    decomposer=new QRDecompositionHouseholder();
  }
  public void setMaxSize(  int maxRows){
    this.maxRows=maxRows;
    a=new double[maxRows];
    u=new double[maxRows];
  }
  /** 
 * Performs QR decomposition on A
 * @param A not modified.
 */
  @Override public boolean setA(  DenseMatrix64F A){
    if (A.numRows > maxRows) {
      setMaxSize(A.numRows);
    }
    _setA(A);
    if (!decomposer.decompose(A))     return false;
    gammas=decomposer.getGammas();
    QR=decomposer.getQR();
    return true;
  }
  @Override public double quality(){
    return SpecializedOps.qualityTriangular(true,QR);
  }
  /** 
 * Solves for X using the QR decomposition.
 * @param B A matrix that is n by m.  Not modified.
 * @param X An n by m matrix where the solution is writen to.  Modified.
 */
  @Override public void solve(  DenseMatrix64F B,  DenseMatrix64F X){
    if (X.numRows != numCols)     throw new IllegalArgumentException("Unexpected dimensions for X");
 else     if (B.numRows != numRows || B.numCols != X.numCols)     throw new IllegalArgumentException("Unexpected dimensions for B");
    int BnumCols=B.numCols;
    for (int colB=0; colB < BnumCols; colB++) {
      for (int i=0; i < numRows; i++) {
        a[i]=B.data[i * BnumCols + colB];
      }
      for (int n=0; n < numCols; n++) {
        u[n]=1;
        double ub=a[n];
        for (int i=n + 1; i < numRows; i++) {
          ub+=(u[i]=QR.unsafe_get(i,n)) * a[i];
        }
        ub*=gammas[n];
        for (int i=n; i < numRows; i++) {
          a[i]-=u[i] * ub;
        }
      }
      TriangularSolver.solveU(QR.data,a,numCols);
      for (int i=0; i < numCols; i++) {
        X.data[i * X.numCols + colB]=a[i];
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
