package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: GeometricMeanTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class GeometricMeanTest extends StorelessUnivariateStatisticAbstractTest {
  protected GeometricMean stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new GeometricMean();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.geoMean;
  }
  @Test public void testSpecialValues(){
    GeometricMean mean=new GeometricMean();
    Assert.assertTrue(Double.isNaN(mean.getResult()));
    mean.increment(1d);
    Assert.assertFalse(Double.isNaN(mean.getResult()));
    mean.increment(0d);
    Assert.assertEquals(0d,mean.getResult(),0);
    mean.increment(Double.POSITIVE_INFINITY);
    Assert.assertTrue(Double.isNaN(mean.getResult()));
    mean.clear();
    Assert.assertTrue(Double.isNaN(mean.getResult()));
    mean.increment(Double.POSITIVE_INFINITY);
    Assert.assertEquals(Double.POSITIVE_INFINITY,mean.getResult(),0);
    mean.increment(-2d);
    Assert.assertTrue(Double.isNaN(mean.getResult()));
  }
}
