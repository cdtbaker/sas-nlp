package org.apache.commons.math3.stat.descriptive.rank;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.UnivariateStatisticAbstractTest;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: MedianTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class MedianTest extends UnivariateStatisticAbstractTest {
  protected Median stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new Median();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.median;
  }
}
