package org.apache.commons.math3.ode.nonstiff;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.events.EventHandler;
public class StepProblem implements FirstOrderDifferentialEquations, EventHandler {
  public StepProblem(  double rateBefore,  double rateAfter,  double switchTime){
    this.rateAfter=rateAfter;
    this.switchTime=switchTime;
    setRate(rateBefore);
  }
  public void computeDerivatives(  double t,  double[] y,  double[] yDot){
    yDot[0]=rate;
  }
  public int getDimension(){
    return 1;
  }
  public void setRate(  double rate){
    this.rate=rate;
  }
  public void init(  double t0,  double[] y0,  double t){
  }
  public Action eventOccurred(  double t,  double[] y,  boolean increasing){
    setRate(rateAfter);
    return Action.RESET_DERIVATIVES;
  }
  public double g(  double t,  double[] y){
    return t - switchTime;
  }
  public void resetState(  double t,  double[] y){
  }
  private double rate;
  private double rateAfter;
  private double switchTime;
}
