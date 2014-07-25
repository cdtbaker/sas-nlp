package org.apache.commons.math3.ml.distance;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Tests for {@link CanberraDistance} class.
 */
public class CanberraDistanceTest {
  final DistanceMeasure distance=new CanberraDistance();
  @Test public void testZero(){
    final double[] a={0,1,-2,3.4,5,-6.7,89};
    Assert.assertEquals(0,distance.compute(a,a),0d);
  }
  @Test public void testZero2(){
    final double[] a={0,0};
    Assert.assertEquals(0,distance.compute(a,a),0d);
  }
  @Test public void test(){
    final double[] a={1,2,3,4,9};
    final double[] b={-5,-6,7,4,3};
    final double expected=2.9;
    Assert.assertEquals(expected,distance.compute(a,b),0d);
    Assert.assertEquals(expected,distance.compute(b,a),0d);
  }
}
