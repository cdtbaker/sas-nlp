package org.apache.commons.math3.ode;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
/** 
 * This interface represents a second order integrator for
 * differential equations.
 * <p>The classes which are devoted to solve second order differential
 * equations should implement this interface. The problems which can
 * be handled should implement the {@link SecondOrderDifferentialEquations} interface.</p>
 * @see SecondOrderDifferentialEquations
 * @version $Id: SecondOrderIntegrator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
public interface SecondOrderIntegrator extends ODEIntegrator {
  /** 
 * Integrate the differential equations up to the given time
 * @param equations differential equations to integrate
 * @param t0 initial time
 * @param y0 initial value of the state vector at t0
 * @param yDot0 initial value of the first derivative of the state
 * vector at t0
 * @param t target time for the integration
 * (can be set to a value smaller thant <code>t0</code> for backward integration)
 * @param y placeholder where to put the state vector at each
 * successful step (and hence at the end of integration), can be the
 * same object as y0
 * @param yDot placeholder where to put the first derivative of
 * the state vector at time t, can be the same object as yDot0
 * @throws MathIllegalStateException if the integrator cannot perform integration
 * @throws MathIllegalArgumentException if integration parameters are wrong (typically
 * too small integration span)
 */
  void integrate(  SecondOrderDifferentialEquations equations,  double t0,  double[] y0,  double[] yDot0,  double t,  double[] y,  double[] yDot) throws MathIllegalStateException, MathIllegalArgumentException ;
}
