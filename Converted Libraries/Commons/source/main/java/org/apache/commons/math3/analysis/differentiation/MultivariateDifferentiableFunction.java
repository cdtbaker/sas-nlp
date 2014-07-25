package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Extension of {@link MultivariateFunction} representing a
 * multivariate differentiable real function.
 * @version $Id: MultivariateDifferentiableFunction.java 1462496 2013-03-29 14:56:08Z psteitz $
 * @since 3.1
 */
public interface MultivariateDifferentiableFunction extends MultivariateFunction {
  /** 
 * Compute the value for the function at the given point.
 * @param point Point at which the function must be evaluated.
 * @return the function value for the given point.
 * @exception MathIllegalArgumentException if {@code point} does not
 * satisfy the function's constraints (wrong dimension, argument out of bound,
 * or unsupported derivative order for example)
 */
  DerivativeStructure value(  DerivativeStructure[] point) throws MathIllegalArgumentException ;
}
