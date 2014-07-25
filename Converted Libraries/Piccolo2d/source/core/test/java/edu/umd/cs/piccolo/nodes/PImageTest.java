package edu.umd.cs.piccolo.nodes;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.util.PPaintContext;
/** 
 * Unit test for PImage.
 */
public class PImageTest extends TestCase {
  public void testClone(){
    final PImage srcNode=new PImage(new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB));
    final PImage clonedNode=(PImage)srcNode.clone();
    assertNotNull(clonedNode.getImage());
    assertEquals(srcNode.getImage().getWidth(null),clonedNode.getImage().getWidth(null));
    assertEquals(srcNode.getImage().getHeight(null),clonedNode.getImage().getHeight(null));
    assertEquals(srcNode.getBounds(),clonedNode.getBounds());
  }
  public void testToString(){
    final PImage aNode=new PImage(new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB));
    assertNotNull(aNode.toString());
  }
  public void testToBufferedImageReturnsCopyIfToldTo(){
    final BufferedImage img=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    final BufferedImage copy=PImage.toBufferedImage(img,true);
    assertNotSame(img,copy);
  }
  public void testCanBeCreatedFromFile() throws IOException {
    final BufferedImage img=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    final File imgFile=File.createTempFile("test",".jpeg");
    ImageIO.write(img,"JPEG",imgFile);
    imgFile.deleteOnExit();
    final PImage imageNode=new PImage(imgFile.getAbsolutePath());
    assertNotNull(imageNode.getImage());
    assertEquals(100,imageNode.getImage().getWidth(null));
    assertEquals(100,imageNode.getImage().getHeight(null));
  }
  public void testCanBeCreatedFromUrl() throws IOException {
    final BufferedImage img=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    final File imgFile=File.createTempFile("test",".jpeg");
    imgFile.deleteOnExit();
    ImageIO.write(img,"JPEG",imgFile);
    final PImage imageNode=new PImage(imgFile.toURI().toURL());
    assertEquals(100,imageNode.getImage().getWidth(null));
    assertEquals(100,imageNode.getImage().getHeight(null));
  }
  public void testImageCanBeSetFromFile() throws IOException {
    final BufferedImage img=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    final File imgFile=File.createTempFile("test",".jpeg");
    imgFile.deleteOnExit();
    ImageIO.write(img,"JPEG",imgFile);
    final PImage imageNode=new PImage();
    imageNode.setImage(imgFile.getAbsolutePath());
    assertEquals(100,imageNode.getImage().getWidth(null));
    assertEquals(100,imageNode.getImage().getHeight(null));
  }
  public void testPaintAnEmptyImageNodeDoesNothing(){
    final PImage imageNode=new PImage();
    final BufferedImage img=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    final PPaintContext paintContext=new PPaintContext(img.createGraphics());
    imageNode.paint(paintContext);
  }
}
