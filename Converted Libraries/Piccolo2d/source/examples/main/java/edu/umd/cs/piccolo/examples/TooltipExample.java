package edu.umd.cs.piccolo.examples;
import java.awt.geom.Point2D;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
/** 
 * Simple example of one way to add tooltips
 * @author jesse
 */
public class TooltipExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public TooltipExample(){
    this(null);
  }
  public TooltipExample(  final PCanvas aCanvas){
    super("TooltipExample",false,aCanvas);
  }
  public void initialize(){
    final PNode n1=PPath.createEllipse(0,0,100,100);
    final PNode n2=PPath.createRectangle(300,200,100,100);
    n1.addAttribute("tooltip","node 1");
    n2.addAttribute("tooltip","node 2");
    getCanvas().getLayer().addChild(n1);
    getCanvas().getLayer().addChild(n2);
    final PCamera camera=getCanvas().getCamera();
    final PText tooltipNode=new PText();
    tooltipNode.setPickable(false);
    camera.addChild(tooltipNode);
    camera.addInputEventListener(new PBasicInputEventHandler(){
      public void mouseMoved(      final PInputEvent event){
        updateToolTip(event);
      }
      public void mouseDragged(      final PInputEvent event){
        updateToolTip(event);
      }
      public void updateToolTip(      final PInputEvent event){
        final PNode n=event.getPickedNode();
        final String tooltipString=(String)n.getAttribute("tooltip");
        final Point2D p=event.getCanvasPosition();
        event.getPath().canvasToLocal(p,camera);
        tooltipNode.setText(tooltipString);
        tooltipNode.setOffset(p.getX() + 8,p.getY() - 8);
      }
    }
);
  }
  public static void main(  final String[] argv){
    new TooltipExample();
  }
}
