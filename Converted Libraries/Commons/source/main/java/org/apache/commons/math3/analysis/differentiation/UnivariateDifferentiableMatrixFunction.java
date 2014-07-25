package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Extension of {@link UnivariateMatrixFunction} representing a univariate differentiable matrix function.
 * @version $Id: UnivariateDifferentiableMatrixFunction.java 1462496 2013-03-29 14:56:08Z psteitz $
 * @since 3.1
 */
public interface UnivariateDifferentiableMatrixFunction extends UnivariateMatrixFunction {
  /** 
 * Compute the value for the function.
 * @param x the point for which the function value should be computed
 * @return the value
 * @exception MathIllegalArgumentException if {@code x} does not
 * satisfy the function's constraints (argument out of bound, or unsupported
 * derivative order for example)
 */
  DerivativeStructure[][] value(  DerivativeStructure x) throws MathIllegalArgumentException ;
}
