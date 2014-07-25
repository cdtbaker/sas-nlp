package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
/** 
 * Implements <a href="http://mathworld.wolfram.com/NewtonsMethod.html">
 * Newton's Method</a> for finding zeros of real univariate differentiable
 * functions.
 * @since 3.1
 * @version $Id: NewtonRaphsonSolver.java 1383441 2012-09-11 14:56:39Z luc $
 */
public class NewtonRaphsonSolver extends AbstractUnivariateDifferentiableSolver {
  /** 
 * Default absolute accuracy. 
 */
  private static final double DEFAULT_ABSOLUTE_ACCURACY=1e-6;
  /** 
 * Construct a solver.
 */
  public NewtonRaphsonSolver(){
    this(DEFAULT_ABSOLUTE_ACCURACY);
  }
  /** 
 * Construct a solver.
 * @param absoluteAccuracy Absolute accuracy.
 */
  public NewtonRaphsonSolver(  double absoluteAccuracy){
    super(absoluteAccuracy);
  }
  /** 
 * Find a zero near the midpoint of {@code min} and {@code max}.
 * @param f Function to solve.
 * @param min Lower bound for the interval.
 * @param max Upper bound for the interval.
 * @param maxEval Maximum number of evaluations.
 * @return the value where the function is zero.
 * @throws org.apache.commons.math3.exception.TooManyEvaluationsExceptionif the maximum evaluation count is exceeded.
 * @throws org.apache.commons.math3.exception.NumberIsTooLargeExceptionif {@code min >= max}.
 */
  @Override public double solve(  int maxEval,  final UnivariateDifferentiableFunction f,  final double min,  final double max) throws TooManyEvaluationsException {
    return super.solve(maxEval,f,UnivariateSolverUtils.midpoint(min,max));
  }
  /** 
 * {@inheritDoc}
 */
  @Override protected double doSolve() throws TooManyEvaluationsException {
    final double startValue=getStartValue();
    final double absoluteAccuracy=getAbsoluteAccuracy();
    double x0=startValue;
    double x1;
    while (true) {
      final DerivativeStructure y0=computeObjectiveValueAndDerivative(x0);
      x1=x0 - (y0.getValue() / y0.getPartialDerivative(1));
      if (FastMath.abs(x1 - x0) <= absoluteAccuracy) {
        return x1;
      }
      x0=x1;
    }
  }
}
