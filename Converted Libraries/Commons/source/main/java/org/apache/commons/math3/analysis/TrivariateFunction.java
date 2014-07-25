package org.apache.commons.math3.analysis;
/** 
 * An interface representing a trivariate real function.
 * @since 2.2
 * @version $Id: TrivariateFunction.java 1364387 2012-07-22 18:14:11Z tn $
 */
public interface TrivariateFunction {
  /** 
 * Compute the value for the function.
 * @param x x-coordinate for which the function value should be computed.
 * @param y y-coordinate for which the function value should be computed.
 * @param z z-coordinate for which the function value should be computed.
 * @return the value.
 */
  double value(  double x,  double y,  double z);
}
