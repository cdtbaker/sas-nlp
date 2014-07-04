package org.apache.commons.math3.optim.nonlinear.vector;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Target of the optimization procedure.
 * They are the values which the objective vector function must reproduce
 * When the parameters of the model have been optimized.
 * <br/>
 * Immutable class.
 * @version $Id: Target.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class Target implements OptimizationData {
  /** 
 * Target values (of the objective vector function). 
 */
  private final double[] target;
  /** 
 * @param observations Target values.
 */
  public Target(  double[] observations){
    target=observations.clone();
  }
  /** 
 * Gets the initial guess.
 * @return the initial guess.
 */
  public double[] getTarget(){
    return target.clone();
  }
}
