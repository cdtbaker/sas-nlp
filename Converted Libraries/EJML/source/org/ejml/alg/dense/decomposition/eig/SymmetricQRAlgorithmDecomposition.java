package org.ejml.alg.dense.decomposition.eig;
import org.ejml.alg.dense.decomposition.eig.symm.SymmetricQREigenHelper;
import org.ejml.alg.dense.decomposition.eig.symm.SymmetricQrAlgorithm;
import org.ejml.alg.dense.decomposition.hessenberg.TridiagonalSimilarDecomposition;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.EigenDecomposition;
import org.ejml.ops.CommonOps;
/** 
 * <p>
 * Computes the eigenvalues and eigenvectors of a real symmetric matrix using the symmetric implicit QR algorithm.
 * Inside each iteration a QR decomposition of A<sub>i</sub>-p<sub>i</sub>I is implicitly computed.
 * </p>
 * <p>
 * This implementation is based on the algorithm is sketched out in:<br>
 * David S. Watkins, "Fundamentals of Matrix Computations," Second Edition. page 377-385
 * </p>
 * @see org.ejml.alg.dense.decomposition.eig.symm.SymmetricQrAlgorithm
 * @see org.ejml.alg.dense.decomposition.hessenberg.TridiagonalDecompositionHouseholder
 * @author Peter Abeles
 */
public class SymmetricQRAlgorithmDecomposition implements EigenDecomposition<DenseMatrix64F> {
  private TridiagonalSimilarDecomposition<DenseMatrix64F> decomp;
  private SymmetricQREigenHelper helper;
  private SymmetricQrAlgorithm vector;
  private boolean computeVectorsWithValues=false;
  private double values[];
  private double diag[];
  private double off[];
  private double diagSaved[];
  private double offSaved[];
  private DenseMatrix64F V;
  private DenseMatrix64F eigenvectors[];
  boolean computeVectors;
  public SymmetricQRAlgorithmDecomposition(  TridiagonalSimilarDecomposition<DenseMatrix64F> decomp,  boolean computeVectors){
    this.decomp=decomp;
    this.computeVectors=computeVectors;
    helper=new SymmetricQREigenHelper();
    vector=new SymmetricQrAlgorithm(helper);
  }
  public SymmetricQRAlgorithmDecomposition(  boolean computeVectors){
    this(DecompositionFactory.tridiagonal(0),computeVectors);
  }
  public void setComputeVectorsWithValues(  boolean computeVectorsWithValues){
    if (!computeVectors)     throw new IllegalArgumentException("Compute eigenvalues has been set to false");
    this.computeVectorsWithValues=computeVectorsWithValues;
  }
  /** 
 * Used to limit the number of internal QR iterations that the QR algorithm performs.  20
 * should be enough for most applications.
 * @param max The maximum number of QR iterations it will perform.
 */
  public void setMaxIterations(  int max){
    vector.setMaxIterations(max);
  }
  @Override public int getNumberOfEigenvalues(){
    return helper.getMatrixSize();
  }
  @Override public Complex64F getEigenvalue(  int index){
    return new Complex64F(values[index],0);
  }
  @Override public DenseMatrix64F getEigenVector(  int index){
    return eigenvectors[index];
  }
  /** 
 * Decomposes the matrix using the QR algorithm.  Care was taken to minimize unnecessary memory copying
 * and cache skipping.
 * @param orig The matrix which is being decomposed.  Not modified.
 * @return true if it decomposed the matrix or false if an error was detected.  This will not catch all errors.
 */
  @Override public boolean decompose(  DenseMatrix64F orig){
    if (orig.numCols != orig.numRows)     throw new IllegalArgumentException("Matrix must be square.");
    int N=orig.numRows;
    if (!decomp.decompose(orig))     return false;
    if (diag == null || diag.length < N) {
      diag=new double[N];
      off=new double[N - 1];
    }
    decomp.getDiagonal(diag,off);
    helper.init(diag,off,N);
    if (computeVectors) {
      if (computeVectorsWithValues) {
        return extractTogether();
      }
 else {
        return extractSeparate(N);
      }
    }
 else {
      return computeEigenValues();
    }
  }
  @Override public boolean inputModified(){
    return decomp.inputModified();
  }
  private boolean extractTogether(){
    V=decomp.getQ(V,true);
    helper.setQ(V);
    vector.setFastEigenvalues(false);
    if (!vector.process(-1,null,null))     return false;
    eigenvectors=CommonOps.rowsToVector(V,eigenvectors);
    values=helper.copyEigenvalues(values);
    return true;
  }
  private boolean extractSeparate(  int numCols){
    if (!computeEigenValues())     return false;
    helper.reset(numCols);
    diagSaved=helper.swapDiag(diagSaved);
    offSaved=helper.swapOff(offSaved);
    V=decomp.getQ(V,true);
    vector.setQ(V);
    if (!vector.process(-1,null,null,values))     return false;
    values=helper.copyEigenvalues(values);
    eigenvectors=CommonOps.rowsToVector(V,eigenvectors);
    return true;
  }
  /** 
 * Computes eigenvalues only
 * @return
 */
  private boolean computeEigenValues(){
    diagSaved=helper.copyDiag(diagSaved);
    offSaved=helper.copyOff(offSaved);
    vector.setQ(null);
    vector.setFastEigenvalues(true);
    if (!vector.process(-1,null,null))     return false;
    values=helper.copyEigenvalues(values);
    return true;
  }
}
