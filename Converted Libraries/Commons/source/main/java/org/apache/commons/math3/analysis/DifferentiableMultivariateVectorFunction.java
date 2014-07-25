package org.apache.commons.math3.analysis;
/** 
 * Extension of {@link MultivariateVectorFunction} representing a differentiable
 * multivariate vectorial function.
 * @version $Id: DifferentiableMultivariateVectorFunction.java 1415149 2012-11-29 13:12:55Z erans $
 * @since 2.0
 * @deprecated as of 3.1 replaced by {@link org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction}
 */
@Deprecated public interface DifferentiableMultivariateVectorFunction extends MultivariateVectorFunction {
  /** 
 * Returns the jacobian function.
 * @return the jacobian function
 */
  MultivariateMatrixFunction jacobian();
}
