package org.apache.commons.math3.distribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for FDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * @version $Id: FDistributionTest.java 1364028 2012-07-21 00:42:49Z erans $
 */
public class FDistributionTest extends RealDistributionAbstractTest {
  /** 
 * Creates the default continuous distribution instance to use in tests. 
 */
  @Override public FDistribution makeDistribution(){
    return new FDistribution(5.0,6.0);
  }
  /** 
 * Creates the default cumulative probability distribution test input values 
 */
  @Override public double[] makeCumulativeTestPoints(){
    return new double[]{0.0346808448626,0.0937009113303,0.143313661184,0.202008445998,0.293728320107,20.8026639595,8.74589525602,5.98756512605,4.38737418741,3.10751166664};
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
    return new double[]{0.0689156576706,0.236735653193,0.364074131941,0.481570789649,0.595880479994,0.000133443915657,0.00286681303403,0.00969192007502,0.0242883861471,0.0605491314658};
  }
  @Override public void setUp(){
    super.setUp();
    setTolerance(1e-9);
  }
  @Test public void testCumulativeProbabilityExtremes(){
    setCumulativeTestPoints(new double[]{-2,0});
    setCumulativeTestValues(new double[]{0,0});
    verifyCumulativeProbabilities();
  }
  @Test public void testInverseCumulativeProbabilityExtremes(){
    setInverseCumulativeTestPoints(new double[]{0,1});
    setInverseCumulativeTestValues(new double[]{0,Double.POSITIVE_INFINITY});
    verifyInverseCumulativeProbabilities();
  }
  @Test public void testDfAccessors(){
    FDistribution dist=(FDistribution)getDistribution();
    Assert.assertEquals(5d,dist.getNumeratorDegreesOfFreedom(),Double.MIN_VALUE);
    Assert.assertEquals(6d,dist.getDenominatorDegreesOfFreedom(),Double.MIN_VALUE);
  }
  @Test public void testPreconditions(){
    try {
      new FDistribution(0,1);
      Assert.fail("Expecting NotStrictlyPositiveException for df = 0");
    }
 catch (    NotStrictlyPositiveException ex) {
    }
    try {
      new FDistribution(1,0);
      Assert.fail("Expecting NotStrictlyPositiveException for df = 0");
    }
 catch (    NotStrictlyPositiveException ex) {
    }
  }
  @Test public void testLargeDegreesOfFreedom(){
    FDistribution fd=new FDistribution(100000,100000);
    double p=fd.cumulativeProbability(.999);
    double x=fd.inverseCumulativeProbability(p);
    Assert.assertEquals(.999,x,1.0e-5);
  }
  @Test public void testSmallDegreesOfFreedom(){
    FDistribution fd=new FDistribution(1,1);
    double p=fd.cumulativeProbability(0.975);
    double x=fd.inverseCumulativeProbability(p);
    Assert.assertEquals(0.975,x,1.0e-5);
    fd=new FDistribution(1,2);
    p=fd.cumulativeProbability(0.975);
    x=fd.inverseCumulativeProbability(p);
    Assert.assertEquals(0.975,x,1.0e-5);
  }
  @Test public void testMoments(){
    final double tol=1e-9;
    FDistribution dist;
    dist=new FDistribution(1,2);
    Assert.assertTrue(Double.isNaN(dist.getNumericalMean()));
    Assert.assertTrue(Double.isNaN(dist.getNumericalVariance()));
    dist=new FDistribution(1,3);
    Assert.assertEquals(dist.getNumericalMean(),3d / (3d - 2d),tol);
    Assert.assertTrue(Double.isNaN(dist.getNumericalVariance()));
    dist=new FDistribution(1,5);
    Assert.assertEquals(dist.getNumericalMean(),5d / (5d - 2d),tol);
    Assert.assertEquals(dist.getNumericalVariance(),(2d * 5d * 5d* 4d) / 9d,tol);
  }
  @Test public void testMath785(){
    try {
      double prob=0.01;
      FDistribution f=new FDistribution(200000,200000);
      double result=f.inverseCumulativeProbability(prob);
      Assert.assertTrue(result < 1.0);
    }
 catch (    Exception e) {
      Assert.fail("Failing to calculate inverse cumulative probability");
    }
  }
}