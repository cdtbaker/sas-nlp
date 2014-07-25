package org.apache.commons.math3.optim.univariate;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Scalar function to be optimized.
 * @version $Id: UnivariateObjectiveFunction.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class UnivariateObjectiveFunction implements OptimizationData {
  /** 
 * Function to be optimized. 
 */
  private final UnivariateFunction function;
  /** 
 * @param f Function to be optimized.
 */
  public UnivariateObjectiveFunction(  UnivariateFunction f){
    function=f;
  }
  /** 
 * Gets the function to be optimized.
 * @return the objective function.
 */
  public UnivariateFunction getObjectiveFunction(){
    return function;
  }
}
