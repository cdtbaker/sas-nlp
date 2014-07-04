package org.ejml.ops;
import org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionInner;
import org.ejml.data.DenseMatrix64F;
import java.util.Random;
import static org.ejml.ops.CommonOps.multAdd;
/** 
 * Generates random vectors based on a zero mean multivariate Gaussian distribution.  The covariance
 * matrix is provided in the contructor.
 */
public class CovarianceRandomDraw {
  private DenseMatrix64F A;
  private Random rand;
  private DenseMatrix64F r;
  /** 
 * Creates a random distribution with the specified mean and covariance.  The references
 * to the variables are not saved, their value are copied.
 * @param rand Used to create the random numbers for the draw. Reference is saved.
 * @param cov The covariance of the stribution.  Not modified.
 */
  public CovarianceRandomDraw(  Random rand,  DenseMatrix64F cov){
    r=new DenseMatrix64F(cov.numRows,1);
    CholeskyDecompositionInner choleky=new CholeskyDecompositionInner(true);
    choleky.decompose(cov);
    A=choleky.getT();
    this.rand=rand;
  }
  /** 
 * Makes a draw on the distribution.  The results are added to parameter 'x'
 */
  public void next(  DenseMatrix64F x){
    for (int i=0; i < r.numRows; i++) {
      r.set(i,0,rand.nextGaussian());
    }
    multAdd(A,r,x);
  }
  /** 
 * Computes the likelihood of the random draw
 * @return The likelihood.
 */
  public double computeLikelihoodP(){
    double ret=1.0;
    for (int i=0; i < r.numRows; i++) {
      double a=r.get(i,0);
      ret*=Math.exp(-a * a / 2.0);
    }
    return ret;
  }
}
