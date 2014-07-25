package org.apache.commons.math3.filter;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.util.MathUtils;
/** 
 * Implementation of a Kalman filter to estimate the state <i>x<sub>k</sub></i>
 * of a discrete-time controlled process that is governed by the linear
 * stochastic difference equation:
 * <pre>
 * <i>x<sub>k</sub></i> = <b>A</b><i>x<sub>k-1</sub></i> + <b>B</b><i>u<sub>k-1</sub></i> + <i>w<sub>k-1</sub></i>
 * </pre>
 * with a measurement <i>x<sub>k</sub></i> that is
 * <pre>
 * <i>z<sub>k</sub></i> = <b>H</b><i>x<sub>k</sub></i> + <i>v<sub>k</sub></i>.
 * </pre>
 * <p>
 * The random variables <i>w<sub>k</sub></i> and <i>v<sub>k</sub></i> represent
 * the process and measurement noise and are assumed to be independent of each
 * other and distributed with normal probability (white noise).
 * <p>
 * The Kalman filter cycle involves the following steps:
 * <ol>
 * <li>predict: project the current state estimate ahead in time</li>
 * <li>correct: adjust the projected estimate by an actual measurement</li>
 * </ol>
 * <p>
 * The Kalman filter is initialized with a {@link ProcessModel} and a{@link MeasurementModel}, which contain the corresponding transformation and
 * noise covariance matrices. The parameter names used in the respective models
 * correspond to the following names commonly used in the mathematical
 * literature:
 * <ul>
 * <li>A - state transition matrix</li>
 * <li>B - control input matrix</li>
 * <li>H - measurement matrix</li>
 * <li>Q - process noise covariance matrix</li>
 * <li>R - measurement noise covariance matrix</li>
 * <li>P - error covariance matrix</li>
 * </ul>
 * @see <a href="http://www.cs.unc.edu/~welch/kalman/">Kalman filter
 *      resources</a>
 * @see <a href="http://www.cs.unc.edu/~welch/media/pdf/kalman_intro.pdf">An
 *      introduction to the Kalman filter by Greg Welch and Gary Bishop</a>
 * @see <a href="http://academic.csuohio.edu/simond/courses/eec644/kalman.pdf">
 *      Kalman filter example by Dan Simon</a>
 * @see ProcessModel
 * @see MeasurementModel
 * @since 3.0
 * @version $Id: KalmanFilter.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class KalmanFilter {
  /** 
 * The process model used by this filter instance. 
 */
  private final ProcessModel processModel;
  /** 
 * The measurement model used by this filter instance. 
 */
  private final MeasurementModel measurementModel;
  /** 
 * The transition matrix, equivalent to A. 
 */
  private RealMatrix transitionMatrix;
  /** 
 * The transposed transition matrix. 
 */
  private RealMatrix transitionMatrixT;
  /** 
 * The control matrix, equivalent to B. 
 */
  private RealMatrix controlMatrix;
  /** 
 * The measurement matrix, equivalent to H. 
 */
  private RealMatrix measurementMatrix;
  /** 
 * The transposed measurement matrix. 
 */
  private RealMatrix measurementMatrixT;
  /** 
 * The internal state estimation vector, equivalent to x hat. 
 */
  private RealVector stateEstimation;
  /** 
 * The error covariance matrix, equivalent to P. 
 */
  private RealMatrix errorCovariance;
  /** 
 * Creates a new Kalman filter with the given process and measurement models.
 * @param processthe model defining the underlying process dynamics
 * @param measurementthe model defining the given measurement characteristics
 * @throws NullArgumentExceptionif any of the given inputs is null (except for the control matrix)
 * @throws NonSquareMatrixExceptionif the transition matrix is non square
 * @throws DimensionMismatchExceptionif the column dimension of the transition matrix does not match the dimension of the
 * initial state estimation vector
 * @throws MatrixDimensionMismatchExceptionif the matrix dimensions do not fit together
 */
  public KalmanFilter(  final ProcessModel process,  final MeasurementModel measurement) throws NullArgumentException, NonSquareMatrixException, DimensionMismatchException, MatrixDimensionMismatchException {
    MathUtils.checkNotNull(process);
    MathUtils.checkNotNull(measurement);
    this.processModel=process;
    this.measurementModel=measurement;
    transitionMatrix=processModel.getStateTransitionMatrix();
    MathUtils.checkNotNull(transitionMatrix);
    transitionMatrixT=transitionMatrix.transpose();
    if (processModel.getControlMatrix() == null) {
      controlMatrix=new Array2DRowRealMatrix();
    }
 else {
      controlMatrix=processModel.getControlMatrix();
    }
    measurementMatrix=measurementModel.getMeasurementMatrix();
    MathUtils.checkNotNull(measurementMatrix);
    measurementMatrixT=measurementMatrix.transpose();
    RealMatrix processNoise=processModel.getProcessNoise();
    MathUtils.checkNotNull(processNoise);
    RealMatrix measNoise=measurementModel.getMeasurementNoise();
    MathUtils.checkNotNull(measNoise);
    if (processModel.getInitialStateEstimate() == null) {
      stateEstimation=new ArrayRealVector(transitionMatrix.getColumnDimension());
    }
 else {
      stateEstimation=processModel.getInitialStateEstimate();
    }
    if (transitionMatrix.getColumnDimension() != stateEstimation.getDimension()) {
      throw new DimensionMismatchException(transitionMatrix.getColumnDimension(),stateEstimation.getDimension());
    }
    if (processModel.getInitialErrorCovariance() == null) {
      errorCovariance=processNoise.copy();
    }
 else {
      errorCovariance=processModel.getInitialErrorCovariance();
    }
    if (!transitionMatrix.isSquare()) {
      throw new NonSquareMatrixException(transitionMatrix.getRowDimension(),transitionMatrix.getColumnDimension());
    }
    if (controlMatrix != null && controlMatrix.getRowDimension() > 0 && controlMatrix.getColumnDimension() > 0 && (controlMatrix.getRowDimension() != transitionMatrix.getRowDimension() || controlMatrix.getColumnDimension() != 1)) {
      throw new MatrixDimensionMismatchException(controlMatrix.getRowDimension(),controlMatrix.getColumnDimension(),transitionMatrix.getRowDimension(),1);
    }
    MatrixUtils.checkAdditionCompatible(transitionMatrix,processNoise);
    if (measurementMatrix.getColumnDimension() != transitionMatrix.getRowDimension()) {
      throw new MatrixDimensionMismatchException(measurementMatrix.getRowDimension(),measurementMatrix.getColumnDimension(),measurementMatrix.getRowDimension(),transitionMatrix.getRowDimension());
    }
    if (measNoise.getRowDimension() != measurementMatrix.getRowDimension() || measNoise.getColumnDimension() != 1) {
      throw new MatrixDimensionMismatchException(measNoise.getRowDimension(),measNoise.getColumnDimension(),measurementMatrix.getRowDimension(),1);
    }
  }
  /** 
 * Returns the dimension of the state estimation vector.
 * @return the state dimension
 */
  public int getStateDimension(){
    return stateEstimation.getDimension();
  }
  /** 
 * Returns the dimension of the measurement vector.
 * @return the measurement vector dimension
 */
  public int getMeasurementDimension(){
    return measurementMatrix.getRowDimension();
  }
  /** 
 * Returns the current state estimation vector.
 * @return the state estimation vector
 */
  public double[] getStateEstimation(){
    return stateEstimation.toArray();
  }
  /** 
 * Returns a copy of the current state estimation vector.
 * @return the state estimation vector
 */
  public RealVector getStateEstimationVector(){
    return stateEstimation.copy();
  }
  /** 
 * Returns the current error covariance matrix.
 * @return the error covariance matrix
 */
  public double[][] getErrorCovariance(){
    return errorCovariance.getData();
  }
  /** 
 * Returns a copy of the current error covariance matrix.
 * @return the error covariance matrix
 */
  public RealMatrix getErrorCovarianceMatrix(){
    return errorCovariance.copy();
  }
  /** 
 * Predict the internal state estimation one time step ahead.
 */
  public void predict(){
    predict((RealVector)null);
  }
  /** 
 * Predict the internal state estimation one time step ahead.
 * @param uthe control vector
 * @throws DimensionMismatchExceptionif the dimension of the control vector does not fit
 */
  public void predict(  final double[] u) throws DimensionMismatchException {
    predict(new ArrayRealVector(u));
  }
  /** 
 * Predict the internal state estimation one time step ahead.
 * @param uthe control vector
 * @throws DimensionMismatchExceptionif the dimension of the control vector does not match
 */
  public void predict(  final RealVector u) throws DimensionMismatchException {
    if (u != null && u.getDimension() != controlMatrix.getColumnDimension()) {
      throw new DimensionMismatchException(u.getDimension(),controlMatrix.getColumnDimension());
    }
    stateEstimation=transitionMatrix.operate(stateEstimation);
    if (u != null) {
      stateEstimation=stateEstimation.add(controlMatrix.operate(u));
    }
    errorCovariance=transitionMatrix.multiply(errorCovariance).multiply(transitionMatrixT).add(processModel.getProcessNoise());
  }
  /** 
 * Correct the current state estimate with an actual measurement.
 * @param zthe measurement vector
 * @throws NullArgumentExceptionif the measurement vector is {@code null}
 * @throws DimensionMismatchExceptionif the dimension of the measurement vector does not fit
 * @throws SingularMatrixExceptionif the covariance matrix could not be inverted
 */
  public void correct(  final double[] z) throws NullArgumentException, DimensionMismatchException, SingularMatrixException {
    correct(new ArrayRealVector(z));
  }
  /** 
 * Correct the current state estimate with an actual measurement.
 * @param zthe measurement vector
 * @throws NullArgumentExceptionif the measurement vector is {@code null}
 * @throws DimensionMismatchExceptionif the dimension of the measurement vector does not fit
 * @throws SingularMatrixExceptionif the covariance matrix could not be inverted
 */
  public void correct(  final RealVector z) throws NullArgumentException, DimensionMismatchException, SingularMatrixException {
    MathUtils.checkNotNull(z);
    if (z.getDimension() != measurementMatrix.getRowDimension()) {
      throw new DimensionMismatchException(z.getDimension(),measurementMatrix.getRowDimension());
    }
    RealMatrix s=measurementMatrix.multiply(errorCovariance).multiply(measurementMatrixT).add(measurementModel.getMeasurementNoise());
    DecompositionSolver solver=new CholeskyDecomposition(s).getSolver();
    RealMatrix invertedS=solver.getInverse();
    RealVector innovation=z.subtract(measurementMatrix.operate(stateEstimation));
    RealMatrix kalmanGain=errorCovariance.multiply(measurementMatrixT).multiply(invertedS);
    stateEstimation=stateEstimation.add(kalmanGain.operate(innovation));
    RealMatrix identity=MatrixUtils.createRealIdentityMatrix(kalmanGain.getRowDimension());
    errorCovariance=identity.subtract(kalmanGain.multiply(measurementMatrix)).multiply(errorCovariance);
  }
}
