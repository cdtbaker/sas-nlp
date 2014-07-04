package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.util.FastMath;
/** 
 * Arc-tangent function.
 * @since 3.0
 * @version $Id: Atan2.java 1364377 2012-07-22 17:39:16Z tn $
 */
public class Atan2 implements BivariateFunction {
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x,  double y){
    return FastMath.atan2(x,y);
  }
}
