package org.apache.commons.math3.stat.correlation;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for Spearman's rank correlation
 * @since 2.0
 * @version $Id: SpearmansRankCorrelationTest.java 1461822 2013-03-27 19:44:22Z tn $
 */
public class SpearmansRankCorrelationTest extends PearsonsCorrelationTest {
  /** 
 * Test Longley dataset against R.
 */
  @Override @Test public void testLongly(){
    RealMatrix matrix=createRealMatrix(longleyData,16,7);
    SpearmansCorrelation corrInstance=new SpearmansCorrelation(matrix);
    RealMatrix correlationMatrix=corrInstance.getCorrelationMatrix();
    double[] rData=new double[]{1,0.982352941176471,0.985294117647059,0.564705882352941,0.2264705882352941,0.976470588235294,0.976470588235294,0.982352941176471,1,0.997058823529412,0.664705882352941,0.2205882352941176,0.997058823529412,0.997058823529412,0.985294117647059,0.997058823529412,1,0.638235294117647,0.2235294117647059,0.9941176470588236,0.9941176470588236,0.564705882352941,0.664705882352941,0.638235294117647,1,-0.3411764705882353,0.685294117647059,0.685294117647059,0.2264705882352941,0.2205882352941176,0.2235294117647059,-0.3411764705882353,1,0.2264705882352941,0.2264705882352941,0.976470588235294,0.997058823529412,0.9941176470588236,0.685294117647059,0.2264705882352941,1,1,0.976470588235294,0.997058823529412,0.9941176470588236,0.685294117647059,0.2264705882352941,1,1};
    TestUtils.assertEquals("Spearman's correlation matrix",createRealMatrix(rData,7,7),correlationMatrix,10E-15);
  }
  /** 
 * Test R swiss fertility dataset.
 */
  @Test public void testSwiss(){
    RealMatrix matrix=createRealMatrix(swissData,47,5);
    SpearmansCorrelation corrInstance=new SpearmansCorrelation(matrix);
    RealMatrix correlationMatrix=corrInstance.getCorrelationMatrix();
    double[] rData=new double[]{1,0.2426642769364176,-0.660902996352354,-0.443257690360988,0.4136455623012432,0.2426642769364176,1,-0.598859938748963,-0.650463814145816,0.2886878090882852,-0.660902996352354,-0.598859938748963,1,0.674603831406147,-0.4750575257171745,-0.443257690360988,-0.650463814145816,0.674603831406147,1,-0.1444163088302244,0.4136455623012432,0.2886878090882852,-0.4750575257171745,-0.1444163088302244,1};
    TestUtils.assertEquals("Spearman's correlation matrix",createRealMatrix(rData,5,5),correlationMatrix,10E-15);
  }
  /** 
 * Constant column
 */
  @Override @Test public void testConstant(){
    double[] noVariance=new double[]{1,1,1,1};
    double[] values=new double[]{1,2,3,4};
    Assert.assertTrue(Double.isNaN(new SpearmansCorrelation().correlation(noVariance,values)));
  }
  /** 
 * Insufficient data
 */
  @Override @Test public void testInsufficientData(){
    double[] one=new double[]{1};
    double[] two=new double[]{2};
    try {
      new SpearmansCorrelation().correlation(one,two);
      Assert.fail("Expecting IllegalArgumentException");
    }
 catch (    IllegalArgumentException ex) {
    }
    RealMatrix matrix=new BlockRealMatrix(new double[][]{{0},{1}});
    try {
      new SpearmansCorrelation(matrix);
      Assert.fail("Expecting IllegalArgumentException");
    }
 catch (    IllegalArgumentException ex) {
    }
  }
  @Override @Test public void testConsistency(){
    RealMatrix matrix=createRealMatrix(longleyData,16,7);
    SpearmansCorrelation corrInstance=new SpearmansCorrelation(matrix);
    double[][] data=matrix.getData();
    double[] x=matrix.getColumn(0);
    double[] y=matrix.getColumn(1);
    Assert.assertEquals(new SpearmansCorrelation().correlation(x,y),corrInstance.getCorrelationMatrix().getEntry(0,1),Double.MIN_VALUE);
    TestUtils.assertEquals("Correlation matrix",corrInstance.getCorrelationMatrix(),new SpearmansCorrelation().computeCorrelationMatrix(data),Double.MIN_VALUE);
  }
  @Test public void testMath891Array(){
    final double[] xArray=new double[]{Double.NaN,1.9,2,100,3};
    final double[] yArray=new double[]{10,2,10,Double.NaN,4};
    NaturalRanking ranking=new NaturalRanking(NaNStrategy.REMOVED);
    SpearmansCorrelation spearman=new SpearmansCorrelation(ranking);
    Assert.assertEquals(0.5,spearman.correlation(xArray,yArray),Double.MIN_VALUE);
  }
  @Test public void testMath891Matrix(){
    final double[] xArray=new double[]{Double.NaN,1.9,2,100,3};
    final double[] yArray=new double[]{10,2,10,Double.NaN,4};
    RealMatrix matrix=MatrixUtils.createRealMatrix(xArray.length,2);
    for (int i=0; i < xArray.length; i++) {
      matrix.addToEntry(i,0,xArray[i]);
      matrix.addToEntry(i,1,yArray[i]);
    }
    NaturalRanking ranking=new NaturalRanking(NaNStrategy.REMOVED);
    SpearmansCorrelation spearman=new SpearmansCorrelation(matrix,ranking);
    Assert.assertEquals(0.5,spearman.getCorrelationMatrix().getEntry(0,1),Double.MIN_VALUE);
  }
  @Override @Test public void testStdErrorConsistency(){
  }
  @Override @Test public void testCovarianceConsistency(){
  }
}