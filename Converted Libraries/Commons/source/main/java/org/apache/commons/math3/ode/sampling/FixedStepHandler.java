package org.apache.commons.math3.ode.sampling;
/** 
 * This interface represents a handler that should be called after
 * each successful fixed step.
 * <p>This interface should be implemented by anyone who is interested
 * in getting the solution of an ordinary differential equation at
 * fixed time steps. Objects implementing this interface should be
 * wrapped within an instance of {@link StepNormalizer} that itself
 * is used as the general {@link StepHandler} by the integrator. The{@link StepNormalizer} object is called according to the integrator
 * internal algorithms and it calls objects implementing this
 * interface as necessary at fixed time steps.</p>
 * @see StepHandler
 * @see StepNormalizer
 * @version $Id: FixedStepHandler.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
public interface FixedStepHandler {
  /** 
 * Initialize step handler at the start of an ODE integration.
 * <p>
 * This method is called once at the start of the integration. It
 * may be used by the step handler to initialize some internal data
 * if needed.
 * </p>
 * @param t0 start value of the independent <i>time</i> variable
 * @param y0 array containing the start value of the state vector
 * @param t target time for the integration
 */
  void init(  double t0,  double[] y0,  double t);
  /** 
 * Handle the last accepted step
 * @param t time of the current step
 * @param y state vector at t. For efficiency purposes, the {@link StepNormalizer} class reuses the same array on each call, so if
 * the instance wants to keep it across all calls (for example to
 * provide at the end of the integration a complete array of all
 * steps), it should build a local copy store this copy.
 * @param yDot derivatives of the state vector state vector at t.
 * For efficiency purposes, the {@link StepNormalizer} class reuses
 * the same array on each call, so if
 * the instance wants to keep it across all calls (for example to
 * provide at the end of the integration a complete array of all
 * steps), it should build a local copy store this copy.
 * @param isLast true if the step is the last one
 */
  void handleStep(  double t,  double[] y,  double[] yDot,  boolean isLast);
}
