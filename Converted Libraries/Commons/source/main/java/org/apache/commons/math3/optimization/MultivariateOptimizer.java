package org.apache.commons.math3.optimization;
import org.apache.commons.math3.analysis.MultivariateFunction;
/** 
 * This interface represents an optimization algorithm for {@link MultivariateFunctionscalar objective functions}.
 * <p>Optimization algorithms find the input point set that either {@link GoalTypemaximize or minimize} an objective function.</p>
 * @see MultivariateDifferentiableOptimizer
 * @see MultivariateDifferentiableVectorOptimizer
 * @version $Id: MultivariateOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public interface MultivariateOptimizer extends BaseMultivariateOptimizer<MultivariateFunction> {
}
