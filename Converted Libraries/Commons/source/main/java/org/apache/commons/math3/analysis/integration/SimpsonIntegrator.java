package org.apache.commons.math3.analysis.integration;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
/** 
 * Implements <a href="http://mathworld.wolfram.com/SimpsonsRule.html">
 * Simpson's Rule</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * This implementation employs the basic trapezoid rule to calculate Simpson's
 * rule.</p>
 * @version $Id: SimpsonIntegrator.java 1364387 2012-07-22 18:14:11Z tn $
 * @since 1.2
 */
public class SimpsonIntegrator extends BaseAbstractUnivariateIntegrator {
  /** 
 * Maximal number of iterations for Simpson. 
 */
  public static final int SIMPSON_MAX_ITERATIONS_COUNT=64;
  /** 
 * Build a Simpson integrator with given accuracies and iterations counts.
 * @param relativeAccuracy relative accuracy of the result
 * @param absoluteAccuracy absolute accuracy of the result
 * @param minimalIterationCount minimum number of iterations
 * @param maximalIterationCount maximum number of iterations
 * (must be less than or equal to {@link #SIMPSON_MAX_ITERATIONS_COUNT})
 * @exception NotStrictlyPositiveException if minimal number of iterations
 * is not strictly positive
 * @exception NumberIsTooSmallException if maximal number of iterations
 * is lesser than or equal to the minimal number of iterations
 * @exception NumberIsTooLargeException if maximal number of iterations
 * is greater than {@link #SIMPSON_MAX_ITERATIONS_COUNT}
 */
  public SimpsonIntegrator(  final double relativeAccuracy,  final double absoluteAccuracy,  final int minimalIterationCount,  final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
    super(relativeAccuracy,absoluteAccuracy,minimalIterationCount,maximalIterationCount);
    if (maximalIterationCount > SIMPSON_MAX_ITERATIONS_COUNT) {
      throw new NumberIsTooLargeException(maximalIterationCount,SIMPSON_MAX_ITERATIONS_COUNT,false);
    }
  }
  /** 
 * Build a Simpson integrator with given iteration counts.
 * @param minimalIterationCount minimum number of iterations
 * @param maximalIterationCount maximum number of iterations
 * (must be less than or equal to {@link #SIMPSON_MAX_ITERATIONS_COUNT})
 * @exception NotStrictlyPositiveException if minimal number of iterations
 * is not strictly positive
 * @exception NumberIsTooSmallException if maximal number of iterations
 * is lesser than or equal to the minimal number of iterations
 * @exception NumberIsTooLargeException if maximal number of iterations
 * is greater than {@link #SIMPSON_MAX_ITERATIONS_COUNT}
 */
  public SimpsonIntegrator(  final int minimalIterationCount,  final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
    super(minimalIterationCount,maximalIterationCount);
    if (maximalIterationCount > SIMPSON_MAX_ITERATIONS_COUNT) {
      throw new NumberIsTooLargeException(maximalIterationCount,SIMPSON_MAX_ITERATIONS_COUNT,false);
    }
  }
  /** 
 * Construct an integrator with default settings.
 * (max iteration count set to {@link #SIMPSON_MAX_ITERATIONS_COUNT})
 */
  public SimpsonIntegrator(){
    super(DEFAULT_MIN_ITERATIONS_COUNT,SIMPSON_MAX_ITERATIONS_COUNT);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected double doIntegrate() throws TooManyEvaluationsException, MaxCountExceededException {
    TrapezoidIntegrator qtrap=new TrapezoidIntegrator();
    if (getMinimalIterationCount() == 1) {
      return (4 * qtrap.stage(this,1) - qtrap.stage(this,0)) / 3.0;
    }
    double olds=0;
    double oldt=qtrap.stage(this,0);
    while (true) {
      final double t=qtrap.stage(this,iterations.getCount());
      iterations.incrementCount();
      final double s=(4 * t - oldt) / 3.0;
      if (iterations.getCount() >= getMinimalIterationCount()) {
        final double delta=FastMath.abs(s - olds);
        final double rLimit=getRelativeAccuracy() * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5;
        if ((delta <= rLimit) || (delta <= getAbsoluteAccuracy())) {
          return s;
        }
      }
      olds=s;
      oldt=t;
    }
  }
}
