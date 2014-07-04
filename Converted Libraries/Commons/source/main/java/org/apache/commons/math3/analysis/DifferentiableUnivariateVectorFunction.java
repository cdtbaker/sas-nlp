package org.apache.commons.math3.analysis;
/** 
 * Extension of {@link UnivariateVectorFunction} representing a differentiable univariate vectorial function.
 * @version $Id: DifferentiableUnivariateVectorFunction.java 1383852 2012-09-12 08:54:40Z luc $
 * @since 2.0
 * @deprecated as of 3.1 replaced by {@link org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction}
 */
public interface DifferentiableUnivariateVectorFunction extends UnivariateVectorFunction {
  /** 
 * Returns the derivative of the function
 * @return  the derivative function
 */
  UnivariateVectorFunction derivative();
}
