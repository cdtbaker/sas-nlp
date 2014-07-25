package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import java.awt.geom.Dimension2D;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example shows how to make a node handle events.
 */
public class NodeEventExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public NodeEventExample(){
    this(null);
  }
  public NodeEventExample(  final PCanvas aCanvas){
    super("NodeEventExample",false,aCanvas);
  }
  public void initialize(){
    final PLayer layer=getCanvas().getLayer();
    final PNode aNode=new PNode();
    aNode.addInputEventListener(new PBasicInputEventHandler(){
      public void mousePressed(      final PInputEvent aEvent){
        aNode.setPaint(Color.orange);
        printEventCoords(aEvent);
        aEvent.setHandled(true);
      }
      public void mouseDragged(      final PInputEvent aEvent){
        final Dimension2D delta=aEvent.getDeltaRelativeTo(aNode);
        aNode.translate(delta.getWidth(),delta.getHeight());
        printEventCoords(aEvent);
        aEvent.setHandled(true);
      }
      public void mouseReleased(      final PInputEvent aEvent){
        aNode.setPaint(Color.green);
        printEventCoords(aEvent);
        aEvent.setHandled(true);
      }
      public void printEventCoords(      final PInputEvent aEvent){
        System.out.println("Canvas Location: " + aEvent.getCanvasPosition());
        System.out.println("Local Location: " + aEvent.getPositionRelativeTo(aNode));
        System.out.println("Canvas Delta: " + aEvent.getCanvasDelta());
        System.out.println("Local Delta: " + aEvent.getDeltaRelativeTo(aNode));
      }
    }
);
    aNode.setBounds(0,0,200,200);
    aNode.setPaint(Color.green);
    layer.addChild(PPath.createRectangle(0,0,100,80));
    layer.addChild(aNode);
  }
  public static void main(  final String[] args){
    new NodeEventExample();
  }
}
