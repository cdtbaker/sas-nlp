package edu.umd.cs.piccolox.pswing;
import javax.swing.JLabel;
import junit.framework.TestCase;
/** 
 * Unit test for PSwingCanvas.
 */
public class PSwingCanvasTest extends TestCase {
  protected int finalizerCallCount;
  public void setUp(){
    finalizerCallCount=0;
  }
  public void testRemovePSwingDoesNothingWithForeignPSwing(){
    final PSwingCanvas canvas=new PSwingCanvas();
    final PSwing orphanPSwing=new PSwing(new JLabel());
    canvas.removePSwing(orphanPSwing);
  }
}
