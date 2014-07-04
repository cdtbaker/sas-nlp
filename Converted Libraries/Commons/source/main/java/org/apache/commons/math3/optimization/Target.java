package org.apache.commons.math3.optimization;
/** 
 * Target of the optimization procedure.
 * They are the values which the objective vector function must reproduce
 * When the parameters of the model have been optimized.
 * <br/>
 * Immutable class.
 * @version $Id: Target.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated public class Target implements OptimizationData {
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
