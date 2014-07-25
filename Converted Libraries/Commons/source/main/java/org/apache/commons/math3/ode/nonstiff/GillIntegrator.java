package org.apache.commons.math3.ode.nonstiff;
import org.apache.commons.math3.util.FastMath;
/** 
 * This class implements the Gill fourth order Runge-Kutta
 * integrator for Ordinary Differential Equations .
 * <p>This method is an explicit Runge-Kutta method, its Butcher-array
 * is the following one :
 * <pre>
 * 0  |    0        0       0      0
 * 1/2 |   1/2       0       0      0
 * 1/2 | (q-1)/2  (2-q)/2    0      0
 * 1  |    0       -q/2  (2+q)/2   0
 * |-------------------------------
 * |   1/6    (2-q)/6 (2+q)/6  1/6
 * </pre>
 * where q = sqrt(2)</p>
 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see MidpointIntegrator
 * @see ThreeEighthesIntegrator
 * @version $Id: GillIntegrator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
public class GillIntegrator extends RungeKuttaIntegrator {
  /** 
 * Time steps Butcher array. 
 */
  private static final double[] STATIC_C={1.0 / 2.0,1.0 / 2.0,1.0};
  /** 
 * Internal weights Butcher array. 
 */
  private static final double[][] STATIC_A={{1.0 / 2.0},{(FastMath.sqrt(2.0) - 1.0) / 2.0,(2.0 - FastMath.sqrt(2.0)) / 2.0},{0.0,-FastMath.sqrt(2.0) / 2.0,(2.0 + FastMath.sqrt(2.0)) / 2.0}};
  /** 
 * Propagation weights Butcher array. 
 */
  private static final double[] STATIC_B={1.0 / 6.0,(2.0 - FastMath.sqrt(2.0)) / 6.0,(2.0 + FastMath.sqrt(2.0)) / 6.0,1.0 / 6.0};
  /** 
 * Simple constructor.
 * Build a fourth-order Gill integrator with the given step.
 * @param step integration step
 */
  public GillIntegrator(  final double step){
    super("Gill",STATIC_C,STATIC_A,STATIC_B,new GillStepInterpolator(),step);
  }
}
