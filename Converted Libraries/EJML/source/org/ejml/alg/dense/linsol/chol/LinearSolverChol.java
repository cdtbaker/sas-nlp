package org.ejml.alg.dense.linsol.chol;
import org.ejml.alg.dense.decomposition.TriangularSolver;
import org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionCommon;
import org.ejml.alg.dense.linsol.LinearSolverAbstract;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.SpecializedOps;
/** 
 * @author Peter Abeles
 */
public class LinearSolverChol extends LinearSolverAbstract {
  CholeskyDecompositionCommon decomp;
  int n;
  double vv[];
  double t[];
  public LinearSolverChol(  CholeskyDecompositionCommon decomp){
    this.decomp=decomp;
  }
  @Override public boolean setA(  DenseMatrix64F A){
    _setA(A);
    if (decomp.decompose(A)) {
      n=A.numCols;
      vv=decomp._getVV();
      t=decomp.getT().data;
      return true;
    }
 else {
      return false;
    }
  }
  @Override public double quality(){
    return SpecializedOps.qualityTriangular(true,decomp.getT());
  }
  /** 
 * <p>
 * Using the decomposition, finds the value of 'X' in the linear equation below:<br>
 * A*x = b<br>
 * where A has dimension of n by n, x and b are n by m dimension.
 * </p>
 * <p>
 * *Note* that 'b' and 'x' can be the same matrix instance.
 * </p>
 * @param B A matrix that is n by m.  Not modified.
 * @param X An n by m matrix where the solution is writen to.  Modified.
 */
  @Override public void solve(  DenseMatrix64F B,  DenseMatrix64F X){
    if (B.numCols != X.numCols && B.numRows != n && X.numRows != n) {
      throw new IllegalArgumentException("Unexpected matrix size");
    }
    int numCols=B.numCols;
    double dataB[]=B.data;
    double dataX[]=X.data;
    if (decomp.isLower()) {
      for (int j=0; j < numCols; j++) {
        for (int i=0; i < n; i++)         vv[i]=dataB[i * numCols + j];
        solveInternalL();
        for (int i=0; i < n; i++)         dataX[i * numCols + j]=vv[i];
      }
    }
 else {
      throw new RuntimeException("Implement");
    }
  }
  /** 
 * Used internally to find the solution to a single column vector.
 */
  private void solveInternalL(){
    TriangularSolver.solveL(t,vv,n);
    TriangularSolver.solveTranL(t,vv,n);
  }
  /** 
 * Sets the matrix 'inv' equal to the inverse of the matrix that was decomposed.
 * @param inv Where the value of the inverse will be stored.  Modified.
 */
  @Override public void invert(  DenseMatrix64F inv){
    if (inv.numRows != n || inv.numCols != n) {
      throw new RuntimeException("Unexpected matrix dimension");
    }
    if (inv.data == t) {
      throw new IllegalArgumentException("Passing in the same matrix that was decomposed.");
    }
    double a[]=inv.data;
    if (decomp.isLower()) {
      setToInverseL(a);
    }
 else {
      throw new RuntimeException("Implement");
    }
  }
  /** 
 * Sets the matrix to the inverse using a lower triangular matrix.
 */
  public void setToInverseL(  double a[]){
    for (int i=0; i < n; i++) {
      double el_ii=t[i * n + i];
      for (int j=0; j <= i; j++) {
        double sum=(i == j) ? 1.0 : 0;
        for (int k=i - 1; k >= j; k--) {
          sum-=t[i * n + k] * a[j * n + k];
        }
        a[j * n + i]=sum / el_ii;
      }
    }
    for (int i=n - 1; i >= 0; i--) {
      double el_ii=t[i * n + i];
      for (int j=0; j <= i; j++) {
        double sum=(i < j) ? 0 : a[j * n + i];
        for (int k=i + 1; k < n; k++) {
          sum-=t[k * n + i] * a[j * n + k];
        }
        a[i * n + j]=a[j * n + i]=sum / el_ii;
      }
    }
  }
  @Override public boolean modifiesA(){
    return decomp.inputModified();
  }
  @Override public boolean modifiesB(){
    return false;
  }
}
