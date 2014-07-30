package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
/** 
 * Test cases for the {@link FirstMoment} class.
 * @version $Id: FirstMomentTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class FirstMomentTest extends StorelessUnivariateStatisticAbstractTest {
  /** 
 * descriptive statistic. 
 */
  protected FirstMoment stat;
  /** 
 * @see org.apache.commons.math3.stat.descriptive.UnivariateStatisticAbstractTest#getUnivariateStatistic()
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new FirstMoment();
  }
  /** 
 * @see org.apache.commons.math3.stat.descriptive.UnivariateStatisticAbstractTest#expectedValue()
 */
  @Override public double expectedValue(){
    return this.mean;
  }
}