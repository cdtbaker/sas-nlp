package org.apache.commons.math3.optim.nonlinear.scalar;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.CircleScalar;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
import org.junit.Assert;
import org.junit.Test;
public class MultiStartMultivariateOptimizerTest {
  @Test public void testCircleFitting(){
    CircleScalar circle=new CircleScalar();
    circle.addPoint(30.0,68.0);
    circle.addPoint(50.0,-6.0);
    circle.addPoint(110.0,-20.0);
    circle.addPoint(35.0,15.0);
    circle.addPoint(45.0,97.0);
    GradientMultivariateOptimizer underlying=new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,new SimpleValueChecker(1e-10,1e-10));
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(753289573253l);
    RandomVectorGenerator generator=new UncorrelatedRandomVectorGenerator(new double[]{50,50},new double[]{10,10},new GaussianRandomGenerator(g));
    MultiStartMultivariateOptimizer optimizer=new MultiStartMultivariateOptimizer(underlying,10,generator);
    PointValuePair optimum=optimizer.optimize(new MaxEval(200),circle.getObjectiveFunction(),circle.getObjectiveFunctionGradient(),GoalType.MINIMIZE,new InitialGuess(new double[]{98.680,47.345}));
    Assert.assertEquals(200,optimizer.getMaxEvaluations());
    PointValuePair[] optima=optimizer.getOptima();
    for (    PointValuePair o : optima) {
      Vector2D center=new Vector2D(o.getPointRef()[0],o.getPointRef()[1]);
      Assert.assertEquals(69.960161753,circle.getRadius(center),1e-8);
      Assert.assertEquals(96.075902096,center.getX(),1e-8);
      Assert.assertEquals(48.135167894,center.getY(),1e-8);
    }
    Assert.assertTrue(optimizer.getEvaluations() > 70);
    Assert.assertTrue(optimizer.getEvaluations() < 90);
    Assert.assertEquals(3.1267527,optimum.getValue(),1e-8);
  }
  @Test public void testRosenbrock(){
    Rosenbrock rosenbrock=new Rosenbrock();
    SimplexOptimizer underlying=new SimplexOptimizer(new SimpleValueChecker(-1,1e-3));
    NelderMeadSimplex simplex=new NelderMeadSimplex(new double[][]{{-1.2,1.0},{0.9,1.2},{3.5,-2.3}});
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(16069223052l);
    RandomVectorGenerator generator=new UncorrelatedRandomVectorGenerator(2,new GaussianRandomGenerator(g));
    MultiStartMultivariateOptimizer optimizer=new MultiStartMultivariateOptimizer(underlying,10,generator);
    PointValuePair optimum=optimizer.optimize(new MaxEval(1100),new ObjectiveFunction(rosenbrock),GoalType.MINIMIZE,simplex,new InitialGuess(new double[]{-1.2,1.0}));
    Assert.assertEquals(rosenbrock.getCount(),optimizer.getEvaluations());
    Assert.assertTrue(optimizer.getEvaluations() > 900);
    Assert.assertTrue(optimizer.getEvaluations() < 1200);
    Assert.assertTrue(optimum.getValue() < 8e-4);
  }
private static class Rosenbrock implements MultivariateFunction {
    private int count;
    public Rosenbrock(){
      count=0;
    }
    public double value(    double[] x){
      ++count;
      double a=x[1] - x[0] * x[0];
      double b=1 - x[0];
      return 100 * a * a + b * b;
    }
    public int getCount(){
      return count;
    }
  }
}
