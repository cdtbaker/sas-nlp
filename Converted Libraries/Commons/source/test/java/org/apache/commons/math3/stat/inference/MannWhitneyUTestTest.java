package org.apache.commons.math3.stat.inference;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the MannWhitneyUTestImpl class.
 * @version $Id: MannWhitneyUTestTest.java 1364030 2012-07-21 01:10:04Z erans $
 */
public class MannWhitneyUTestTest {
  protected MannWhitneyUTest testStatistic=new MannWhitneyUTest();
  @Test public void testMannWhitneyUSimple(){
    final double x[]={19,22,16,29,24};
    final double y[]={20,11,17,12};
    Assert.assertEquals(17,testStatistic.mannWhitneyU(x,y),1e-10);
    Assert.assertEquals(0.08641,testStatistic.mannWhitneyUTest(x,y),1e-5);
  }
  @Test public void testMannWhitneyUInputValidation(){
    try {
      testStatistic.mannWhitneyUTest(new double[]{},new double[]{1.0});
      Assert.fail("x does not contain samples (exact), NoDataException expected");
    }
 catch (    NoDataException ex) {
    }
    try {
      testStatistic.mannWhitneyUTest(new double[]{1.0},new double[]{});
      Assert.fail("y does not contain samples (exact), NoDataException expected");
    }
 catch (    NoDataException ex) {
    }
    try {
      testStatistic.mannWhitneyUTest(null,null);
      Assert.fail("x and y is null (exact), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.mannWhitneyUTest(null,null);
      Assert.fail("x and y is null (asymptotic), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.mannWhitneyUTest(null,new double[]{1.0});
      Assert.fail("x is null (exact), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.mannWhitneyUTest(new double[]{1.0},null);
      Assert.fail("y is null (exact), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
  }
  @Test public void testBigDataSet(){
    double[] d1=new double[1500];
    double[] d2=new double[1500];
    for (int i=0; i < 1500; i++) {
      d1[i]=2 * i;
      d2[i]=2 * i + 1;
    }
    double result=testStatistic.mannWhitneyUTest(d1,d2);
    Assert.assertTrue(result > 0.1);
  }
}
