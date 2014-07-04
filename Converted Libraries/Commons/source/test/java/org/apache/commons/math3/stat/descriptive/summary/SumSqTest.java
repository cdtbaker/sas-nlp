package org.apache.commons.math3.stat.descriptive.summary;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link SumOfSquares} class.
 * @version $Id: SumSqTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class SumSqTest extends StorelessUnivariateStatisticAbstractTest {
  protected SumOfSquares stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new SumOfSquares();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.sumSq;
  }
  @Test public void testSpecialValues(){
    SumOfSquares sumSq=new SumOfSquares();
    Assert.assertEquals(0,sumSq.getResult(),0);
    sumSq.increment(2d);
    Assert.assertEquals(4d,sumSq.getResult(),0);
    sumSq.increment(Double.POSITIVE_INFINITY);
    Assert.assertEquals(Double.POSITIVE_INFINITY,sumSq.getResult(),0);
    sumSq.increment(Double.NEGATIVE_INFINITY);
    Assert.assertEquals(Double.POSITIVE_INFINITY,sumSq.getResult(),0);
    sumSq.increment(Double.NaN);
    Assert.assertTrue(Double.isNaN(sumSq.getResult()));
    sumSq.increment(1);
    Assert.assertTrue(Double.isNaN(sumSq.getResult()));
  }
  @Override protected void checkClearValue(  StorelessUnivariateStatistic statistic){
    Assert.assertEquals(0,statistic.getResult(),0);
  }
}
