package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.util.PNodeLocator;
/** 
 * This example show how to add the default handles to a node, and also how to
 * create your own custom handles.
 */
public class HandleExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public HandleExample(){
    this(null);
  }
  public HandleExample(  final PCanvas aCanvas){
    super("HandleExample",false,aCanvas);
  }
  public void initialize(){
    final PPath n=PPath.createRectangle(0,0,100,80);
    getCanvas().getLayer().addChild(PPath.createRectangle(0,0,100,80));
    getCanvas().getLayer().addChild(n);
    PBoundsHandle.addBoundsHandlesTo(n);
    n.setStroke(new BasicStroke(10));
    n.setPaint(Color.green);
    final PHandle h=new PHandle(new PNodeLocator(n)){
      /** 
 */
      private static final long serialVersionUID=1L;
      public void dragHandle(      final PDimension aLocalDimension,      final PInputEvent aEvent){
        localToParent(aLocalDimension);
        getParent().translate(aLocalDimension.getWidth(),aLocalDimension.getHeight());
      }
    }
;
    h.addInputEventListener(new PBasicInputEventHandler(){
      public void mousePressed(      final PInputEvent aEvent){
        h.setPaint(Color.YELLOW);
      }
      public void mouseReleased(      final PInputEvent aEvent){
        h.setPaint(Color.RED);
      }
    }
);
    h.setPaint(Color.RED);
    h.setBounds(-10,-10,20,20);
    n.addChild(h);
  }
  public static void main(  final String[] args){
    new HandleExample();
  }
}
