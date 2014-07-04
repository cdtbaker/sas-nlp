package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.UnivariateFunction;
/** 
 * Base class for solvers.
 * @since 3.0
 * @version $Id: AbstractUnivariateSolver.java 1379560 2012-08-31 19:40:30Z erans $
 */
public abstract class AbstractUnivariateSolver extends BaseAbstractUnivariateSolver<UnivariateFunction> implements UnivariateSolver {
  /** 
 * Construct a solver with given absolute accuracy.
 * @param absoluteAccuracy Maximum absolute error.
 */
  protected AbstractUnivariateSolver(  final double absoluteAccuracy){
    super(absoluteAccuracy);
  }
  /** 
 * Construct a solver with given accuracies.
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 */
  protected AbstractUnivariateSolver(  final double relativeAccuracy,  final double absoluteAccuracy){
    super(relativeAccuracy,absoluteAccuracy);
  }
  /** 
 * Construct a solver with given accuracies.
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 * @param functionValueAccuracy Maximum function value error.
 */
  protected AbstractUnivariateSolver(  final double relativeAccuracy,  final double absoluteAccuracy,  final double functionValueAccuracy){
    super(relativeAccuracy,absoluteAccuracy,functionValueAccuracy);
  }
}
