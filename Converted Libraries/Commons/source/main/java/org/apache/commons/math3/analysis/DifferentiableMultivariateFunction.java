package org.apache.commons.math3.analysis;
/** 
 * Extension of {@link MultivariateFunction} representing a differentiable
 * multivariate real function.
 * @version $Id: DifferentiableMultivariateFunction.java 1415149 2012-11-29 13:12:55Z erans $
 * @since 2.0
 * @deprecated as of 3.1 replaced by {@link org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction}
 */
@Deprecated public interface DifferentiableMultivariateFunction extends MultivariateFunction {
  /** 
 * Returns the partial derivative of the function with respect to a point coordinate.
 * <p>
 * The partial derivative is defined with respect to point coordinate
 * x<sub>k</sub>. If the partial derivatives with respect to all coordinates are
 * needed, it may be more efficient to use the {@link #gradient()} method which will
 * compute them all at once.
 * </p>
 * @param k index of the coordinate with respect to which the partial
 * derivative is computed
 * @return the partial derivative function with respect to k<sup>th</sup> point coordinate
 */
  MultivariateFunction partialDerivative(  int k);
  /** 
 * Returns the gradient function.
 * <p>If only one partial derivative with respect to a specific coordinate is
 * needed, it may be more efficient to use the {@link #partialDerivative(int)} method
 * which will compute only the specified component.</p>
 * @return the gradient function
 */
  MultivariateVectorFunction gradient();
}
