package org.apache.commons.math3.optimization;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
import org.junit.Assert;
import org.junit.Test;
public class MultivariateMultiStartOptimizerTest {
  @Test public void testRosenbrock(){
    Rosenbrock rosenbrock=new Rosenbrock();
    SimplexOptimizer underlying=new SimplexOptimizer(new SimpleValueChecker(-1,1.0e-3));
    NelderMeadSimplex simplex=new NelderMeadSimplex(new double[][]{{-1.2,1.0},{0.9,1.2},{3.5,-2.3}});
    underlying.setSimplex(simplex);
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(16069223052l);
    RandomVectorGenerator generator=new UncorrelatedRandomVectorGenerator(2,new GaussianRandomGenerator(g));
    MultivariateMultiStartOptimizer optimizer=new MultivariateMultiStartOptimizer(underlying,10,generator);
    PointValuePair optimum=optimizer.optimize(1100,rosenbrock,GoalType.MINIMIZE,new double[]{-1.2,1.0});
    Assert.assertEquals(rosenbrock.getCount(),optimizer.getEvaluations());
    Assert.assertTrue(optimizer.getEvaluations() > 900);
    Assert.assertTrue(optimizer.getEvaluations() < 1200);
    Assert.assertTrue(optimum.getValue() < 8.0e-4);
  }
private static class Rosenbrock implements MultivariateFunction {
    private int count;
    public Rosenbrock(){
      count=0;
    }
    public double value(    double[] x){
      ++count;
      double a=x[1] - x[0] * x[0];
      double b=1.0 - x[0];
      return 100 * a * a + b * b;
    }
    public int getCount(){
      return count;
    }
  }
}