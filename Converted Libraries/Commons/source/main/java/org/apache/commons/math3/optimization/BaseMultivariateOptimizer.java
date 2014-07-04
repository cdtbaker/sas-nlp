package org.apache.commons.math3.optimization;
import org.apache.commons.math3.analysis.MultivariateFunction;
/** 
 * This interface is mainly intended to enforce the internal coherence of
 * Commons-FastMath. Users of the API are advised to base their code on
 * the following interfaces:
 * <ul>
 * <li>{@link org.apache.commons.math3.optimization.MultivariateOptimizer}</li>
 * <li>{@link org.apache.commons.math3.optimization.MultivariateDifferentiableOptimizer}</li>
 * </ul>
 * @param<FUNC>
 *  Type of the objective function to be optimized.
 * @version $Id: BaseMultivariateOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.0
 */
@Deprecated public interface BaseMultivariateOptimizer<FUNC extends MultivariateFunction> extends BaseOptimizer<PointValuePair> {
  /** 
 * Optimize an objective function.
 * @param f Objective function.
 * @param goalType Type of optimization goal: either{@link GoalType#MAXIMIZE} or {@link GoalType#MINIMIZE}.
 * @param startPoint Start point for optimization.
 * @param maxEval Maximum number of function evaluations.
 * @return the point/value pair giving the optimal value for objective
 * function.
 * @throws org.apache.commons.math3.exception.DimensionMismatchExceptionif the start point dimension is wrong.
 * @throws org.apache.commons.math3.exception.TooManyEvaluationsExceptionif the maximal number of evaluations is exceeded.
 * @throws org.apache.commons.math3.exception.NullArgumentException if
 * any argument is {@code null}.
 * @deprecated As of 3.1. In 4.0, it will be replaced by the declaration
 * corresponding to this {@link org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer#optimize(int,MultivariateFunction,GoalType,OptimizationData[]) method}.
 */
  @Deprecated PointValuePair optimize(  int maxEval,  FUNC f,  GoalType goalType,  double[] startPoint);
}
