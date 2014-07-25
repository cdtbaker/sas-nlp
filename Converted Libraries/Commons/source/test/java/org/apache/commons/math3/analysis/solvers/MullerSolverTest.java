package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Expm1;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test case for {@link MullerSolver Muller} solver.
 * <p>
 * Muller's method converges almost quadratically near roots, but it can
 * be very slow in regions far away from zeros. Test runs show that for
 * reasonably good initial values, for a default absolute accuracy of 1E-6,
 * it generally takes 5 to 10 iterations for the solver to converge.
 * <p>
 * Tests for the exponential function illustrate the situations where
 * Muller solver performs poorly.
 * @version $Id: MullerSolverTest.java 1374632 2012-08-18 18:11:11Z luc $
 */
public final class MullerSolverTest {
  /** 
 * Test of solver for the sine function.
 */
  @Test public void testSinFunction(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=new MullerSolver();
    double min, max, expected, result, tolerance;
    min=3.0;
    max=4.0;
    expected=FastMath.PI;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=-1.0;
    max=1.5;
    expected=0.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of solver for the quintic function.
 */
  @Test public void testQuinticFunction(){
    UnivariateFunction f=new QuinticFunction();
    UnivariateSolver solver=new MullerSolver();
    double min, max, expected, result, tolerance;
    min=-0.4;
    max=0.2;
    expected=0.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=0.75;
    max=1.5;
    expected=1.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=-0.9;
    max=-0.2;
    expected=-0.5;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of solver for the exponential function.
 * <p>
 * It takes 10 to 15 iterations for the last two tests to converge.
 * In fact, if not for the bisection alternative, the solver would
 * exceed the default maximal iteration of 100.
 */
  @Test public void testExpm1Function(){
    UnivariateFunction f=new Expm1();
    UnivariateSolver solver=new MullerSolver();
    double min, max, expected, result, tolerance;
    min=-1.0;
    max=2.0;
    expected=0.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=-20.0;
    max=10.0;
    expected=0.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=-50.0;
    max=100.0;
    expected=0.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of parameters for the solver.
 */
  @Test public void testParameters(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=new MullerSolver();
    try {
      double root=solver.solve(100,f,1,-1);
      System.out.println("root=" + root);
      Assert.fail("Expecting NumberIsTooLargeException - bad interval");
    }
 catch (    NumberIsTooLargeException ex) {
    }
    try {
      solver.solve(100,f,2,3);
      Assert.fail("Expecting NoBracketingException - no bracketing");
    }
 catch (    NoBracketingException ex) {
    }
  }
}
