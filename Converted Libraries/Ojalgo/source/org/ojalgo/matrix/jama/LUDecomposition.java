package org.ojalgo.matrix.jama;
/** 
 * LU Decomposition.
 * <P>
 * For an m-by-n matrix A with m >= n, the LU decomposition is an m-by-n
 * unit lower triangular matrix L, an n-by-n upper triangular matrix U,
 * and a permutation vector piv of length m so that A(piv,:) = L*U.
 * If m < n, then L is m-by-m and U is m-by-n.
 * <P>
 * The LU decompostion with pivoting always exists, even if the matrix is
 * singular, so the constructor will never fail.  The primary use of the
 * LU decomposition is in the solution of square systems of simultaneous
 * linear equations.  This will fail if isNonsingular() returns false.
 */
class LUDecomposition implements java.io.Serializable {
  /** 
 * Array for internal storage of decomposition.
 * @serial internal array storage.
 */
  private final double[][] LU;
  /** 
 * Row and column dimensions, and pivot sign.
 * @serial column dimension.
 * @serial row dimension.
 * @serial pivot sign.
 */
  private final int m, n, d;
  private int pivsign;
  /** 
 * Internal storage of pivot vector.
 * @serial pivot vector.
 */
  private final int[] piv;
  private static final long serialVersionUID=1;
  /** 
 * LU Decomposition
 * Structure to access L, U and piv.
 * @param A Rectangular matrix
 */
  public LUDecomposition(  final Matrix A){
    LU=A.getArrayCopy();
    m=A.getRowDimension();
    n=A.getColumnDimension();
    d=Math.min(m,n);
    piv=new int[m];
    for (int i=0; i < m; i++) {
      piv[i]=i;
    }
    pivsign=1;
    double[] LUrowi;
    final double[] LUcolj=new double[m];
    for (int j=0; j < n; j++) {
      for (int i=0; i < m; i++) {
        LUcolj[i]=LU[i][j];
      }
      for (int i=0; i < m; i++) {
        LUrowi=LU[i];
        final int kmax=Math.min(i,j);
        double s=0.0;
        for (int k=0; k < kmax; k++) {
          s+=LUrowi[k] * LUcolj[k];
        }
        LUrowi[j]=LUcolj[i]-=s;
      }
      int p=j;
      for (int i=j + 1; i < m; i++) {
        if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
          p=i;
        }
      }
      if (p != j) {
        for (int k=0; k < n; k++) {
          final double t=LU[p][k];
          LU[p][k]=LU[j][k];
          LU[j][k]=t;
        }
        final int k=piv[p];
        piv[p]=piv[j];
        piv[j]=k;
        pivsign=-pivsign;
      }
      if ((j < m) && (LU[j][j] != 0.0)) {
        for (int i=j + 1; i < m; i++) {
          LU[i][j]/=LU[j][j];
        }
      }
    }
  }
  /** 
 * Determinant
 * @return     det(A)
 * @exception IllegalArgumentException  Matrix must be square
 */
  public double det(){
    if (m != n) {
      throw new IllegalArgumentException("Matrix must be square.");
    }
    double d=pivsign;
    for (int j=0; j < n; j++) {
      d*=LU[j][j];
    }
    return d;
  }
  /** 
 * Return pivot permutation vector as a one-dimensional double array
 * @return     (double) piv
 */
  public double[] getDoublePivot(){
    final double[] vals=new double[m];
    for (int i=0; i < m; i++) {
      vals[i]=piv[i];
    }
    return vals;
  }
  /** 
 * Return lower triangular factor
 * @return     L
 */
  public Matrix getL(){
    final Matrix X=new Matrix(m,d);
    final double[][] L=X.getArray();
    for (int i=0; i < m; i++) {
      for (int j=0; j < d; j++) {
        if (i > j) {
          L[i][j]=LU[i][j];
        }
 else         if (i == j) {
          L[i][j]=1.0;
        }
 else {
          L[i][j]=0.0;
        }
      }
    }
    return X;
  }
  /** 
 * Return pivot permutation vector
 * @return     piv
 */
  public int[] getPivot(){
    final int[] p=new int[m];
    for (int i=0; i < m; i++) {
      p[i]=piv[i];
    }
    return p;
  }
  /** 
 * Return upper triangular factor
 * @return     U
 */
  public Matrix getU(){
    final Matrix X=new Matrix(d,n);
    final double[][] U=X.getArray();
    for (int i=0; i < d; i++) {
      for (int j=0; j < n; j++) {
        if (i <= j) {
          U[i][j]=LU[i][j];
        }
 else {
          U[i][j]=0.0;
        }
      }
    }
    return X;
  }
  /** 
 * Is the matrix nonsingular?
 * @return     true if U, and hence A, is nonsingular.
 */
  public boolean isNonsingular(){
    for (int j=0; j < n; j++) {
      if (LU[j][j] == 0) {
        return false;
      }
    }
    return true;
  }
  /** 
 * Solve A*X = B
 * @param B   A Matrix with as many rows as A and any number of columns.
 * @return     X so that L*U*X = B(piv,:)
 * @exception IllegalArgumentException Matrix row dimensions must agree.
 * @exception RuntimeException  Matrix is singular.
 */
  public Matrix solve(  final Matrix B){
    if (B.getRowDimension() != m) {
      throw new IllegalArgumentException("Matrix row dimensions must agree.");
    }
    if (!this.isNonsingular()) {
      throw new RuntimeException("Matrix is singular.");
    }
    final int nx=B.getColumnDimension();
    final Matrix Xmat=B.getMatrix(piv,0,nx - 1);
    final double[][] X=Xmat.getArray();
    for (int k=0; k < n; k++) {
      for (int i=k + 1; i < n; i++) {
        for (int j=0; j < nx; j++) {
          X[i][j]-=X[k][j] * LU[i][k];
        }
      }
    }
    for (int k=n - 1; k >= 0; k--) {
      for (int j=0; j < nx; j++) {
        X[k][j]/=LU[k][k];
      }
      for (int i=0; i < k; i++) {
        for (int j=0; j < nx; j++) {
          X[i][j]-=X[k][j] * LU[i][k];
        }
      }
    }
    return Xmat;
  }
}
