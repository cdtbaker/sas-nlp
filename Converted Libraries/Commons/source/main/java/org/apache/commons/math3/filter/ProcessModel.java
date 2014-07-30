package org.apache.commons.math3.filter;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
/** 
 * Defines the process dynamics model for the use with a {@link KalmanFilter}.
 * @since 3.0
 * @version $Id: ProcessModel.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface ProcessModel {
  /** 
 * Returns the state transition matrix.
 * @return the state transition matrix
 */
  RealMatrix getStateTransitionMatrix();
  /** 
 * Returns the control matrix.
 * @return the control matrix
 */
  RealMatrix getControlMatrix();
  /** 
 * Returns the process noise matrix. This method is called by the {@link KalmanFilter} every
 * prediction step, so implementations of this interface may return a modified process noise
 * depending on the current iteration step.
 * @return the process noise matrix
 * @see KalmanFilter#predict()
 * @see KalmanFilter#predict(double[])
 * @see KalmanFilter#predict(RealVector)
 */
  RealMatrix getProcessNoise();
  /** 
 * Returns the initial state estimation vector.
 * <p>
 * <b>Note:</b> if the return value is zero, the Kalman filter will initialize the
 * state estimation with a zero vector.
 * @return the initial state estimation vector
 */
  RealVector getInitialStateEstimate();
  /** 
 * Returns the initial error covariance matrix.
 * <p>
 * <b>Note:</b> if the return value is zero, the Kalman filter will initialize the
 * error covariance with the process noise matrix.
 * @return the initial error covariance matrix
 */
  RealMatrix getInitialErrorCovariance();
}