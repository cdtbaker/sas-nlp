package edu.umd.cs.piccolo.examples;
import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
/** 
 * Demonstrates the use of PSwing in a Piccolo application.
 */
public class PSwingExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public PSwingExample(){
    this(new PSwingCanvas());
  }
  public PSwingExample(  final PCanvas aCanvas){
    super("PSwingExample",false,aCanvas);
  }
  public void initialize(){
    final PSwingCanvas pswingCanvas=(PSwingCanvas)getCanvas();
    final PLayer l=pswingCanvas.getLayer();
    final JSlider js=new JSlider(0,100);
    js.addChangeListener(new ChangeListener(){
      public void stateChanged(      final ChangeEvent e){
        System.out.println("e = " + e);
      }
    }
);
    js.setBorder(BorderFactory.createTitledBorder("Test JSlider"));
    final PSwing pSwing=new PSwing(js);
    pSwing.translate(100,100);
    l.addChild(pSwing);
    pswingCanvas.setPanEventHandler(null);
  }
  public static void main(  final String[] args){
    new PSwingExample();
  }
}
