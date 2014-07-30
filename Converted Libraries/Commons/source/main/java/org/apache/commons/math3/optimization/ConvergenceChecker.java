package org.apache.commons.math3.optimization;
/** 
 * This interface specifies how to check if an optimization algorithm has
 * converged.
 * <br/>
 * Deciding if convergence has been reached is a problem-dependent issue. The
 * user should provide a class implementing this interface to allow the
 * optimization algorithm to stop its search according to the problem at hand.
 * <br/>
 * For convenience, three implementations that fit simple needs are already
 * provided: {@link SimpleValueChecker}, {@link SimpleVectorValueChecker} and{@link SimplePointChecker}. The first two consider that convergence is
 * reached when the objective function value does not change much anymore, it
 * does not use the point set at all.
 * The third one considers that convergence is reached when the input point
 * set does not change much anymore, it does not use objective function value
 * at all.
 * @param<PAIR>
 *  Type of the (point, objective value) pair.
 * @see org.apache.commons.math3.optimization.SimplePointChecker
 * @see org.apache.commons.math3.optimization.SimpleValueChecker
 * @see org.apache.commons.math3.optimization.SimpleVectorValueChecker
 * @version $Id: ConvergenceChecker.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.0
 */
@Deprecated public interface ConvergenceChecker<PAIR> {
  /** 
 * Check if the optimization algorithm has converged.
 * @param iteration Current iteration.
 * @param previous Best point in the previous iteration.
 * @param current Best point in the current iteration.
 * @return {@code true} if the algorithm is considered to have converged.
 */
  boolean converged(  int iteration,  PAIR previous,  PAIR current);
}