package org.ejml.alg.dense.decomposition.eig;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.EigenDecomposition;
import org.ejml.ops.MatrixFeatures;
/** 
 * Checks to see what type of matrix is being decomposed and calls different eigenvalue decomposition
 * algorithms depending on the results.  This primarily checks to see if the matrix is symmetric or not.
 * @author Peter Abeles
 */
public class SwitchingEigenDecomposition implements EigenDecomposition<DenseMatrix64F> {
  private double tol;
  EigenDecomposition<DenseMatrix64F> symmetricAlg;
  EigenDecomposition<DenseMatrix64F> generalAlg;
  boolean symmetric;
  boolean computeVectors;
  DenseMatrix64F A=new DenseMatrix64F(1,1);
  /** 
 * @param computeVectors
 * @param tol Tolerance for a matrix being symmetric
 */
  public SwitchingEigenDecomposition(  int matrixSize,  boolean computeVectors,  double tol){
    symmetricAlg=DecompositionFactory.eig(matrixSize,computeVectors,true);
    generalAlg=DecompositionFactory.eig(matrixSize,computeVectors,false);
    this.computeVectors=computeVectors;
    this.tol=tol;
  }
  public SwitchingEigenDecomposition(  int matrixSize){
    this(matrixSize,true,1e-8);
  }
  @Override public int getNumberOfEigenvalues(){
    return symmetric ? symmetricAlg.getNumberOfEigenvalues() : generalAlg.getNumberOfEigenvalues();
  }
  @Override public Complex64F getEigenvalue(  int index){
    return symmetric ? symmetricAlg.getEigenvalue(index) : generalAlg.getEigenvalue(index);
  }
  @Override public DenseMatrix64F getEigenVector(  int index){
    if (!computeVectors)     throw new IllegalArgumentException("Configured to not compute eignevectors");
    return symmetric ? symmetricAlg.getEigenVector(index) : generalAlg.getEigenVector(index);
  }
  @Override public boolean decompose(  DenseMatrix64F orig){
    A.setReshape(orig);
    symmetric=MatrixFeatures.isSymmetric(A,tol);
    return symmetric ? symmetricAlg.decompose(A) : generalAlg.decompose(A);
  }
  @Override public boolean inputModified(){
    return false;
  }
}
