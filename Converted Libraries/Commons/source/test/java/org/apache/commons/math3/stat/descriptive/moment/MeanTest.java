package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: MeanTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class MeanTest extends StorelessUnivariateStatisticAbstractTest {
  protected Mean stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new Mean();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.mean;
  }
  /** 
 * Expected value for  the testArray defined in UnivariateStatisticAbstractTest 
 */
  public double expectedWeightedValue(){
    return this.weightedMean;
  }
  @Test public void testSmallSamples(){
    Mean mean=new Mean();
    Assert.assertTrue(Double.isNaN(mean.getResult()));
    mean.increment(1d);
    Assert.assertEquals(1d,mean.getResult(),0);
  }
  @Test public void testWeightedMean(){
    Mean mean=new Mean();
    Assert.assertEquals(expectedWeightedValue(),mean.evaluate(testArray,testWeightsArray,0,testArray.length),getTolerance());
    Assert.assertEquals(expectedValue(),mean.evaluate(testArray,identicalWeightsArray,0,testArray.length),getTolerance());
  }
}
