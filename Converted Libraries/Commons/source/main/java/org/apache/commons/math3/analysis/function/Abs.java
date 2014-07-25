package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
/** 
 * Absolute value function.
 * @since 3.0
 * @version $Id: Abs.java 1364377 2012-07-22 17:39:16Z tn $
 */
public class Abs implements UnivariateFunction {
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x){
    return FastMath.abs(x);
  }
}
