package org.apache.commons.math3.stat.descriptive.summary;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: SumLogTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class SumLogTest extends StorelessUnivariateStatisticAbstractTest {
  protected SumOfLogs stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new SumOfLogs();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.sumLog;
  }
  @Test public void testSpecialValues(){
    SumOfLogs sum=new SumOfLogs();
    Assert.assertEquals(0,sum.getResult(),0);
    sum.increment(1d);
    Assert.assertFalse(Double.isNaN(sum.getResult()));
    sum.increment(0d);
    Assert.assertEquals(Double.NEGATIVE_INFINITY,sum.getResult(),0);
    sum.increment(Double.POSITIVE_INFINITY);
    Assert.assertTrue(Double.isNaN(sum.getResult()));
    sum.clear();
    Assert.assertEquals(0,sum.getResult(),0);
    sum.increment(Double.POSITIVE_INFINITY);
    Assert.assertEquals(Double.POSITIVE_INFINITY,sum.getResult(),0);
    sum.increment(-2d);
    Assert.assertTrue(Double.isNaN(sum.getResult()));
  }
  @Override protected void checkClearValue(  StorelessUnivariateStatistic statistic){
    Assert.assertEquals(0,statistic.getResult(),0);
  }
}
