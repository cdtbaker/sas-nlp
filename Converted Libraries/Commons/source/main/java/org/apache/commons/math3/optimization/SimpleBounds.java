package org.apache.commons.math3.optimization;
/** 
 * Simple optimization constraints: lower and upper bounds.
 * The valid range of the parameters is an interval that can be infinite
 * (in one or both directions).
 * <br/>
 * Immutable class.
 * @version $Id: SimpleBounds.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated public class SimpleBounds implements OptimizationData {
  /** 
 * Lower bounds. 
 */
  private final double[] lower;
  /** 
 * Upper bounds. 
 */
  private final double[] upper;
  /** 
 * @param lB Lower bounds.
 * @param uB Upper bounds.
 */
  public SimpleBounds(  double[] lB,  double[] uB){
    lower=lB.clone();
    upper=uB.clone();
  }
  /** 
 * Gets the lower bounds.
 * @return the initial guess.
 */
  public double[] getLower(){
    return lower.clone();
  }
  /** 
 * Gets the lower bounds.
 * @return the initial guess.
 */
  public double[] getUpper(){
    return upper.clone();
  }
}
