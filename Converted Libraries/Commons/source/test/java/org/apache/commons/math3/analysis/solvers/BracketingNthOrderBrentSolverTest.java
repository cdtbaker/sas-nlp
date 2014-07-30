package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test case for {@link BracketingNthOrderBrentSolver bracketing n<sup>th</sup> order Brent} solver.
 * @version $Id: BracketingNthOrderBrentSolverTest.java 1383441 2012-09-11 14:56:39Z luc $
 */
public final class BracketingNthOrderBrentSolverTest extends BaseSecantSolverAbstractTest {
  /** 
 * {@inheritDoc} 
 */
  @Override protected UnivariateSolver getSolver(){
    return new BracketingNthOrderBrentSolver();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected int[] getQuinticEvalCounts(){
    return new int[]{1,3,8,1,9,4,8,1,12,1,16};
  }
  @Test(expected=NumberIsTooSmallException.class) public void testInsufficientOrder1(){
    new BracketingNthOrderBrentSolver(1.0e-10,1);
  }
  @Test(expected=NumberIsTooSmallException.class) public void testInsufficientOrder2(){
    new BracketingNthOrderBrentSolver(1.0e-10,1.0e-10,1);
  }
  @Test(expected=NumberIsTooSmallException.class) public void testInsufficientOrder3(){
    new BracketingNthOrderBrentSolver(1.0e-10,1.0e-10,1.0e-10,1);
  }
  @Test public void testConstructorsOK(){
    Assert.assertEquals(2,new BracketingNthOrderBrentSolver(1.0e-10,2).getMaximalOrder());
    Assert.assertEquals(2,new BracketingNthOrderBrentSolver(1.0e-10,1.0e-10,2).getMaximalOrder());
    Assert.assertEquals(2,new BracketingNthOrderBrentSolver(1.0e-10,1.0e-10,1.0e-10,2).getMaximalOrder());
  }
  @Test public void testConvergenceOnFunctionAccuracy(){
    BracketingNthOrderBrentSolver solver=new BracketingNthOrderBrentSolver(1.0e-12,1.0e-10,0.001,3);
    QuinticFunction f=new QuinticFunction();
    double result=solver.solve(20,f,0.2,0.9,0.4,AllowedSolution.BELOW_SIDE);
    Assert.assertEquals(0,f.value(result),solver.getFunctionValueAccuracy());
    Assert.assertTrue(f.value(result) <= 0);
    Assert.assertTrue(result - 0.5 > solver.getAbsoluteAccuracy());
    result=solver.solve(20,f,-0.9,-0.2,-0.4,AllowedSolution.ABOVE_SIDE);
    Assert.assertEquals(0,f.value(result),solver.getFunctionValueAccuracy());
    Assert.assertTrue(f.value(result) >= 0);
    Assert.assertTrue(result + 0.5 < -solver.getAbsoluteAccuracy());
  }
  @Test public void testIssue716(){
    BracketingNthOrderBrentSolver solver=new BracketingNthOrderBrentSolver(1.0e-12,1.0e-10,1.0e-22,5);
    UnivariateFunction sharpTurn=new UnivariateFunction(){
      public double value(      double x){
        return (2 * x + 1) / (1.0e9 * (x + 1));
      }
    }
;
    double result=solver.solve(100,sharpTurn,-0.9999999,30,15,AllowedSolution.RIGHT_SIDE);
    Assert.assertEquals(0,sharpTurn.value(result),solver.getFunctionValueAccuracy());
    Assert.assertTrue(sharpTurn.value(result) >= 0);
    Assert.assertEquals(-0.5,result,1.0e-10);
  }
  @Test public void testFasterThanNewton(){
    compare(new TestFunction(0.0,-2,2){
      @Override public DerivativeStructure value(      DerivativeStructure x){
        return x.sin().subtract(x.multiply(0.5));
      }
    }
);
    compare(new TestFunction(6.3087771299726890947,-5,10){
      @Override public DerivativeStructure value(      DerivativeStructure x){
        return x.pow(5).add(x).subtract(10000);
      }
    }
);
    compare(new TestFunction(9.6335955628326951924,0.001,10){
      @Override public DerivativeStructure value(      DerivativeStructure x){
        return x.sqrt().subtract(x.reciprocal()).subtract(3);
      }
    }
);
    compare(new TestFunction(2.8424389537844470678,-5,5){
      @Override public DerivativeStructure value(      DerivativeStructure x){
        return x.exp().add(x).subtract(20);
      }
    }
);
    compare(new TestFunction(8.3094326942315717953,0.001,10){
      @Override public DerivativeStructure value(      DerivativeStructure x){
        return x.log().add(x.sqrt()).subtract(5);
      }
    }
);
    compare(new TestFunction(1.4655712318767680266,-0.5,1.5){
      @Override public DerivativeStructure value(      DerivativeStructure x){
        return x.subtract(1).multiply(x).multiply(x).subtract(1);
      }
    }
);
  }
  private void compare(  TestFunction f){
    compare(f,f.getRoot(),f.getMin(),f.getMax());
  }
  private void compare(  final UnivariateDifferentiableFunction f,  double root,  double min,  double max){
    NewtonRaphsonSolver newton=new NewtonRaphsonSolver(1.0e-12);
    BracketingNthOrderBrentSolver bracketing=new BracketingNthOrderBrentSolver(1.0e-12,1.0e-12,1.0e-18,5);
    double resultN;
    try {
      resultN=newton.solve(100,f,min,max);
    }
 catch (    TooManyEvaluationsException tmee) {
      resultN=Double.NaN;
    }
    double resultB;
    try {
      resultB=bracketing.solve(100,f,min,max);
    }
 catch (    TooManyEvaluationsException tmee) {
      resultB=Double.NaN;
    }
    Assert.assertEquals(root,resultN,newton.getAbsoluteAccuracy());
    Assert.assertEquals(root,resultB,bracketing.getAbsoluteAccuracy());
    final int weightedBracketingEvaluations=bracketing.getEvaluations();
    final int weightedNewtonEvaluations=2 * newton.getEvaluations();
    Assert.assertTrue(weightedBracketingEvaluations < weightedNewtonEvaluations);
  }
private static abstract class TestFunction implements UnivariateDifferentiableFunction {
    private final double root;
    private final double min;
    private final double max;
    protected TestFunction(    final double root,    final double min,    final double max){
      this.root=root;
      this.min=min;
      this.max=max;
    }
    public double getRoot(){
      return root;
    }
    public double getMin(){
      return min;
    }
    public double getMax(){
      return max;
    }
    public double value(    final double x){
      return value(new DerivativeStructure(0,0,x)).getValue();
    }
    public abstract DerivativeStructure value(    final DerivativeStructure t);
  }
}