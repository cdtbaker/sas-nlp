package edu.umd.cs.piccolox.nodes;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.util.PPaintContext;
/** 
 * Unit test for P3DRect.
 */
public class P3DRectTest extends TestCase {
  public void testClone(){
    final P3DRect rect=new P3DRect(10,10,10,10);
    rect.setPaint(Color.BLUE);
    final P3DRect cloned=(P3DRect)rect.clone();
    assertNotNull(cloned);
    assertEquals(Color.BLUE,cloned.getPaint());
    final BufferedImage img=new BufferedImage(3,2,BufferedImage.TYPE_INT_ARGB);
    cloned.paint(new PPaintContext((Graphics2D)img.getGraphics()));
  }
}
