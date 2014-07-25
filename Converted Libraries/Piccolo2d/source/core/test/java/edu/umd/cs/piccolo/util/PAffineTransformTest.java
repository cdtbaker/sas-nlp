package edu.umd.cs.piccolo.util;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.PiccoloAsserts;
/** 
 * Unit test for PAffineTransform.
 */
public class PAffineTransformTest extends TestCase {
  private PAffineTransform at;
  public PAffineTransformTest(  final String aName){
    super(aName);
  }
  public void setUp(){
    at=new PAffineTransform();
  }
  public void testRotation(){
    at.rotate(Math.toRadians(45));
    assertEquals(at.getRotation(),Math.toRadians(45),0.000000001);
    at.setRotation(Math.toRadians(90));
    assertEquals(at.getRotation(),Math.toRadians(90),0.000000001);
  }
  public void testScale(){
    at.scaleAboutPoint(0.45,0,1);
    assertEquals(at.getScale(),0.45,0.000000001);
    at.setScale(0.11);
    assertEquals(at.getScale(),0.11,0.000000001);
  }
  public void testTransformRectLeavesEmptyBoundsEmpty(){
    final PBounds b1=new PBounds();
    at.scale(0.5,0.5);
    at.translate(100,50);
    at.transform(b1,b1);
    assertTrue(b1.isEmpty());
  }
  public void testTransformRect(){
    final PBounds b1=new PBounds(0,0,100,80);
    final PBounds b2=new PBounds(100,100,100,80);
    at.scale(0.5,0.5);
    at.translate(100,50);
    at.transform(b1,b1);
    at.transform(b2,b2);
    PiccoloAsserts.assertEquals(new PBounds(50,25,50,40),b1,0.0001);
    PiccoloAsserts.assertEquals(new PBounds(100,75,50,40),b2,0.0001);
    at.inverseTransform(b1,b1);
    at.inverseTransform(b2,b2);
    PiccoloAsserts.assertEquals(new PBounds(0,0,100,80),b1,0.0001);
    PiccoloAsserts.assertEquals(new PBounds(100,100,100,80),b2,0.0001);
  }
  public void testThrowsExceptionWhenSetting0Scale(){
    try {
      at.setScale(0);
      fail("Setting 0 scale should throw exception");
    }
 catch (    final RuntimeException e) {
    }
  }
  public void testSetOffsetLeavesRotationUntouched(){
    at.setRotation(Math.PI);
    at.setOffset(100,50);
    assertEquals(Math.PI,at.getRotation(),0.001);
  }
  public void testTransformDimensionWorks(){
    final Dimension d1=new Dimension(100,50);
    at.setScale(2);
    final Dimension d2=new Dimension(0,0);
    at.transform(d1,d2);
    assertEquals(new Dimension(200,100),d2);
  }
  public void testTransformDimensionWorksWithSecondParamNull(){
    final Dimension d1=new Dimension(100,50);
    at.setScale(2);
    final Dimension2D d2=at.transform(d1,null);
    assertEquals(new Dimension(200,100),d2);
  }
  public void testLocalToViewDimensionThrowsExceptionWhenTransformIsNonInvertible(){
    at.setTransform(new PAffineTransform(new double[]{0,0,0,0,0,0}));
    try {
      at.inverseTransform(new PDimension(1,2),null);
      fail("Exception not thrown when inverting non-invertible transform");
    }
 catch (    final PAffineTransformException e) {
    }
  }
  public void testLocalToViewPoint2DThrowsExceptionWhenTransformIsNonInvertible(){
    at.setTransform(new PAffineTransform(new double[]{0,0,0,0,0,0}));
    try {
      at.inverseTransform(new Point2D.Double(1,2),null);
      fail("Exception not thrown when inverting non-invertible transform");
    }
 catch (    final PAffineTransformException e) {
    }
  }
  public void testLocalToViewRectangle2DThrowsExceptionWhenTransformIsNonInvertible(){
    at.setTransform(new PAffineTransform(new double[]{0,0,0,0,0,0}));
    try {
      at.inverseTransform(new Rectangle2D.Double(1,2,3,4),null);
      fail("Exception not thrown when inverting non-invertible transform");
    }
 catch (    final PAffineTransformException e) {
    }
  }
}
