package org.apache.commons.math3.optim.nonlinear.vector;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.NonSquareMatrixException;
/** 
 * Weight matrix of the residuals between model and observations.
 * <br/>
 * Immutable class.
 * @version $Id: Weight.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class Weight implements OptimizationData {
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
