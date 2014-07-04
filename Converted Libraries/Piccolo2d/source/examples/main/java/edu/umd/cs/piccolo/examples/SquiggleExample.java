package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
public class SquiggleExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  private PLayer layer;
  public SquiggleExample(){
    this(null);
  }
  public SquiggleExample(  final PCanvas aCanvas){
    super("SquiggleExample",false,aCanvas);
  }
  public void initialize(){
    super.initialize();
    final PBasicInputEventHandler squiggleEventHandler=createSquiggleEventHandler();
    squiggleEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().addInputEventListener(squiggleEventHandler);
    layer=getCanvas().getLayer();
  }
  public PBasicInputEventHandler createSquiggleEventHandler(){
    return new PDragSequenceEventHandler(){
      protected PPath squiggle;
      public void startDrag(      final PInputEvent e){
        super.startDrag(e);
        final Point2D p=e.getPosition();
        squiggle=new PPath();
        squiggle.moveTo((float)p.getX(),(float)p.getY());
        squiggle.setStroke(new BasicStroke((float)(1 / e.getCamera().getViewScale())));
        layer.addChild(squiggle);
      }
      public void drag(      final PInputEvent e){
        super.drag(e);
        updateSquiggle(e);
      }
      public void endDrag(      final PInputEvent e){
        super.endDrag(e);
        updateSquiggle(e);
        squiggle=null;
      }
      public void updateSquiggle(      final PInputEvent aEvent){
        final Point2D p=aEvent.getPosition();
        squiggle.lineTo((float)p.getX(),(float)p.getY());
      }
    }
;
  }
  public static void main(  final String[] args){
    new SquiggleExample();
  }
}
