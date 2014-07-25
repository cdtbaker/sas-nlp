package org.apache.commons.math3.optim.linear;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
/** 
 * Base class for implementing linear optimizers.
 * @version $Id: LinearOptimizer.java 1443444 2013-02-07 12:41:36Z erans $
 * @since 3.1
 */
public abstract class LinearOptimizer extends MultivariateOptimizer {
  /** 
 * Linear objective function.
 */
  private LinearObjectiveFunction function;
  /** 
 * Linear constraints.
 */
  private Collection<LinearConstraint> linearConstraints;
  /** 
 * Whether to restrict the variables to non-negative values.
 */
  private boolean nonNegative;
  /** 
 * Simple constructor with default settings.
 */
  protected LinearOptimizer(){
    super(null);
  }
  /** 
 * @return {@code true} if the variables are restricted to non-negative values.
 */
  protected boolean isRestrictedToNonNegative(){
    return nonNegative;
  }
  /** 
 * @return the optimization type.
 */
  protected LinearObjectiveFunction getFunction(){
    return function;
  }
  /** 
 * @return the optimization type.
 */
  protected Collection<LinearConstraint> getConstraints(){
    return Collections.unmodifiableCollection(linearConstraints);
  }
  /** 
 * {@inheritDoc}
 * @param optData Optimization data. In addition to those documented in{@link MultivariateOptimizer#parseOptimizationData(OptimizationData[])MultivariateOptimizer}, this method will register the following data:
 * <ul>
 * <li>{@link LinearObjectiveFunction}</li>
 * <li>{@link LinearConstraintSet}</li>
 * <li>{@link NonNegativeConstraint}</li>
 * </ul>
 * @return {@inheritDoc}
 * @throws TooManyIterationsException if the maximal number of
 * iterations is exceeded.
 */
  @Override public PointValuePair optimize(  OptimizationData... optData) throws TooManyIterationsException {
    return super.optimize(optData);
  }
  /** 
 * Scans the list of (required and optional) optimization data that
 * characterize the problem.
 * @param optData Optimization data.
 * The following data will be looked for:
 * <ul>
 * <li>{@link LinearObjectiveFunction}</li>
 * <li>{@link LinearConstraintSet}</li>
 * <li>{@link NonNegativeConstraint}</li>
 * </ul>
 */
  @Override protected void parseOptimizationData(  OptimizationData... optData){
    super.parseOptimizationData(optData);
    for (    OptimizationData data : optData) {
      if (data instanceof LinearObjectiveFunction) {
        function=(LinearObjectiveFunction)data;
        continue;
      }
      if (data instanceof LinearConstraintSet) {
        linearConstraints=((LinearConstraintSet)data).getConstraints();
        continue;
      }
      if (data instanceof NonNegativeConstraint) {
        nonNegative=((NonNegativeConstraint)data).isRestrictedToNonNegative();
        continue;
      }
    }
  }
}
