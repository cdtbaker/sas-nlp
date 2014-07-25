package edu.umd.cs.piccolox.nodes;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;
/** 
 * <b>PClip</b> is a simple node that applies a clip before rendering or picking
 * its children. PClip is a subclass of PPath, the clip applies is the
 * GeneralPath wrapped by its super class. See piccolo2d/examples ClipExample.
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PClip extends PPath {
  private static final long serialVersionUID=1L;
  /** 
 * Computes the full bounds and stores them in dstBounds, if dstBounds is
 * null, create a new Bounds and returns it.
 * @param dstBounds output parameter where computed bounds will be stored
 * @return the computed full bounds
 */
  public PBounds computeFullBounds(  final PBounds dstBounds){
    final PBounds result;
    if (dstBounds == null) {
      result=new PBounds();
    }
 else {
      result=dstBounds;
      result.reset();
    }
    result.add(getBoundsReference());
    localToParent(result);
    return result;
  }
  /** 
 * Callback that receives notification of repaint requests from nodes in
 * this node's tree.
 * @param localBounds region in local coordinations the needs repainting
 * @param childOrThis the node that emitted the repaint notification
 */
  public void repaintFrom(  final PBounds localBounds,  final PNode childOrThis){
    if (childOrThis != this) {
      Rectangle2D.intersect(getBoundsReference(),localBounds,localBounds);
      super.repaintFrom(localBounds,childOrThis);
    }
 else {
      super.repaintFrom(localBounds,childOrThis);
    }
  }
  /** 
 * Paint's this node as a solid rectangle if paint is provided, clipping
 * appropriately.
 * @param paintContext context into which this node will be painted
 */
  protected void paint(  final PPaintContext paintContext){
    final Paint p=getPaint();
    if (p != null) {
      final Graphics2D g2=paintContext.getGraphics();
      g2.setPaint(p);
      g2.fill(getPathReference());
    }
    paintContext.pushClip(getPathReference());
  }
  /** 
 * Paints a border around this node if it has a stroke and stroke paint
 * provided.
 * @param paintContext context into which the border will be drawn
 */
  protected void paintAfterChildren(  final PPaintContext paintContext){
    paintContext.popClip(getPathReference());
    if (getStroke() != null && getStrokePaint() != null) {
      final Graphics2D g2=paintContext.getGraphics();
      g2.setPaint(getStrokePaint());
      g2.setStroke(getStroke());
      g2.draw(getPathReference());
    }
  }
  /** 
 * Try to pick this node and all of its descendants if they are visible in
 * the clipping region.
 * @param pickPath the pick path to add the node to if its picked
 * @return true if this node or one of its descendants was picked.
 */
  public boolean fullPick(  final PPickPath pickPath){
    if (getPickable() && fullIntersects(pickPath.getPickBounds())) {
      pickPath.pushNode(this);
      pickPath.pushTransform(getTransformReference(false));
      if (pick(pickPath)) {
        return true;
      }
      if (getChildrenPickable() && getPathReference().intersects(pickPath.getPickBounds())) {
        final int count=getChildrenCount();
        for (int i=count - 1; i >= 0; i--) {
          final PNode each=getChild(i);
          if (each.fullPick(pickPath)) {
            return true;
          }
        }
      }
      if (pickAfterChildren(pickPath)) {
        return true;
      }
      pickPath.popTransform(getTransformReference(false));
      pickPath.popNode(this);
    }
    return false;
  }
}
