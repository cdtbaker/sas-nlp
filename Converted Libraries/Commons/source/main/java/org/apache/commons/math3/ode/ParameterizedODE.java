package org.apache.commons.math3.ode;
/** 
 * Interface to compute by finite difference Jacobian matrix for some parameter
 * when computing {@link JacobianMatrices partial derivatives equations}.
 * @version $Id: ParameterizedODE.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public interface ParameterizedODE extends Parameterizable {
  /** 
 * Get parameter value from its name.
 * @param name parameter name
 * @return parameter value
 * @exception UnknownParameterException if parameter is not supported
 */
  double getParameter(  String name) throws UnknownParameterException ;
  /** 
 * Set the value for a given parameter.
 * @param name parameter name
 * @param value parameter value
 * @exception UnknownParameterException if parameter is not supported
 */
  void setParameter(  String name,  double value) throws UnknownParameterException ;
}
