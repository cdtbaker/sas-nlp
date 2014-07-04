package org.apache.commons.math3.distribution;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for {@link TriangularDistribution}. See class javadoc for{@link RealDistributionAbstractTest} for further details.
 */
public class TriangularDistributionTest extends RealDistributionAbstractTest {
  @Override public void setUp(){
    super.setUp();
    setTolerance(1e-4);
  }
  /** 
 * Creates the default triangular distribution instance to use in tests.
 */
  @Override public TriangularDistribution makeDistribution(){
    return new TriangularDistribution(-3,2,12);
  }
  /** 
 * Creates the default cumulative probability distribution test input
 * values.
 */
  @Override public double[] makeCumulativeTestPoints(){
    return new double[]{-3.0001,-3.0,-2.0,-1.0,0.0,1.0,2.0,3.0,4.0,10.0,11.0,12.0,12.0001};
  }
  /** 
 * Creates the default cumulative probability density test expected values.
 */
  @Override public double[] makeCumulativeTestValues(){
    double third=1 / 3.0;
    double left=18.75;
    double area=56.25;
    return new double[]{0.0,0.0,0.75 / area,3 / area,6.75 / area,12 / area,third,(left + 7.125) / area,(left + 13.5) / area,(left + 36) / area,(left + 37.125) / area,1.0,1.0};
  }
  /** 
 * Creates the default inverse cumulative probability distribution test
 * input values.
 */
  @Override public double[] makeInverseCumulativeTestPoints(){
    double[] points=makeCumulativeTestValues();
    double[] points2=new double[points.length - 2];
    System.arraycopy(points,1,points2,0,points2.length);
    return points2;
  }
  /** 
 * Creates the default inverse cumulative probability density test expected
 * values.
 */
  @Override public double[] makeInverseCumulativeTestValues(){
    double[] points=makeCumulativeTestPoints();
    double[] points2=new double[points.length - 2];
    System.arraycopy(points,1,points2,0,points2.length);
    return points2;
  }
  /** 
 * Creates the default probability density test expected values. 
 */
  @Override public double[] makeDensityTestValues(){
    return new double[]{0,0,2 / 75.0,4 / 75.0,6 / 75.0,8 / 75.0,10 / 75.0,9 / 75.0,8 / 75.0,2 / 75.0,1 / 75.0,0,0};
  }
  /** 
 * Test lower bound getter. 
 */
  @Test public void testGetLowerBound(){
    TriangularDistribution distribution=makeDistribution();
    Assert.assertEquals(-3.0,distribution.getSupportLowerBound(),0);
  }
  /** 
 * Test upper bound getter. 
 */
  @Test public void testGetUpperBound(){
    TriangularDistribution distribution=makeDistribution();
    Assert.assertEquals(12.0,distribution.getSupportUpperBound(),0);
  }
  /** 
 * Test pre-condition for equal lower/upper limit. 
 */
  @Test(expected=NumberIsTooLargeException.class) public void testPreconditions1(){
    new TriangularDistribution(0,0,0);
  }
  /** 
 * Test pre-condition for lower limit larger than upper limit. 
 */
  @Test(expected=NumberIsTooLargeException.class) public void testPreconditions2(){
    new TriangularDistribution(1,1,0);
  }
  /** 
 * Test pre-condition for mode larger than upper limit. 
 */
  @Test(expected=NumberIsTooLargeException.class) public void testPreconditions3(){
    new TriangularDistribution(0,2,1);
  }
  /** 
 * Test pre-condition for mode smaller than lower limit. 
 */
  @Test(expected=NumberIsTooSmallException.class) public void testPreconditions4(){
    new TriangularDistribution(2,1,3);
  }
  /** 
 * Test mean/variance. 
 */
  @Test public void testMeanVariance(){
    TriangularDistribution dist;
    dist=new TriangularDistribution(0,0.5,1.0);
    Assert.assertEquals(dist.getNumericalMean(),0.5,0);
    Assert.assertEquals(dist.getNumericalVariance(),1 / 24.0,0);
    dist=new TriangularDistribution(0,1,1);
    Assert.assertEquals(dist.getNumericalMean(),2 / 3.0,0);
    Assert.assertEquals(dist.getNumericalVariance(),1 / 18.0,0);
    dist=new TriangularDistribution(-3,2,12);
    Assert.assertEquals(dist.getNumericalMean(),3 + (2 / 3.0),0);
    Assert.assertEquals(dist.getNumericalVariance(),175 / 18.0,0);
  }
}
