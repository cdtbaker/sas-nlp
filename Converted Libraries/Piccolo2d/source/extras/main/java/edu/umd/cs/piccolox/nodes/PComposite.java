package edu.umd.cs.piccolox.nodes;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPickPath;
/** 
 * <b>PComposite</b> is a simple node that makes a group of nodes appear to be a
 * single node when picking and interacting. There is also partial (commented
 * out) support for resizing the child node to fit when this nodes bounds are
 * set.
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PComposite extends PNode {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Return true if this node or any pickable descendants are picked. If a
 * pick occurs the pickPath is modified so that this node is always returned
 * as the picked node, event if it was a descendant node that initially
 * reported the pick.
 * @param pickPath the pick path to add the nodes to if they are picked
 * @return true if this node or one of its descendants was picked
 */
  public boolean fullPick(  final PPickPath pickPath){
    if (super.fullPick(pickPath)) {
      PNode picked=pickPath.getPickedNode();
      while (picked != this) {
        pickPath.popTransform(picked.getTransformReference(false));
        pickPath.popNode(picked);
        picked=pickPath.getPickedNode();
      }
      return true;
    }
    return false;
  }
}
