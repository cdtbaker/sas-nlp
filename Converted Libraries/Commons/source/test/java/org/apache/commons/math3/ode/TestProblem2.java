package org.apache.commons.math3.ode;
import org.apache.commons.math3.util.FastMath;
/** 
 * This class is used in the junit tests for the ODE integrators.
 * <p>This specific problem is the following differential equation :
 * <pre>
 * y' = t^3 - t y
 * </pre>
 * with the initial condition y (0) = 0. The solution of this equation
 * is the following function :
 * <pre>
 * y (t) = t^2 + 2 (exp (- t^2 / 2) - 1)
 * </pre>
 * </p>
 */
public class TestProblem2 extends TestProblemAbstract {
  /** 
 * theoretical state 
 */
  private double[] y;
  /** 
 * Simple constructor.
 */
  public TestProblem2(){
    super();
    double[] y0={0.0};
    setInitialConditions(0.0,y0);
    setFinalConditions(1.0);
    double[] errorScale={1.0};
    setErrorScale(errorScale);
    y=new double[y0.length];
  }
  /** 
 * Copy constructor.
 * @param problem problem to copy
 */
  public TestProblem2(  TestProblem2 problem){
    super(problem);
    y=problem.y.clone();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public TestProblem2 copy(){
    return new TestProblem2(this);
  }
  @Override public void doComputeDerivatives(  double t,  double[] y,  double[] yDot){
    for (int i=0; i < n; ++i)     yDot[i]=t * (t * t - y[i]);
  }
  @Override public double[] computeTheoreticalState(  double t){
    double t2=t * t;
    double c=t2 + 2 * (FastMath.exp(-0.5 * t2) - 1);
    for (int i=0; i < n; ++i) {
      y[i]=c;
    }
    return y;
  }
}
