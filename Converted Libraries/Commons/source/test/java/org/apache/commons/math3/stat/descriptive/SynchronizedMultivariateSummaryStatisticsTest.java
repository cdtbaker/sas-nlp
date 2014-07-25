package org.apache.commons.math3.stat.descriptive;
/** 
 * Test cases for the {@link SynchronizedMultivariateSummaryStatisticsTest} class.
 * @version $Id: SynchronizedMultivariateSummaryStatisticsTest.java 1244107 2012-02-14 16:17:55Z erans $
 * 2007) $
 */
public final class SynchronizedMultivariateSummaryStatisticsTest extends MultivariateSummaryStatisticsTest {
  @Override protected MultivariateSummaryStatistics createMultivariateSummaryStatistics(  int k,  boolean isCovarianceBiasCorrected){
    return new SynchronizedMultivariateSummaryStatistics(k,isCovarianceBiasCorrected);
  }
}
