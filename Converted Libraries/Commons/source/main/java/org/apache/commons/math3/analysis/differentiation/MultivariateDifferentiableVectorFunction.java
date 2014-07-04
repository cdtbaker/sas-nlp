package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Extension of {@link MultivariateVectorFunction} representing a
 * multivariate differentiable vectorial function.
 * @version $Id: MultivariateDifferentiableVectorFunction.java 1462496 2013-03-29 14:56:08Z psteitz $
 * @since 3.1
 */
public interface MultivariateDifferentiableVectorFunction extends MultivariateVectorFunction {
  /** 
 * Compute the value for the function at the given point.
 * @param point point at which the function must be evaluated
 * @return function value for the given point
 * @exception MathIllegalArgumentException if {@code point} does not
 * satisfy the function's constraints (wrong dimension, argument out of bound,
 * or unsupported derivative order for example)
 */
  DerivativeStructure[] value(  DerivativeStructure[] point) throws MathIllegalArgumentException ;
}
