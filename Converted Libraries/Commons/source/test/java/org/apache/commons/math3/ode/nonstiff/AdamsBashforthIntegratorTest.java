package org.apache.commons.math3.ode.nonstiff;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.TestProblem1;
import org.apache.commons.math3.ode.TestProblem5;
import org.apache.commons.math3.ode.TestProblem6;
import org.apache.commons.math3.ode.TestProblemHandler;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class AdamsBashforthIntegratorTest {
  @Test(expected=DimensionMismatchException.class) public void dimensionCheck() throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
    TestProblem1 pb=new TestProblem1();
    FirstOrderIntegrator integ=new AdamsBashforthIntegrator(2,0.0,1.0,1.0e-10,1.0e-10);
    integ.integrate(pb,0.0,new double[pb.getDimension() + 10],1.0,new double[pb.getDimension() + 10]);
  }
  @Test(expected=NumberIsTooSmallException.class) public void testMinStep() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    TestProblem1 pb=new TestProblem1();
    double minStep=0.1 * (pb.getFinalTime() - pb.getInitialTime());
    double maxStep=pb.getFinalTime() - pb.getInitialTime();
    double[] vecAbsoluteTolerance={1.0e-15,1.0e-16};
    double[] vecRelativeTolerance={1.0e-15,1.0e-16};
    FirstOrderIntegrator integ=new AdamsBashforthIntegrator(4,minStep,maxStep,vecAbsoluteTolerance,vecRelativeTolerance);
    TestProblemHandler handler=new TestProblemHandler(pb,integ);
    integ.addStepHandler(handler);
    integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
  }
  @Test public void testIncreasingTolerance() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    int previousCalls=Integer.MAX_VALUE;
    for (int i=-12; i < -5; ++i) {
      TestProblem1 pb=new TestProblem1();
      double minStep=0;
      double maxStep=pb.getFinalTime() - pb.getInitialTime();
      double scalAbsoluteTolerance=FastMath.pow(10.0,i);
      double scalRelativeTolerance=0.01 * scalAbsoluteTolerance;
      FirstOrderIntegrator integ=new AdamsBashforthIntegrator(4,minStep,maxStep,scalAbsoluteTolerance,scalRelativeTolerance);
      TestProblemHandler handler=new TestProblemHandler(pb,integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
      Assert.assertTrue(handler.getMaximalValueError() > (50.0 * scalAbsoluteTolerance));
      Assert.assertTrue(handler.getMaximalValueError() < (300.0 * scalAbsoluteTolerance));
      Assert.assertEquals(0,handler.getMaximalTimeError(),1.0e-16);
      int calls=pb.getCalls();
      Assert.assertEquals(integ.getEvaluations(),calls);
      Assert.assertTrue(calls <= previousCalls);
      previousCalls=calls;
    }
  }
  @Test(expected=MaxCountExceededException.class) public void exceedMaxEvaluations() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    TestProblem1 pb=new TestProblem1();
    double range=pb.getFinalTime() - pb.getInitialTime();
    AdamsBashforthIntegrator integ=new AdamsBashforthIntegrator(2,0,range,1.0e-12,1.0e-12);
    TestProblemHandler handler=new TestProblemHandler(pb,integ);
    integ.addStepHandler(handler);
    integ.setMaxEvaluations(650);
    integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
  }
  @Test public void backward() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    TestProblem5 pb=new TestProblem5();
    double range=FastMath.abs(pb.getFinalTime() - pb.getInitialTime());
    FirstOrderIntegrator integ=new AdamsBashforthIntegrator(4,0,range,1.0e-12,1.0e-12);
    TestProblemHandler handler=new TestProblemHandler(pb,integ);
    integ.addStepHandler(handler);
    integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
    Assert.assertTrue(handler.getLastError() < 1.5e-8);
    Assert.assertTrue(handler.getMaximalValueError() < 1.5e-8);
    Assert.assertEquals(0,handler.getMaximalTimeError(),1.0e-16);
    Assert.assertEquals("Adams-Bashforth",integ.getName());
  }
  @Test public void polynomial() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    TestProblem6 pb=new TestProblem6();
    double range=FastMath.abs(pb.getFinalTime() - pb.getInitialTime());
    for (int nSteps=2; nSteps < 8; ++nSteps) {
      AdamsBashforthIntegrator integ=new AdamsBashforthIntegrator(nSteps,1.0e-6 * range,0.1 * range,1.0e-5,1.0e-5);
      TestProblemHandler handler=new TestProblemHandler(pb,integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
      if (nSteps < 4) {
        Assert.assertTrue(handler.getMaximalValueError() > 1.0e-03);
      }
 else {
        Assert.assertTrue(handler.getMaximalValueError() < 4.0e-12);
      }
    }
  }
}