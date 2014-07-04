package org.apache.commons.math3.analysis;
/** 
 * Extension of {@link UnivariateMatrixFunction} representing a differentiable univariate matrix function.
 * @version $Id: DifferentiableUnivariateMatrixFunction.java 1383854 2012-09-12 08:55:32Z luc $
 * @since 2.0
 * @deprecated as of 3.1 replaced by  {@link org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableMatrixFunction}
 */
public interface DifferentiableUnivariateMatrixFunction extends UnivariateMatrixFunction {
  /** 
 * Returns the derivative of the function
 * @return  the derivative function
 */
  UnivariateMatrixFunction derivative();
}
