package edu.umd.cs.piccolox.swt;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.eclipse.swt.SWT;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.event.PSelectionEventHandler;
/** 
 * Selection event handler modified to use SWT paths instead of normal paths.
 * @version 1.0
 * @author Lance Good
 */
public class PSWTSelectionEventHandler extends PSelectionEventHandler {
  PSWTPath marquee;
  PNode marqueeParent;
  Point2D pressPt;
  Point2D canvasPressPt;
  /** 
 * Creates a selection event handler.
 * @param marqueeParent The node to which the event handler dynamically adds
 * a marquee (temporarily) to represent the area being selected.
 * @param selectableParent The node whose children will be selected by this
 * event handler.
 */
  public PSWTSelectionEventHandler(  final PNode marqueeParent,  final PNode selectableParent){
    super(new PNode(),selectableParent);
    this.marqueeParent=marqueeParent;
  }
  /** 
 * Creates a selection event handler.
 * @param marqueeParent The node to which the event handler dynamically adds
 * a marquee (temporarily) to represent the area being selected.
 * @param selectableParents A list of nodes whose children will be selected
 * by this event handler.
 */
  public PSWTSelectionEventHandler(  final PNode marqueeParent,  final List selectableParents){
    super(new PNode(),selectableParents);
    this.marqueeParent=marqueeParent;
  }
  /** 
 * Modifies the provided node so that it is displayed as selected.
 * @param node node to be decorated
 */
  public void decorateSelectedNode(  final PNode node){
    PSWTBoundsHandle.addBoundsHandlesTo(node);
  }
  /** 
 * Undoes any modifications to the provided node so that it is not displayed as selected.
 * @param node node to be undecorated
 */
  public void undecorateSelectedNode(  final PNode node){
    PSWTBoundsHandle.removeBoundsHandlesFrom(node);
  }
  /** 
 * {@inheritDoc} 
 */
  protected void initializeSelection(  final PInputEvent pie){
    super.initializeSelection(pie);
    pressPt=pie.getPosition();
    canvasPressPt=pie.getCanvasPosition();
  }
  /** 
 * {@inheritDoc} 
 */
  protected void initializeMarquee(  final PInputEvent e){
    super.initializeMarquee(e);
    marquee=new PSWTPath(new Rectangle2D.Float((float)pressPt.getX(),(float)pressPt.getY(),0,0)){
      /** 
 */
      private static final long serialVersionUID=1L;
      protected void paint(      final PPaintContext paintContext){
        final SWTGraphics2D s2g=(SWTGraphics2D)paintContext.getGraphics();
        s2g.gc.setLineStyle(SWT.LINE_DASH);
        super.paint(paintContext);
        s2g.gc.setLineStyle(SWT.LINE_SOLID);
      }
    }
;
    marquee.setStrokeColor(Color.black);
    marquee.setPaint(null);
    marqueeParent.addChild(marquee);
  }
  /** 
 * {@inheritDoc} 
 */
  protected void updateMarquee(  final PInputEvent pie){
    super.updateMarquee(pie);
    final PBounds b=new PBounds();
    if (marqueeParent instanceof PCamera) {
      b.add(canvasPressPt);
      b.add(pie.getCanvasPosition());
    }
 else {
      b.add(pressPt);
      b.add(pie.getPosition());
    }
    marquee.setPathToRectangle((float)b.x,(float)b.y,(float)b.width,(float)b.height);
    b.reset();
    b.add(pressPt);
    b.add(pie.getPosition());
  }
  /** 
 * {@inheritDoc} 
 */
  protected PBounds getMarqueeBounds(){
    if (marquee != null) {
      return marquee.getBounds();
    }
    return new PBounds();
  }
  /** 
 * {@inheritDoc} 
 */
  protected void endMarqueeSelection(  final PInputEvent e){
    super.endMarqueeSelection(e);
    marquee.removeFromParent();
    marquee=null;
  }
  /** 
 * {@inheritDoc} 
 */
  protected void dragActivityStep(  final PInputEvent aEvent){
  }
}
