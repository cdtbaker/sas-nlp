package edu.umd.cs.piccolox.swt;
import java.awt.geom.Point2D;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.util.PBounds;
/** 
 * Unit test for PSWTPath.
 */
public class PSWTPathTest extends TestCase {
  public void testCenterEmpty(){
    PSWTPath path=new PSWTPath();
    PBounds bounds=path.getBoundsReference();
    assertEquals(0.0d,bounds.getX(),0.1d);
    assertEquals(0.0d,bounds.getY(),0.1d);
    assertEquals(0.0d,bounds.getHeight(),0.1d);
    assertEquals(0.0d,bounds.getWidth(),0.1d);
    Point2D center=path.getCenter();
    assertEquals(0.0d,center.getX(),0.1d);
    assertEquals(0.0d,center.getY(),0.1d);
  }
  public void testCenter(){
    PSWTPath path=PSWTPath.createRectangle(10.0f,20.0f,100.0f,200.0f);
    PBounds bounds=path.getBoundsReference();
    assertEquals(10.0d,bounds.getX(),1.0d);
    assertEquals(20.0d,bounds.getY(),1.0d);
    assertEquals(200.0d,bounds.getHeight(),2.0d);
    assertEquals(100.0d,bounds.getWidth(),2.0d);
    Point2D center=path.getCenter();
    assertEquals(60.0d,center.getX(),0.1d);
    assertEquals(120.0d,center.getY(),0.1d);
  }
  public void testCenterScale(){
    PSWTPath path=PSWTPath.createRectangle(10.0f,20.0f,100.0f,200.0f);
    path.scale(10.0d);
    PBounds bounds=path.getBoundsReference();
    assertEquals(10.0d,bounds.getX(),1.0d);
    assertEquals(20.0d,bounds.getY(),1.0d);
    assertEquals(200.0d,bounds.getHeight(),2.0d);
    assertEquals(100.0d,bounds.getWidth(),2.0d);
    Point2D center=path.getCenter();
    assertEquals(60.0d,center.getX(),0.1d);
    assertEquals(120.0d,center.getY(),0.1d);
  }
  public void testCenterRotate(){
    PSWTPath path=PSWTPath.createRectangle(10.0f,20.0f,100.0f,200.0f);
    path.rotate(Math.PI / 8.0d);
    PBounds bounds=path.getBoundsReference();
    assertEquals(10.0d,bounds.getX(),1.0d);
    assertEquals(20.0d,bounds.getY(),1.0d);
    assertEquals(200.0d,bounds.getHeight(),2.0d);
    assertEquals(100.0d,bounds.getWidth(),2.0d);
    Point2D center=path.getCenter();
    assertEquals(60.0d,center.getX(),0.1d);
    assertEquals(120.0d,center.getY(),0.1d);
  }
}
