package org.apache.commons.math3.stat.descriptive;
/** 
 * Test cases for the {@link SynchronizedDescriptiveStatisticsTest} class.
 * @version $Id: SynchronizedDescriptiveStatisticsTest.java 1244107 2012-02-14 16:17:55Z erans $
 * 2007) $
 */
public final class SynchronizedDescriptiveStatisticsTest extends DescriptiveStatisticsTest {
  @Override protected DescriptiveStatistics createDescriptiveStatistics(){
    return new SynchronizedDescriptiveStatistics();
  }
}
