package org.apache.commons.math3.ode;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
/** 
 * This interface represents a first order integrator for
 * differential equations.
 * <p>The classes which are devoted to solve first order differential
 * equations should implement this interface. The problems which can
 * be handled should implement the {@link FirstOrderDifferentialEquations} interface.</p>
 * @see FirstOrderDifferentialEquations
 * @see org.apache.commons.math3.ode.sampling.StepHandler
 * @see org.apache.commons.math3.ode.events.EventHandler
 * @version $Id: FirstOrderIntegrator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
public interface FirstOrderIntegrator extends ODEIntegrator {
  /** 
 * Integrate the differential equations up to the given time.
 * <p>This method solves an Initial Value Problem (IVP).</p>
 * <p>Since this method stores some internal state variables made
 * available in its public interface during integration ({@link #getCurrentSignedStepsize()}), it is <em>not</em> thread-safe.</p>
 * @param equations differential equations to integrate
 * @param t0 initial time
 * @param y0 initial value of the state vector at t0
 * @param t target time for the integration
 * (can be set to a value smaller than <code>t0</code> for backward integration)
 * @param y placeholder where to put the state vector at each successful
 * step (and hence at the end of integration), can be the same object as y0
 * @return stop time, will be the same as target time if integration reached its
 * target, but may be different if some {@link org.apache.commons.math3.ode.events.EventHandler} stops it at some point.
 * @exception DimensionMismatchException if arrays dimension do not match equations settings
 * @exception NumberIsTooSmallException if integration step is too small
 * @exception MaxCountExceededException if the number of functions evaluations is exceeded
 * @exception NoBracketingException if the location of an event cannot be bracketed
 */
  double integrate(  FirstOrderDifferentialEquations equations,  double t0,  double[] y0,  double t,  double[] y) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException ;
}
