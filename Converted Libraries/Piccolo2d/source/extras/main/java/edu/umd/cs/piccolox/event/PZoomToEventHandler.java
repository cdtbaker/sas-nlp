package edu.umd.cs.piccolox.event;
import java.awt.event.InputEvent;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.util.PBounds;
/** 
 * <b>PZoomToEventHandler</b> is used to zoom the camera view to the node
 * clicked on with button one.
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PZoomToEventHandler extends PBasicInputEventHandler {
  private static final int ZOOM_SPEED=500;
  /** 
 * Constructs a PZoomToEventHandler that only recognizes BUTTON1 events.
 */
  public PZoomToEventHandler(){
    setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
  }
  /** 
 * Zooms the camera's view to the pressed node when button 1 is pressed.
 * @param event event representing the mouse press
 */
  public void mousePressed(  final PInputEvent event){
    zoomTo(event);
  }
  /** 
 * Zooms the camera to the picked node of the event.
 * @param event Event from which to extract the zoom target
 */
  protected void zoomTo(  final PInputEvent event){
    PBounds zoomToBounds;
    final PNode picked=event.getPickedNode();
    if (picked instanceof PCamera) {
      final PCamera c=(PCamera)picked;
      zoomToBounds=c.getUnionOfLayerFullBounds();
    }
 else {
      zoomToBounds=picked.getGlobalFullBounds();
    }
    event.getCamera().animateViewToCenterBounds(zoomToBounds,true,ZOOM_SPEED);
  }
}
