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
 * Test case for {@link RiddersSolver Ridders} solver.
 * <p>
 * Ridders' method converges superlinearly, more specific, its rate of
 * convergence is sqrt(2). Test runs show that for a default absolute
 * accuracy of 1E-6, it generally takes less than 5 iterations for close
 * initial bracket and 5 to 10 iterations for distant initial bracket
 * to converge.
 * @version $Id: RiddersSolverTest.java 1374632 2012-08-18 18:11:11Z luc $
 */
public final class RiddersSolverTest {
  /** 
 * Test of solver for the sine function.
 */
  @Test public void testSinFunction(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=new RiddersSolver();
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
    UnivariateSolver solver=new RiddersSolver();
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
 */
  @Test public void testExpm1Function(){
    UnivariateFunction f=new Expm1();
    UnivariateSolver solver=new RiddersSolver();
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
    UnivariateSolver solver=new RiddersSolver();
    try {
      solver.solve(100,f,1,-1);
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
