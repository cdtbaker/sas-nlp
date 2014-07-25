package org.apache.commons.math3.analysis;
/** 
 * An interface representing a univariate vectorial function.
 * @version $Id: UnivariateVectorFunction.java 1364387 2012-07-22 18:14:11Z tn $
 * @since 2.0
 */
public interface UnivariateVectorFunction {
  /** 
 * Compute the value for the function.
 * @param x the point for which the function value should be computed
 * @return the value
 */
  double[] value(  double x);
}
