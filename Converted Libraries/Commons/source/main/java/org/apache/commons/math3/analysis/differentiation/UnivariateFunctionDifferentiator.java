package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.UnivariateFunction;
/** 
 * Interface defining the function differentiation operation.
 * @version $Id: UnivariateFunctionDifferentiator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.1
 */
public interface UnivariateFunctionDifferentiator {
  /** 
 * Create an implementation of a {@link UnivariateDifferentiableFunctiondifferential} from a regular {@link UnivariateFunction function}.
 * @param function function to differentiate
 * @return differential function
 */
  UnivariateDifferentiableFunction differentiate(  UnivariateFunction function);
}
