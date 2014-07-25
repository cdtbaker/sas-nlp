package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Extension of {@link UnivariateVectorFunction} representing a univariate differentiable vectorial function.
 * @version $Id: UnivariateDifferentiableVectorFunction.java 1462699 2013-03-30 04:09:17Z psteitz $
 * @since 3.1
 */
public interface UnivariateDifferentiableVectorFunction extends UnivariateVectorFunction {
  /** 
 * Compute the value for the function.
 * @param x the point for which the function value should be computed
 * @return the value
 * @exception MathIllegalArgumentException if {@code x} does not
 * satisfy the function's constraints (argument out of bound, or unsupported
 * derivative order for example)
 */
  DerivativeStructure[] value(  DerivativeStructure x) throws MathIllegalArgumentException ;
}
