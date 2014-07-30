package org.apache.commons.math3.distribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for CauchyDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * @version $Id: CauchyDistributionTest.java 1364028 2012-07-21 00:42:49Z erans $
 */
public class CauchyDistributionTest extends RealDistributionAbstractTest {
  protected double defaultTolerance=NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY;
  @Override public void setUp(){
    super.setUp();
    setTolerance(defaultTolerance);
  }
  /** 
 * Creates the default continuous distribution instance to use in tests. 
 */
  @Override public CauchyDistribution makeDistribution(){
    return new CauchyDistribution(1.2,2.1);
  }
  /** 
 * Creates the default cumulative probability distribution test input values 
 */
  @Override public double[] makeCumulativeTestPoints(){
    return new double[]{-667.24856187,-65.6230835029,-25.4830299460,-12.0588781808,-5.26313542807,669.64856187,68.0230835029,27.8830299460,14.4588781808,7.66313542807};
  }
  /** 
 * Creates the default cumulative probability density test expected values 
 */
  @Override public double[] makeCumulativeTestValues(){
    return new double[]{0.001,0.01,0.025,0.05,0.1,0.999,0.990,0.975,0.950,0.900};
  }
  /** 
 * Creates the default probability density test expected values 
 */
  @Override public double[] makeDensityTestValues(){
    return new double[]{1.49599158008e-06,0.000149550440335,0.000933076881878,0.00370933207799,0.0144742330437,1.49599158008e-06,0.000149550440335,0.000933076881878,0.00370933207799,0.0144742330437};
  }
  @Test public void testInverseCumulativeProbabilityExtremes(){
    setInverseCumulativeTestPoints(new double[]{0.0,1.0});
    setInverseCumulativeTestValues(new double[]{Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY});
    verifyInverseCumulativeProbabilities();
  }
  @Test public void testMedian(){
    CauchyDistribution distribution=(CauchyDistribution)getDistribution();
    Assert.assertEquals(1.2,distribution.getMedian(),0.0);
  }
  @Test public void testScale(){
    CauchyDistribution distribution=(CauchyDistribution)getDistribution();
    Assert.assertEquals(2.1,distribution.getScale(),0.0);
  }
  @Test public void testPreconditions(){
    try {
      new CauchyDistribution(0,0);
      Assert.fail("Cannot have zero scale");
    }
 catch (    NotStrictlyPositiveException ex) {
    }
    try {
      new CauchyDistribution(0,-1);
      Assert.fail("Cannot have negative scale");
    }
 catch (    NotStrictlyPositiveException ex) {
    }
  }
  @Test public void testMoments(){
    CauchyDistribution dist;
    dist=new CauchyDistribution(10.2,0.15);
    Assert.assertTrue(Double.isNaN(dist.getNumericalMean()));
    Assert.assertTrue(Double.isNaN(dist.getNumericalVariance()));
    dist=new CauchyDistribution(23.12,2.12);
    Assert.assertTrue(Double.isNaN(dist.getNumericalMean()));
    Assert.assertTrue(Double.isNaN(dist.getNumericalVariance()));
  }
}