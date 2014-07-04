package org.apache.commons.math3.stat.regression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
public class GLSMultipleLinearRegressionTest extends MultipleLinearRegressionAbstractTest {
  private double[] y;
  private double[][] x;
  private double[][] omega;
  private double[] longley=new double[]{60323,83.0,234289,2356,1590,107608,1947,61122,88.5,259426,2325,1456,108632,1948,60171,88.2,258054,3682,1616,109773,1949,61187,89.5,284599,3351,1650,110929,1950,63221,96.2,328975,2099,3099,112075,1951,63639,98.1,346999,1932,3594,113270,1952,64989,99.0,365385,1870,3547,115094,1953,63761,100.0,363112,3578,3350,116219,1954,66019,101.2,397469,2904,3048,117388,1955,67857,104.6,419180,2822,2857,118734,1956,68169,108.4,442769,2936,2798,120445,1957,66513,110.8,444546,4681,2637,121950,1958,68655,112.6,482704,3813,2552,123366,1959,69564,114.2,502601,3931,2514,125368,1960,69331,115.7,518173,4806,2572,127852,1961,70551,116.9,554894,4007,2827,130081,1962};
  @Before @Override public void setUp(){
    y=new double[]{11.0,12.0,13.0,14.0,15.0,16.0};
    x=new double[6][];
    x[0]=new double[]{0,0,0,0,0};
    x[1]=new double[]{2.0,0,0,0,0};
    x[2]=new double[]{0,3.0,0,0,0};
    x[3]=new double[]{0,0,4.0,0,0};
    x[4]=new double[]{0,0,0,5.0,0};
    x[5]=new double[]{0,0,0,0,6.0};
    omega=new double[6][];
    omega[0]=new double[]{1.0,0,0,0,0,0};
    omega[1]=new double[]{0,2.0,0,0,0,0};
    omega[2]=new double[]{0,0,3.0,0,0,0};
    omega[3]=new double[]{0,0,0,4.0,0,0};
    omega[4]=new double[]{0,0,0,0,5.0,0};
    omega[5]=new double[]{0,0,0,0,0,6.0};
    super.setUp();
  }
  @Test(expected=IllegalArgumentException.class) public void cannotAddXSampleData(){
    createRegression().newSampleData(new double[]{},null,null);
  }
  @Test(expected=IllegalArgumentException.class) public void cannotAddNullYSampleData(){
    createRegression().newSampleData(null,new double[][]{},null);
  }
  @Test(expected=IllegalArgumentException.class) public void cannotAddSampleDataWithSizeMismatch(){
    double[] y=new double[]{1.0,2.0};
    double[][] x=new double[1][];
    x[0]=new double[]{1.0,0};
    createRegression().newSampleData(y,x,null);
  }
  @Test(expected=IllegalArgumentException.class) public void cannotAddNullCovarianceData(){
    createRegression().newSampleData(new double[]{},new double[][]{},null);
  }
  @Test(expected=IllegalArgumentException.class) public void notEnoughData(){
    double[] reducedY=new double[y.length - 1];
    double[][] reducedX=new double[x.length - 1][];
    double[][] reducedO=new double[omega.length - 1][];
    System.arraycopy(y,0,reducedY,0,reducedY.length);
    System.arraycopy(x,0,reducedX,0,reducedX.length);
    System.arraycopy(omega,0,reducedO,0,reducedO.length);
    createRegression().newSampleData(reducedY,reducedX,reducedO);
  }
  @Test(expected=IllegalArgumentException.class) public void cannotAddCovarianceDataWithSampleSizeMismatch(){
    double[] y=new double[]{1.0,2.0};
    double[][] x=new double[2][];
    x[0]=new double[]{1.0,0};
    x[1]=new double[]{0,1.0};
    double[][] omega=new double[1][];
    omega[0]=new double[]{1.0,0};
    createRegression().newSampleData(y,x,omega);
  }
  @Test(expected=IllegalArgumentException.class) public void cannotAddCovarianceDataThatIsNotSquare(){
    double[] y=new double[]{1.0,2.0};
    double[][] x=new double[2][];
    x[0]=new double[]{1.0,0};
    x[1]=new double[]{0,1.0};
    double[][] omega=new double[3][];
    omega[0]=new double[]{1.0,0};
    omega[1]=new double[]{0,1.0};
    omega[2]=new double[]{0,2.0};
    createRegression().newSampleData(y,x,omega);
  }
  @Override protected GLSMultipleLinearRegression createRegression(){
    GLSMultipleLinearRegression regression=new GLSMultipleLinearRegression();
    regression.newSampleData(y,x,omega);
    return regression;
  }
  @Override protected int getNumberOfRegressors(){
    return x[0].length + 1;
  }
  @Override protected int getSampleSize(){
    return y.length;
  }
  /** 
 * test calculateYVariance
 */
  @Test public void testYVariance(){
    GLSMultipleLinearRegression model=new GLSMultipleLinearRegression();
    model.newSampleData(y,x,omega);
    TestUtils.assertEquals(model.calculateYVariance(),3.5,0);
  }
  /** 
 * Verifies that setting X, Y and covariance separately has the same effect as newSample(X,Y,cov).
 */
  @Test public void testNewSample2(){
    double[] y=new double[]{1,2,3,4};
    double[][] x=new double[][]{{19,22,33},{20,30,40},{25,35,45},{27,37,47}};
    double[][] covariance=MatrixUtils.createRealIdentityMatrix(4).scalarMultiply(2).getData();
    GLSMultipleLinearRegression regression=new GLSMultipleLinearRegression();
    regression.newSampleData(y,x,covariance);
    RealMatrix combinedX=regression.getX().copy();
    RealVector combinedY=regression.getY().copy();
    RealMatrix combinedCovInv=regression.getOmegaInverse();
    regression.newXSampleData(x);
    regression.newYSampleData(y);
    Assert.assertEquals(combinedX,regression.getX());
    Assert.assertEquals(combinedY,regression.getY());
    Assert.assertEquals(combinedCovInv,regression.getOmegaInverse());
  }
  /** 
 * Verifies that GLS with identity covariance matrix gives the same results
 * as OLS.
 */
  @Test public void testGLSOLSConsistency(){
    RealMatrix identityCov=MatrixUtils.createRealIdentityMatrix(16);
    GLSMultipleLinearRegression glsModel=new GLSMultipleLinearRegression();
    OLSMultipleLinearRegression olsModel=new OLSMultipleLinearRegression();
    glsModel.newSampleData(longley,16,6);
    olsModel.newSampleData(longley,16,6);
    glsModel.newCovarianceData(identityCov.getData());
    double[] olsBeta=olsModel.calculateBeta().toArray();
    double[] glsBeta=glsModel.calculateBeta().toArray();
    for (int i=0; i < olsBeta.length; i++) {
      TestUtils.assertRelativelyEquals(olsBeta[i],glsBeta[i],10E-7);
    }
  }
  /** 
 * Generate an error covariance matrix and sample data representing models
 * with this error structure. Then verify that GLS estimated coefficients,
 * on average, perform better than OLS.
 */
  @Test public void testGLSEfficiency(){
    RandomGenerator rg=new JDKRandomGenerator();
    rg.setSeed(200);
    final int nObs=16;
    double[] sigma=new double[nObs];
    for (int i=0; i < nObs; i++) {
      sigma[i]=10 * rg.nextDouble();
    }
    final int numSeeds=1000;
    RealMatrix errorSeeds=MatrixUtils.createRealMatrix(numSeeds,nObs);
    for (int i=0; i < numSeeds; i++) {
      for (int j=0; j < nObs; j++) {
        errorSeeds.setEntry(i,j,rg.nextGaussian() * sigma[j]);
      }
    }
    RealMatrix cov=(new Covariance(errorSeeds)).getCovarianceMatrix();
    GaussianRandomGenerator rawGenerator=new GaussianRandomGenerator(rg);
    double[] errorMeans=new double[nObs];
    CorrelatedRandomVectorGenerator gen=new CorrelatedRandomVectorGenerator(errorMeans,cov,1.0e-12 * cov.getNorm(),rawGenerator);
    OLSMultipleLinearRegression ols=new OLSMultipleLinearRegression();
    ols.newSampleData(longley,nObs,6);
    final RealVector b=ols.calculateBeta().copy();
    final RealMatrix x=ols.getX().copy();
    GLSMultipleLinearRegression gls=new GLSMultipleLinearRegression();
    gls.newSampleData(longley,nObs,6);
    gls.newCovarianceData(cov.getData());
    DescriptiveStatistics olsBetaStats=new DescriptiveStatistics();
    DescriptiveStatistics glsBetaStats=new DescriptiveStatistics();
    final int nModels=10000;
    for (int i=0; i < nModels; i++) {
      RealVector u=MatrixUtils.createRealVector(gen.nextVector());
      double[] y=u.add(x.operate(b)).toArray();
      ols.newYSampleData(y);
      RealVector olsBeta=ols.calculateBeta();
      gls.newYSampleData(y);
      RealVector glsBeta=gls.calculateBeta();
      double dist=olsBeta.getDistance(b);
      olsBetaStats.addValue(dist * dist);
      dist=glsBeta.getDistance(b);
      glsBetaStats.addValue(dist * dist);
    }
    assert (olsBetaStats.getMean() > 1.5 * glsBetaStats.getMean());
    assert (olsBetaStats.getStandardDeviation() > glsBetaStats.getStandardDeviation());
  }
}
