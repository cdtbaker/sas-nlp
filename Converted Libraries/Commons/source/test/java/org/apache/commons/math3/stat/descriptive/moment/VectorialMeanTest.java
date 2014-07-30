package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.TestUtils;
import org.junit.Test;
import org.junit.Assert;
public class VectorialMeanTest {
  private double[][] points;
  public VectorialMeanTest(){
    points=new double[][]{{1.2,2.3,4.5},{-0.7,2.3,5.0},{3.1,0.0,-3.1},{6.0,1.2,4.2},{-0.7,2.3,5.0}};
  }
  @Test public void testMismatch(){
    try {
      new VectorialMean(8).increment(new double[5]);
      Assert.fail("an exception should have been thrown");
    }
 catch (    DimensionMismatchException dme) {
      Assert.assertEquals(5,dme.getArgument());
      Assert.assertEquals(8,dme.getDimension());
    }
  }
  @Test public void testSimplistic(){
    VectorialMean stat=new VectorialMean(2);
    stat.increment(new double[]{-1.0,1.0});
    stat.increment(new double[]{1.0,-1.0});
    double[] mean=stat.getResult();
    Assert.assertEquals(0.0,mean[0],1.0e-12);
    Assert.assertEquals(0.0,mean[1],1.0e-12);
  }
  @Test public void testBasicStats(){
    VectorialMean stat=new VectorialMean(points[0].length);
    for (int i=0; i < points.length; ++i) {
      stat.increment(points[i]);
    }
    Assert.assertEquals(points.length,stat.getN());
    double[] mean=stat.getResult();
    double[] refMean=new double[]{1.78,1.62,3.12};
    for (int i=0; i < mean.length; ++i) {
      Assert.assertEquals(refMean[i],mean[i],1.0e-12);
    }
  }
  @Test public void testSerial(){
    VectorialMean stat=new VectorialMean(points[0].length);
    for (int i=0; i < points.length; ++i) {
      stat.increment(points[i]);
    }
    Assert.assertEquals(stat,TestUtils.serializeAndRecover(stat));
  }
}