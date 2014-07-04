package org.apache.commons.math3.optim;
/** 
 * Starting point (first guess) of the optimization procedure.
 * <br/>
 * Immutable class.
 * @version $Id: InitialGuess.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class InitialGuess implements OptimizationData {
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
