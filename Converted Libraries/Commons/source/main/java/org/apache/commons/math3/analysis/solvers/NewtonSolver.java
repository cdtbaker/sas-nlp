package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
/** 
 * Implements <a href="http://mathworld.wolfram.com/NewtonsMethod.html">
 * Newton's Method</a> for finding zeros of real univariate functions.
 * <p>
 * The function should be continuous but not necessarily smooth.</p>
 * @deprecated as of 3.1, replaced by {@link NewtonRaphsonSolver}
 * @version $Id: NewtonSolver.java 1395937 2012-10-09 10:04:36Z luc $
 */
@Deprecated public class NewtonSolver extends AbstractDifferentiableUnivariateSolver {
  /** 
 * Default absolute accuracy. 
 */
  private static final double DEFAULT_ABSOLUTE_ACCURACY=1e-6;
  /** 
 * Construct a solver.
 */
  public NewtonSolver(){
    this(DEFAULT_ABSOLUTE_ACCURACY);
  }
  /** 
 * Construct a solver.
 * @param absoluteAccuracy Absolute accuracy.
 */
  public NewtonSolver(  double absoluteAccuracy){
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
  @Override public double solve(  int maxEval,  final DifferentiableUnivariateFunction f,  final double min,  final double max) throws TooManyEvaluationsException {
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
      x1=x0 - (computeObjectiveValue(x0) / computeDerivativeObjectiveValue(x0));
      if (FastMath.abs(x1 - x0) <= absoluteAccuracy) {
        return x1;
      }
      x0=x1;
    }
  }
}
