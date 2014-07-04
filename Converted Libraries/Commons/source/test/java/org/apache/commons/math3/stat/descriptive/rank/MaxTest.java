package org.apache.commons.math3.stat.descriptive.rank;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: MaxTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class MaxTest extends StorelessUnivariateStatisticAbstractTest {
  protected Max stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new Max();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.max;
  }
  @Test public void testSpecialValues(){
    double[] testArray={0d,Double.NaN,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY};
    Max max=new Max();
    Assert.assertTrue(Double.isNaN(max.getResult()));
    max.increment(testArray[0]);
    Assert.assertEquals(0d,max.getResult(),0);
    max.increment(testArray[1]);
    Assert.assertEquals(0d,max.getResult(),0);
    max.increment(testArray[2]);
    Assert.assertEquals(0d,max.getResult(),0);
    max.increment(testArray[3]);
    Assert.assertEquals(Double.POSITIVE_INFINITY,max.getResult(),0);
    Assert.assertEquals(Double.POSITIVE_INFINITY,max.evaluate(testArray),0);
  }
  @Test public void testNaNs(){
    Max max=new Max();
    double nan=Double.NaN;
    Assert.assertEquals(3d,max.evaluate(new double[]{nan,2d,3d}),0);
    Assert.assertEquals(3d,max.evaluate(new double[]{1d,nan,3d}),0);
    Assert.assertEquals(2d,max.evaluate(new double[]{1d,2d,nan}),0);
    Assert.assertTrue(Double.isNaN(max.evaluate(new double[]{nan,nan,nan})));
  }
}
