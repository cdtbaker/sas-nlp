package org.apache.commons.math3.ode;
/** 
 * This class is used in the junit tests for the ODE integrators.
 * <p>This specific problem is the following differential equation :
 * <pre>
 * y' = 3x^5 - y
 * </pre>
 * when the initial condition is y(0) = -360, the solution of this
 * equation degenerates to a simple quintic polynomial function :
 * <pre>
 * y (t) = 3x^5 - 15x^4 + 60x^3 - 180x^2 + 360x - 360
 * </pre>
 * </p>
 */
public class TestProblem6 extends TestProblemAbstract {
  /** 
 * theoretical state 
 */
  private double[] y;
  /** 
 * Simple constructor.
 */
  public TestProblem6(){
    super();
    double[] y0={-360.0};
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
  public TestProblem6(  TestProblem6 problem){
    super(problem);
    y=problem.y.clone();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public TestProblem6 copy(){
    return new TestProblem6(this);
  }
  @Override public void doComputeDerivatives(  double t,  double[] y,  double[] yDot){
    double t2=t * t;
    double t4=t2 * t2;
    double t5=t4 * t;
    for (int i=0; i < n; ++i) {
      yDot[i]=3 * t5 - y[i];
    }
  }
  @Override public double[] computeTheoreticalState(  double t){
    for (int i=0; i < n; ++i) {
      y[i]=((((3 * t - 15) * t + 60) * t - 180) * t + 360) * t - 360;
    }
    return y;
  }
}
