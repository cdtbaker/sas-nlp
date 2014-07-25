package org.ejml.alg.dense.linsol.qr;
import org.ejml.alg.dense.decomposition.TriangularSolver;
import org.ejml.alg.dense.decomposition.qr.QRDecompositionHouseholderTran;
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
public class LinearSolverQrHouseTran extends LinearSolverAbstract {
  private QRDecompositionHouseholderTran decomposer;
  private double[] a;
  protected int maxRows=-1;
  protected int maxCols=-1;
  private DenseMatrix64F QR;
  private DenseMatrix64F U;
  /** 
 * Creates a linear solver that uses QR decomposition.
 */
  public LinearSolverQrHouseTran(){
    decomposer=new QRDecompositionHouseholderTran();
  }
  public void setMaxSize(  int maxRows,  int maxCols){
    this.maxRows=maxRows;
    this.maxCols=maxCols;
    a=new double[maxRows];
  }
  /** 
 * Performs QR decomposition on A
 * @param A not modified.
 */
  @Override public boolean setA(  DenseMatrix64F A){
    if (A.numRows > maxRows || A.numCols > maxCols)     setMaxSize(A.numRows,A.numCols);
    _setA(A);
    if (!decomposer.decompose(A))     return false;
    QR=decomposer.getQR();
    return true;
  }
  @Override public double quality(){
    return SpecializedOps.qualityTriangular(true,QR);
  }
  /** 
 * Solves for X using the QR decomposition.
 * @param B A matrix that is n by m.  Not modified.
 * @param X An n by m matrix where the solution is written to.  Modified.
 */
  @Override public void solve(  DenseMatrix64F B,  DenseMatrix64F X){
    if (X.numRows != numCols)     throw new IllegalArgumentException("Unexpected dimensions for X: X rows = " + X.numRows + " expected = "+ numCols);
 else     if (B.numRows != numRows || B.numCols != X.numCols)     throw new IllegalArgumentException("Unexpected dimensions for B");
    U=decomposer.getR(U,true);
    final double gammas[]=decomposer.getGammas();
    final double dataQR[]=QR.data;
    final int BnumCols=B.numCols;
    for (int colB=0; colB < BnumCols; colB++) {
      for (int i=0; i < numRows; i++) {
        a[i]=B.data[i * BnumCols + colB];
      }
      for (int n=0; n < numCols; n++) {
        int indexU=n * numRows + n + 1;
        double ub=a[n];
        for (int i=n + 1; i < numRows; i++, indexU++) {
          ub+=dataQR[indexU] * a[i];
        }
        ub*=gammas[n];
        a[n]-=ub;
        indexU=n * numRows + n + 1;
        for (int i=n + 1; i < numRows; i++, indexU++) {
          a[i]-=dataQR[indexU] * ub;
        }
      }
      TriangularSolver.solveU(U.data,a,numCols);
      for (int i=0; i < numCols; i++) {
        X.data[i * X.numCols + colB]=a[i];
      }
    }
  }
  @Override public boolean modifiesA(){
    return decomposer.inputModified();
  }
  @Override public boolean modifiesB(){
    return false;
  }
}
