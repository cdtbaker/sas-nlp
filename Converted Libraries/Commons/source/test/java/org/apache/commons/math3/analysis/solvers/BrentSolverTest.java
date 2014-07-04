package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.MonitoredFunction;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test case for {@link BrentSolver Brent} solver.
 * Because Brent-Dekker is guaranteed to converge in less than the default
 * maximum iteration count due to bisection fallback, it is quite hard to
 * debug. I include measured iteration counts plus one in order to detect
 * regressions. On average Brent-Dekker should use 4..5 iterations for the
 * default absolute accuracy of 10E-8 for sinus and the quintic function around
 * zero, and 5..10 iterations for the other zeros.
 * @version $Id: BrentSolverTest.java 1383845 2012-09-12 08:34:10Z luc $
 */
public final class BrentSolverTest {
  @Test public void testSinZero(){
    UnivariateFunction f=new Sin();
    double result;
    UnivariateSolver solver=new BrentSolver();
    result=solver.solve(100,f,3,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 7);
    result=solver.solve(100,f,1,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 8);
  }
  @Test public void testQuinticZero(){
    UnivariateFunction f=new QuinticFunction();
    double result;
    UnivariateSolver solver=new BrentSolver();
    result=solver.solve(100,f,-0.2,0.2);
    Assert.assertEquals(result,0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 3);
    result=solver.solve(100,f,-0.1,0.3);
    Assert.assertEquals(result,0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 7);
    result=solver.solve(100,f,-0.3,0.45);
    Assert.assertEquals(result,0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 8);
    result=solver.solve(100,f,0.3,0.7);
    Assert.assertEquals(result,0.5,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 9);
    result=solver.solve(100,f,0.2,0.6);
    Assert.assertEquals(result,0.5,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 10);
    result=solver.solve(100,f,0.05,0.95);
    Assert.assertEquals(result,0.5,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 11);
    result=solver.solve(100,f,0.85,1.25);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 11);
    result=solver.solve(100,f,0.8,1.2);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 11);
    result=solver.solve(100,f,0.85,1.75);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 13);
    result=solver.solve(100,f,0.55,1.45);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 10);
    result=solver.solve(100,f,0.85,5);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 15);
    try {
      result=solver.solve(5,f,0.85,5);
      Assert.fail("Expected TooManyEvaluationsException");
    }
 catch (    TooManyEvaluationsException e) {
    }
  }
  @Test public void testRootEndpoints(){
    UnivariateFunction f=new Sin();
    BrentSolver solver=new BrentSolver();
    double result=solver.solve(100,f,FastMath.PI,4);
    Assert.assertEquals(FastMath.PI,result,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,3,FastMath.PI);
    Assert.assertEquals(FastMath.PI,result,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,FastMath.PI,4,3.5);
    Assert.assertEquals(FastMath.PI,result,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,3,FastMath.PI,3.07);
    Assert.assertEquals(FastMath.PI,result,solver.getAbsoluteAccuracy());
  }
  @Test public void testBadEndpoints(){
    UnivariateFunction f=new Sin();
    BrentSolver solver=new BrentSolver();
    try {
      solver.solve(100,f,1,-1);
      Assert.fail("Expecting NumberIsTooLargeException - bad interval");
    }
 catch (    NumberIsTooLargeException ex) {
    }
    try {
      solver.solve(100,f,1,1.5);
      Assert.fail("Expecting NoBracketingException - non-bracketing");
    }
 catch (    NoBracketingException ex) {
    }
    try {
      solver.solve(100,f,1,1.5,1.2);
      Assert.fail("Expecting NoBracketingException - non-bracketing");
    }
 catch (    NoBracketingException ex) {
    }
  }
  @Test public void testInitialGuess(){
    MonitoredFunction f=new MonitoredFunction(new QuinticFunction());
    BrentSolver solver=new BrentSolver();
    double result;
    result=solver.solve(100,f,0.6,7.0);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    int referenceCallsCount=f.getCallsCount();
    Assert.assertTrue(referenceCallsCount >= 13);
    try {
      result=solver.solve(100,f,0.6,7.0,0.0);
      Assert.fail("a NumberIsTooLargeException was expected");
    }
 catch (    NumberIsTooLargeException iae) {
    }
    f.setCallsCount(0);
    result=solver.solve(100,f,0.6,7.0,0.61);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(f.getCallsCount() > referenceCallsCount);
    f.setCallsCount(0);
    result=solver.solve(100,f,0.6,7.0,0.999999);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(f.getCallsCount() < referenceCallsCount);
    f.setCallsCount(0);
    result=solver.solve(100,f,0.6,7.0,1.0);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertEquals(1,solver.getEvaluations());
    Assert.assertEquals(1,f.getCallsCount());
  }
  @Test public void testMath832(){
    final UnivariateFunction f=new UnivariateFunction(){
      private final UnivariateDifferentiableFunction sqrt=new Sqrt();
      private final UnivariateDifferentiableFunction inv=new Inverse();
      private final UnivariateDifferentiableFunction func=FunctionUtils.add(FunctionUtils.multiply(new Constant(1e2),sqrt),FunctionUtils.multiply(new Constant(1e6),inv),FunctionUtils.multiply(new Constant(1e4),FunctionUtils.compose(inv,sqrt)));
      public double value(      double x){
        return func.value(new DerivativeStructure(1,1,0,x)).getPartialDerivative(1);
      }
    }
;
    BrentSolver solver=new BrentSolver();
    final double result=solver.solve(99,f,1,1e30,1 + 1e-10);
    Assert.assertEquals(804.93558250,result,1e-8);
  }
}
