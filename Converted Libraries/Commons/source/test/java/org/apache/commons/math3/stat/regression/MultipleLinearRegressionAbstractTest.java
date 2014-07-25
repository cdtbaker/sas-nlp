package org.apache.commons.math3.stat.regression;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public abstract class MultipleLinearRegressionAbstractTest {
  protected AbstractMultipleLinearRegression regression;
  @Before public void setUp(){
    regression=createRegression();
  }
  protected abstract AbstractMultipleLinearRegression createRegression();
  protected abstract int getNumberOfRegressors();
  protected abstract int getSampleSize();
  @Test public void canEstimateRegressionParameters(){
    double[] beta=regression.estimateRegressionParameters();
    Assert.assertEquals(getNumberOfRegressors(),beta.length);
  }
  @Test public void canEstimateResiduals(){
    double[] e=regression.estimateResiduals();
    Assert.assertEquals(getSampleSize(),e.length);
  }
  @Test public void canEstimateRegressionParametersVariance(){
    double[][] variance=regression.estimateRegressionParametersVariance();
    Assert.assertEquals(getNumberOfRegressors(),variance.length);
  }
  @Test public void canEstimateRegressandVariance(){
    if (getSampleSize() > getNumberOfRegressors()) {
      double variance=regression.estimateRegressandVariance();
      Assert.assertTrue(variance > 0.0);
    }
  }
  /** 
 * Verifies that newSampleData methods consistently insert unitary columns
 * in design matrix.  Confirms the fix for MATH-411.
 */
  @Test public void testNewSample(){
    double[] design=new double[]{1,19,22,33,2,20,30,40,3,25,35,45,4,27,37,47};
    double[] y=new double[]{1,2,3,4};
    double[][] x=new double[][]{{19,22,33},{20,30,40},{25,35,45},{27,37,47}};
    AbstractMultipleLinearRegression regression=createRegression();
    regression.newSampleData(design,4,3);
    RealMatrix flatX=regression.getX().copy();
    RealVector flatY=regression.getY().copy();
    regression.newXSampleData(x);
    regression.newYSampleData(y);
    Assert.assertEquals(flatX,regression.getX());
    Assert.assertEquals(flatY,regression.getY());
    regression.setNoIntercept(true);
    regression.newSampleData(design,4,3);
    flatX=regression.getX().copy();
    flatY=regression.getY().copy();
    regression.newXSampleData(x);
    regression.newYSampleData(y);
    Assert.assertEquals(flatX,regression.getX());
    Assert.assertEquals(flatY,regression.getY());
  }
  @Test(expected=IllegalArgumentException.class) public void testNewSampleNullData(){
    double[] data=null;
    createRegression().newSampleData(data,2,3);
  }
  @Test(expected=IllegalArgumentException.class) public void testNewSampleInvalidData(){
    double[] data=new double[]{1,2,3,4};
    createRegression().newSampleData(data,2,3);
  }
  @Test(expected=IllegalArgumentException.class) public void testNewSampleInsufficientData(){
    double[] data=new double[]{1,2,3,4};
    createRegression().newSampleData(data,1,3);
  }
  @Test(expected=IllegalArgumentException.class) public void testXSampleDataNull(){
    createRegression().newXSampleData(null);
  }
  @Test(expected=IllegalArgumentException.class) public void testYSampleDataNull(){
    createRegression().newYSampleData(null);
  }
}
