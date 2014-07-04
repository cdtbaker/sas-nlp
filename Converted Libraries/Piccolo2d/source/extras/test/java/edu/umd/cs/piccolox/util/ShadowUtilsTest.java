package edu.umd.cs.piccolox.util;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import edu.umd.cs.piccolox.nodes.PShadow;
import junit.framework.TestCase;
/** 
 * Unit test for ShadowUtils.
 */
public final class ShadowUtilsTest extends TestCase {
  private static final int TEST_IMAGE_SIZE=25;
  private static final Paint shadowPaint=new Color(20,20,20,200);
  private BufferedImage src;
  public void setUp(){
    src=new BufferedImage(TEST_IMAGE_SIZE,TEST_IMAGE_SIZE,BufferedImage.TYPE_INT_ARGB);
    Paint srcPaint=new Color(255,0,0,200);
    Graphics2D g=src.createGraphics();
    g.setPaint(srcPaint);
    g.drawRect(25,25,50,50);
    g.dispose();
  }
  public void testCreateShadowAcceptsTinyShadow(){
    BufferedImage dest=ShadowUtils.createShadow(src,shadowPaint,1);
    assertNotNull(dest);
    assertEquals(TEST_IMAGE_SIZE + 4,dest.getWidth());
    assertEquals(TEST_IMAGE_SIZE + 4,dest.getHeight());
  }
  public void testCreateShadowAcceptsHugeShadow(){
    BufferedImage dest=ShadowUtils.createShadow(src,shadowPaint,25);
    assertNotNull(dest);
    assertEquals(TEST_IMAGE_SIZE + 100,dest.getWidth());
    assertEquals(TEST_IMAGE_SIZE + 100,dest.getHeight());
  }
  public void testNonPositiveBlurRadiusFails(){
    try {
      ShadowUtils.createShadow(src,shadowPaint,0);
      fail("Non positive blur radius should fail");
    }
 catch (    IllegalArgumentException e) {
    }
    try {
      ShadowUtils.createShadow(src,shadowPaint,-1);
      fail("Non positive blur radius should fail");
    }
 catch (    IllegalArgumentException e) {
    }
  }
  public void testConstructorDoesNotAcceptNullSrc(){
    try {
      ShadowUtils.createShadow(null,Color.BLACK,4);
      fail("ctr(null, ...) expected IllegalArgumentException");
    }
 catch (    IllegalArgumentException e) {
    }
  }
}
