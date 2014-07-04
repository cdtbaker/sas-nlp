package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;
import org.junit.Assert;
public class VectorialCovarianceTest {
  private double[][] points;
  public VectorialCovarianceTest(){
    points=new double[][]{{1.2,2.3,4.5},{-0.7,2.3,5.0},{3.1,0.0,-3.1},{6.0,1.2,4.2},{-0.7,2.3,5.0}};
  }
  @Test public void testMismatch(){
    try {
      new VectorialCovariance(8,true).increment(new double[5]);
      Assert.fail("an exception should have been thrown");
    }
 catch (    DimensionMismatchException dme) {
      Assert.assertEquals(5,dme.getArgument());
      Assert.assertEquals(8,dme.getDimension());
    }
  }
  @Test public void testSimplistic(){
    VectorialCovariance stat=new VectorialCovariance(2,true);
    stat.increment(new double[]{-1.0,1.0});
    stat.increment(new double[]{1.0,-1.0});
    RealMatrix c=stat.getResult();
    Assert.assertEquals(2.0,c.getEntry(0,0),1.0e-12);
    Assert.assertEquals(-2.0,c.getEntry(1,0),1.0e-12);
    Assert.assertEquals(2.0,c.getEntry(1,1),1.0e-12);
  }
  @Test public void testBasicStats(){
    VectorialCovariance stat=new VectorialCovariance(points[0].length,true);
    for (int i=0; i < points.length; ++i) {
      stat.increment(points[i]);
    }
    Assert.assertEquals(points.length,stat.getN());
    RealMatrix c=stat.getResult();
    double[][] refC=new double[][]{{8.0470,-1.9195,-3.4445},{-1.9195,1.0470,3.2795},{-3.4445,3.2795,12.2070}};
    for (int i=0; i < c.getRowDimension(); ++i) {
      for (int j=0; j <= i; ++j) {
        Assert.assertEquals(refC[i][j],c.getEntry(i,j),1.0e-12);
      }
    }
  }
  @Test public void testSerial(){
    VectorialCovariance stat=new VectorialCovariance(points[0].length,true);
    Assert.assertEquals(stat,TestUtils.serializeAndRecover(stat));
  }
}
