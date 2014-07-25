package org.apache.commons.math3.random;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class UnitSphereRandomVectorGeneratorTest {
  /** 
 * Test the distribution of points from {@link UnitSphereRandomVectorGenerator#nextVector()}in two dimensions.
 */
  @Test public void test2DDistribution(){
    RandomGenerator rg=new JDKRandomGenerator();
    rg.setSeed(17399225432l);
    UnitSphereRandomVectorGenerator generator=new UnitSphereRandomVectorGenerator(2,rg);
    int[] angleBuckets=new int[100];
    int steps=1000000;
    for (int i=0; i < steps; ++i) {
      final double[] v=generator.nextVector();
      Assert.assertEquals(2,v.length);
      Assert.assertEquals(1,length(v),1e-10);
      final double angle=FastMath.acos(v[0]);
      final int bucket=(int)(angleBuckets.length * (angle / FastMath.PI));
      ++angleBuckets[bucket];
    }
    final int expectedBucketSize=steps / angleBuckets.length;
    for (    int bucket : angleBuckets) {
      Assert.assertTrue("Bucket count " + bucket + " vs expected "+ expectedBucketSize,FastMath.abs(expectedBucketSize - bucket) < 350);
    }
  }
  /** 
 * @return length (L2 norm) of given vector
 */
  private static double length(  double[] vector){
    double total=0;
    for (    double d : vector) {
      total+=d * d;
    }
    return FastMath.sqrt(total);
  }
}
