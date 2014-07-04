package org.apache.commons.math3.optimization;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.NonSquareMatrixException;
/** 
 * Weight matrix of the residuals between model and observations.
 * <br/>
 * Immutable class.
 * @version $Id: Weight.java 1426759 2012-12-29 13:26:44Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated public class Weight implements OptimizationData {
  /** 
 * Weight matrix. 
 */
  private final RealMatrix weightMatrix;
  /** 
 * Creates a diagonal weight matrix.
 * @param weight List of the values of the diagonal.
 */
  public Weight(  double[] weight){
    weightMatrix=new DiagonalMatrix(weight);
  }
  /** 
 * @param weight Weight matrix.
 * @throws NonSquareMatrixException if the argument is not
 * a square matrix.
 */
  public Weight(  RealMatrix weight){
    if (weight.getColumnDimension() != weight.getRowDimension()) {
      throw new NonSquareMatrixException(weight.getColumnDimension(),weight.getRowDimension());
    }
    weightMatrix=weight.copy();
  }
  /** 
 * Gets the initial guess.
 * @return the initial guess.
 */
  public RealMatrix getWeight(){
    return weightMatrix.copy();
  }
}
