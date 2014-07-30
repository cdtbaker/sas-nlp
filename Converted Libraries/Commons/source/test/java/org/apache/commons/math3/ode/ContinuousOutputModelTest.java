package org.apache.commons.math3.ode;
import java.util.Random;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.DummyStepInterpolator;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class ContinuousOutputModelTest {
  public ContinuousOutputModelTest(){
    pb=null;
    integ=null;
  }
  @Test public void testBoundaries() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
    ContinuousOutputModel cm=(ContinuousOutputModel)integ.getStepHandlers().iterator().next();
    cm.setInterpolatedTime(2.0 * pb.getInitialTime() - pb.getFinalTime());
    cm.setInterpolatedTime(2.0 * pb.getFinalTime() - pb.getInitialTime());
    cm.setInterpolatedTime(0.5 * (pb.getFinalTime() + pb.getInitialTime()));
  }
  @Test public void testRandomAccess() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    ContinuousOutputModel cm=new ContinuousOutputModel();
    integ.addStepHandler(cm);
    integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
    Random random=new Random(347588535632l);
    double maxError=0.0;
    for (int i=0; i < 1000; ++i) {
      double r=random.nextDouble();
      double time=r * pb.getInitialTime() + (1.0 - r) * pb.getFinalTime();
      cm.setInterpolatedTime(time);
      double[] interpolatedY=cm.getInterpolatedState();
      double[] theoreticalY=pb.computeTheoreticalState(time);
      double dx=interpolatedY[0] - theoreticalY[0];
      double dy=interpolatedY[1] - theoreticalY[1];
      double error=dx * dx + dy * dy;
      if (error > maxError) {
        maxError=error;
      }
    }
    Assert.assertTrue(maxError < 1.0e-9);
  }
  @Test public void testModelsMerging() throws MaxCountExceededException, MathIllegalArgumentException {
    FirstOrderDifferentialEquations problem=new FirstOrderDifferentialEquations(){
      public void computeDerivatives(      double t,      double[] y,      double[] dot){
        dot[0]=-y[1];
        dot[1]=y[0];
      }
      public int getDimension(){
        return 2;
      }
    }
;
    ContinuousOutputModel cm1=new ContinuousOutputModel();
    FirstOrderIntegrator integ1=new DormandPrince853Integrator(0,1.0,1.0e-8,1.0e-8);
    integ1.addStepHandler(cm1);
    integ1.integrate(problem,FastMath.PI,new double[]{-1.0,0.0},0,new double[2]);
    ContinuousOutputModel cm2=new ContinuousOutputModel();
    FirstOrderIntegrator integ2=new DormandPrince853Integrator(0,0.1,1.0e-12,1.0e-12);
    integ2.addStepHandler(cm2);
    integ2.integrate(problem,2.0 * FastMath.PI,new double[]{1.0,0.0},FastMath.PI,new double[2]);
    ContinuousOutputModel cm=new ContinuousOutputModel();
    cm.append(cm2);
    cm.append(new ContinuousOutputModel());
    cm.append(cm1);
    Assert.assertEquals(2.0 * FastMath.PI,cm.getInitialTime(),1.0e-12);
    Assert.assertEquals(0,cm.getFinalTime(),1.0e-12);
    Assert.assertEquals(cm.getFinalTime(),cm.getInterpolatedTime(),1.0e-12);
    for (double t=0; t < 2.0 * FastMath.PI; t+=0.1) {
      cm.setInterpolatedTime(t);
      double[] y=cm.getInterpolatedState();
      Assert.assertEquals(FastMath.cos(t),y[0],1.0e-7);
      Assert.assertEquals(FastMath.sin(t),y[1],1.0e-7);
    }
  }
  @Test public void testErrorConditions() throws MaxCountExceededException, MathIllegalArgumentException {
    ContinuousOutputModel cm=new ContinuousOutputModel();
    cm.handleStep(buildInterpolator(0,new double[]{0.0,1.0,-2.0},1),true);
    Assert.assertTrue(checkAppendError(cm,1.0,new double[]{0.0,1.0},2.0));
    Assert.assertTrue(checkAppendError(cm,10.0,new double[]{0.0,1.0,-2.0},20.0));
    Assert.assertTrue(checkAppendError(cm,1.0,new double[]{0.0,1.0,-2.0},0.0));
    Assert.assertFalse(checkAppendError(cm,1.0,new double[]{0.0,1.0,-2.0},2.0));
  }
  private boolean checkAppendError(  ContinuousOutputModel cm,  double t0,  double[] y0,  double t1) throws MaxCountExceededException, MathIllegalArgumentException {
    try {
      ContinuousOutputModel otherCm=new ContinuousOutputModel();
      otherCm.handleStep(buildInterpolator(t0,y0,t1),true);
      cm.append(otherCm);
    }
 catch (    IllegalArgumentException iae) {
      return true;
    }
    return false;
  }
  private StepInterpolator buildInterpolator(  double t0,  double[] y0,  double t1){
    DummyStepInterpolator interpolator=new DummyStepInterpolator(y0,new double[y0.length],t1 >= t0);
    interpolator.storeTime(t0);
    interpolator.shift();
    interpolator.storeTime(t1);
    return interpolator;
  }
  public void checkValue(  double value,  double reference){
    Assert.assertTrue(FastMath.abs(value - reference) < 1.0e-10);
  }
  @Before public void setUp(){
    pb=new TestProblem3(0.9);
    double minStep=0;
    double maxStep=pb.getFinalTime() - pb.getInitialTime();
    integ=new DormandPrince54Integrator(minStep,maxStep,1.0e-8,1.0e-8);
  }
  @After public void tearDown(){
    pb=null;
    integ=null;
  }
  TestProblem3 pb;
  FirstOrderIntegrator integ;
}