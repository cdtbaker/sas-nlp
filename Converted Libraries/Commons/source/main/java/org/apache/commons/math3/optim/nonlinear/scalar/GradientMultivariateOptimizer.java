package org.apache.commons.math3.optim.nonlinear.scalar;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
/** 
 * Base class for implementing optimizers for multivariate scalar
 * differentiable functions.
 * It contains boiler-plate code for dealing with gradient evaluation.
 * @version $Id: GradientMultivariateOptimizer.java 1443444 2013-02-07 12:41:36Z erans $
 * @since 3.1
 */
public abstract class GradientMultivariateOptimizer extends MultivariateOptimizer {
  /** 
 * Gradient of the objective function.
 */
  private MultivariateVectorFunction gradient;
  /** 
 * @param checker Convergence checker.
 */
  protected GradientMultivariateOptimizer(  ConvergenceChecker<PointValuePair> checker){
    super(checker);
  }
  /** 
 * Compute the gradient vector.
 * @param params Point at which the gradient must be evaluated.
 * @return the gradient at the specified point.
 */
  protected double[] computeObjectiveGradient(  final double[] params){
    return gradient.value(params);
  }
  /** 
 * {@inheritDoc}
 * @param optData Optimization data. In addition to those documented in{@link MultivariateOptimizer#parseOptimizationData(OptimizationData[])MultivariateOptimizer}, this method will register the following data:
 * <ul>
 * <li>{@link ObjectiveFunctionGradient}</li>
 * </ul>
 * @return {@inheritDoc}
 * @throws TooManyEvaluationsException if the maximal number of
 * evaluations (of the objective function) is exceeded.
 */
  @Override public PointValuePair optimize(  OptimizationData... optData) throws TooManyEvaluationsException {
    return super.optimize(optData);
  }
  /** 
 * Scans the list of (required and optional) optimization data that
 * characterize the problem.
 * @param optData Optimization data.
 * The following data will be looked for:
 * <ul>
 * <li>{@link ObjectiveFunctionGradient}</li>
 * </ul>
 */
  @Override protected void parseOptimizationData(  OptimizationData... optData){
    super.parseOptimizationData(optData);
    for (    OptimizationData data : optData) {
      if (data instanceof ObjectiveFunctionGradient) {
        gradient=((ObjectiveFunctionGradient)data).getObjectiveFunctionGradient();
        break;
      }
    }
  }
}
