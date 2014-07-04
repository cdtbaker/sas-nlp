package org.apache.commons.math3.optim.nonlinear.vector;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Model (vector) function to be optimized.
 * @version $Id: ModelFunction.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class ModelFunction implements OptimizationData {
  /** 
 * Function to be optimized. 
 */
  private final MultivariateVectorFunction model;
  /** 
 * @param m Model function to be optimized.
 */
  public ModelFunction(  MultivariateVectorFunction m){
    model=m;
  }
  /** 
 * Gets the model function to be optimized.
 * @return the model function.
 */
  public MultivariateVectorFunction getModelFunction(){
    return model;
  }
}
