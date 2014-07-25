package org.ojalgo.matrix.jama;
/** 
 * Cholesky Decomposition.
 * <P>
 * For a symmetric, positive definite matrix A, the Cholesky decomposition
 * is an lower triangular matrix L so that A = L*L'.
 * <P>
 * If the matrix is not symmetric or positive definite, the constructor
 * returns a partial decomposition and sets an internal flag that may
 * be queried by the isSPD() method.
 */
class CholeskyDecomposition implements java.io.Serializable {
  /** 
 * Array for internal storage of decomposition.
 * @serial internal array storage.
 */
  private final double[][] L;
  /** 
 * Row and column dimension (square matrix).
 * @serial matrix dimension.
 */
  private final int n;
  /** 
 * Symmetric and positive definite flag.
 * @serial is symmetric and positive definite flag.
 */
  private boolean isspd;
  private static final long serialVersionUID=1;
  /** 
 * Cholesky algorithm for symmetric and positive definite matrix.
 * Structure to access L and isspd flag.
 * @param Arg   Square, symmetric matrix.
 */
  public CholeskyDecomposition(  final Matrix Arg){
    final double[][] A=Arg.getArray();
    n=Arg.getRowDimension();
    L=new double[n][n];
    isspd=(Arg.getColumnDimension() == n);
    for (int j=0; j < n; j++) {
      final double[] Lrowj=L[j];
      double d=0.0;
      for (int k=0; k < j; k++) {
        final double[] Lrowk=L[k];
        double s=0.0;
        for (int i=0; i < k; i++) {
          s+=Lrowk[i] * Lrowj[i];
        }
        Lrowj[k]=s=(A[j][k] - s) / L[k][k];
        d=d + (s * s);
        isspd=isspd & (A[k][j] == A[j][k]);
      }
      d=A[j][j] - d;
      isspd=isspd & (d > 0.0);
      L[j][j]=Math.sqrt(Math.max(d,0.0));
      for (int k=j + 1; k < n; k++) {
        L[j][k]=0.0;
      }
    }
  }
  /** 
 * Return triangular factor.
 * @return     L
 */
  public Matrix getL(){
    return new Matrix(L,n,n);
  }
  /** 
 * Is the matrix symmetric and positive definite?
 * @return     true if A is symmetric and positive definite.
 */
  public boolean isSPD(){
    return isspd;
  }
  /** 
 * Solve A*X = B
 * @param B   A Matrix with as many rows as A and any number of columns.
 * @return     X so that L*L'*X = B
 * @exception IllegalArgumentException  Matrix row dimensions must agree.
 * @exception RuntimeException  Matrix is not symmetric positive definite.
 */
  public Matrix solve(  final Matrix B){
    if (B.getRowDimension() != n) {
      throw new IllegalArgumentException("Matrix row dimensions must agree.");
    }
    if (!isspd) {
      throw new RuntimeException("Matrix is not symmetric positive definite.");
    }
    final double[][] X=B.getArrayCopy();
    final int nx=B.getColumnDimension();
    for (int k=0; k < n; k++) {
      for (int j=0; j < nx; j++) {
        for (int i=0; i < k; i++) {
          X[k][j]-=X[i][j] * L[k][i];
        }
        X[k][j]/=L[k][k];
      }
    }
    for (int k=n - 1; k >= 0; k--) {
      for (int j=0; j < nx; j++) {
        for (int i=k + 1; i < n; i++) {
          X[k][j]-=X[i][j] * L[i][k];
        }
        X[k][j]/=L[k][k];
      }
    }
    return new Matrix(X,n,nx);
  }
}
