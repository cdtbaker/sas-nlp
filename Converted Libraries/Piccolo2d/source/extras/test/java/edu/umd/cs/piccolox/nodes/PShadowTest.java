package edu.umd.cs.piccolox.nodes;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import junit.framework.TestCase;
/** 
 * Unit test for PShadow.
 */
public final class PShadowTest extends TestCase {
  private static final int TEST_IMAGE_WIDTH=25;
  private static final int TEST_IMAGE_HEIGHT=10;
  private BufferedImage src;
  private Color shadowPaint;
  public void setUp(){
    shadowPaint=new Color(20,20,20,200);
    src=new BufferedImage(TEST_IMAGE_WIDTH,TEST_IMAGE_HEIGHT,BufferedImage.TYPE_INT_ARGB);
    Paint srcPaint=new Color(255,0,0,200);
    Graphics2D g=src.createGraphics();
    g.setPaint(srcPaint);
    g.drawRect(25,25,50,50);
    g.dispose();
  }
  public void testShadowCreatesCorrectImageSize(){
    PShadow shadowNode=new PShadow(src,shadowPaint,4);
    assertNotNull(shadowNode);
    assertEquals(TEST_IMAGE_WIDTH + 16,shadowNode.getWidth(),0.001d);
    assertEquals(TEST_IMAGE_HEIGHT + 16,shadowNode.getHeight(),0.001d);
  }
  public void testClone(){
    PShadow shadowNode=new PShadow(src,shadowPaint,4);
    PShadow clone=(PShadow)shadowNode.clone();
    assertNotNull(clone);
    assertEquals(shadowNode.getImage().getWidth(null),clone.getImage().getWidth(null));
    assertEquals(shadowNode.getImage().getHeight(null),clone.getImage().getHeight(null));
  }
}
