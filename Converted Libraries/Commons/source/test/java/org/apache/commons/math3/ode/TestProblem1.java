package org.apache.commons.math3.ode;
import org.apache.commons.math3.util.FastMath;
/** 
 * This class is used in the junit tests for the ODE integrators.
 * <p>This specific problem is the following differential equation :
 * <pre>
 * y' = -y
 * </pre>
 * the solution of this equation is a simple exponential function :
 * <pre>
 * y (t) = y (t0) exp (t0-t)
 * </pre>
 * </p>
 */
public class TestProblem1 extends TestProblemAbstract {
  /** 
 * theoretical state 
 */
  private double[] y;
  /** 
 * Simple constructor.
 */
  public TestProblem1(){
    super();
    double[] y0={1.0,0.1};
    setInitialConditions(0.0,y0);
    setFinalConditions(4.0);
    double[] errorScale={1.0,1.0};
    setErrorScale(errorScale);
    y=new double[y0.length];
  }
  /** 
 * Copy constructor.
 * @param problem problem to copy
 */
  public TestProblem1(  TestProblem1 problem){
    super(problem);
    y=problem.y.clone();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public TestProblem1 copy(){
    return new TestProblem1(this);
  }
  @Override public void doComputeDerivatives(  double t,  double[] y,  double[] yDot){
    for (int i=0; i < n; ++i)     yDot[i]=-y[i];
  }
  @Override public double[] computeTheoreticalState(  double t){
    double c=FastMath.exp(t0 - t);
    for (int i=0; i < n; ++i) {
      y[i]=c * y0[i];
    }
    return y;
  }
}
