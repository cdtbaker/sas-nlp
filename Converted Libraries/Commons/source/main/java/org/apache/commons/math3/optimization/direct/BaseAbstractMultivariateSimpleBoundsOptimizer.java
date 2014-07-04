package org.apache.commons.math3.optimization.direct;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.BaseMultivariateOptimizer;
import org.apache.commons.math3.optimization.BaseMultivariateSimpleBoundsOptimizer;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.SimpleBounds;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
/** 
 * Base class for implementing optimizers for multivariate scalar functions,
 * subject to simple bounds: The valid range of the parameters is an interval.
 * The interval can possibly be infinite (in one or both directions).
 * This base class handles the boiler-plate methods associated to thresholds
 * settings, iterations and evaluations counting.
 * @param<FUNC>
 *  Type of the objective function to be optimized.
 * @version $Id: BaseAbstractMultivariateSimpleBoundsOptimizer.java 1462503 2013-03-29 15:48:27Z luc $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.0
 * @deprecated As of 3.1 since the {@link BaseAbstractMultivariateOptimizerbase class} contains similar functionality.
 */
@Deprecated public abstract class BaseAbstractMultivariateSimpleBoundsOptimizer<FUNC extends MultivariateFunction> extends BaseAbstractMultivariateOptimizer<FUNC> implements BaseMultivariateOptimizer<FUNC>, BaseMultivariateSimpleBoundsOptimizer<FUNC> {
  /** 
 * Simple constructor with default settings.
 * The convergence checker is set to a{@link org.apache.commons.math3.optimization.SimpleValueChecker}.
 * @see BaseAbstractMultivariateOptimizer#BaseAbstractMultivariateOptimizer()
 * @deprecated See {@link org.apache.commons.math3.optimization.SimpleValueChecker#SimpleValueChecker()}
 */
  @Deprecated protected BaseAbstractMultivariateSimpleBoundsOptimizer(){
  }
  /** 
 * @param checker Convergence checker.
 */
  protected BaseAbstractMultivariateSimpleBoundsOptimizer(  ConvergenceChecker<PointValuePair> checker){
    super(checker);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PointValuePair optimize(  int maxEval,  FUNC f,  GoalType goalType,  double[] startPoint){
    return super.optimizeInternal(maxEval,f,goalType,new InitialGuess(startPoint));
  }
  /** 
 * {@inheritDoc} 
 */
  public PointValuePair optimize(  int maxEval,  FUNC f,  GoalType goalType,  double[] startPoint,  double[] lower,  double[] upper){
    return super.optimizeInternal(maxEval,f,goalType,new InitialGuess(startPoint),new SimpleBounds(lower,upper));
  }
}
