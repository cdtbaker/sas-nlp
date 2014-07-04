package org.apache.commons.math3.analysis.integration;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test case for Simpson integrator.
 * <p>
 * Test runs show that for a default relative accuracy of 1E-6, it
 * generally takes 5 to 10 iterations for the integral to converge.
 * @version $Id: SimpsonIntegratorTest.java 1374632 2012-08-18 18:11:11Z luc $
 */
public final class SimpsonIntegratorTest {
  /** 
 * Test of integrator for the sine function.
 */
  @Test public void testSinFunction(){
    UnivariateFunction f=new Sin();
    UnivariateIntegrator integrator=new SimpsonIntegrator();
    double min, max, expected, result, tolerance;
    min=0;
    max=FastMath.PI;
    expected=2;
    tolerance=FastMath.abs(expected * integrator.getRelativeAccuracy());
    result=integrator.integrate(1000,f,min,max);
    Assert.assertTrue(integrator.getEvaluations() < 100);
    Assert.assertTrue(integrator.getIterations() < 10);
    Assert.assertEquals(expected,result,tolerance);
    min=-FastMath.PI / 3;
    max=0;
    expected=-0.5;
    tolerance=FastMath.abs(expected * integrator.getRelativeAccuracy());
    result=integrator.integrate(1000,f,min,max);
    Assert.assertTrue(integrator.getEvaluations() < 50);
    Assert.assertTrue(integrator.getIterations() < 10);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of integrator for the quintic function.
 */
  @Test public void testQuinticFunction(){
    UnivariateFunction f=new QuinticFunction();
    UnivariateIntegrator integrator=new SimpsonIntegrator();
    double min, max, expected, result, tolerance;
    min=0;
    max=1;
    expected=-1.0 / 48;
    tolerance=FastMath.abs(expected * integrator.getRelativeAccuracy());
    result=integrator.integrate(1000,f,min,max);
    Assert.assertTrue(integrator.getEvaluations() < 150);
    Assert.assertTrue(integrator.getIterations() < 10);
    Assert.assertEquals(expected,result,tolerance);
    min=0;
    max=0.5;
    expected=11.0 / 768;
    tolerance=FastMath.abs(expected * integrator.getRelativeAccuracy());
    result=integrator.integrate(1000,f,min,max);
    Assert.assertTrue(integrator.getEvaluations() < 100);
    Assert.assertTrue(integrator.getIterations() < 10);
    Assert.assertEquals(expected,result,tolerance);
    min=-1;
    max=4;
    expected=2048 / 3.0 - 78 + 1.0 / 48;
    tolerance=FastMath.abs(expected * integrator.getRelativeAccuracy());
    result=integrator.integrate(1000,f,min,max);
    Assert.assertTrue(integrator.getEvaluations() < 150);
    Assert.assertTrue(integrator.getIterations() < 10);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of parameters for the integrator.
 */
  @Test public void testParameters(){
    UnivariateFunction f=new Sin();
    try {
      new SimpsonIntegrator().integrate(1000,f,1,-1);
      Assert.fail("Expecting NumberIsTooLargeException - bad interval");
    }
 catch (    NumberIsTooLargeException ex) {
    }
    try {
      new SimpsonIntegrator(5,4);
      Assert.fail("Expecting NumberIsTooSmallException - bad iteration limits");
    }
 catch (    NumberIsTooSmallException ex) {
    }
    try {
      new SimpsonIntegrator(10,99);
      Assert.fail("Expecting NumberIsTooLargeException - bad iteration limits");
    }
 catch (    NumberIsTooLargeException ex) {
    }
  }
}
