package org.apache.commons.math3.stat.descriptive;
/** 
 * Test cases for the {@link SynchronizedSummaryStatisticsTest} class.
 * @version $Id: SynchronizedSummaryStatisticsTest.java 1244107 2012-02-14 16:17:55Z erans $
 * 2007) $
 */
public final class SynchronizedSummaryStatisticsTest extends SummaryStatisticsTest {
  @Override protected SummaryStatistics createSummaryStatistics(){
    return new SynchronizedSummaryStatistics();
  }
}
