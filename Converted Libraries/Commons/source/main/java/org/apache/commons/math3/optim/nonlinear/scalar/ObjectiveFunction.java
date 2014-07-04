package org.apache.commons.math3.optim.nonlinear.scalar;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Scalar function to be optimized.
 * @version $Id: ObjectiveFunction.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class ObjectiveFunction implements OptimizationData {
  /** 
 * Function to be optimized. 
 */
  private final MultivariateFunction function;
  /** 
 * @param f Function to be optimized.
 */
  public ObjectiveFunction(  MultivariateFunction f){
    function=f;
  }
  /** 
 * Gets the function to be optimized.
 * @return the objective function.
 */
  public MultivariateFunction getObjectiveFunction(){
    return function;
  }
}
