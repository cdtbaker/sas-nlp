package org.apache.commons.math3.optim.nonlinear.vector.jacobian;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.optim.nonlinear.vector.JacobianMultivariateVectorOptimizer;
import org.apache.commons.math3.util.FastMath;
/** 
 * Base class for implementing least-squares optimizers.
 * It provides methods for error estimation.
 * @version $Id: AbstractLeastSquaresOptimizer.java 1443444 2013-02-07 12:41:36Z erans $
 * @since 3.1
 */
public abstract class AbstractLeastSquaresOptimizer extends JacobianMultivariateVectorOptimizer {
  /** 
 * Square-root of the weight matrix. 
 */
  private RealMatrix weightMatrixSqrt;
  /** 
 * Cost value (square root of the sum of the residuals). 
 */
  private double cost;
  /** 
 * @param checker Convergence checker.
 */
  protected AbstractLeastSquaresOptimizer(  ConvergenceChecker<PointVectorValuePair> checker){
    super(checker);
  }
  /** 
 * Computes the weighted Jacobian matrix.
 * @param params Model parameters at which to compute the Jacobian.
 * @return the weighted Jacobian: W<sup>1/2</sup> J.
 * @throws DimensionMismatchException if the Jacobian dimension does not
 * match problem dimension.
 */
  protected RealMatrix computeWeightedJacobian(  double[] params){
    return weightMatrixSqrt.multiply(MatrixUtils.createRealMatrix(computeJacobian(params)));
  }
  /** 
 * Computes the cost.
 * @param residuals Residuals.
 * @return the cost.
 * @see #computeResiduals(double[])
 */
  protected double computeCost(  double[] residuals){
    final ArrayRealVector r=new ArrayRealVector(residuals);
    return FastMath.sqrt(r.dotProduct(getWeight().operate(r)));
  }
  /** 
 * Gets the root-mean-square (RMS) value.
 * The RMS the root of the arithmetic mean of the square of all weighted
 * residuals.
 * This is related to the criterion that is minimized by the optimizer
 * as follows: If <em>c</em> if the criterion, and <em>n</em> is the
 * number of measurements, then the RMS is <em>sqrt (c/n)</em>.
 * @return the RMS value.
 */
  public double getRMS(){
    return FastMath.sqrt(getChiSquare() / getTargetSize());
  }
  /** 
 * Get a Chi-Square-like value assuming the N residuals follow N
 * distinct normal distributions centered on 0 and whose variances are
 * the reciprocal of the weights.
 * @return chi-square value
 */
  public double getChiSquare(){
    return cost * cost;
  }
  /** 
 * Gets the square-root of the weight matrix.
 * @return the square-root of the weight matrix.
 */
  public RealMatrix getWeightSquareRoot(){
    return weightMatrixSqrt.copy();
  }
  /** 
 * Sets the cost.
 * @param cost Cost value.
 */
  protected void setCost(  double cost){
    this.cost=cost;
  }
  /** 
 * Get the covariance matrix of the optimized parameters.
 * <br/>
 * Note that this operation involves the inversion of the
 * <code>J<sup>T</sup>J</code> matrix, where {@code J} is the
 * Jacobian matrix.
 * The {@code threshold} parameter is a way for the caller to specify
 * that the result of this computation should be considered meaningless,
 * and thus trigger an exception.
 * @param params Model parameters.
 * @param threshold Singularity threshold.
 * @return the covariance matrix.
 * @throws org.apache.commons.math3.linear.SingularMatrixExceptionif the covariance matrix cannot be computed (singular problem).
 */
  public double[][] computeCovariances(  double[] params,  double threshold){
    final RealMatrix j=computeWeightedJacobian(params);
    final RealMatrix jTj=j.transpose().multiply(j);
    final DecompositionSolver solver=new QRDecomposition(jTj,threshold).getSolver();
    return solver.getInverse().getData();
  }
  /** 
 * Computes an estimate of the standard deviation of the parameters. The
 * returned values are the square root of the diagonal coefficients of the
 * covariance matrix, {@code sd(a[i]) ~= sqrt(C[i][i])}, where {@code a[i]}is the optimized value of the {@code i}-th parameter, and {@code C} is
 * the covariance matrix.
 * @param params Model parameters.
 * @param covarianceSingularityThreshold Singularity threshold (see{@link #computeCovariances(double[],double) computeCovariances}).
 * @return an estimate of the standard deviation of the optimized parameters
 * @throws org.apache.commons.math3.linear.SingularMatrixExceptionif the covariance matrix cannot be computed.
 */
  public double[] computeSigma(  double[] params,  double covarianceSingularityThreshold){
    final int nC=params.length;
    final double[] sig=new double[nC];
    final double[][] cov=computeCovariances(params,covarianceSingularityThreshold);
    for (int i=0; i < nC; ++i) {
      sig[i]=FastMath.sqrt(cov[i][i]);
    }
    return sig;
  }
  /** 
 * {@inheritDoc}
 * @param optData Optimization data. In addition to those documented in{@link JacobianMultivariateVectorOptimizer#parseOptimizationData(OptimizationData[])JacobianMultivariateVectorOptimizer}, this method will register the following data:
 * <ul>
 * <li>{@link org.apache.commons.math3.optim.nonlinear.vector.Weight}</li>
 * </ul>
 * @return {@inheritDoc}
 * @throws TooManyEvaluationsException if the maximal number of
 * evaluations is exceeded.
 * @throws DimensionMismatchException if the initial guess, target, and weight
 * arguments have inconsistent dimensions.
 */
  @Override public PointVectorValuePair optimize(  OptimizationData... optData) throws TooManyEvaluationsException {
    return super.optimize(optData);
  }
  /** 
 * Computes the residuals.
 * The residual is the difference between the observed (target)
 * values and the model (objective function) value.
 * There is one residual for each element of the vector-valued
 * function.
 * @param objectiveValue Value of the the objective function. This is
 * the value returned from a call to{@link #computeObjectiveValue(double[]) computeObjectiveValue}(whose array argument contains the model parameters).
 * @return the residuals.
 * @throws DimensionMismatchException if {@code params} has a wrong
 * length.
 */
  protected double[] computeResiduals(  double[] objectiveValue){
    final double[] target=getTarget();
    if (objectiveValue.length != target.length) {
      throw new DimensionMismatchException(target.length,objectiveValue.length);
    }
    final double[] residuals=new double[target.length];
    for (int i=0; i < target.length; i++) {
      residuals[i]=target[i] - objectiveValue[i];
    }
    return residuals;
  }
  /** 
 * Scans the list of (required and optional) optimization data that
 * characterize the problem.
 * If the weight matrix is specified, the {@link #weightMatrixSqrt}field is recomputed.
 * @param optData Optimization data. The following data will be looked for:
 * <ul>
 * <li>{@link Weight}</li>
 * </ul>
 */
  @Override protected void parseOptimizationData(  OptimizationData... optData){
    super.parseOptimizationData(optData);
    for (    OptimizationData data : optData) {
      if (data instanceof Weight) {
        weightMatrixSqrt=squareRoot(((Weight)data).getWeight());
        break;
      }
    }
  }
  /** 
 * Computes the square-root of the weight matrix.
 * @param m Symmetric, positive-definite (weight) matrix.
 * @return the square-root of the weight matrix.
 */
  private RealMatrix squareRoot(  RealMatrix m){
    if (m instanceof DiagonalMatrix) {
      final int dim=m.getRowDimension();
      final RealMatrix sqrtM=new DiagonalMatrix(dim);
      for (int i=0; i < dim; i++) {
        sqrtM.setEntry(i,i,FastMath.sqrt(m.getEntry(i,i)));
      }
      return sqrtM;
    }
 else {
      final EigenDecomposition dec=new EigenDecomposition(m);
      return dec.getSquareRoot();
    }
  }
}