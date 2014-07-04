package org.apache.commons.math3.distribution;
import org.apache.commons.math3.random.Well19937a;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;
public class LevyDistributionTest extends RealDistributionAbstractTest {
  @Test public void testParameters(){
    LevyDistribution d=makeDistribution();
    Assert.assertEquals(1.2,d.getLocation(),Precision.EPSILON);
    Assert.assertEquals(0.4,d.getScale(),Precision.EPSILON);
  }
  @Test public void testSupport(){
    LevyDistribution d=makeDistribution();
    Assert.assertEquals(d.getLocation(),d.getSupportLowerBound(),Precision.EPSILON);
    Assert.assertTrue(Double.isInfinite(d.getSupportUpperBound()));
    Assert.assertTrue(d.isSupportConnected());
  }
  public LevyDistribution makeDistribution(){
    return new LevyDistribution(new Well19937a(0xc5a5506bbb17e57al),1.2,0.4);
  }
  public double[] makeCumulativeTestPoints(){
    return new double[]{1.2001,1.21,1.225,1.25,1.3,1.9,3.4,5.6};
  }
  public double[] makeCumulativeTestValues(){
    return new double[]{0,2.53962850749e-10,6.33424836662e-05,0.00467773498105,0.0455002638964,0.449691797969,0.669815357599,0.763024600553};
  }
  public double[] makeDensityTestValues(){
    return new double[]{0,5.20056373765e-07,0.0214128361224,0.413339707082,1.07981933026,0.323749319161,0.0706032550094,0.026122839884};
  }
}
