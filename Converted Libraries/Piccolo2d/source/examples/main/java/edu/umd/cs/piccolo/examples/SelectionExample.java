package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PSelectionEventHandler;
/** 
 * This example shows how the selection event handler works. It creates a bunch
 * of objects that can be selected.
 */
public class SelectionExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public SelectionExample(){
    this(null);
  }
  public SelectionExample(  final PCanvas aCanvas){
    super("SelectionExample",false,aCanvas);
  }
  public void initialize(){
    for (int i=0; i < 5; i++) {
      for (int j=0; j < 5; j++) {
        final PNode rect=PPath.createRectangle(i * 60,j * 60,50,50);
        rect.setPaint(Color.blue);
        getCanvas().getLayer().addChild(rect);
      }
    }
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().removeInputEventListener(getCanvas().getZoomEventHandler());
    final PSelectionEventHandler selectionEventHandler=new PSelectionEventHandler(getCanvas().getLayer(),getCanvas().getLayer());
    getCanvas().addInputEventListener(selectionEventHandler);
    getCanvas().getRoot().getDefaultInputManager().setKeyboardFocus(selectionEventHandler);
    PNotificationCenter.defaultCenter().addListener(this,"selectionChanged",PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION,selectionEventHandler);
  }
  public void selectionChanged(  final PNotification notfication){
    System.out.println("selection changed");
  }
  public static void main(  final String[] args){
    new SelectionExample();
  }
}
