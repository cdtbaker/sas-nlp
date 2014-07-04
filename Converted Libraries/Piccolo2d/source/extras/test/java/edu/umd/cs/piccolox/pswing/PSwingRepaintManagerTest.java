package edu.umd.cs.piccolox.pswing;
import java.awt.Canvas;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import junit.framework.TestCase;
/** 
 * Unit test for PSwingRepaintManager.
 */
public class PSwingRepaintManagerTest extends TestCase {
  public void testConstructor(){
    final PSwingRepaintManager repaintManager=new PSwingRepaintManager();
    assertNotNull(repaintManager);
  }
  public void testCurrentManager(){
    RepaintManager currentManager=RepaintManager.currentManager(null);
    assertNotNull(currentManager);
    final Component awtComponent=new Canvas();
    currentManager=RepaintManager.currentManager(awtComponent);
    assertNotNull(currentManager);
    final JComponent swingComponent=new JPanel();
    currentManager=RepaintManager.currentManager(swingComponent);
    assertNotNull(currentManager);
    final PSwingCanvas pswingCanvas=new PSwingCanvas();
    currentManager=RepaintManager.currentManager(pswingCanvas);
    assertNotNull(currentManager);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    currentManager=RepaintManager.currentManager(awtComponent);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    currentManager=RepaintManager.currentManager(swingComponent);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    currentManager=RepaintManager.currentManager(pswingCanvas);
    assertTrue(currentManager instanceof PSwingRepaintManager);
  }
  public void testLockRepaint(){
    final PSwingCanvas canvas=new PSwingCanvas();
    final RepaintManager currentManager=RepaintManager.currentManager(canvas);
    assertNotNull(currentManager);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    final PSwingRepaintManager repaintManager=(PSwingRepaintManager)currentManager;
    repaintManager.lockRepaint(null);
    repaintManager.lockRepaint(canvas);
  }
  public void testUnlockRepaint(){
    final PSwingCanvas canvas=new PSwingCanvas();
    final RepaintManager currentManager=RepaintManager.currentManager(canvas);
    assertNotNull(currentManager);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    final PSwingRepaintManager repaintManager=(PSwingRepaintManager)currentManager;
    repaintManager.lockRepaint(null);
    repaintManager.lockRepaint(canvas);
    repaintManager.unlockRepaint(null);
    repaintManager.unlockRepaint(canvas);
    final JComponent notLocked=new JPanel();
    try {
      repaintManager.unlockRepaint(notLocked);
    }
 catch (    final ArrayIndexOutOfBoundsException e) {
    }
  }
  public void testIsPainting(){
    final PSwingCanvas canvas=new PSwingCanvas();
    final RepaintManager currentManager=RepaintManager.currentManager(canvas);
    assertNotNull(currentManager);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    final PSwingRepaintManager repaintManager=(PSwingRepaintManager)currentManager;
    repaintManager.lockRepaint(null);
    repaintManager.lockRepaint(canvas);
    final JComponent notLocked=new JPanel();
    assertTrue(repaintManager.isPainting(null));
    assertTrue(repaintManager.isPainting(canvas));
    assertFalse(repaintManager.isPainting(notLocked));
  }
  public void testAddDirtyRegion(){
    final PSwingCanvas canvas=new PSwingCanvas();
    final RepaintManager currentManager=RepaintManager.currentManager(canvas);
    assertNotNull(currentManager);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    final PSwingRepaintManager repaintManager=(PSwingRepaintManager)currentManager;
    repaintManager.addDirtyRegion(canvas,0,0,canvas.getWidth(),canvas.getHeight());
    final JComponent child=new JPanel();
    canvas.add(child);
    repaintManager.addDirtyRegion(child,0,0,child.getWidth(),child.getHeight());
  }
  public void testAddInvalidComponent(){
    final PSwingCanvas canvas=new PSwingCanvas();
    final RepaintManager currentManager=RepaintManager.currentManager(canvas);
    assertNotNull(currentManager);
    assertTrue(currentManager instanceof PSwingRepaintManager);
    final PSwingRepaintManager repaintManager=(PSwingRepaintManager)currentManager;
    try {
      repaintManager.addInvalidComponent(null);
    }
 catch (    final NullPointerException e) {
    }
    final JComponent component=new JPanel();
    final JComponent child=new JPanel();
    canvas.add(child);
    repaintManager.addInvalidComponent(canvas);
    repaintManager.addInvalidComponent(component);
    repaintManager.addInvalidComponent(child);
  }
}
