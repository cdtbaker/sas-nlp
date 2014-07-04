package org.apache.commons.math3.ode.nonstiff;
/** 
 * This class implements a second order Runge-Kutta integrator for
 * Ordinary Differential Equations.
 * <p>This method is an explicit Runge-Kutta method, its Butcher-array
 * is the following one :
 * <pre>
 * 0  |  0    0
 * 1/2 | 1/2   0
 * |----------
 * |  0    1
 * </pre>
 * </p>
 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see GillIntegrator
 * @version $Id: MidpointIntegrator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
public class MidpointIntegrator extends RungeKuttaIntegrator {
  /** 
 * Time steps Butcher array. 
 */
  private static final double[] STATIC_C={1.0 / 2.0};
  /** 
 * Internal weights Butcher array. 
 */
  private static final double[][] STATIC_A={{1.0 / 2.0}};
  /** 
 * Propagation weights Butcher array. 
 */
  private static final double[] STATIC_B={0.0,1.0};
  /** 
 * Simple constructor.
 * Build a midpoint integrator with the given step.
 * @param step integration step
 */
  public MidpointIntegrator(  final double step){
    super("midpoint",STATIC_C,STATIC_A,STATIC_B,new MidpointStepInterpolator(),step);
  }
}
