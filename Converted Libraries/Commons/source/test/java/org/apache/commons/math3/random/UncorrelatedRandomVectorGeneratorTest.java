package org.apache.commons.math3.random;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.VectorialCovariance;
import org.apache.commons.math3.stat.descriptive.moment.VectorialMean;
import org.junit.Test;
import org.junit.Assert;
public class UncorrelatedRandomVectorGeneratorTest {
  private double[] mean;
  private double[] standardDeviation;
  private UncorrelatedRandomVectorGenerator generator;
  public UncorrelatedRandomVectorGeneratorTest(){
    mean=new double[]{0.0,1.0,-3.0,2.3};
    standardDeviation=new double[]{1.0,2.0,10.0,0.1};
    RandomGenerator rg=new JDKRandomGenerator();
    rg.setSeed(17399225432l);
    generator=new UncorrelatedRandomVectorGenerator(mean,standardDeviation,new GaussianRandomGenerator(rg));
  }
  @Test public void testMeanAndCorrelation(){
    VectorialMean meanStat=new VectorialMean(mean.length);
    VectorialCovariance covStat=new VectorialCovariance(mean.length,true);
    for (int i=0; i < 10000; ++i) {
      double[] v=generator.nextVector();
      meanStat.increment(v);
      covStat.increment(v);
    }
    double[] estimatedMean=meanStat.getResult();
    double scale;
    RealMatrix estimatedCorrelation=covStat.getResult();
    for (int i=0; i < estimatedMean.length; ++i) {
      Assert.assertEquals(mean[i],estimatedMean[i],0.07);
      for (int j=0; j < i; ++j) {
        scale=standardDeviation[i] * standardDeviation[j];
        Assert.assertEquals(0,estimatedCorrelation.getEntry(i,j) / scale,0.03);
      }
      scale=standardDeviation[i] * standardDeviation[i];
      Assert.assertEquals(1,estimatedCorrelation.getEntry(i,i) / scale,0.025);
    }
  }
}
