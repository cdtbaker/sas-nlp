package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.BivariateFunction;
/** 
 * Divide the first operand by the second.
 * @since 3.0
 * @version $Id: Divide.java 1364377 2012-07-22 17:39:16Z tn $
 */
public class Divide implements BivariateFunction {
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x,  double y){
    return x / y;
  }
}
