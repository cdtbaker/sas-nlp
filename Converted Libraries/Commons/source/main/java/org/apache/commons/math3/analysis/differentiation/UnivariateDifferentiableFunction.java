package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
/** 
 * Interface for univariate functions derivatives.
 * <p>This interface represents a simple function which computes
 * both the value and the first derivative of a mathematical function.
 * The derivative is computed with respect to the input variable.</p>
 * @see UnivariateDifferentiableFunction
 * @see UnivariateFunctionDifferentiator
 * @since 3.1
 * @version $Id: UnivariateDifferentiableFunction.java 1462496 2013-03-29 14:56:08Z psteitz $
 */
public interface UnivariateDifferentiableFunction extends UnivariateFunction {
  /** 
 * Simple mathematical function.
 * <p>{@link UnivariateDifferentiableFunction} classes compute both the
 * value and the first derivative of the function.</p>
 * @param t function input value
 * @return function result
 * @exception DimensionMismatchException if t is inconsistent with the
 * function's free parameters or order
 */
  DerivativeStructure value(  DerivativeStructure t) throws DimensionMismatchException ;
}
