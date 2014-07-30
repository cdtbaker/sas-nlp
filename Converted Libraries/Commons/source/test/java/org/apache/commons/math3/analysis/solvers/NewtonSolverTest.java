package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * @version $Id: NewtonSolverTest.java 1383441 2012-09-11 14:56:39Z luc $
 * @deprecated
 */
@Deprecated public final class NewtonSolverTest {
  /** 
 */
  @Test public void testSinZero(){
    DifferentiableUnivariateFunction f=new Sin();
    double result;
    NewtonSolver solver=new NewtonSolver();
    result=solver.solve(100,f,3,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
    result=solver.solve(100,f,1,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() > 0);
  }
  /** 
 */
  @Test public void testQuinticZero(){
    final UnivariateDifferentiableFunction q=new QuinticFunction();
    DifferentiableUnivariateFunction f=new DifferentiableUnivariateFunction(){
      public double value(      double x){
        return q.value(x);
      }
      public UnivariateFunction derivative(){
        return new UnivariateFunction(){
          public double value(          double x){
            return q.value(new DerivativeStructure(1,1,0,x)).getPartialDerivative(1);
          }
        }
;
      }
    }
;
    double result;
    NewtonSolver solver=new NewtonSolver();
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
  }
}