package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * @version $Id: BisectionSolverTest.java 1374632 2012-08-18 18:11:11Z luc $
 */
public final class BisectionSolverTest {
  @Test public void testSinZero(){
    UnivariateFunction f=new Sin();
    double result;
    BisectionSolver solver=new BisectionSolver();
    result=solver.solve(100,f,3,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,1,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
  }
  @Test public void testQuinticZero(){
    UnivariateFunction f=new QuinticFunction();
    double result;
    BisectionSolver solver=new BisectionSolver();
    result=solver.solve(100,f,-0.2,0.2);
    Assert.assertEquals(result,0,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,-0.1,0.3);
    Assert.assertEquals(result,0,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,-0.3,0.45);
    Assert.assertEquals(result,0,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.3,0.7);
    Assert.assertEquals(result,0.5,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.2,0.6);
    Assert.assertEquals(result,0.5,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.05,0.95);
    Assert.assertEquals(result,0.5,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.85,1.25);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.8,1.2);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.85,1.75);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.55,1.45);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,0.85,5);
    Assert.assertEquals(result,1.0,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() > 0);
  }
  @Test public void testMath369(){
    UnivariateFunction f=new Sin();
    BisectionSolver solver=new BisectionSolver();
    Assert.assertEquals(FastMath.PI,solver.solve(100,f,3.0,3.2,3.1),solver.getAbsoluteAccuracy());
  }
}
