package org.apache.commons.math3.ode;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
/** 
 * Interface to compute exactly Jacobian matrix for some parameter
 * when computing {@link JacobianMatrices partial derivatives equations}.
 * @version $Id: ParameterJacobianProvider.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public interface ParameterJacobianProvider extends Parameterizable {
  /** 
 * Compute the Jacobian matrix of ODE with respect to one parameter.
 * <p>If the parameter does not belong to the collection returned by{@link #getParametersNames()}, the Jacobian will be set to 0,
 * but no errors will be triggered.</p>
 * @param t current value of the independent <I>time</I> variable
 * @param y array containing the current value of the main state vector
 * @param yDot array containing the current value of the time derivative
 * of the main state vector
 * @param paramName name of the parameter to consider
 * @param dFdP placeholder array where to put the Jacobian matrix of the
 * ODE with respect to the parameter
 * @exception MaxCountExceededException if the number of functions evaluations is exceeded
 * @exception DimensionMismatchException if arrays dimensions do not match equations settings
 * @exception UnknownParameterException if the parameter is not supported
 */
  void computeParameterJacobian(  double t,  double[] y,  double[] yDot,  String paramName,  double[] dFdP) throws DimensionMismatchException, MaxCountExceededException, UnknownParameterException ;
}
