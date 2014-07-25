package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.BivariateFunction;
/** 
 * Multiply the two operands.
 * @since 3.0
 * @version $Id: Multiply.java 1364377 2012-07-22 17:39:16Z tn $
 */
public class Multiply implements BivariateFunction {
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x,  double y){
    return x * y;
  }
}
