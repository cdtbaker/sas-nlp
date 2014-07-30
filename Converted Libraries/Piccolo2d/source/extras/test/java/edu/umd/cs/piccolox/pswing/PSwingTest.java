package edu.umd.cs.piccolox.pswing;
import edu.umd.cs.piccolo.util.PPaintContext;
import junit.framework.TestCase;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
public class PSwingTest extends TestCase {
  public void setUp(){
    RepaintManager.setCurrentManager(new PSwingRepaintManager());
  }
  public void testConstructorFailsOnNullComponent(){
    try {
      new PSwing(null);
    }
 catch (    final NullPointerException e) {
    }
  }
  public void testPSwingRegistersItselfWithComponent(){
    final JPanel panel=new JPanel();
    final PSwing pSwing=new PSwing(panel);
    assertEquals(pSwing,panel.getClientProperty(PSwing.PSWING_PROPERTY));
  }
  public void testGetComponentReturnsValidComponent(){
    final JPanel panel=new JPanel();
    final PSwing pSwing=new PSwing(panel);
    assertEquals(panel,pSwing.getComponent());
  }
  public void testPSwingResizesItselfWhenComponentIsResized(){
    final boolean[] reshaped=new boolean[1];
    final JPanel panel=new JPanel();
    new PSwing(panel){
      public void updateBounds(){
        super.updateBounds();
        reshaped[0]=true;
      }
    }
;
    panel.setSize(100,100);
    assertTrue(reshaped[0]);
  }
  public void testPSwingDelegatesPaintingToItsComponent() throws IOException {
    final JPanel panel=new JPanel();
    final MockPaintingPSwing pSwing=new MockPaintingPSwing(panel);
    panel.setBackground(Color.RED);
    panel.setPreferredSize(new Dimension(100,100));
    final BufferedImage image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics=image.createGraphics();
    PPaintContext paintContext=new PPaintContext(graphics);
    pSwing.paint(paintContext);
    assertEquals(Color.RED.getRGB(),image.getRGB(50,50));
  }
  public void testPSwingWithBufferedPaintingDelegatesPaintingToItsComponent() throws IOException {
    final JPanel panel=new JPanel();
    final MockPaintingPSwing pSwing=new MockPaintingPSwing(panel);
    pSwing.setUseBufferedPainting(true);
    panel.setBackground(Color.RED);
    panel.setPreferredSize(new Dimension(100,100));
    final BufferedImage image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics=image.createGraphics();
    PPaintContext paintContext=new PPaintContext(graphics);
    pSwing.paint(paintContext);
    assertEquals(Color.RED.getRGB(),image.getRGB(50,50));
  }
  public void testHidingPNodeHidesComponent(){
    final JPanel panel=new JPanel();
    final PSwing pSwing=new PSwing(panel);
    pSwing.setVisible(false);
    assertFalse(panel.isVisible());
  }
  public void testAddingSwingComponentToWrappedHierarchyMakesItNotDoubleBuffer(){
    final JPanel panel=new JPanel();
    final PSwing pSwing=new PSwing(panel);
    final JComponent child=new JLabel("Test Component");
    child.setDoubleBuffered(true);
    panel.add(child);
    assertFalse(child.isDoubleBuffered());
  }
  public void assertDelayedSuccess(  String message,  int delay,  Predicate p){
    int remainingTries=delay / 50;
    while (remainingTries > 0) {
      if (p.isTrue()) {
        return;
      }
      remainingTries--;
      try {
        Thread.sleep(50);
      }
 catch (      InterruptedException e) {
      }
    }
    fail(message);
  }
  public void assertDelayedSuccess(  int delay,  Predicate p){
    assertDelayedSuccess("Failed asserting delayed success",delay,p);
  }
private interface Predicate {
    boolean isTrue();
  }
  public void testPaintTooSmallPaintsGreek(){
    final JPanel panel=new JPanel();
    panel.setBounds(0,0,100,100);
    final MockPaintingPSwing pSwing=new MockPaintingPSwing(panel);
    BufferedImage image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics=image.createGraphics();
    graphics.setTransform(AffineTransform.getScaleInstance(0.01,0.01));
    PPaintContext paintContext=new PPaintContext(graphics);
    pSwing.paint(paintContext);
    assertTrue(pSwing.isPaintedGreek());
    assertFalse(pSwing.isPaintedComponent());
  }
  public void testPaintBigPaintsComponent(){
    final JPanel panel=new JPanel();
    panel.setBounds(0,0,100,100);
    final MockPaintingPSwing pSwing=new MockPaintingPSwing(panel);
    BufferedImage image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics=image.createGraphics();
    graphics.setTransform(AffineTransform.getScaleInstance(5,5));
    PPaintContext paintContext=new PPaintContext(graphics);
    pSwing.paint(paintContext);
    assertFalse(pSwing.isPaintedGreek());
    assertTrue(pSwing.isPaintedComponent());
  }
  public void testGreekThresholdIsHonoured(){
    final JPanel panel=new JPanel();
    panel.setBounds(0,0,100,100);
    final MockPaintingPSwing pSwing=new MockPaintingPSwing(panel);
    pSwing.setGreekThreshold(2);
    BufferedImage image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics=image.createGraphics();
    PPaintContext paintContext=new PPaintContext(graphics);
    pSwing.paint(paintContext);
    assertTrue(pSwing.isPaintedGreek());
    assertFalse(pSwing.isPaintedComponent());
  }
  public void testGreekThresholdIsPersisted(){
    final JPanel panel=new JPanel();
    final MockPaintingPSwing pSwing=new MockPaintingPSwing(panel);
    pSwing.setGreekThreshold(2);
    assertEquals(2,pSwing.getGreekThreshold(),Double.MIN_VALUE);
    pSwing.setGreekThreshold(0.5);
    assertEquals(0.5,pSwing.getGreekThreshold(),Double.MIN_VALUE);
  }
  public void testAssertSettingJLabelWidthTooSmallGrowsIt(){
    final JLabel label=new JLabel("Hello");
    PSwingCanvas canvas=new PSwingCanvas();
    canvas.setBounds(0,0,100,100);
    final MockPaintingPSwing swing=new MockPaintingPSwing(label);
    assertDelayedSuccess(500,new Predicate(){
      public boolean isTrue(){
        return label.getMinimumSize().getWidth() != 0;
      }
    }
);
    swing.setWidth(10);
    canvas.getLayer().addChild(swing);
    canvas.doLayout();
    BufferedImage image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics=image.createGraphics();
    PPaintContext paintContext=new PPaintContext(graphics);
    swing.paint(paintContext);
    assertFalse(10 == swing.getWidth());
  }
  public void testAssertSettingJButtonWidthTooSmallGrowsIt(){
    JButton label=new JButton("Hello");
    PSwingCanvas canvas=new PSwingCanvas();
    canvas.setBounds(0,0,100,100);
    MockPaintingPSwing swing=new MockPaintingPSwing(label);
    assertFalse(label.getMinimumSize().getWidth() == 0);
    swing.setWidth(10);
    canvas.getLayer().addChild(swing);
    canvas.doLayout();
    BufferedImage image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics=image.createGraphics();
    PPaintContext paintContext=new PPaintContext(graphics);
    swing.paint(paintContext);
    assertFalse(10 == swing.getWidth());
  }
  public void testPSwingAttachesItselfToItsCanvasWhenAddedToItsSceneGraph(){
    PSwingCanvas canvas1=new PSwingCanvas();
    PSwing label=new PSwing(new JLabel("Hello"));
    assertEquals(0,canvas1.getSwingWrapper().getComponentCount());
    canvas1.getLayer().addChild(label);
    assertEquals(1,canvas1.getSwingWrapper().getComponentCount());
  }
  public void testPSwingRemovesItselfFromItsCanvasWhenRemovedFromScene(){
    PSwingCanvas canvas1=new PSwingCanvas();
    PSwing label=new PSwing(new JLabel("Hello"));
    canvas1.getLayer().addChild(label);
    assertEquals(1,canvas1.getSwingWrapper().getComponentCount());
    label.removeFromParent();
    assertEquals(0,canvas1.getSwingWrapper().getComponentCount());
  }
  public void testPSwingReattachesItselfWhenMovedFromCanvasToCanvas(){
    PSwingCanvas canvas1=new PSwingCanvas();
    PSwingCanvas canvas2=new PSwingCanvas();
    PSwing label=new PSwing(new JLabel("Hello"));
    canvas1.getLayer().addChild(label);
    canvas2.getLayer().addChild(label);
    assertEquals(0,canvas1.getSwingWrapper().getComponentCount());
    assertEquals(1,canvas2.getSwingWrapper().getComponentCount());
  }
  public void testPSwingRegistersWithCanvasThroughoutItsLifeCycle(){
    PSwingCanvas canvas=new PSwingCanvas();
    PSwing label=new PSwing(new JLabel("Hello"));
    canvas.getLayer().addChild(label);
    assertEquals(1,canvas.getSwingWrapper().getComponentCount());
    label.removeFromParent();
    assertEquals(0,canvas.getSwingWrapper().getComponentCount());
    canvas.getLayer().addChild(label);
    assertEquals(1,canvas.getSwingWrapper().getComponentCount());
  }
public class MockPaintingPSwing extends PSwing {
    private boolean paintedGreek;
    private boolean paintedComponent;
    public MockPaintingPSwing(    JComponent component){
      super(component);
    }
    public void paint(    Graphics2D paintContext){
      super.paint(paintContext);
      paintedComponent=true;
    }
    public void paintAsGreek(    Graphics2D paintContext){
      super.paintAsGreek(paintContext);
      paintedGreek=true;
    }
    public boolean isPaintedGreek(){
      return paintedGreek;
    }
    public boolean isPaintedComponent(){
      return paintedComponent;
    }
  }
}