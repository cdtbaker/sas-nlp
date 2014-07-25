package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
/** 
 * Provide a default implementation for several functions useful to generic
 * solvers.
 * @since 3.0
 * @version $Id: AbstractDifferentiableUnivariateSolver.java 1455194 2013-03-11 15:45:54Z luc $
 * @deprecated as of 3.1, replaced by {@link AbstractUnivariateDifferentiableSolver}
 */
@Deprecated public abstract class AbstractDifferentiableUnivariateSolver extends BaseAbstractUnivariateSolver<DifferentiableUnivariateFunction> implements DifferentiableUnivariateSolver {
  /** 
 * Derivative of the function to solve. 
 */
  private UnivariateFunction functionDerivative;
  /** 
 * Construct a solver with given absolute accuracy.
 * @param absoluteAccuracy Maximum absolute error.
 */
  protected AbstractDifferentiableUnivariateSolver(  final double absoluteAccuracy){
    super(absoluteAccuracy);
  }
  /** 
 * Construct a solver with given accuracies.
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 * @param functionValueAccuracy Maximum function value error.
 */
  protected AbstractDifferentiableUnivariateSolver(  final double relativeAccuracy,  final double absoluteAccuracy,  final double functionValueAccuracy){
    super(relativeAccuracy,absoluteAccuracy,functionValueAccuracy);
  }
  /** 
 * Compute the objective function value.
 * @param point Point at which the objective function must be evaluated.
 * @return the objective function value at specified point.
 * @throws TooManyEvaluationsException if the maximal number of evaluations is exceeded.
 */
  protected double computeDerivativeObjectiveValue(  double point) throws TooManyEvaluationsException {
    incrementEvaluationCount();
    return functionDerivative.value(point);
  }
  /** 
 * {@inheritDoc}
 */
  @Override protected void setup(  int maxEval,  DifferentiableUnivariateFunction f,  double min,  double max,  double startValue){
    super.setup(maxEval,f,min,max,startValue);
    functionDerivative=f.derivative();
  }
}
