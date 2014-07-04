package org.apache.commons.math3.ode;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
/** 
 * Interface expanding {@link FirstOrderDifferentialEquations first order
 * differential equations} in order to compute exactly the main state jacobian
 * matrix for {@link JacobianMatrices partial derivatives equations}.
 * @version $Id: MainStateJacobianProvider.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public interface MainStateJacobianProvider extends FirstOrderDifferentialEquations {
  /** 
 * Compute the jacobian matrix of ODE with respect to main state.
 * @param t current value of the independent <I>time</I> variable
 * @param y array containing the current value of the main state vector
 * @param yDot array containing the current value of the time derivative of the main state vector
 * @param dFdY placeholder array where to put the jacobian matrix of the ODE w.r.t. the main state vector
 * @exception MaxCountExceededException if the number of functions evaluations is exceeded
 * @exception DimensionMismatchException if arrays dimensions do not match equations settings
 */
  void computeMainStateJacobian(  double t,  double[] y,  double[] yDot,  double[][] dFdY) throws MaxCountExceededException, DimensionMismatchException ;
}
