package org.apache.commons.math3.optimization.general;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.GradientFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer;
/** 
 * Base class for implementing optimizers for multivariate scalar
 * differentiable functions.
 * It contains boiler-plate code for dealing with gradient evaluation.
 * @version $Id: AbstractDifferentiableOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated public abstract class AbstractDifferentiableOptimizer extends BaseAbstractMultivariateOptimizer<MultivariateDifferentiableFunction> {
  /** 
 * Objective function gradient.
 */
  private MultivariateVectorFunction gradient;
  /** 
 * @param checker Convergence checker.
 */
  protected AbstractDifferentiableOptimizer(  ConvergenceChecker<PointValuePair> checker){
    super(checker);
  }
  /** 
 * Compute the gradient vector.
 * @param evaluationPoint Point at which the gradient must be evaluated.
 * @return the gradient at the specified point.
 */
  protected double[] computeObjectiveGradient(  final double[] evaluationPoint){
    return gradient.value(evaluationPoint);
  }
  /** 
 * {@inheritDoc}
 * @deprecated In 3.1. Please use{@link #optimizeInternal(int,MultivariateDifferentiableFunction,GoalType,OptimizationData[])}instead.
 */
  @Override @Deprecated protected PointValuePair optimizeInternal(  final int maxEval,  final MultivariateDifferentiableFunction f,  final GoalType goalType,  final double[] startPoint){
    return optimizeInternal(maxEval,f,goalType,new InitialGuess(startPoint));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected PointValuePair optimizeInternal(  final int maxEval,  final MultivariateDifferentiableFunction f,  final GoalType goalType,  final OptimizationData... optData){
    gradient=new GradientFunction(f);
    return super.optimizeInternal(maxEval,f,goalType,optData);
  }
}
