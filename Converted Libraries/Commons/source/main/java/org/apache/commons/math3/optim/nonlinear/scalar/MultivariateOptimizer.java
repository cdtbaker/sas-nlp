package org.apache.commons.math3.optim.nonlinear.scalar;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
/** 
 * Base class for a multivariate scalar function optimizer.
 * @version $Id: MultivariateOptimizer.java 1443444 2013-02-07 12:41:36Z erans $
 * @since 3.1
 */
public abstract class MultivariateOptimizer extends BaseMultivariateOptimizer<PointValuePair> {
  /** 
 * Objective function. 
 */
  private MultivariateFunction function;
  /** 
 * Type of optimization. 
 */
  private GoalType goal;
  /** 
 * @param checker Convergence checker.
 */
  protected MultivariateOptimizer(  ConvergenceChecker<PointValuePair> checker){
    super(checker);
  }
  /** 
 * {@inheritDoc}
 * @param optData Optimization data. In addition to those documented in{@link BaseMultivariateOptimizer#parseOptimizationData(OptimizationData[])BaseMultivariateOptimizer}, this method will register the following data:
 * <ul>
 * <li>{@link ObjectiveFunction}</li>
 * <li>{@link GoalType}</li>
 * </ul>
 * @return {@inheritDoc}
 * @throws TooManyEvaluationsException if the maximal number of
 * evaluations is exceeded.
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
 * <li>{@link ObjectiveFunction}</li>
 * <li>{@link GoalType}</li>
 * </ul>
 */
  @Override protected void parseOptimizationData(  OptimizationData... optData){
    super.parseOptimizationData(optData);
    for (    OptimizationData data : optData) {
      if (data instanceof GoalType) {
        goal=(GoalType)data;
        continue;
      }
      if (data instanceof ObjectiveFunction) {
        function=((ObjectiveFunction)data).getObjectiveFunction();
        continue;
      }
    }
  }
  /** 
 * @return the optimization type.
 */
  public GoalType getGoalType(){
    return goal;
  }
  /** 
 * Computes the objective function value.
 * This method <em>must</em> be called by subclasses to enforce the
 * evaluation counter limit.
 * @param params Point at which the objective function must be evaluated.
 * @return the objective function value at the specified point.
 * @throws TooManyEvaluationsException if the maximal number of
 * evaluations is exceeded.
 */
  protected double computeObjectiveValue(  double[] params){
    super.incrementEvaluationCount();
    return function.value(params);
  }
}
