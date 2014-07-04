package org.apache.commons.math3.stat.inference;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the WilcoxonSignedRangTest class.
 * @version $Id: WilcoxonSignedRankTestTest.java 1364030 2012-07-21 01:10:04Z erans $
 */
public class WilcoxonSignedRankTestTest {
  protected WilcoxonSignedRankTest testStatistic=new WilcoxonSignedRankTest();
  @Test public void testWilcoxonSignedRankSimple(){
    final double x[]={1.83,0.50,1.62,2.48,1.68,1.88,1.55,3.06,1.30};
    final double y[]={0.878,0.647,0.598,2.05,1.06,1.29,1.06,3.14,1.29};
    Assert.assertEquals(40,testStatistic.wilcoxonSignedRank(x,y),1e-10);
    Assert.assertEquals(0.03906,testStatistic.wilcoxonSignedRankTest(x,y,true),1e-5);
    Assert.assertEquals(40,testStatistic.wilcoxonSignedRank(x,y),1e-10);
    Assert.assertEquals(0.0329693812,testStatistic.wilcoxonSignedRankTest(x,y,false),1e-10);
  }
  @Test public void testWilcoxonSignedRankInputValidation(){
    final double[] x1=new double[30];
    final double[] x2=new double[31];
    final double[] y1=new double[30];
    final double[] y2=new double[31];
    for (int i=0; i < 30; ++i) {
      x1[i]=x2[i]=y1[i]=y2[i]=i;
    }
    try {
      testStatistic.wilcoxonSignedRankTest(x2,y2,true);
      Assert.fail("More than 30 samples and exact chosen, NumberIsTooLargeException expected");
    }
 catch (    NumberIsTooLargeException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{},new double[]{1.0},true);
      Assert.fail("x does not contain samples (exact), NoDataException expected");
    }
 catch (    NoDataException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{},new double[]{1.0},false);
      Assert.fail("x does not contain samples (asymptotic), NoDataException expected");
    }
 catch (    NoDataException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{1.0},new double[]{},true);
      Assert.fail("y does not contain samples (exact), NoDataException expected");
    }
 catch (    NoDataException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{1.0},new double[]{},false);
      Assert.fail("y does not contain samples (asymptotic), NoDataException expected");
    }
 catch (    NoDataException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{1.0,2.0},new double[]{3.0},true);
      Assert.fail("x and y not same size (exact), DimensionMismatchException expected");
    }
 catch (    DimensionMismatchException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{1.0,2.0},new double[]{3.0},false);
      Assert.fail("x and y not same size (asymptotic), DimensionMismatchException expected");
    }
 catch (    DimensionMismatchException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(null,null,true);
      Assert.fail("x and y is null (exact), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(null,null,false);
      Assert.fail("x and y is null (asymptotic), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(null,new double[]{1.0},true);
      Assert.fail("x is null (exact), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(null,new double[]{1.0},false);
      Assert.fail("x is null (asymptotic), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{1.0},null,true);
      Assert.fail("y is null (exact), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
    try {
      testStatistic.wilcoxonSignedRankTest(new double[]{1.0},null,false);
      Assert.fail("y is null (asymptotic), NullArgumentException expected");
    }
 catch (    NullArgumentException ex) {
    }
  }
}
