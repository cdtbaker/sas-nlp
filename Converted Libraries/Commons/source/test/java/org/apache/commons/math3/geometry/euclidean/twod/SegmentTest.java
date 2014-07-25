package org.apache.commons.math3.geometry.euclidean.twod;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class SegmentTest {
  @Test public void testDistance(){
    Vector2D start=new Vector2D(2,2);
    Vector2D end=new Vector2D(-2,-2);
    Segment segment=new Segment(start,end,new Line(start,end));
    Assert.assertEquals(FastMath.sqrt(2),segment.distance(new Vector2D(1,-1)),1.0e-10);
    Assert.assertEquals(FastMath.sin(Math.PI / 4.0),segment.distance(new Vector2D(0,-1)),1.0e-10);
    Assert.assertEquals(FastMath.sqrt(8),segment.distance(new Vector2D(0,4)),1.0e-10);
    Assert.assertEquals(FastMath.sqrt(8),segment.distance(new Vector2D(0,-4)),1.0e-10);
  }
}
