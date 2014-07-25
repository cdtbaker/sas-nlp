package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
/** 
 * Interface defining the function differentiation operation.
 * @version $Id: UnivariateMatrixFunctionDifferentiator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.1
 */
public interface UnivariateMatrixFunctionDifferentiator {
  /** 
 * Create an implementation of a {@link UnivariateDifferentiableMatrixFunctiondifferential} from a regular {@link UnivariateMatrixFunction matrix function}.
 * @param function function to differentiate
 * @return differential function
 */
  UnivariateDifferentiableMatrixFunction differentiate(  UnivariateMatrixFunction function);
}
