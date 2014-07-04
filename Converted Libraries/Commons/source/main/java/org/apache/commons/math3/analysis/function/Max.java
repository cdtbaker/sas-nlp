package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.util.FastMath;
/** 
 * Maximum function.
 * @since 3.0
 * @version $Id: Max.java 1364377 2012-07-22 17:39:16Z tn $
 */
public class Max implements BivariateFunction {
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x,  double y){
    return FastMath.max(x,y);
  }
}
