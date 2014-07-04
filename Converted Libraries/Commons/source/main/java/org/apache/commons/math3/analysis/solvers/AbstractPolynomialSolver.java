package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
/** 
 * Base class for solvers.
 * @since 3.0
 * @version $Id: AbstractPolynomialSolver.java 1364387 2012-07-22 18:14:11Z tn $
 */
public abstract class AbstractPolynomialSolver extends BaseAbstractUnivariateSolver<PolynomialFunction> implements PolynomialSolver {
  /** 
 * Function. 
 */
  private PolynomialFunction polynomialFunction;
  /** 
 * Construct a solver with given absolute accuracy.
 * @param absoluteAccuracy Maximum absolute error.
 */
  protected AbstractPolynomialSolver(  final double absoluteAccuracy){
    super(absoluteAccuracy);
  }
  /** 
 * Construct a solver with given accuracies.
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 */
  protected AbstractPolynomialSolver(  final double relativeAccuracy,  final double absoluteAccuracy){
    super(relativeAccuracy,absoluteAccuracy);
  }
  /** 
 * Construct a solver with given accuracies.
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 * @param functionValueAccuracy Maximum function value error.
 */
  protected AbstractPolynomialSolver(  final double relativeAccuracy,  final double absoluteAccuracy,  final double functionValueAccuracy){
    super(relativeAccuracy,absoluteAccuracy,functionValueAccuracy);
  }
  /** 
 * {@inheritDoc}
 */
  @Override protected void setup(  int maxEval,  PolynomialFunction f,  double min,  double max,  double startValue){
    super.setup(maxEval,f,min,max,startValue);
    polynomialFunction=f;
  }
  /** 
 * @return the coefficients of the polynomial function.
 */
  protected double[] getCoefficients(){
    return polynomialFunction.getCoefficients();
  }
}
