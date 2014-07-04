package org.apache.commons.math3.stat.clustering;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class EuclideanDoublePointTest {
  @Test public void testArrayIsReference(){
    final double[] array={-3.0,-2.0,-1.0,0.0,1.0};
    Assert.assertArrayEquals(array,new EuclideanDoublePoint(array).getPoint(),1.0e-15);
  }
  @Test public void testDistance(){
    final EuclideanDoublePoint e1=new EuclideanDoublePoint(new double[]{-3.0,-2.0,-1.0,0.0,1.0});
    final EuclideanDoublePoint e2=new EuclideanDoublePoint(new double[]{1.0,0.0,-1.0,1.0,1.0});
    Assert.assertEquals(FastMath.sqrt(21.0),e1.distanceFrom(e2),1.0e-15);
    Assert.assertEquals(0.0,e1.distanceFrom(e1),1.0e-15);
    Assert.assertEquals(0.0,e2.distanceFrom(e2),1.0e-15);
  }
  @Test public void testCentroid(){
    final List<EuclideanDoublePoint> list=new ArrayList<EuclideanDoublePoint>();
    list.add(new EuclideanDoublePoint(new double[]{1.0,3.0}));
    list.add(new EuclideanDoublePoint(new double[]{2.0,2.0}));
    list.add(new EuclideanDoublePoint(new double[]{3.0,3.0}));
    list.add(new EuclideanDoublePoint(new double[]{2.0,4.0}));
    final EuclideanDoublePoint c=list.get(0).centroidOf(list);
    Assert.assertEquals(2.0,c.getPoint()[0],1.0e-15);
    Assert.assertEquals(3.0,c.getPoint()[1],1.0e-15);
  }
  @Test public void testSerial(){
    final EuclideanDoublePoint p=new EuclideanDoublePoint(new double[]{-3.0,-2.0,-1.0,0.0,1.0});
    Assert.assertEquals(p,TestUtils.serializeAndRecover(p));
  }
}
