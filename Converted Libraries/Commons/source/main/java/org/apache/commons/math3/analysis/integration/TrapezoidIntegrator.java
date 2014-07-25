package org.apache.commons.math3.analysis.integration;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
/** 
 * Implements the <a href="http://mathworld.wolfram.com/TrapezoidalRule.html">
 * Trapezoid Rule</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * The function should be integrable.</p>
 * @version $Id: TrapezoidIntegrator.java 1455194 2013-03-11 15:45:54Z luc $
 * @since 1.2
 */
public class TrapezoidIntegrator extends BaseAbstractUnivariateIntegrator {
  /** 
 * Maximum number of iterations for trapezoid. 
 */
  public static final int TRAPEZOID_MAX_ITERATIONS_COUNT=64;
  /** 
 * Intermediate result. 
 */
  private double s;
  /** 
 * Build a trapezoid integrator with given accuracies and iterations counts.
 * @param relativeAccuracy relative accuracy of the result
 * @param absoluteAccuracy absolute accuracy of the result
 * @param minimalIterationCount minimum number of iterations
 * @param maximalIterationCount maximum number of iterations
 * (must be less than or equal to {@link #TRAPEZOID_MAX_ITERATIONS_COUNT}
 * @exception NotStrictlyPositiveException if minimal number of iterations
 * is not strictly positive
 * @exception NumberIsTooSmallException if maximal number of iterations
 * is lesser than or equal to the minimal number of iterations
 * @exception NumberIsTooLargeException if maximal number of iterations
 * is greater than {@link #TRAPEZOID_MAX_ITERATIONS_COUNT}
 */
  public TrapezoidIntegrator(  final double relativeAccuracy,  final double absoluteAccuracy,  final int minimalIterationCount,  final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
    super(relativeAccuracy,absoluteAccuracy,minimalIterationCount,maximalIterationCount);
    if (maximalIterationCount > TRAPEZOID_MAX_ITERATIONS_COUNT) {
      throw new NumberIsTooLargeException(maximalIterationCount,TRAPEZOID_MAX_ITERATIONS_COUNT,false);
    }
  }
  /** 
 * Build a trapezoid integrator with given iteration counts.
 * @param minimalIterationCount minimum number of iterations
 * @param maximalIterationCount maximum number of iterations
 * (must be less than or equal to {@link #TRAPEZOID_MAX_ITERATIONS_COUNT}
 * @exception NotStrictlyPositiveException if minimal number of iterations
 * is not strictly positive
 * @exception NumberIsTooSmallException if maximal number of iterations
 * is lesser than or equal to the minimal number of iterations
 * @exception NumberIsTooLargeException if maximal number of iterations
 * is greater than {@link #TRAPEZOID_MAX_ITERATIONS_COUNT}
 */
  public TrapezoidIntegrator(  final int minimalIterationCount,  final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
    super(minimalIterationCount,maximalIterationCount);
    if (maximalIterationCount > TRAPEZOID_MAX_ITERATIONS_COUNT) {
      throw new NumberIsTooLargeException(maximalIterationCount,TRAPEZOID_MAX_ITERATIONS_COUNT,false);
    }
  }
  /** 
 * Construct a trapezoid integrator with default settings.
 * (max iteration count set to {@link #TRAPEZOID_MAX_ITERATIONS_COUNT})
 */
  public TrapezoidIntegrator(){
    super(DEFAULT_MIN_ITERATIONS_COUNT,TRAPEZOID_MAX_ITERATIONS_COUNT);
  }
  /** 
 * Compute the n-th stage integral of trapezoid rule. This function
 * should only be called by API <code>integrate()</code> in the package.
 * To save time it does not verify arguments - caller does.
 * <p>
 * The interval is divided equally into 2^n sections rather than an
 * arbitrary m sections because this configuration can best utilize the
 * already computed values.</p>
 * @param baseIntegrator integrator holding integration parameters
 * @param n the stage of 1/2 refinement, n = 0 is no refinement
 * @return the value of n-th stage integral
 * @throws TooManyEvaluationsException if the maximal number of evaluations
 * is exceeded.
 */
  double stage(  final BaseAbstractUnivariateIntegrator baseIntegrator,  final int n) throws TooManyEvaluationsException {
    if (n == 0) {
      final double max=baseIntegrator.getMax();
      final double min=baseIntegrator.getMin();
      s=0.5 * (max - min) * (baseIntegrator.computeObjectiveValue(min) + baseIntegrator.computeObjectiveValue(max));
      return s;
    }
 else {
      final long np=1L << (n - 1);
      double sum=0;
      final double max=baseIntegrator.getMax();
      final double min=baseIntegrator.getMin();
      final double spacing=(max - min) / np;
      double x=min + 0.5 * spacing;
      for (long i=0; i < np; i++) {
        sum+=baseIntegrator.computeObjectiveValue(x);
        x+=spacing;
      }
      s=0.5 * (s + sum * spacing);
      return s;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
    double oldt=stage(this,0);
    iterations.incrementCount();
    while (true) {
      final int i=iterations.getCount();
      final double t=stage(this,i);
      if (i >= getMinimalIterationCount()) {
        final double delta=FastMath.abs(t - oldt);
        final double rLimit=getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5;
        if ((delta <= rLimit) || (delta <= getAbsoluteAccuracy())) {
          return t;
        }
      }
      oldt=t;
      iterations.incrementCount();
    }
  }
}
