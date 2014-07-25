package org.apache.commons.math3.optimization;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optimization.general.CircleScalar;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
import org.junit.Assert;
import org.junit.Test;
public class MultivariateDifferentiableMultiStartOptimizerTest {
  @Test public void testCircleFitting(){
    CircleScalar circle=new CircleScalar();
    circle.addPoint(30.0,68.0);
    circle.addPoint(50.0,-6.0);
    circle.addPoint(110.0,-20.0);
    circle.addPoint(35.0,15.0);
    circle.addPoint(45.0,97.0);
    MultivariateDifferentiableOptimizer underlying=new MultivariateDifferentiableOptimizer(){
      private final NonLinearConjugateGradientOptimizer cg=new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE,new SimpleValueChecker(1.0e-10,1.0e-10));
      public PointValuePair optimize(      int maxEval,      MultivariateDifferentiableFunction f,      GoalType goalType,      double[] startPoint){
        return cg.optimize(maxEval,f,goalType,startPoint);
      }
      public int getMaxEvaluations(){
        return cg.getMaxEvaluations();
      }
      public int getEvaluations(){
        return cg.getEvaluations();
      }
      public ConvergenceChecker<PointValuePair> getConvergenceChecker(){
        return cg.getConvergenceChecker();
      }
    }
;
    JDKRandomGenerator g=new JDKRandomGenerator();
    g.setSeed(753289573253l);
    RandomVectorGenerator generator=new UncorrelatedRandomVectorGenerator(new double[]{50.0,50.0},new double[]{10.0,10.0},new GaussianRandomGenerator(g));
    MultivariateDifferentiableMultiStartOptimizer optimizer=new MultivariateDifferentiableMultiStartOptimizer(underlying,10,generator);
    PointValuePair optimum=optimizer.optimize(200,circle,GoalType.MINIMIZE,new double[]{98.680,47.345});
    Assert.assertEquals(200,optimizer.getMaxEvaluations());
    PointValuePair[] optima=optimizer.getOptima();
    for (    PointValuePair o : optima) {
      Vector2D center=new Vector2D(o.getPointRef()[0],o.getPointRef()[1]);
      Assert.assertEquals(69.960161753,circle.getRadius(center),1.0e-8);
      Assert.assertEquals(96.075902096,center.getX(),1.0e-8);
      Assert.assertEquals(48.135167894,center.getY(),1.0e-8);
    }
    Assert.assertTrue(optimizer.getEvaluations() > 70);
    Assert.assertTrue(optimizer.getEvaluations() < 90);
    Assert.assertEquals(3.1267527,optimum.getValue(),1.0e-8);
  }
}
