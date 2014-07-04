package org.apache.commons.math3.optim.nonlinear.vector;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Jacobian of the model (vector) function to be optimized.
 * @version $Id: ModelFunctionJacobian.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class ModelFunctionJacobian implements OptimizationData {
  /** 
 * Function to be optimized. 
 */
  private final MultivariateMatrixFunction jacobian;
  /** 
 * @param j Jacobian of the model function to be optimized.
 */
  public ModelFunctionJacobian(  MultivariateMatrixFunction j){
    jacobian=j;
  }
  /** 
 * Gets the Jacobian of the model function to be optimized.
 * @return the model function Jacobian.
 */
  public MultivariateMatrixFunction getModelFunctionJacobian(){
    return jacobian;
  }
}
