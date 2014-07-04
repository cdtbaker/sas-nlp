package org.apache.commons.math3.ode.sampling;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.TestProblemAbstract;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
public class StepInterpolatorTestUtils {
  public static void checkDerivativesConsistency(  final FirstOrderIntegrator integrator,  final TestProblemAbstract problem,  final double threshold) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    integrator.addStepHandler(new StepHandler(){
      public void handleStep(      StepInterpolator interpolator,      boolean isLast) throws MaxCountExceededException {
        final double h=0.001 * (interpolator.getCurrentTime() - interpolator.getPreviousTime());
        final double t=interpolator.getCurrentTime() - 300 * h;
        if (FastMath.abs(h) < 10 * FastMath.ulp(t)) {
          return;
        }
        interpolator.setInterpolatedTime(t - 4 * h);
        final double[] yM4h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t - 3 * h);
        final double[] yM3h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t - 2 * h);
        final double[] yM2h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t - h);
        final double[] yM1h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t + h);
        final double[] yP1h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t + 2 * h);
        final double[] yP2h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t + 3 * h);
        final double[] yP3h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t + 4 * h);
        final double[] yP4h=interpolator.getInterpolatedState().clone();
        interpolator.setInterpolatedTime(t);
        final double[] yDot=interpolator.getInterpolatedDerivatives();
        for (int i=0; i < yDot.length; ++i) {
          final double approYDot=(-3 * (yP4h[i] - yM4h[i]) + 32 * (yP3h[i] - yM3h[i]) + -168 * (yP2h[i] - yM2h[i]) + 672 * (yP1h[i] - yM1h[i])) / (840 * h);
          Assert.assertEquals(approYDot,yDot[i],threshold);
        }
      }
      public void init(      double t0,      double[] y0,      double t){
      }
    }
);
    integrator.integrate(problem,problem.getInitialTime(),problem.getInitialState(),problem.getFinalTime(),new double[problem.getDimension()]);
  }
}
