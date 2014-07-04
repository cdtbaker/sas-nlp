package org.ejml.alg.dense.linsol.svd;
import org.ejml.UtilEjml;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.LinearSolver;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
/** 
 * <p>
 * The pseudo-inverse is typically used to solve over determined system for which there is no unique solution.<br>
 * x=inv(A<sup>T</sup>A)A<sup>T</sup>b<br>
 * where A &isin; &real; <sup>m &times; n</sup> and m &ge; n.
 * </p>
 * <p>
 * This class implements the Moore-Penrose pseudo-inverse using SVD and should never fail.  Alternative implementations
 * can use Cholesky decomposition, but those will fail if the A<sup>T</sup>A matrix is singular.
 * However the Cholesky implementation is much faster.
 * </p>
 * @author Peter Abeles
 */
public class SolvePseudoInverseSvd implements LinearSolver<DenseMatrix64F> {
  private SingularValueDecomposition<DenseMatrix64F> svd;
  private DenseMatrix64F pinv=new DenseMatrix64F(1,1);
  private double threshold=UtilEjml.EPS;
  /** 
 * Creates a new solver targeted at the specified matrix size.
 * @param maxRows The expected largest matrix it might have to process.  Can be larger.
 * @param maxCols The expected largest matrix it might have to process.  Can be larger.
 */
  public SolvePseudoInverseSvd(  int maxRows,  int maxCols){
    svd=DecompositionFactory.svd(maxRows,maxCols,true,true,true);
  }
  /** 
 * Creates a solver targeted at matrices around 100x100
 */
  public SolvePseudoInverseSvd(){
    this(100,100);
  }
  @Override public boolean setA(  DenseMatrix64F A){
    pinv.reshape(A.numCols,A.numRows,false);
    if (!svd.decompose(A))     return false;
    DenseMatrix64F U_t=svd.getU(null,true);
    DenseMatrix64F V=svd.getV(null,false);
    double[] S=svd.getSingularValues();
    int N=Math.min(A.numRows,A.numCols);
    double maxSingular=0;
    for (int i=0; i < N; i++) {
      if (S[i] > maxSingular)       maxSingular=S[i];
    }
    double tau=threshold * Math.max(A.numCols,A.numRows) * maxSingular;
    for (int i=0; i < N; i++) {
      double s=S[i];
      if (s < tau)       S[i]=0;
 else       S[i]=1.0 / S[i];
    }
    for (int i=0; i < V.numRows; i++) {
      int index=i * V.numCols;
      for (int j=0; j < V.numCols; j++) {
        V.data[index++]*=S[j];
      }
    }
    CommonOps.mult(V,U_t,pinv);
    return true;
  }
  @Override public double quality(){
    throw new IllegalArgumentException("Not supported by this solver.");
  }
  @Override public void solve(  DenseMatrix64F b,  DenseMatrix64F x){
    CommonOps.mult(pinv,b,x);
  }
  @Override public void invert(  DenseMatrix64F A_inv){
    A_inv.set(pinv);
  }
  @Override public boolean modifiesA(){
    return svd.inputModified();
  }
  @Override public boolean modifiesB(){
    return false;
  }
  /** 
 * Specify the relative threshold used to select singular values.  By default it's UtilEjml.EPS.
 * @param threshold The singular value threshold
 */
  public void setThreshold(  double threshold){
    this.threshold=threshold;
  }
}
