package org.apache.commons.math3.stat.descriptive;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for {@link StorelessUnivariateStatistic} classes.
 * @version $Id: StorelessUnivariateStatisticAbstractTest.java 1364030 2012-07-21 01:10:04Z erans $
 */
public abstract class StorelessUnivariateStatisticAbstractTest extends UnivariateStatisticAbstractTest {
  /** 
 * Small sample arrays 
 */
  protected double[][] smallSamples={{},{1},{1,2},{1,2,3},{1,2,3,4}};
  /** 
 * Return a new instance of the statistic 
 */
  @Override public abstract UnivariateStatistic getUnivariateStatistic();
  /** 
 * Expected value for  the testArray defined in UnivariateStatisticAbstractTest 
 */
  @Override public abstract double expectedValue();
  /** 
 * Verifies that increment() and incrementAll work properly.
 */
  @Test public void testIncrementation(){
    StorelessUnivariateStatistic statistic=(StorelessUnivariateStatistic)getUnivariateStatistic();
    for (int i=0; i < testArray.length; i++) {
      statistic.increment(testArray[i]);
    }
    Assert.assertEquals(expectedValue(),statistic.getResult(),getTolerance());
    Assert.assertEquals(testArray.length,statistic.getN());
    statistic.clear();
    statistic.incrementAll(testArray);
    Assert.assertEquals(expectedValue(),statistic.getResult(),getTolerance());
    Assert.assertEquals(testArray.length,statistic.getN());
    statistic.clear();
    checkClearValue(statistic);
    Assert.assertEquals(0,statistic.getN());
  }
  protected void checkClearValue(  StorelessUnivariateStatistic statistic){
    Assert.assertTrue(Double.isNaN(statistic.getResult()));
  }
  @Test public void testSerialization(){
    StorelessUnivariateStatistic statistic=(StorelessUnivariateStatistic)getUnivariateStatistic();
    TestUtils.checkSerializedEquality(statistic);
    statistic.clear();
    for (int i=0; i < testArray.length; i++) {
      statistic.increment(testArray[i]);
      if (i % 5 == 0)       statistic=(StorelessUnivariateStatistic)TestUtils.serializeAndRecover(statistic);
    }
    TestUtils.checkSerializedEquality(statistic);
    Assert.assertEquals(expectedValue(),statistic.getResult(),getTolerance());
    statistic.clear();
    checkClearValue(statistic);
  }
  @Test public void testEqualsAndHashCode(){
    StorelessUnivariateStatistic statistic=(StorelessUnivariateStatistic)getUnivariateStatistic();
    StorelessUnivariateStatistic statistic2=null;
    Assert.assertTrue("non-null, compared to null",!statistic.equals(statistic2));
    Assert.assertTrue("reflexive, non-null",statistic.equals(statistic));
    int emptyHash=statistic.hashCode();
    statistic2=(StorelessUnivariateStatistic)getUnivariateStatistic();
    Assert.assertTrue("empty stats should be equal",statistic.equals(statistic2));
    Assert.assertEquals("empty stats should have the same hashcode",emptyHash,statistic2.hashCode());
    statistic.increment(1d);
    Assert.assertTrue("reflexive, non-empty",statistic.equals(statistic));
    Assert.assertTrue("non-empty, compared to empty",!statistic.equals(statistic2));
    Assert.assertTrue("non-empty, compared to empty",!statistic2.equals(statistic));
    Assert.assertTrue("non-empty stat should have different hashcode from empty stat",statistic.hashCode() != emptyHash);
    statistic2.increment(1d);
    Assert.assertTrue("stats with same data should be equal",statistic.equals(statistic2));
    Assert.assertEquals("stats with same data should have the same hashcode",statistic.hashCode(),statistic2.hashCode());
    statistic.increment(Double.POSITIVE_INFINITY);
    Assert.assertTrue("stats with different n's should not be equal",!statistic2.equals(statistic));
    Assert.assertTrue("stats with different n's should have different hashcodes",statistic.hashCode() != statistic2.hashCode());
    statistic2.increment(Double.POSITIVE_INFINITY);
    Assert.assertTrue("stats with same data should be equal",statistic.equals(statistic2));
    Assert.assertEquals("stats with same data should have the same hashcode",statistic.hashCode(),statistic2.hashCode());
    statistic.clear();
    statistic2.clear();
    Assert.assertTrue("cleared stats should be equal",statistic.equals(statistic2));
    Assert.assertEquals("cleared stats should have thashcode of empty stat",emptyHash,statistic2.hashCode());
    Assert.assertEquals("cleared stats should have thashcode of empty stat",emptyHash,statistic.hashCode());
  }
  @Test public void testMomentSmallSamples(){
    UnivariateStatistic stat=getUnivariateStatistic();
    if (stat instanceof SecondMoment) {
      SecondMoment moment=(SecondMoment)getUnivariateStatistic();
      Assert.assertTrue(Double.isNaN(moment.getResult()));
      moment.increment(1d);
      Assert.assertEquals(0d,moment.getResult(),0);
    }
  }
  /** 
 * Make sure that evaluate(double[]) and inrementAll(double[]),
 * getResult() give same results.
 */
  @Test public void testConsistency(){
    StorelessUnivariateStatistic stat=(StorelessUnivariateStatistic)getUnivariateStatistic();
    stat.incrementAll(testArray);
    Assert.assertEquals(stat.getResult(),stat.evaluate(testArray),getTolerance());
    for (int i=0; i < smallSamples.length; i++) {
      stat.clear();
      for (int j=0; j < smallSamples[i].length; j++) {
        stat.increment(smallSamples[i][j]);
      }
      TestUtils.assertEquals(stat.getResult(),stat.evaluate(smallSamples[i]),getTolerance());
    }
  }
  /** 
 * Verifies that copied statistics remain equal to originals when
 * incremented the same way.
 */
  @Test public void testCopyConsistency(){
    StorelessUnivariateStatistic master=(StorelessUnivariateStatistic)getUnivariateStatistic();
    StorelessUnivariateStatistic replica=null;
    long index=FastMath.round((FastMath.random()) * testArray.length);
    master.incrementAll(testArray,0,(int)index);
    replica=master.copy();
    Assert.assertTrue(replica.equals(master));
    Assert.assertTrue(master.equals(replica));
    master.incrementAll(testArray,(int)index,(int)(testArray.length - index));
    replica.incrementAll(testArray,(int)index,(int)(testArray.length - index));
    Assert.assertTrue(replica.equals(master));
    Assert.assertTrue(master.equals(replica));
  }
  @Test public void testSerial(){
    StorelessUnivariateStatistic s=(StorelessUnivariateStatistic)getUnivariateStatistic();
    Assert.assertEquals(s,TestUtils.serializeAndRecover(s));
  }
}
