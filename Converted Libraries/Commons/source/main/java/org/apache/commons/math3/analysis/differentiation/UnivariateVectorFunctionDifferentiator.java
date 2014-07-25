package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;
/** 
 * Interface defining the function differentiation operation.
 * @version $Id: UnivariateVectorFunctionDifferentiator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.1
 */
public interface UnivariateVectorFunctionDifferentiator {
  /** 
 * Create an implementation of a {@link UnivariateDifferentiableVectorFunctiondifferential} from a regular {@link UnivariateVectorFunction vector function}.
 * @param function function to differentiate
 * @return differential function
 */
  UnivariateDifferentiableVectorFunction differentiate(  UnivariateVectorFunction function);
}
