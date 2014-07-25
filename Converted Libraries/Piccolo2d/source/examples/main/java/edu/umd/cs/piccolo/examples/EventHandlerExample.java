package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example shows how to create and install a custom event listener that
 * draws rectangles.
 */
public class EventHandlerExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public EventHandlerExample(){
    this(null);
  }
  public EventHandlerExample(  final PCanvas aCanvas){
    super("EventHandlerExample",false,aCanvas);
  }
  public void initialize(){
    super.initialize();
    final PBasicInputEventHandler rectEventHandler=createRectangleEventHandler();
    rectEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().addInputEventListener(rectEventHandler);
  }
  public PBasicInputEventHandler createRectangleEventHandler(){
    return new PBasicInputEventHandler(){
      protected PPath rectangle;
      protected Point2D pressPoint;
      protected Point2D dragPoint;
      public void mousePressed(      final PInputEvent e){
        super.mousePressed(e);
        final PLayer layer=getCanvas().getLayer();
        pressPoint=e.getPosition();
        dragPoint=pressPoint;
        rectangle=new PPath();
        rectangle.setStroke(new BasicStroke((float)(1 / e.getCamera().getViewScale())));
        layer.addChild(rectangle);
        updateRectangle();
      }
      public void mouseDragged(      final PInputEvent e){
        super.mouseDragged(e);
        dragPoint=e.getPosition();
        updateRectangle();
      }
      public void mouseReleased(      final PInputEvent e){
        super.mouseReleased(e);
        updateRectangle();
        rectangle=null;
      }
      public void updateRectangle(){
        final PBounds b=new PBounds();
        b.add(pressPoint);
        b.add(dragPoint);
        rectangle.setPathTo(b);
      }
    }
;
  }
  public static void main(  final String[] args){
    new EventHandlerExample();
  }
}
