package org.apache.commons.math3.analysis.integration;
import java.util.Random;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class IterativeLegendreGaussIntegratorTest {
  @Test public void testSinFunction(){
    UnivariateFunction f=new Sin();
    BaseAbstractUnivariateIntegrator integrator=new IterativeLegendreGaussIntegrator(5,1.0e-14,1.0e-10,2,15);
    double min, max, expected, result, tolerance;
    min=0;
    max=FastMath.PI;
    expected=2;
    tolerance=FastMath.max(integrator.getAbsoluteAccuracy(),FastMath.abs(expected * integrator.getRelativeAccuracy()));
    result=integrator.integrate(10000,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=-FastMath.PI / 3;
    max=0;
    expected=-0.5;
    tolerance=FastMath.max(integrator.getAbsoluteAccuracy(),FastMath.abs(expected * integrator.getRelativeAccuracy()));
    result=integrator.integrate(10000,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
  }
  @Test public void testQuinticFunction(){
    UnivariateFunction f=new QuinticFunction();
    UnivariateIntegrator integrator=new IterativeLegendreGaussIntegrator(3,BaseAbstractUnivariateIntegrator.DEFAULT_RELATIVE_ACCURACY,BaseAbstractUnivariateIntegrator.DEFAULT_ABSOLUTE_ACCURACY,BaseAbstractUnivariateIntegrator.DEFAULT_MIN_ITERATIONS_COUNT,64);
    double min, max, expected, result;
    min=0;
    max=1;
    expected=-1.0 / 48;
    result=integrator.integrate(10000,f,min,max);
    Assert.assertEquals(expected,result,1.0e-16);
    min=0;
    max=0.5;
    expected=11.0 / 768;
    result=integrator.integrate(10000,f,min,max);
    Assert.assertEquals(expected,result,1.0e-16);
    min=-1;
    max=4;
    expected=2048 / 3.0 - 78 + 1.0 / 48;
    result=integrator.integrate(10000,f,min,max);
    Assert.assertEquals(expected,result,1.0e-16);
  }
  @Test public void testExactIntegration(){
    Random random=new Random(86343623467878363l);
    for (int n=2; n < 6; ++n) {
      IterativeLegendreGaussIntegrator integrator=new IterativeLegendreGaussIntegrator(n,BaseAbstractUnivariateIntegrator.DEFAULT_RELATIVE_ACCURACY,BaseAbstractUnivariateIntegrator.DEFAULT_ABSOLUTE_ACCURACY,BaseAbstractUnivariateIntegrator.DEFAULT_MIN_ITERATIONS_COUNT,64);
      for (int degree=0; degree <= 2 * n - 1; ++degree) {
        for (int i=0; i < 10; ++i) {
          double[] coeff=new double[degree + 1];
          for (int k=0; k < coeff.length; ++k) {
            coeff[k]=2 * random.nextDouble() - 1;
          }
          PolynomialFunction p=new PolynomialFunction(coeff);
          double result=integrator.integrate(10000,p,-5.0,15.0);
          double reference=exactIntegration(p,-5.0,15.0);
          Assert.assertEquals(n + " " + degree+ " "+ i,reference,result,1.0e-12 * (1.0 + FastMath.abs(reference)));
        }
      }
    }
  }
  @Test public void testIssue464(){
    final double value=0.2;
    UnivariateFunction f=new UnivariateFunction(){
      public double value(      double x){
        return (x >= 0 && x <= 5) ? value : 0.0;
      }
    }
;
    IterativeLegendreGaussIntegrator gauss=new IterativeLegendreGaussIntegrator(5,3,100);
    double maxX=0.32462367623786328;
    Assert.assertEquals(maxX * value,gauss.integrate(Integer.MAX_VALUE,f,-10,maxX),1.0e-7);
    Assert.assertTrue(gauss.getEvaluations() > 37000000);
    Assert.assertTrue(gauss.getIterations() < 30);
    try {
      gauss.integrate(1000,f,-10,maxX);
      Assert.fail("expected TooManyEvaluationsException");
    }
 catch (    TooManyEvaluationsException tmee) {
      Assert.assertEquals(1000,tmee.getMax());
    }
    double sum1=gauss.integrate(1000,f,-10,0);
    int eval1=gauss.getEvaluations();
    double sum2=gauss.integrate(1000,f,0,maxX);
    int eval2=gauss.getEvaluations();
    Assert.assertEquals(maxX * value,sum1 + sum2,1.0e-7);
    Assert.assertTrue(eval1 + eval2 < 200);
  }
  private double exactIntegration(  PolynomialFunction p,  double a,  double b){
    final double[] coeffs=p.getCoefficients();
    double yb=coeffs[coeffs.length - 1] / coeffs.length;
    double ya=yb;
    for (int i=coeffs.length - 2; i >= 0; --i) {
      yb=yb * b + coeffs[i] / (i + 1);
      ya=ya * a + coeffs[i] / (i + 1);
    }
    return yb * b - ya * a;
  }
}