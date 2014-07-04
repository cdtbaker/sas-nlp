package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: KurtosisTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class KurtosisTest extends StorelessUnivariateStatisticAbstractTest {
  protected Kurtosis stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new Kurtosis();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.kurt;
  }
  /** 
 * Make sure Double.NaN is returned iff n < 4
 */
  @Test public void testNaN(){
    Kurtosis kurt=new Kurtosis();
    Assert.assertTrue(Double.isNaN(kurt.getResult()));
    kurt.increment(1d);
    Assert.assertTrue(Double.isNaN(kurt.getResult()));
    kurt.increment(1d);
    Assert.assertTrue(Double.isNaN(kurt.getResult()));
    kurt.increment(1d);
    Assert.assertTrue(Double.isNaN(kurt.getResult()));
    kurt.increment(1d);
    Assert.assertFalse(Double.isNaN(kurt.getResult()));
  }
}
