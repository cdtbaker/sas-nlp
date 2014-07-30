package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test case for Laguerre solver.
 * <p>
 * Laguerre's method is very efficient in solving polynomials. Test runs
 * show that for a default absolute accuracy of 1E-6, it generally takes
 * less than 5 iterations to find one root, provided solveAll() is not
 * invoked, and 15 to 20 iterations to find all roots for quintic function.
 * @version $Id: LaguerreSolverTest.java 1361793 2012-07-15 20:50:13Z erans $
 */
public final class LaguerreSolverTest {
  /** 
 * Test of solver for the linear function.
 */
  @Test public void testLinearFunction(){
    double min, max, expected, result, tolerance;
    double coefficients[]={-1.0,4.0};
    PolynomialFunction f=new PolynomialFunction(coefficients);
    LaguerreSolver solver=new LaguerreSolver();
    min=0.0;
    max=1.0;
    expected=0.25;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of solver for the quadratic function.
 */
  @Test public void testQuadraticFunction(){
    double min, max, expected, result, tolerance;
    double coefficients[]={-3.0,5.0,2.0};
    PolynomialFunction f=new PolynomialFunction(coefficients);
    LaguerreSolver solver=new LaguerreSolver();
    min=0.0;
    max=2.0;
    expected=0.5;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=-4.0;
    max=-1.0;
    expected=-3.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of solver for the quintic function.
 */
  @Test public void testQuinticFunction(){
    double min, max, expected, result, tolerance;
    double coefficients[]={-12.0,-1.0,1.0,-12.0,-1.0,1.0};
    PolynomialFunction f=new PolynomialFunction(coefficients);
    LaguerreSolver solver=new LaguerreSolver();
    min=-2.0;
    max=2.0;
    expected=-1.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=-5.0;
    max=-2.5;
    expected=-3.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
    min=3.0;
    max=6.0;
    expected=4.0;
    tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected * solver.getRelativeAccuracy()));
    result=solver.solve(100,f,min,max);
    Assert.assertEquals(expected,result,tolerance);
  }
  /** 
 * Test of solver for the quintic function using{@link LaguerreSolver#solveAllComplex(double[],double) solveAllComplex}.
 */
  @Test public void testQuinticFunction2(){
    final double[] coefficients={4.0,0.0,1.0,4.0,0.0,1.0};
    final LaguerreSolver solver=new LaguerreSolver();
    final Complex[] result=solver.solveAllComplex(coefficients,0);
    for (    Complex expected : new Complex[]{new Complex(0,-2),new Complex(0,2),new Complex(0.5,0.5 * FastMath.sqrt(3)),new Complex(-1,0),new Complex(0.5,-0.5 * FastMath.sqrt(3.0))}) {
      final double tolerance=FastMath.max(solver.getAbsoluteAccuracy(),FastMath.abs(expected.abs() * solver.getRelativeAccuracy()));
      TestUtils.assertContains(result,expected,tolerance);
    }
  }
  /** 
 * Test of parameters for the solver.
 */
  @Test public void testParameters(){
    double coefficients[]={-3.0,5.0,2.0};
    PolynomialFunction f=new PolynomialFunction(coefficients);
    LaguerreSolver solver=new LaguerreSolver();
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