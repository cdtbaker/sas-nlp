package edu.umd.cs.piccolo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;
/** 
 * Unit test for POffscreenCanvas.
 */
public class POffscreenCanvasTest extends TestCase {
  public void testConstructor(){
    final POffscreenCanvas canvas0=new POffscreenCanvas(100,100);
    assertNotNull(canvas0);
    final POffscreenCanvas canvas1=new POffscreenCanvas(0,0);
    assertNotNull(canvas1);
    final POffscreenCanvas canvas2=new POffscreenCanvas(0,100);
    assertNotNull(canvas2);
    final POffscreenCanvas canvas3=new POffscreenCanvas(100,0);
    assertNotNull(canvas3);
    try {
      new POffscreenCanvas(-1,100);
      fail("ctr(-1, 100) expected IllegalArgumentException");
    }
 catch (    final IllegalArgumentException e) {
    }
    try {
      new POffscreenCanvas(100,-1);
      fail("ctr(100, -1) expected IllegalArgumentException");
    }
 catch (    final IllegalArgumentException e) {
    }
    try {
      new POffscreenCanvas(-1,-1);
      fail("ctr(-1, -1) expected IllegalArgumentException");
    }
 catch (    final IllegalArgumentException e) {
    }
  }
  public void testCamera(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    assertNotNull(canvas);
    final PCamera camera=canvas.getCamera();
    assertNotNull(camera);
    assertEquals(canvas,camera.getComponent());
    final PCamera camera1=new PCamera();
    canvas.setCamera(camera1);
    assertEquals(camera1,canvas.getCamera());
    assertEquals(null,camera.getComponent());
    assertEquals(canvas,camera1.getComponent());
    canvas.setCamera(null);
    assertEquals(null,camera1.getComponent());
    assertEquals(null,canvas.getCamera());
  }
  public void testRenderEmpty(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    final BufferedImage image=new BufferedImage(100,200,BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics=image.createGraphics();
    canvas.render(graphics);
    for (int x=0; x < 100; x++) {
      for (int y=0; y < 200; y++) {
        assertEquals(0,image.getRGB(x,y));
      }
    }
  }
  public void testRenderFull(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    final PPath rect=PPath.createRectangle(0.0f,0.0f,200.0f,300.0f);
    rect.setPaint(new Color(255,0,0));
    rect.setStroke(null);
    rect.offset(-100.0d,-100.0d);
    canvas.getCamera().getLayer(0).addChild(rect);
    final BufferedImage image=new BufferedImage(100,200,BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics=image.createGraphics();
    canvas.render(graphics);
    for (int x=0; x < 100; x++) {
      for (int y=0; y < 200; y++) {
        assertEquals(-65536,image.getRGB(x,y));
      }
    }
  }
  public void testRenderNull(){
    try {
      final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
      canvas.render(null);
      fail("render(null) expected IllegalArgumentException");
    }
 catch (    final IllegalArgumentException e) {
    }
  }
  public void testRenderQuality(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    assertEquals(POffscreenCanvas.DEFAULT_RENDER_QUALITY,canvas.getRenderQuality());
    canvas.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
    assertEquals(PPaintContext.HIGH_QUALITY_RENDERING,canvas.getRenderQuality());
    canvas.setRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
    assertEquals(PPaintContext.LOW_QUALITY_RENDERING,canvas.getRenderQuality());
    try {
      canvas.setRenderQuality(-1);
    }
 catch (    final IllegalArgumentException e) {
    }
  }
  public void testPaintImmediately(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    canvas.paintImmediately();
  }
  public void testPopCursor(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    canvas.popCursor();
  }
  public void testPushCursor(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    canvas.pushCursor(null);
    canvas.pushCursor(Cursor.getDefaultCursor());
  }
  public void testInteracting(){
    final POffscreenCanvas canvas=new POffscreenCanvas(100,200);
    canvas.setInteracting(true);
    canvas.setInteracting(false);
  }
}
