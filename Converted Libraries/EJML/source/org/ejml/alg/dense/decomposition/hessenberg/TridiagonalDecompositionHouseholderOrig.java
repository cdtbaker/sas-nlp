package org.ejml.alg.dense.decomposition.hessenberg;
import org.ejml.alg.dense.decomposition.qr.QrHelperFunctions;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
/** 
 * <p>
 * A straight forward implementation from "Fundamentals of Matrix Computations," Second Edition.<br>
 * <br>
 * This is only saved to provide a point of reference in benchmarks.
 * </p>
 * @author Peter Abeles
 */
public class TridiagonalDecompositionHouseholderOrig {
  /** 
 * Internal storage of decomposed matrix.  The tridiagonal matrix is stored in the
 * upper tridiagonal portion of the matrix.  The householder vectors are stored
 * in the upper rows.
 */
  DenseMatrix64F QT;
  int N;
  double w[];
  double gammas[];
  double b[];
  public TridiagonalDecompositionHouseholderOrig(){
    N=1;
    QT=new DenseMatrix64F(N,N);
    w=new double[N];
    b=new double[N];
    gammas=new double[N];
  }
  /** 
 * Returns the interal matrix where the decomposed results are stored.
 * @return
 */
  public DenseMatrix64F getQT(){
    return QT;
  }
  /** 
 * Extracts the tridiagonal matrix found in the decomposition.
 * @param T If not null then the results will be stored here.  Otherwise a new matrix will be created.
 * @return The extracted T matrix.
 */
  public DenseMatrix64F getT(  DenseMatrix64F T){
    if (T == null) {
      T=new DenseMatrix64F(N,N);
    }
 else     if (N != T.numRows || N != T.numCols)     throw new IllegalArgumentException("The provided H must have the same dimensions as the decomposed matrix.");
 else     T.zero();
    T.data[0]=QT.data[0];
    T.data[1]=QT.data[1];
    for (int i=1; i < N - 1; i++) {
      T.set(i,i,QT.get(i,i));
      T.set(i,i + 1,QT.get(i,i + 1));
      T.set(i,i - 1,QT.get(i - 1,i));
    }
    T.data[(N - 1) * N + N - 1]=QT.data[(N - 1) * N + N - 1];
    T.data[(N - 1) * N + N - 2]=QT.data[(N - 2) * N + N - 1];
    return T;
  }
  /** 
 * An orthogonal matrix that has the following property: T = Q<sup>T</sup>AQ
 * @param Q If not null then the results will be stored here.  Otherwise a new matrix will be created.
 * @return The extracted Q matrix.
 */
  public DenseMatrix64F getQ(  DenseMatrix64F Q){
    if (Q == null) {
      Q=new DenseMatrix64F(N,N);
      for (int i=0; i < N; i++) {
        Q.data[i * N + i]=1;
      }
    }
 else     if (N != Q.numRows || N != Q.numCols)     throw new IllegalArgumentException("The provided H must have the same dimensions as the decomposed matrix.");
 else     CommonOps.setIdentity(Q);
    for (int i=0; i < N; i++)     w[i]=0;
    for (int j=N - 2; j >= 0; j--) {
      w[j + 1]=1;
      for (int i=j + 2; i < N; i++) {
        w[i]=QT.get(j,i);
      }
      QrHelperFunctions.rank1UpdateMultR(Q,w,gammas[j + 1],j + 1,j + 1,N,b);
    }
    return Q;
  }
  /** 
 * Decomposes the provided symmetric matrix.
 * @param A Symmetric matrix that is going to be decomposed.  Not modified.
 */
  public void decompose(  DenseMatrix64F A){
    init(A);
    for (int k=1; k < N; k++) {
      similarTransform(k);
    }
  }
  /** 
 * Computes and performs the similar a transform for submatrix k.
 */
  private void similarTransform(  int k){
    double t[]=QT.data;
    double max=0;
    int rowU=(k - 1) * N;
    for (int i=k; i < N; i++) {
      double val=Math.abs(t[rowU + i]);
      if (val > max)       max=val;
    }
    if (max > 0) {
      double tau=0;
      for (int i=k; i < N; i++) {
        double val=t[rowU + i]/=max;
        tau+=val * val;
      }
      tau=Math.sqrt(tau);
      if (t[rowU + k] < 0)       tau=-tau;
      double nu=t[rowU + k] + tau;
      t[rowU + k]=1.0;
      for (int i=k + 1; i < N; i++) {
        t[rowU + i]/=nu;
      }
      double gamma=nu / tau;
      gammas[k]=gamma;
      householderSymmetric(k,gamma);
      t[rowU + k]=-tau * max;
    }
 else {
      gammas[k]=0;
    }
  }
  /** 
 * Performs the householder operations on left and right and side of the matrix.  Q<sup>T</sup>AQ
 * @param row Specifies the submatrix.
 * @param gamma The gamma for the householder operation
 */
  public void householderSymmetric(  int row,  double gamma){
    int startU=(row - 1) * N;
    for (int i=row; i < N; i++) {
      double total=0;
      for (int j=row; j < N; j++) {
        total+=QT.data[i * N + j] * QT.data[startU + j];
      }
      w[i]=-gamma * total;
    }
    double alpha=0;
    for (int i=row; i < N; i++) {
      alpha+=QT.data[startU + i] * w[i];
    }
    alpha*=-0.5 * gamma;
    for (int i=row; i < N; i++) {
      w[i]+=alpha * QT.data[startU + i];
    }
    for (int i=row; i < N; i++) {
      double ww=w[i];
      double uu=QT.data[startU + i];
      for (int j=i; j < N; j++) {
        QT.data[j * N + i]=QT.data[i * N + j]+=ww * QT.data[startU + j] + w[j] * uu;
      }
    }
  }
  /** 
 * If needed declares and sets up internal data structures.
 * @param A Matrix being decomposed.
 */
  public void init(  DenseMatrix64F A){
    if (A.numRows != A.numCols)     throw new IllegalArgumentException("Must be square");
    if (A.numCols != N) {
      N=A.numCols;
      QT.reshape(N,N,false);
      if (w.length < N) {
        w=new double[N];
        gammas=new double[N];
        b=new double[N];
      }
    }
    QT.set(A);
  }
  public double getGamma(  int index){
    return gammas[index];
  }
}
