package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.XMinus5Function;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Base class for root-finding algorithms tests derived from{@link BaseSecantSolver}.
 * @version $Id$
 */
public abstract class BaseSecantSolverAbstractTest {
  /** 
 * Returns the solver to use to perform the tests.
 * @return the solver to use to perform the tests
 */
  protected abstract UnivariateSolver getSolver();
  /** 
 * Returns the expected number of evaluations for the{@link #testQuinticZero} unit test. A value of {@code -1} indicates that
 * the test should be skipped for that solver.
 * @return the expected number of evaluations for the{@link #testQuinticZero} unit test
 */
  protected abstract int[] getQuinticEvalCounts();
  @Test public void testSinZero(){
    UnivariateFunction f=new Sin();
    double result;
    UnivariateSolver solver=getSolver();
    result=solver.solve(100,f,3,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 6);
    result=solver.solve(100,f,1,4);
    Assert.assertEquals(result,FastMath.PI,solver.getAbsoluteAccuracy());
    Assert.assertTrue(solver.getEvaluations() <= 7);
  }
  @Test public void testQuinticZero(){
    UnivariateFunction f=new QuinticFunction();
    double result;
    UnivariateSolver solver=getSolver();
    double atol=solver.getAbsoluteAccuracy();
    int[] counts=getQuinticEvalCounts();
    double[][] testsData={{-0.2,0.2,0.0},{-0.1,0.3,0.0},{-0.3,0.45,0.0},{0.3,0.7,0.5},{0.2,0.6,0.5},{0.05,0.95,0.5},{0.85,1.25,1.0},{0.8,1.2,1.0},{0.85,1.75,1.0},{0.55,1.45,1.0},{0.85,5.0,1.0}};
    int maxIter=500;
    for (int i=0; i < testsData.length; i++) {
      if (counts[i] == -1)       continue;
      double[] testData=testsData[i];
      result=solver.solve(maxIter,f,testData[0],testData[1]);
      Assert.assertEquals(result,testData[2],atol);
      Assert.assertTrue(solver.getEvaluations() <= counts[i] + 1);
    }
  }
  @Test public void testRootEndpoints(){
    UnivariateFunction f=new XMinus5Function();
    UnivariateSolver solver=getSolver();
    double result=solver.solve(100,f,5.0,6.0);
    Assert.assertEquals(5.0,result,0.0);
    result=solver.solve(100,f,4.0,5.0);
    Assert.assertEquals(5.0,result,0.0);
    result=solver.solve(100,f,5.0,6.0,5.5);
    Assert.assertEquals(5.0,result,0.0);
    result=solver.solve(100,f,4.0,5.0,4.5);
    Assert.assertEquals(5.0,result,0.0);
  }
  @Test public void testBadEndpoints(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=getSolver();
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
  @Test public void testSolutionLeftSide(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=getSolver();
    double left=-1.5;
    double right=0.05;
    for (int i=0; i < 10; i++) {
      double solution=getSolution(solver,100,f,left,right,AllowedSolution.LEFT_SIDE);
      if (!Double.isNaN(solution)) {
        Assert.assertTrue(solution <= 0.0);
      }
      left-=0.1;
      right+=0.3;
    }
  }
  @Test public void testSolutionRightSide(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=getSolver();
    double left=-1.5;
    double right=0.05;
    for (int i=0; i < 10; i++) {
      double solution=getSolution(solver,100,f,left,right,AllowedSolution.RIGHT_SIDE);
      if (!Double.isNaN(solution)) {
        Assert.assertTrue(solution >= 0.0);
      }
      left-=0.1;
      right+=0.3;
    }
  }
  @Test public void testSolutionBelowSide(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=getSolver();
    double left=-1.5;
    double right=0.05;
    for (int i=0; i < 10; i++) {
      double solution=getSolution(solver,100,f,left,right,AllowedSolution.BELOW_SIDE);
      if (!Double.isNaN(solution)) {
        Assert.assertTrue(f.value(solution) <= 0.0);
      }
      left-=0.1;
      right+=0.3;
    }
  }
  @Test public void testSolutionAboveSide(){
    UnivariateFunction f=new Sin();
    UnivariateSolver solver=getSolver();
    double left=-1.5;
    double right=0.05;
    for (int i=0; i < 10; i++) {
      double solution=getSolution(solver,100,f,left,right,AllowedSolution.ABOVE_SIDE);
      if (!Double.isNaN(solution)) {
        Assert.assertTrue(f.value(solution) >= 0.0);
      }
      left-=0.1;
      right+=0.3;
    }
  }
  private double getSolution(  UnivariateSolver solver,  int maxEval,  UnivariateFunction f,  double left,  double right,  AllowedSolution allowedSolution){
    try {
      @SuppressWarnings("unchecked") BracketedUnivariateSolver<UnivariateFunction> bracketing=(BracketedUnivariateSolver<UnivariateFunction>)solver;
      return bracketing.solve(100,f,left,right,allowedSolution);
    }
 catch (    ClassCastException cce) {
      double baseRoot=solver.solve(maxEval,f,left,right);
      if ((baseRoot <= left) || (baseRoot >= right)) {
        return Double.NaN;
      }
      PegasusSolver bracketing=new PegasusSolver(solver.getRelativeAccuracy(),solver.getAbsoluteAccuracy(),solver.getFunctionValueAccuracy());
      return UnivariateSolverUtils.forceSide(maxEval - solver.getEvaluations(),f,bracketing,baseRoot,left,right,allowedSolution);
    }
  }
}
