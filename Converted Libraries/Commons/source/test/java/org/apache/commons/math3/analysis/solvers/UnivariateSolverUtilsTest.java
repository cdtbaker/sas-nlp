package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * @version $Id: UnivariateSolverUtilsTest.java 1374632 2012-08-18 18:11:11Z luc $
 */
public class UnivariateSolverUtilsTest {
  protected UnivariateFunction sin=new Sin();
  @Test(expected=MathIllegalArgumentException.class) public void testSolveNull(){
    UnivariateSolverUtils.solve(null,0.0,4.0);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testSolveBadEndpoints(){
    double root=UnivariateSolverUtils.solve(sin,4.0,-0.1,1e-6);
    System.out.println("root=" + root);
  }
  @Test public void testSolveBadAccuracy(){
    try {
      UnivariateSolverUtils.solve(sin,0.0,4.0,0.0);
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test public void testSolveSin(){
    double x=UnivariateSolverUtils.solve(sin,1.0,4.0);
    Assert.assertEquals(FastMath.PI,x,1.0e-4);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testSolveAccuracyNull(){
    double accuracy=1.0e-6;
    UnivariateSolverUtils.solve(null,0.0,4.0,accuracy);
  }
  @Test public void testSolveAccuracySin(){
    double accuracy=1.0e-6;
    double x=UnivariateSolverUtils.solve(sin,1.0,4.0,accuracy);
    Assert.assertEquals(FastMath.PI,x,accuracy);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testSolveNoRoot(){
    UnivariateSolverUtils.solve(sin,1.0,1.5);
  }
  @Test public void testBracketSin(){
    double[] result=UnivariateSolverUtils.bracket(sin,0.0,-2.0,2.0);
    Assert.assertTrue(sin.value(result[0]) < 0);
    Assert.assertTrue(sin.value(result[1]) > 0);
  }
  @Test public void testBracketEndpointRoot(){
    double[] result=UnivariateSolverUtils.bracket(sin,1.5,0,2.0);
    Assert.assertEquals(0.0,sin.value(result[0]),1.0e-15);
    Assert.assertTrue(sin.value(result[1]) > 0);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testNullFunction(){
    UnivariateSolverUtils.bracket(null,1.5,0,2.0);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testBadInitial(){
    UnivariateSolverUtils.bracket(sin,2.5,0,2.0);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testBadEndpoints(){
    UnivariateSolverUtils.bracket(sin,1.5,2.0,1.0);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testBadMaximumIterations(){
    UnivariateSolverUtils.bracket(sin,1.5,0,2.0,0);
  }
  @Test public void testMisc(){
    UnivariateFunction f=new QuinticFunction();
    double result;
    result=UnivariateSolverUtils.solve(f,-0.2,0.2);
    Assert.assertEquals(result,0,1E-8);
    result=UnivariateSolverUtils.solve(f,-0.1,0.3);
    Assert.assertEquals(result,0,1E-8);
    result=UnivariateSolverUtils.solve(f,-0.3,0.45);
    Assert.assertEquals(result,0,1E-6);
    result=UnivariateSolverUtils.solve(f,0.3,0.7);
    Assert.assertEquals(result,0.5,1E-6);
    result=UnivariateSolverUtils.solve(f,0.2,0.6);
    Assert.assertEquals(result,0.5,1E-6);
    result=UnivariateSolverUtils.solve(f,0.05,0.95);
    Assert.assertEquals(result,0.5,1E-6);
    result=UnivariateSolverUtils.solve(f,0.85,1.25);
    Assert.assertEquals(result,1.0,1E-6);
    result=UnivariateSolverUtils.solve(f,0.8,1.2);
    Assert.assertEquals(result,1.0,1E-6);
    result=UnivariateSolverUtils.solve(f,0.85,1.75);
    Assert.assertEquals(result,1.0,1E-6);
    result=UnivariateSolverUtils.solve(f,0.55,1.45);
    Assert.assertEquals(result,1.0,1E-6);
    result=UnivariateSolverUtils.solve(f,0.85,5);
    Assert.assertEquals(result,1.0,1E-6);
  }
}
