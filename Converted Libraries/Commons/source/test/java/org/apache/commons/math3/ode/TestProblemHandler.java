package org.apache.commons.math3.ode;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;
/** 
 * This class is used to handle steps for the test problems
 * integrated during the junit tests for the ODE integrators.
 */
public class TestProblemHandler implements StepHandler {
  /** 
 * Associated problem. 
 */
  private TestProblemAbstract problem;
  /** 
 * Maximal errors encountered during the integration. 
 */
  private double maxValueError;
  private double maxTimeError;
  /** 
 * Error at the end of the integration. 
 */
  private double lastError;
  /** 
 * Time at the end of integration. 
 */
  private double lastTime;
  /** 
 * ODE solver used. 
 */
  private ODEIntegrator integrator;
  /** 
 * Expected start for step. 
 */
  private double expectedStepStart;
  /** 
 * Simple constructor.
 * @param problem problem for which steps should be handled
 * @param integrator ODE solver used
 */
  public TestProblemHandler(  TestProblemAbstract problem,  ODEIntegrator integrator){
    this.problem=problem;
    this.integrator=integrator;
    maxValueError=0;
    maxTimeError=0;
    lastError=0;
    expectedStepStart=Double.NaN;
  }
  public void init(  double t0,  double[] y0,  double t){
    maxValueError=0;
    maxTimeError=0;
    lastError=0;
    expectedStepStart=Double.NaN;
  }
  public void handleStep(  StepInterpolator interpolator,  boolean isLast) throws MaxCountExceededException {
    double start=integrator.getCurrentStepStart();
    if (FastMath.abs((start - problem.getInitialTime()) / integrator.getCurrentSignedStepsize()) > 0.001) {
      if (!Double.isNaN(expectedStepStart)) {
        double stepError=FastMath.max(maxTimeError,FastMath.abs(start - expectedStepStart));
        for (        double eventTime : problem.getTheoreticalEventsTimes()) {
          stepError=FastMath.min(stepError,FastMath.abs(start - eventTime));
        }
        maxTimeError=FastMath.max(maxTimeError,stepError);
      }
      expectedStepStart=start + integrator.getCurrentSignedStepsize();
    }
    double pT=interpolator.getPreviousTime();
    double cT=interpolator.getCurrentTime();
    double[] errorScale=problem.getErrorScale();
    if (isLast) {
      double[] interpolatedY=interpolator.getInterpolatedState();
      double[] theoreticalY=problem.computeTheoreticalState(cT);
      for (int i=0; i < interpolatedY.length; ++i) {
        double error=FastMath.abs(interpolatedY[i] - theoreticalY[i]);
        lastError=FastMath.max(error,lastError);
      }
      lastTime=cT;
    }
    for (int k=0; k <= 20; ++k) {
      double time=pT + (k * (cT - pT)) / 20;
      interpolator.setInterpolatedTime(time);
      double[] interpolatedY=interpolator.getInterpolatedState();
      double[] theoreticalY=problem.computeTheoreticalState(interpolator.getInterpolatedTime());
      for (int i=0; i < interpolatedY.length; ++i) {
        double error=errorScale[i] * FastMath.abs(interpolatedY[i] - theoreticalY[i]);
        maxValueError=FastMath.max(error,maxValueError);
      }
    }
  }
  /** 
 * Get the maximal value error encountered during integration.
 * @return maximal value error
 */
  public double getMaximalValueError(){
    return maxValueError;
  }
  /** 
 * Get the maximal time error encountered during integration.
 * @return maximal time error
 */
  public double getMaximalTimeError(){
    return maxTimeError;
  }
  /** 
 * Get the error at the end of the integration.
 * @return error at the end of the integration
 */
  public double getLastError(){
    return lastError;
  }
  /** 
 * Get the time at the end of the integration.
 * @return time at the end of the integration.
 */
  public double getLastTime(){
    return lastTime;
  }
}
