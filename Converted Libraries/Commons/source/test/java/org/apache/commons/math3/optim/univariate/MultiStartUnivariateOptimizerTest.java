package org.apache.commons.math3.optim.univariate;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.junit.Assert;
import org.junit.Test;
public class MultiStartUnivariateOptimizerTest {
  @Test(expected=MathIllegalStateException.class) public void testMissingMaxEval(){
    UnivariateOptimizer underlying=new BrentOptimizer(1e-10,1e-14);
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(44428400075l);
    MultiStartUnivariateOptimizer optimizer=new MultiStartUnivariateOptimizer(underlying,10,g);
    optimizer.optimize(new UnivariateObjectiveFunction(new Sin()),GoalType.MINIMIZE,new SearchInterval(-1,1));
  }
  @Test(expected=MathIllegalStateException.class) public void testMissingSearchInterval(){
    UnivariateOptimizer underlying=new BrentOptimizer(1e-10,1e-14);
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(44428400075l);
    MultiStartUnivariateOptimizer optimizer=new MultiStartUnivariateOptimizer(underlying,10,g);
    optimizer.optimize(new MaxEval(300),new UnivariateObjectiveFunction(new Sin()),GoalType.MINIMIZE);
  }
  @Test public void testSinMin(){
    UnivariateFunction f=new Sin();
    UnivariateOptimizer underlying=new BrentOptimizer(1e-10,1e-14);
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(44428400075l);
    MultiStartUnivariateOptimizer optimizer=new MultiStartUnivariateOptimizer(underlying,10,g);
    optimizer.optimize(new MaxEval(300),new UnivariateObjectiveFunction(f),GoalType.MINIMIZE,new SearchInterval(-100.0,100.0));
    UnivariatePointValuePair[] optima=optimizer.getOptima();
    for (int i=1; i < optima.length; ++i) {
      double d=(optima[i].getPoint() - optima[i - 1].getPoint()) / (2 * FastMath.PI);
      Assert.assertTrue(FastMath.abs(d - FastMath.rint(d)) < 1.0e-8);
      Assert.assertEquals(-1.0,f.value(optima[i].getPoint()),1.0e-10);
      Assert.assertEquals(f.value(optima[i].getPoint()),optima[i].getValue(),1.0e-10);
    }
    Assert.assertTrue(optimizer.getEvaluations() > 200);
    Assert.assertTrue(optimizer.getEvaluations() < 300);
  }
  @Test public void testQuinticMin(){
    UnivariateFunction f=new QuinticFunction();
    UnivariateOptimizer underlying=new BrentOptimizer(1e-9,1e-14);
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(4312000053L);
    MultiStartUnivariateOptimizer optimizer=new MultiStartUnivariateOptimizer(underlying,5,g);
    UnivariatePointValuePair optimum=optimizer.optimize(new MaxEval(300),new UnivariateObjectiveFunction(f),GoalType.MINIMIZE,new SearchInterval(-0.3,-0.2));
    Assert.assertEquals(-0.27195613,optimum.getPoint(),1e-9);
    Assert.assertEquals(-0.0443342695,optimum.getValue(),1e-9);
    UnivariatePointValuePair[] optima=optimizer.getOptima();
    for (int i=0; i < optima.length; ++i) {
      Assert.assertEquals(f.value(optima[i].getPoint()),optima[i].getValue(),1e-9);
    }
    Assert.assertTrue(optimizer.getEvaluations() >= 50);
    Assert.assertTrue(optimizer.getEvaluations() <= 100);
  }
  @Test public void testBadFunction(){
    UnivariateFunction f=new UnivariateFunction(){
      public double value(      double x){
        if (x < 0) {
          throw new LocalException();
        }
        return 0;
      }
    }
;
    UnivariateOptimizer underlying=new BrentOptimizer(1e-9,1e-14);
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(4312000053L);
    MultiStartUnivariateOptimizer optimizer=new MultiStartUnivariateOptimizer(underlying,5,g);
    try {
      optimizer.optimize(new MaxEval(300),new UnivariateObjectiveFunction(f),GoalType.MINIMIZE,new SearchInterval(-0.3,-0.2));
      Assert.fail();
    }
 catch (    LocalException e) {
    }
    Assert.assertTrue(optimizer.getOptima()[0] == null);
  }
private static class LocalException extends RuntimeException {
    private static final long serialVersionUID=1194682757034350629L;
  }
}
