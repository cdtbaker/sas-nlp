package org.apache.commons.math3.ml.distance;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.math3.util.FastMath;
/** 
 * Tests for {@link EuclideanDistance} class.
 */
public class EuclideanDistanceTest {
  final DistanceMeasure distance=new EuclideanDistance();
  @Test public void testZero(){
    final double[] a={0,1,-2,3.4,5,-6.7,89};
    Assert.assertEquals(0,distance.compute(a,a),0d);
  }
  @Test public void test(){
    final double[] a={1,-2,3,4};
    final double[] b={-5,-6,7,8};
    final double expected=FastMath.sqrt(84);
    Assert.assertEquals(expected,distance.compute(a,b),0d);
    Assert.assertEquals(expected,distance.compute(b,a),0d);
  }
}
