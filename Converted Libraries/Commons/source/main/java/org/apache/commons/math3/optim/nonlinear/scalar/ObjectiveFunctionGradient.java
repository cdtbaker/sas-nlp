package org.apache.commons.math3.optim.nonlinear.scalar;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Gradient of the scalar function to be optimized.
 * @version $Id: ObjectiveFunctionGradient.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class ObjectiveFunctionGradient implements OptimizationData {
  /** 
 * Function to be optimized. 
 */
  private final MultivariateVectorFunction gradient;
  /** 
 * @param g Gradient of the function to be optimized.
 */
  public ObjectiveFunctionGradient(  MultivariateVectorFunction g){
    gradient=g;
  }
  /** 
 * Gets the gradient of the function to be optimized.
 * @return the objective function gradient.
 */
  public MultivariateVectorFunction getObjectiveFunctionGradient(){
    return gradient;
  }
}
