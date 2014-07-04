package org.apache.commons.math3.optim;
/** 
 * Base class for all convergence checker implementations.
 * @param<PAIR>
 *  Type of (point, value) pair.
 * @version $Id: AbstractConvergenceChecker.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.0
 */
public abstract class AbstractConvergenceChecker<PAIR> implements ConvergenceChecker<PAIR> {
  /** 
 * Relative tolerance threshold.
 */
  private final double relativeThreshold;
  /** 
 * Absolute tolerance threshold.
 */
  private final double absoluteThreshold;
  /** 
 * Build an instance with a specified thresholds.
 * @param relativeThreshold relative tolerance threshold
 * @param absoluteThreshold absolute tolerance threshold
 */
  public AbstractConvergenceChecker(  final double relativeThreshold,  final double absoluteThreshold){
    this.relativeThreshold=relativeThreshold;
    this.absoluteThreshold=absoluteThreshold;
  }
  /** 
 * @return the relative threshold.
 */
  public double getRelativeThreshold(){
    return relativeThreshold;
  }
  /** 
 * @return the absolute threshold.
 */
  public double getAbsoluteThreshold(){
    return absoluteThreshold;
  }
  /** 
 * {@inheritDoc}
 */
  public abstract boolean converged(  int iteration,  PAIR previous,  PAIR current);
}
