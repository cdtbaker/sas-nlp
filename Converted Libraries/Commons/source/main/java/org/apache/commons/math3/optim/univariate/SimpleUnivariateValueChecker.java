package org.apache.commons.math3.optim.univariate;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.optim.AbstractConvergenceChecker;
/** 
 * Simple implementation of the{@link org.apache.commons.math3.optimization.ConvergenceChecker} interface
 * that uses only objective function values.
 * Convergence is considered to have been reached if either the relative
 * difference between the objective function values is smaller than a
 * threshold or if either the absolute difference between the objective
 * function values is smaller than another threshold.
 * <br/>
 * The {@link #converged(int,UnivariatePointValuePair,UnivariatePointValuePair)converged} method will also return {@code true} if the number of iterations
 * has been set (see {@link #SimpleUnivariateValueChecker(double,double,int)this constructor}).
 * @version $Id: SimpleUnivariateValueChecker.java 1462503 2013-03-29 15:48:27Z luc $
 * @since 3.1
 */
public class SimpleUnivariateValueChecker extends AbstractConvergenceChecker<UnivariatePointValuePair> {
  /** 
 * If {@link #maxIterationCount} is set to this value, the number of
 * iterations will never cause{@link #converged(int,UnivariatePointValuePair,UnivariatePointValuePair)}to return {@code true}.
 */
  private static final int ITERATION_CHECK_DISABLED=-1;
  /** 
 * Number of iterations after which the{@link #converged(int,UnivariatePointValuePair,UnivariatePointValuePair)}method will return true (unless the check is disabled).
 */
  private final int maxIterationCount;
  /** 
 * Build an instance with specified thresholds.
 * In order to perform only relative checks, the absolute tolerance
 * must be set to a negative value. In order to perform only absolute
 * checks, the relative tolerance must be set to a negative value.
 * @param relativeThreshold relative tolerance threshold
 * @param absoluteThreshold absolute tolerance threshold
 */
  public SimpleUnivariateValueChecker(  final double relativeThreshold,  final double absoluteThreshold){
    super(relativeThreshold,absoluteThreshold);
    maxIterationCount=ITERATION_CHECK_DISABLED;
  }
  /** 
 * Builds an instance with specified thresholds.
 * In order to perform only relative checks, the absolute tolerance
 * must be set to a negative value. In order to perform only absolute
 * checks, the relative tolerance must be set to a negative value.
 * @param relativeThreshold relative tolerance threshold
 * @param absoluteThreshold absolute tolerance threshold
 * @param maxIter Maximum iteration count.
 * @throws NotStrictlyPositiveException if {@code maxIter <= 0}.
 * @since 3.1
 */
  public SimpleUnivariateValueChecker(  final double relativeThreshold,  final double absoluteThreshold,  final int maxIter){
    super(relativeThreshold,absoluteThreshold);
    if (maxIter <= 0) {
      throw new NotStrictlyPositiveException(maxIter);
    }
    maxIterationCount=maxIter;
  }
  /** 
 * Check if the optimization algorithm has converged considering the
 * last two points.
 * This method may be called several time from the same algorithm
 * iteration with different points. This can be detected by checking the
 * iteration number at each call if needed. Each time this method is
 * called, the previous and current point correspond to points with the
 * same role at each iteration, so they can be compared. As an example,
 * simplex-based algorithms call this method for all points of the simplex,
 * not only for the best or worst ones.
 * @param iteration Index of current iteration
 * @param previous Best point in the previous iteration.
 * @param current Best point in the current iteration.
 * @return {@code true} if the algorithm has converged.
 */
  @Override public boolean converged(  final int iteration,  final UnivariatePointValuePair previous,  final UnivariatePointValuePair current){
    if (maxIterationCount != ITERATION_CHECK_DISABLED && iteration >= maxIterationCount) {
      return true;
    }
    final double p=previous.getValue();
    final double c=current.getValue();
    final double difference=FastMath.abs(p - c);
    final double size=FastMath.max(FastMath.abs(p),FastMath.abs(c));
    return difference <= size * getRelativeThreshold() || difference <= getAbsoluteThreshold();
  }
}