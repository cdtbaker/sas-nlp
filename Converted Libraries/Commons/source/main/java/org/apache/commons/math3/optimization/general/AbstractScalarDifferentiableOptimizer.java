package org.apache.commons.math3.optimization.general;
import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.optimization.DifferentiableMultivariateOptimizer;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer;
/** 
 * Base class for implementing optimizers for multivariate scalar
 * differentiable functions.
 * It contains boiler-plate code for dealing with gradient evaluation.
 * @version $Id: AbstractScalarDifferentiableOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public abstract class AbstractScalarDifferentiableOptimizer extends BaseAbstractMultivariateOptimizer<DifferentiableMultivariateFunction> implements DifferentiableMultivariateOptimizer {
  /** 
 * Objective function gradient.
 */
  private MultivariateVectorFunction gradient;
  /** 
 * Simple constructor with default settings.
 * The convergence check is set to a{@link org.apache.commons.math3.optimization.SimpleValueCheckerSimpleValueChecker}.
 * @deprecated See {@link org.apache.commons.math3.optimization.SimpleValueChecker#SimpleValueChecker()}
 */
  @Deprecated protected AbstractScalarDifferentiableOptimizer(){
  }
  /** 
 * @param checker Convergence checker.
 */
  protected AbstractScalarDifferentiableOptimizer(  ConvergenceChecker<PointValuePair> checker){
    super(checker);
  }
  /** 
 * Compute the gradient vector.
 * @param evaluationPoint Point at which the gradient must be evaluated.
 * @return the gradient at the specified point.
 * @throws org.apache.commons.math3.exception.TooManyEvaluationsExceptionif the allowed number of evaluations is exceeded.
 */
  protected double[] computeObjectiveGradient(  final double[] evaluationPoint){
    return gradient.value(evaluationPoint);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected PointValuePair optimizeInternal(  int maxEval,  final DifferentiableMultivariateFunction f,  final GoalType goalType,  final double[] startPoint){
    gradient=f.gradient();
    return super.optimizeInternal(maxEval,f,goalType,startPoint);
  }
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
 */
  public PointValuePair optimize(  final int maxEval,  final MultivariateDifferentiableFunction f,  final GoalType goalType,  final double[] startPoint){
    return optimizeInternal(maxEval,FunctionUtils.toDifferentiableMultivariateFunction(f),goalType,startPoint);
  }
}
