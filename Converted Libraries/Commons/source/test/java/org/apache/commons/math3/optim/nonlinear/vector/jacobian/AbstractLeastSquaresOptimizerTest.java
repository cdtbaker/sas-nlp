package org.apache.commons.math3.optim.nonlinear.vector.jacobian;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.vector.Target;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import org.junit.Assert;
public class AbstractLeastSquaresOptimizerTest {
  public static AbstractLeastSquaresOptimizer createOptimizer(){
    return new AbstractLeastSquaresOptimizer(null){
      @Override protected PointVectorValuePair doOptimize(){
        final double[] params=getStartPoint();
        final double[] res=computeResiduals(computeObjectiveValue(params));
        setCost(computeCost(res));
        return new PointVectorValuePair(params,null);
      }
    }
;
  }
  @Test public void testGetChiSquare() throws IOException {
    final StatisticalReferenceDataset dataset=StatisticalReferenceDatasetFactory.createKirby2();
    final AbstractLeastSquaresOptimizer optimizer=createOptimizer();
    final double[] a=dataset.getParameters();
    final double[] y=dataset.getData()[1];
    final double[] w=new double[y.length];
    Arrays.fill(w,1.0);
    StatisticalReferenceDataset.LeastSquaresProblem problem=dataset.getLeastSquaresProblem();
    optimizer.optimize(new MaxEval(1),problem.getModelFunction(),problem.getModelFunctionJacobian(),new Target(y),new Weight(w),new InitialGuess(a));
    final double expected=dataset.getResidualSumOfSquares();
    final double actual=optimizer.getChiSquare();
    Assert.assertEquals(dataset.getName(),expected,actual,1E-11 * expected);
  }
  @Test public void testGetRMS() throws IOException {
    final StatisticalReferenceDataset dataset=StatisticalReferenceDatasetFactory.createKirby2();
    final AbstractLeastSquaresOptimizer optimizer=createOptimizer();
    final double[] a=dataset.getParameters();
    final double[] y=dataset.getData()[1];
    final double[] w=new double[y.length];
    Arrays.fill(w,1);
    StatisticalReferenceDataset.LeastSquaresProblem problem=dataset.getLeastSquaresProblem();
    optimizer.optimize(new MaxEval(1),problem.getModelFunction(),problem.getModelFunctionJacobian(),new Target(y),new Weight(w),new InitialGuess(a));
    final double expected=FastMath.sqrt(dataset.getResidualSumOfSquares() / dataset.getNumObservations());
    final double actual=optimizer.getRMS();
    Assert.assertEquals(dataset.getName(),expected,actual,1E-11 * expected);
  }
  @Test public void testComputeSigma() throws IOException {
    final StatisticalReferenceDataset dataset=StatisticalReferenceDatasetFactory.createKirby2();
    final AbstractLeastSquaresOptimizer optimizer=createOptimizer();
    final double[] a=dataset.getParameters();
    final double[] y=dataset.getData()[1];
    final double[] w=new double[y.length];
    Arrays.fill(w,1);
    StatisticalReferenceDataset.LeastSquaresProblem problem=dataset.getLeastSquaresProblem();
    final PointVectorValuePair optimum=optimizer.optimize(new MaxEval(1),problem.getModelFunction(),problem.getModelFunctionJacobian(),new Target(y),new Weight(w),new InitialGuess(a));
    final double[] sig=optimizer.computeSigma(optimum.getPoint(),1e-14);
    final int dof=y.length - a.length;
    final double[] expected=dataset.getParametersStandardDeviations();
    for (int i=0; i < sig.length; i++) {
      final double actual=FastMath.sqrt(optimizer.getChiSquare() / dof) * sig[i];
      Assert.assertEquals(dataset.getName() + ", parameter #" + i,expected[i],actual,1e-6 * expected[i]);
    }
  }
}
