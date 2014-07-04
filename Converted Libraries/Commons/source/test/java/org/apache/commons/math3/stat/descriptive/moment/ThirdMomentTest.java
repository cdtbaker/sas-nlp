package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
/** 
 * Test cases for the {@link ThirdMoment} class.
 * @version $Id: ThirdMomentTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class ThirdMomentTest extends StorelessUnivariateStatisticAbstractTest {
  /** 
 * descriptive statistic. 
 */
  protected ThirdMoment stat;
  /** 
 * @see org.apache.commons.math3.stat.descriptive.UnivariateStatisticAbstractTest#getUnivariateStatistic()
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new ThirdMoment();
  }
  /** 
 * @see org.apache.commons.math3.stat.descriptive.UnivariateStatisticAbstractTest#expectedValue()
 */
  @Override public double expectedValue(){
    return this.thirdMoment;
  }
}
