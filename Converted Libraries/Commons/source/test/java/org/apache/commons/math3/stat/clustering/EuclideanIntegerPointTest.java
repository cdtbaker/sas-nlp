package org.apache.commons.math3.stat.clustering;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class EuclideanIntegerPointTest {
  @Test public void testArrayIsReference(){
    int[] array={-3,-2,-1,0,1};
    Assert.assertTrue(array == new EuclideanIntegerPoint(array).getPoint());
  }
  @Test public void testDistance(){
    EuclideanIntegerPoint e1=new EuclideanIntegerPoint(new int[]{-3,-2,-1,0,1});
    EuclideanIntegerPoint e2=new EuclideanIntegerPoint(new int[]{1,0,-1,1,1});
    Assert.assertEquals(FastMath.sqrt(21.0),e1.distanceFrom(e2),1.0e-15);
    Assert.assertEquals(0.0,e1.distanceFrom(e1),1.0e-15);
    Assert.assertEquals(0.0,e2.distanceFrom(e2),1.0e-15);
  }
  @Test public void testCentroid(){
    List<EuclideanIntegerPoint> list=new ArrayList<EuclideanIntegerPoint>();
    list.add(new EuclideanIntegerPoint(new int[]{1,3}));
    list.add(new EuclideanIntegerPoint(new int[]{2,2}));
    list.add(new EuclideanIntegerPoint(new int[]{3,3}));
    list.add(new EuclideanIntegerPoint(new int[]{2,4}));
    EuclideanIntegerPoint c=list.get(0).centroidOf(list);
    Assert.assertEquals(2,c.getPoint()[0]);
    Assert.assertEquals(3,c.getPoint()[1]);
  }
  @Test public void testSerial(){
    EuclideanIntegerPoint p=new EuclideanIntegerPoint(new int[]{-3,-2,-1,0,1});
    Assert.assertEquals(p,TestUtils.serializeAndRecover(p));
  }
}
