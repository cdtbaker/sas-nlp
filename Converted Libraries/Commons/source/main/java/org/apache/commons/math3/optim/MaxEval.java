package org.apache.commons.math3.optim;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
/** 
 * Maximum number of evaluations of the function to be optimized.
 * @version $Id: MaxEval.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class MaxEval implements OptimizationData {
  /** 
 * Allowed number of evalutations. 
 */
  private final int maxEval;
  /** 
 * @param max Allowed number of evalutations.
 * @throws NotStrictlyPositiveException if {@code max <= 0}.
 */
  public MaxEval(  int max){
    if (max <= 0) {
      throw new NotStrictlyPositiveException(max);
    }
    maxEval=max;
  }
  /** 
 * Gets the maximum number of evaluations.
 * @return the allowed number of evaluations.
 */
  public int getMaxEval(){
    return maxEval;
  }
  /** 
 * Factory method that creates instance of this class that represents
 * a virtually unlimited number of evaluations.
 * @return a new instance suitable for allowing {@link Integer#MAX_VALUE}evaluations.
 */
  public static MaxEval unlimited(){
    return new MaxEval(Integer.MAX_VALUE);
  }
}
