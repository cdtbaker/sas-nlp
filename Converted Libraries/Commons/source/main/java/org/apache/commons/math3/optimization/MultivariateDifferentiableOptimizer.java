package org.apache.commons.math3.optimization;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
/** 
 * This interface represents an optimization algorithm for{@link MultivariateDifferentiableFunction scalar differentiable objective
 * functions}.
 * Optimization algorithms find the input point set that either {@link GoalTypemaximize or minimize} an objective function.
 * @see MultivariateOptimizer
 * @see MultivariateDifferentiableVectorOptimizer
 * @version $Id: MultivariateDifferentiableOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated public interface MultivariateDifferentiableOptimizer extends BaseMultivariateOptimizer<MultivariateDifferentiableFunction> {
}
