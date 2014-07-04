package org.ejml.alg.dense.decomposition.bidiagonal;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.SpecializedOps;
import org.ejml.simple.SimpleMatrix;
/** 
 * A slower but much simpler version of {@link BidiagonalDecompositionRow} that internally uses
 * SimpleMatrix and explicitly computes the householder matrices.  This was easier to code up and is
 * used to validate other implementations.
 * @author Peter Abeles
 */
public class BidiagonalDecompositionNaive {
  private SimpleMatrix U;
  private SimpleMatrix B;
  private SimpleMatrix V;
  private int m;
  private int n;
  private int min;
  DenseMatrix64F u;
  public SimpleMatrix getU(){
    return U;
  }
  public SimpleMatrix getB(){
    return B;
  }
  public SimpleMatrix getV(){
    return V;
  }
  /** 
 * Computes the decomposition of the provided matrix.  If no errors are detected then true is returned,
 * false otherwise.
 * @param A  The matrix that is being decomposed.  Not modified.
 * @return If it detects any errors or not.
 */
  public boolean decompose(  DenseMatrix64F A){
    init(A);
    return _decompose();
  }
  protected void init(  DenseMatrix64F A){
    m=A.numRows;
    n=A.numCols;
    min=Math.min(m,n);
    U=SimpleMatrix.identity(m);
    B=new SimpleMatrix(A);
    V=SimpleMatrix.identity(n);
    int max=Math.max(m,n);
    u=new DenseMatrix64F(max,1);
  }
  /** 
 * Internal function for computing the decomposition.
 */
  private boolean _decompose(){
    for (int k=0; k < min; k++) {
      computeU(k);
      computeV(k);
    }
    return true;
  }
  protected void computeU(  int k){
    u.reshape(m,1,false);
    double u[]=this.u.data;
    double max=0;
    for (int i=k; i < m; i++) {
      double val=u[i]=B.get(i,k);
      val=Math.abs(val);
      if (val > max)       max=val;
    }
    if (max > 0) {
      double tau=0;
      for (int i=k; i < m; i++) {
        double val=u[i]/=max;
        tau+=val * val;
      }
      tau=Math.sqrt(tau);
      if (u[k] < 0)       tau=-tau;
      double nu=u[k] + tau;
      u[k]=1.0;
      for (int i=k + 1; i < m; i++) {
        u[i]/=nu;
      }
      SimpleMatrix Q_k=SimpleMatrix.wrap(SpecializedOps.createReflector(this.u,nu / tau));
      U=U.mult(Q_k);
      B=Q_k.mult(B);
    }
  }
  protected void computeV(  int k){
    u.reshape(n,1,false);
    u.zero();
    double u[]=this.u.data;
    double max=0;
    for (int i=k + 1; i < n; i++) {
      double val=u[i]=B.get(k,i);
      val=Math.abs(val);
      if (val > max)       max=val;
    }
    if (max > 0) {
      double tau=0;
      for (int i=k + 1; i < n; i++) {
        double val=u[i]/=max;
        tau+=val * val;
      }
      tau=Math.sqrt(tau);
      if (u[k + 1] < 0)       tau=-tau;
      double nu=u[k + 1] + tau;
      u[k + 1]=1.0;
      for (int i=k + 2; i < n; i++) {
        u[i]/=nu;
      }
      SimpleMatrix Q_k=SimpleMatrix.wrap(SpecializedOps.createReflector(this.u,nu / tau));
      V=V.mult(Q_k);
      B=B.mult(Q_k);
    }
  }
}
