package org.apache.commons.math3.optim.nonlinear.vector;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.DimensionMismatchException;
/** 
 * Base class for implementing optimizers for multivariate vector
 * differentiable functions.
 * It contains boiler-plate code for dealing with Jacobian evaluation.
 * It assumes that the rows of the Jacobian matrix iterate on the model
 * functions while the columns iterate on the parameters; thus, the numbers
 * of rows is equal to the dimension of the {@link Target} while the
 * number of columns is equal to the dimension of the{@link org.apache.commons.math3.optim.InitialGuess InitialGuess}.
 * @version $Id: JacobianMultivariateVectorOptimizer.java 1454464 2013-03-08 16:58:10Z luc $
 * @since 3.1
 */
public abstract class JacobianMultivariateVectorOptimizer extends MultivariateVectorOptimizer {
  /** 
 * Jacobian of the model function.
 */
  private MultivariateMatrixFunction jacobian;
  /** 
 * @param checker Convergence checker.
 */
  protected JacobianMultivariateVectorOptimizer(  ConvergenceChecker<PointVectorValuePair> checker){
    super(checker);
  }
  /** 
 * Computes the Jacobian matrix.
 * @param params Point at which the Jacobian must be evaluated.
 * @return the Jacobian at the specified point.
 */
  protected double[][] computeJacobian(  final double[] params){
    return jacobian.value(params);
  }
  /** 
 * {@inheritDoc}
 * @param optData Optimization data. In addition to those documented in{@link MultivariateVectorOptimizer#optimize(OptimizationData)}MultivariateOptimizer}, this method will register the following data:
 * <ul>
 * <li>{@link ModelFunctionJacobian}</li>
 * </ul>
 * @return {@inheritDoc}
 * @throws TooManyEvaluationsException if the maximal number of
 * evaluations is exceeded.
 * @throws DimensionMismatchException if the initial guess, target, and weight
 * arguments have inconsistent dimensions.
 */
  @Override public PointVectorValuePair optimize(  OptimizationData... optData) throws TooManyEvaluationsException, DimensionMismatchException {
    return super.optimize(optData);
  }
  /** 
 * Scans the list of (required and optional) optimization data that
 * characterize the problem.
 * @param optData Optimization data.
 * The following data will be looked for:
 * <ul>
 * <li>{@link ModelFunctionJacobian}</li>
 * </ul>
 */
  @Override protected void parseOptimizationData(  OptimizationData... optData){
    super.parseOptimizationData(optData);
    for (    OptimizationData data : optData) {
      if (data instanceof ModelFunctionJacobian) {
        jacobian=((ModelFunctionJacobian)data).getModelFunctionJacobian();
        break;
      }
    }
  }
}
