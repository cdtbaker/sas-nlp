package org.apache.commons.math3.optim;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
/** 
 * Base class for implementing optimizers for multivariate functions.
 * It contains the boiler-plate code for initial guess and bounds
 * specifications.
 * <em>It is not a "user" class.</em>
 * @param<PAIR>
 *  Type of the point/value pair returned by the optimization
 * algorithm.
 * @version $Id: BaseMultivariateOptimizer.java 1443444 2013-02-07 12:41:36Z erans $
 * @since 3.1
 */
public abstract class BaseMultivariateOptimizer<PAIR> extends BaseOptimizer<PAIR> {
  /** 
 * Initial guess. 
 */
  private double[] start;
  /** 
 * Lower bounds. 
 */
  private double[] lowerBound;
  /** 
 * Upper bounds. 
 */
  private double[] upperBound;
  /** 
 * @param checker Convergence checker.
 */
  protected BaseMultivariateOptimizer(  ConvergenceChecker<PAIR> checker){
    super(checker);
  }
  /** 
 * {@inheritDoc}
 * @param optData Optimization data. In addition to those documented in{@link BaseOptimizer#parseOptimizationData(OptimizationData[]) BaseOptimizer},
 * this method will register the following data:
 * <ul>
 * <li>{@link InitialGuess}</li>
 * <li>{@link SimpleBounds}</li>
 * </ul>
 * @return {@inheritDoc}
 */
  @Override public PAIR optimize(  OptimizationData... optData){
    return super.optimize(optData);
  }
  /** 
 * Scans the list of (required and optional) optimization data that
 * characterize the problem.
 * @param optData Optimization data. The following data will be looked for:
 * <ul>
 * <li>{@link InitialGuess}</li>
 * <li>{@link SimpleBounds}</li>
 * </ul>
 */
  @Override protected void parseOptimizationData(  OptimizationData... optData){
    super.parseOptimizationData(optData);
    for (    OptimizationData data : optData) {
      if (data instanceof InitialGuess) {
        start=((InitialGuess)data).getInitialGuess();
        continue;
      }
      if (data instanceof SimpleBounds) {
        final SimpleBounds bounds=(SimpleBounds)data;
        lowerBound=bounds.getLower();
        upperBound=bounds.getUpper();
        continue;
      }
    }
    checkParameters();
  }
  /** 
 * Gets the initial guess.
 * @return the initial guess, or {@code null} if not set.
 */
  public double[] getStartPoint(){
    return start == null ? null : start.clone();
  }
  /** 
 * @return the lower bounds, or {@code null} if not set.
 */
  public double[] getLowerBound(){
    return lowerBound == null ? null : lowerBound.clone();
  }
  /** 
 * @return the upper bounds, or {@code null} if not set.
 */
  public double[] getUpperBound(){
    return upperBound == null ? null : upperBound.clone();
  }
  /** 
 * Check parameters consistency.
 */
  private void checkParameters(){
    if (start != null) {
      final int dim=start.length;
      if (lowerBound != null) {
        if (lowerBound.length != dim) {
          throw new DimensionMismatchException(lowerBound.length,dim);
        }
        for (int i=0; i < dim; i++) {
          final double v=start[i];
          final double lo=lowerBound[i];
          if (v < lo) {
            throw new NumberIsTooSmallException(v,lo,true);
          }
        }
      }
      if (upperBound != null) {
        if (upperBound.length != dim) {
          throw new DimensionMismatchException(upperBound.length,dim);
        }
        for (int i=0; i < dim; i++) {
          final double v=start[i];
          final double hi=upperBound[i];
          if (v > hi) {
            throw new NumberIsTooLargeException(v,hi,true);
          }
        }
      }
    }
  }
}