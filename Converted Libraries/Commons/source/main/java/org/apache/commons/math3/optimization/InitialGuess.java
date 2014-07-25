package org.apache.commons.math3.optimization;
/** 
 * Starting point (first guess) of the optimization procedure.
 * <br/>
 * Immutable class.
 * @version $Id: InitialGuess.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated public class InitialGuess implements OptimizationData {
  /** 
 * Initial guess. 
 */
  private final double[] init;
  /** 
 * @param startPoint Initial guess.
 */
  public InitialGuess(  double[] startPoint){
    init=startPoint.clone();
  }
  /** 
 * Gets the initial guess.
 * @return the initial guess.
 */
  public double[] getInitialGuess(){
    return init.clone();
  }
}
