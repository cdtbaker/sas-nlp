package org.apache.commons.math3.analysis;
/** 
 * Extension of {@link UnivariateFunction} representing a differentiable univariate real function.
 * @version $Id: DifferentiableUnivariateFunction.java 1383845 2012-09-12 08:34:10Z luc $
 * @deprecated as of 3.1 replaced by {@link org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction}
 */
@Deprecated public interface DifferentiableUnivariateFunction extends UnivariateFunction {
  /** 
 * Returns the derivative of the function
 * @return  the derivative function
 */
  UnivariateFunction derivative();
}
