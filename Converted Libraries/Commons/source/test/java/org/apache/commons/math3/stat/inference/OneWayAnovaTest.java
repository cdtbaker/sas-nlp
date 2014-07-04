package org.apache.commons.math3.stat.inference;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the OneWayAnovaImpl class.
 * @version $Id: OneWayAnovaTest.java 1456958 2013-03-15 13:55:27Z luc $
 */
public class OneWayAnovaTest {
  protected OneWayAnova testStatistic=new OneWayAnova();
  private double[] emptyArray={};
  private double[] classA={93.0,103.0,95.0,101.0,91.0,105.0,96.0,94.0,101.0};
  private double[] classB={99.0,92.0,102.0,100.0,102.0,89.0};
  private double[] classC={110.0,115.0,111.0,117.0,128.0,117.0};
  @Test public void testAnovaFValue(){
    List<double[]> threeClasses=new ArrayList<double[]>();
    threeClasses.add(classA);
    threeClasses.add(classB);
    threeClasses.add(classC);
    Assert.assertEquals("ANOVA F-value",24.67361709460624,testStatistic.anovaFValue(threeClasses),1E-12);
    List<double[]> twoClasses=new ArrayList<double[]>();
    twoClasses.add(classA);
    twoClasses.add(classB);
    Assert.assertEquals("ANOVA F-value",0.0150579150579,testStatistic.anovaFValue(twoClasses),1E-12);
    List<double[]> emptyContents=new ArrayList<double[]>();
    emptyContents.add(emptyArray);
    emptyContents.add(classC);
    try {
      testStatistic.anovaFValue(emptyContents);
      Assert.fail("empty array for key classX, MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    List<double[]> tooFew=new ArrayList<double[]>();
    tooFew.add(classA);
    try {
      testStatistic.anovaFValue(tooFew);
      Assert.fail("less than two classes, MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test public void testAnovaPValue(){
    List<double[]> threeClasses=new ArrayList<double[]>();
    threeClasses.add(classA);
    threeClasses.add(classB);
    threeClasses.add(classC);
    Assert.assertEquals("ANOVA P-value",6.959446E-06,testStatistic.anovaPValue(threeClasses),1E-12);
    List<double[]> twoClasses=new ArrayList<double[]>();
    twoClasses.add(classA);
    twoClasses.add(classB);
    Assert.assertEquals("ANOVA P-value",0.904212960464,testStatistic.anovaPValue(twoClasses),1E-12);
  }
  @Test public void testAnovaPValueSummaryStatistics(){
    List<SummaryStatistics> threeClasses=new ArrayList<SummaryStatistics>();
    SummaryStatistics statsA=new SummaryStatistics();
    for (    double a : classA) {
      statsA.addValue(a);
    }
    threeClasses.add(statsA);
    SummaryStatistics statsB=new SummaryStatistics();
    for (    double b : classB) {
      statsB.addValue(b);
    }
    threeClasses.add(statsB);
    SummaryStatistics statsC=new SummaryStatistics();
    for (    double c : classC) {
      statsC.addValue(c);
    }
    threeClasses.add(statsC);
    Assert.assertEquals("ANOVA P-value",6.959446E-06,testStatistic.anovaPValue(threeClasses,true),1E-12);
    List<SummaryStatistics> twoClasses=new ArrayList<SummaryStatistics>();
    twoClasses.add(statsA);
    twoClasses.add(statsB);
    Assert.assertEquals("ANOVA P-value",0.904212960464,testStatistic.anovaPValue(twoClasses,false),1E-12);
  }
  @Test public void testAnovaTest(){
    List<double[]> threeClasses=new ArrayList<double[]>();
    threeClasses.add(classA);
    threeClasses.add(classB);
    threeClasses.add(classC);
    Assert.assertTrue("ANOVA Test P<0.01",testStatistic.anovaTest(threeClasses,0.01));
    List<double[]> twoClasses=new ArrayList<double[]>();
    twoClasses.add(classA);
    twoClasses.add(classB);
    Assert.assertFalse("ANOVA Test P>0.01",testStatistic.anovaTest(twoClasses,0.01));
  }
}
