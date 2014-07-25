package org.apache.commons.math3.optimization;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
/** 
 * This interface is mainly intended to enforce the internal coherence of
 * Commons-Math. Users of the API are advised to base their code on
 * the following interfaces:
 * <ul>
 * <li>{@link org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer}</li>
 * </ul>
 * @param<FUNC>
 *  Type of the objective function to be optimized.
 * @version $Id: BaseMultivariateVectorOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.0
 */
@Deprecated public interface BaseMultivariateVectorOptimizer<FUNC extends MultivariateVectorFunction> extends BaseOptimizer<PointVectorValuePair> {
  /** 
 * Optimize an objective function.
 * Optimization is considered to be a weighted least-squares minimization.
 * The cost function to be minimized is
 * <code>&sum;weight<sub>i</sub>(objective<sub>i</sub> - target<sub>i</sub>)<sup>2</sup></code>
 * @param f Objective function.
 * @param target Target value for the objective functions at optimum.
 * @param weight Weights for the least squares cost computation.
 * @param startPoint Start point for optimization.
 * @return the point/value pair giving the optimal value for objective
 * function.
 * @param maxEval Maximum number of function evaluations.
 * @throws org.apache.commons.math3.exception.DimensionMismatchExceptionif the start point dimension is wrong.
 * @throws org.apache.commons.math3.exception.TooManyEvaluationsExceptionif the maximal number of evaluations is exceeded.
 * @throws org.apache.commons.math3.exception.NullArgumentException if
 * any argument is {@code null}.
 * @deprecated As of 3.1. In 4.0, this will be replaced by the declaration
 * corresponding to this {@link org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateVectorOptimizer#optimize(int,MultivariateVectorFunction,OptimizationData[]) method}.
 */
  @Deprecated PointVectorValuePair optimize(  int maxEval,  FUNC f,  double[] target,  double[] weight,  double[] startPoint);
}
