package edu.umd.cs.piccolox.handles;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPickPath;
/** 
 * This class relays adjustments to its bounds to its target.
 */
public class PStickyHandleManager extends PNode {
  private static final long serialVersionUID=1L;
  private PNode target;
  private PCamera camera;
  /** 
 * Constructs a sticky handle manager responsible for updating the position
 * of its associated node on the camera provided.
 * @param newCamera camera on which this manager is operating
 * @param newTarget node to be positioned on the camera
 */
  public PStickyHandleManager(  final PCamera newCamera,  final PNode newTarget){
    setCameraTarget(newCamera,newTarget);
    PBoundsHandle.addBoundsHandlesTo(this);
  }
  /** 
 * Changes the node and camera on which this manager is operating.
 * @param newCamera camera on which this manager is operating
 * @param newTarget node to be positioned on the camera
 */
  public void setCameraTarget(  final PCamera newCamera,  final PNode newTarget){
    camera=newCamera;
    camera.addChild(this);
    target=newTarget;
  }
  /** 
 * By changing this sticky handle's bounds, it propagates that change to its
 * associated node.
 * @param x x position of bounds
 * @param y y position of bounds
 * @param width width to apply to the bounds
 * @param height height to apply to the bounds
 * @return true if bounds were successfully changed
 */
  public boolean setBounds(  final double x,  final double y,  final double width,  final double height){
    final PBounds b=new PBounds(x,y,width,height);
    camera.localToGlobal(b);
    camera.localToView(b);
    target.globalToLocal(b);
    target.setBounds(b);
    return super.setBounds(x,y,width,height);
  }
  /** 
 * Since this node's bounds are always dependent on its target, it is
 * volatile.
 * @return true since sticky handle manager's bounds are completely
 * dependent on its children
 */
  protected boolean getBoundsVolatile(){
    return true;
  }
  /** 
 * The sticky handle manager's bounds as computed by examining its target
 * through its camera.
 * @return the sticky handle manager's bounds as computed by examining its
 * target through its camera
 */
  public PBounds getBoundsReference(){
    final PBounds targetBounds=target.getFullBounds();
    camera.viewToLocal(targetBounds);
    camera.globalToLocal(targetBounds);
    final PBounds bounds=super.getBoundsReference();
    bounds.setRect(targetBounds);
    return super.getBoundsReference();
  }
  /** 
 * Dispatches this event to its target as well.
 */
  public void startResizeBounds(){
    super.startResizeBounds();
    target.startResizeBounds();
  }
  /** 
 * Dispatches this event to its target as well.
 */
  public void endResizeBounds(){
    super.endResizeBounds();
    target.endResizeBounds();
  }
  /** 
 * Since this node is invisible, it doesn't make sense to have it be
 * pickable.
 * @return false since it's invisible
 * @param pickPath path in which we're trying to determine if this node is
 * pickable
 */
  public boolean pickAfterChildren(  final PPickPath pickPath){
    return false;
  }
}
