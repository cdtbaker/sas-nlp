package org.apache.commons.math3.analysis;
/** 
 * An interface representing a multivariate matrix function.
 * @version $Id: MultivariateMatrixFunction.java 1462485 2013-03-29 14:36:19Z psteitz $
 * @since 2.0
 */
public interface MultivariateMatrixFunction {
  /** 
 * Compute the value for the function at the given point.
 * @param point point at which the function must be evaluated
 * @return function value for the given point
 * @exception IllegalArgumentException if point's dimension is wrong
 */
  double[][] value(  double[] point) throws IllegalArgumentException ;
}
