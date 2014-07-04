package org.apache.commons.math3.stat.descriptive;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Tests for AbstractUnivariateStatistic
 * @version $Id: AbstractUnivariateStatisticTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class AbstractUnivariateStatisticTest {
  protected double[] testArray={0,1,2,3,4,5};
  protected double[] testWeightsArray={0.3,0.2,1.3,1.1,1.0,1.8};
  protected double[] testNegativeWeightsArray={-0.3,0.2,-1.3,1.1,1.0,1.8};
  protected double[] nullArray=null;
  protected double[] singletonArray={0};
  protected Mean testStatistic=new Mean();
  @Test public void testTestPositive(){
    for (int j=0; j < 6; j++) {
      for (int i=1; i < (7 - j); i++) {
        Assert.assertTrue(testStatistic.test(testArray,0,i));
      }
    }
    Assert.assertTrue(testStatistic.test(singletonArray,0,1));
    Assert.assertTrue(testStatistic.test(singletonArray,0,0,true));
  }
  @Test public void testTestNegative(){
    Assert.assertFalse(testStatistic.test(singletonArray,0,0));
    Assert.assertFalse(testStatistic.test(testArray,0,0));
    try {
      testStatistic.test(singletonArray,2,1);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      testStatistic.test(testArray,0,7);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      testStatistic.test(testArray,-1,1);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      testStatistic.test(testArray,0,-1);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      testStatistic.test(nullArray,0,1);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      testStatistic.test(testArray,nullArray,0,1);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      testStatistic.test(singletonArray,testWeightsArray,0,1);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      testStatistic.test(testArray,testNegativeWeightsArray,0,6);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
}
