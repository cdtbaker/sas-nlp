package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: SkewnessTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class SkewnessTest extends StorelessUnivariateStatisticAbstractTest {
  protected Skewness stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new Skewness();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.skew;
  }
  /** 
 * Make sure Double.NaN is returned iff n < 3
 */
  @Test public void testNaN(){
    Skewness skew=new Skewness();
    Assert.assertTrue(Double.isNaN(skew.getResult()));
    skew.increment(1d);
    Assert.assertTrue(Double.isNaN(skew.getResult()));
    skew.increment(1d);
    Assert.assertTrue(Double.isNaN(skew.getResult()));
    skew.increment(1d);
    Assert.assertFalse(Double.isNaN(skew.getResult()));
  }
}
