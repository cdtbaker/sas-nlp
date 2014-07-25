package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
import edu.umd.cs.piccolox.nodes.PLens;
/** 
 * This example shows one way to create and use lens's in Piccolo.
 */
public class LensExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public LensExample(){
    this(null);
  }
  public LensExample(  final PCanvas aCanvas){
    super("LensExample",false,aCanvas);
  }
  public void initialize(){
    final PRoot root=getCanvas().getRoot();
    final PCamera camera=getCanvas().getCamera();
    final PLayer mainLayer=getCanvas().getLayer();
    final PLayer sharedLayer=new PLayer();
    final PLayer lensOnlyLayer=new PLayer();
    root.addChild(lensOnlyLayer);
    root.addChild(sharedLayer);
    camera.addLayer(0,sharedLayer);
    final PLens lens=new PLens();
    lens.setBounds(10,10,100,130);
    lens.addLayer(0,lensOnlyLayer);
    lens.addLayer(1,sharedLayer);
    mainLayer.addChild(lens);
    PBoundsHandle.addBoundsHandlesTo(lens);
    final PDragSequenceEventHandler squiggleEventHandler=new PDragSequenceEventHandler(){
      protected PPath squiggle;
      public void startDrag(      final PInputEvent e){
        super.startDrag(e);
        final Point2D p=e.getPosition();
        squiggle=new PPath();
        squiggle.moveTo((float)p.getX(),(float)p.getY());
        e.getCamera().getLayer(0).addChild(squiggle);
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
    lens.getCamera().addInputEventListener(squiggleEventHandler);
    camera.addInputEventListener(squiggleEventHandler);
    squiggleEventHandler.getEventFilter().setMarksAcceptedEventsAsHandled(true);
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().removeInputEventListener(getCanvas().getZoomEventHandler());
    final PNode sharedNode=new PNode(){
      /** 
 */
      private static final long serialVersionUID=1L;
      protected void paint(      final PPaintContext paintContext){
        if (paintContext.getCamera() == lens.getCamera()) {
          final Graphics2D g2=paintContext.getGraphics();
          g2.setPaint(Color.RED);
          g2.fill(getBoundsReference());
        }
 else {
          super.paint(paintContext);
        }
      }
    }
;
    sharedNode.setPaint(Color.GREEN);
    sharedNode.setBounds(0,0,100,200);
    sharedNode.translate(200,200);
    sharedLayer.addChild(sharedNode);
    final PText label=new PText("Move the lens \n (by dragging title bar) over the green rectangle, and it will appear red. press and drag the mouse on the canvas and it will draw squiggles. press and drag the mouse over the lens and drag squiggles that are only visible through the lens.");
    label.setConstrainWidthToTextWidth(false);
    label.setConstrainHeightToTextHeight(false);
    label.setBounds(200,100,200,200);
    sharedLayer.addChild(label);
  }
  public static void main(  final String[] args){
    new LensExample();
  }
}
