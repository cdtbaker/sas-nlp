package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.BivariateFunction;
/** 
 * Subtract the second operand from the first.
 * @since 3.0
 * @version $Id: Subtract.java 1364377 2012-07-22 17:39:16Z tn $
 */
public class Subtract implements BivariateFunction {
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x,  double y){
    return x - y;
  }
}
