package org.apache.commons.math3.optimization.univariate;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.StepFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * @version $Id$
 */
public final class BrentOptimizerTest {
  @Test public void testSinMin(){
    UnivariateFunction f=new Sin();
    UnivariateOptimizer optimizer=new BrentOptimizer(1e-10,1e-14);
    Assert.assertEquals(3 * Math.PI / 2,optimizer.optimize(200,f,GoalType.MINIMIZE,4,5).getPoint(),1e-8);
    Assert.assertTrue(optimizer.getEvaluations() <= 50);
    Assert.assertEquals(200,optimizer.getMaxEvaluations());
    Assert.assertEquals(3 * Math.PI / 2,optimizer.optimize(200,f,GoalType.MINIMIZE,1,5).getPoint(),1e-8);
    Assert.assertTrue(optimizer.getEvaluations() <= 100);
    Assert.assertTrue(optimizer.getEvaluations() >= 15);
    try {
      optimizer.optimize(10,f,GoalType.MINIMIZE,4,5);
      Assert.fail("an exception should have been thrown");
    }
 catch (    TooManyEvaluationsException fee) {
    }
  }
  @Test public void testSinMinWithValueChecker(){
    final UnivariateFunction f=new Sin();
    final ConvergenceChecker<UnivariatePointValuePair> checker=new SimpleUnivariateValueChecker(1e-5,1e-14);
    final UnivariateOptimizer optimizer=new BrentOptimizer(1e-10,1e-14,checker);
    final UnivariatePointValuePair result=optimizer.optimize(200,f,GoalType.MINIMIZE,4,5);
    Assert.assertEquals(3 * Math.PI / 2,result.getPoint(),1e-3);
  }
  @Test public void testBoundaries(){
    final double lower=-1.0;
    final double upper=+1.0;
    UnivariateFunction f=new UnivariateFunction(){
      public double value(      double x){
        if (x < lower) {
          throw new NumberIsTooSmallException(x,lower,true);
        }
 else         if (x > upper) {
          throw new NumberIsTooLargeException(x,upper,true);
        }
 else {
          return x;
        }
      }
    }
;
    UnivariateOptimizer optimizer=new BrentOptimizer(1e-10,1e-14);
    Assert.assertEquals(lower,optimizer.optimize(100,f,GoalType.MINIMIZE,lower,upper).getPoint(),1.0e-8);
    Assert.assertEquals(upper,optimizer.optimize(100,f,GoalType.MAXIMIZE,lower,upper).getPoint(),1.0e-8);
  }
  @Test public void testQuinticMin(){
    UnivariateFunction f=new QuinticFunction();
    UnivariateOptimizer optimizer=new BrentOptimizer(1e-10,1e-14);
    Assert.assertEquals(-0.27195613,optimizer.optimize(200,f,GoalType.MINIMIZE,-0.3,-0.2).getPoint(),1.0e-8);
    Assert.assertEquals(0.82221643,optimizer.optimize(200,f,GoalType.MINIMIZE,0.3,0.9).getPoint(),1.0e-8);
    Assert.assertTrue(optimizer.getEvaluations() <= 50);
    Assert.assertEquals(-0.27195613,optimizer.optimize(200,f,GoalType.MINIMIZE,-1.0,0.2).getPoint(),1.0e-8);
    Assert.assertTrue(optimizer.getEvaluations() <= 50);
  }
  @Test public void testQuinticMinStatistics(){
    UnivariateFunction f=new QuinticFunction();
    UnivariateOptimizer optimizer=new BrentOptimizer(1e-11,1e-14);
    final DescriptiveStatistics[] stat=new DescriptiveStatistics[2];
    for (int i=0; i < stat.length; i++) {
      stat[i]=new DescriptiveStatistics();
    }
    final double min=-0.75;
    final double max=0.25;
    final int nSamples=200;
    final double delta=(max - min) / nSamples;
    for (int i=0; i < nSamples; i++) {
      final double start=min + i * delta;
      stat[0].addValue(optimizer.optimize(40,f,GoalType.MINIMIZE,min,max,start).getPoint());
      stat[1].addValue(optimizer.getEvaluations());
    }
    final double meanOptValue=stat[0].getMean();
    final double medianEval=stat[1].getPercentile(50);
    Assert.assertTrue(meanOptValue > -0.2719561281);
    Assert.assertTrue(meanOptValue < -0.2719561280);
    Assert.assertEquals(23,(int)medianEval);
  }
  @Test public void testQuinticMax(){
    UnivariateFunction f=new QuinticFunction();
    UnivariateOptimizer optimizer=new BrentOptimizer(1e-12,1e-14);
    Assert.assertEquals(0.27195613,optimizer.optimize(100,f,GoalType.MAXIMIZE,0.2,0.3).getPoint(),1e-8);
    try {
      optimizer.optimize(5,f,GoalType.MAXIMIZE,0.2,0.3);
      Assert.fail("an exception should have been thrown");
    }
 catch (    TooManyEvaluationsException miee) {
    }
  }
  @Test public void testMinEndpoints(){
    UnivariateFunction f=new Sin();
    UnivariateOptimizer optimizer=new BrentOptimizer(1e-8,1e-14);
    double result=optimizer.optimize(50,f,GoalType.MINIMIZE,3 * Math.PI / 2,5).getPoint();
    Assert.assertEquals(3 * Math.PI / 2,result,1e-6);
    result=optimizer.optimize(50,f,GoalType.MINIMIZE,4,3 * Math.PI / 2).getPoint();
    Assert.assertEquals(3 * Math.PI / 2,result,1e-6);
  }
  @Test public void testMath832(){
    final UnivariateFunction f=new UnivariateFunction(){
      public double value(      double x){
        final double sqrtX=FastMath.sqrt(x);
        final double a=1e2 * sqrtX;
        final double b=1e6 / x;
        final double c=1e4 / sqrtX;
        return a + b + c;
      }
    }
;
    UnivariateOptimizer optimizer=new BrentOptimizer(1e-10,1e-8);
    final double result=optimizer.optimize(1483,f,GoalType.MINIMIZE,Double.MIN_VALUE,Double.MAX_VALUE).getPoint();
    Assert.assertEquals(804.9355825,result,1e-6);
  }
  /** 
 * Contrived example showing that prior to the resolution of MATH-855
 * (second revision), the algorithm would not return the best point if
 * it happened to be the initial guess.
 */
  @Test public void testKeepInitIfBest(){
    final double minSin=3 * Math.PI / 2;
    final double offset=1e-8;
    final double delta=1e-7;
    final UnivariateFunction f1=new Sin();
    final UnivariateFunction f2=new StepFunction(new double[]{minSin,minSin + offset,minSin + 2 * offset},new double[]{0,-1,0});
    final UnivariateFunction f=FunctionUtils.add(f1,f2);
    final double relTol=1e-8;
    final UnivariateOptimizer optimizer=new BrentOptimizer(relTol,1e-100);
    final double init=minSin + 1.5 * offset;
    final UnivariatePointValuePair result=optimizer.optimize(200,f,GoalType.MINIMIZE,minSin - 6.789 * delta,minSin + 9.876 * delta,init);
    final int numEval=optimizer.getEvaluations();
    final double sol=result.getPoint();
    final double expected=init;
    Assert.assertTrue("Best point not reported",f.value(sol) <= f.value(expected));
  }
  /** 
 * Contrived example showing that prior to the resolution of MATH-855,
 * the algorithm, by always returning the last evaluated point, would
 * sometimes not report the best point it had found.
 */
  @Test public void testMath855(){
    final double minSin=3 * Math.PI / 2;
    final double offset=1e-8;
    final double delta=1e-7;
    final UnivariateFunction f1=new Sin();
    final UnivariateFunction f2=new StepFunction(new double[]{minSin,minSin + offset,minSin + 5 * offset},new double[]{0,-1,0});
    final UnivariateFunction f=FunctionUtils.add(f1,f2);
    final UnivariateOptimizer optimizer=new BrentOptimizer(1e-8,1e-100);
    final UnivariatePointValuePair result=optimizer.optimize(200,f,GoalType.MINIMIZE,minSin - 6.789 * delta,minSin + 9.876 * delta);
    final int numEval=optimizer.getEvaluations();
    final double sol=result.getPoint();
    final double expected=4.712389027602411;
    Assert.assertTrue("Best point not reported",f.value(sol) <= f.value(expected));
  }
}
