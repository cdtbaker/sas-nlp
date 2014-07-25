package edu.umd.cs.piccolox.util;
import edu.umd.cs.piccolo.PNode;
/** 
 * <b>PNodeLocator</b> provides an abstraction for locating points on a node.
 * Points are located in the local corrdinate system of the node. The default
 * behavior is to locate the center point of the nodes bounds. The node where
 * the point is located is stored internal to this locator (as an instance
 * varriable). If you want to use the same locator to locate center points on
 * many different nodes you will need to call setNode() before asking for each
 * location.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PNodeLocator extends PLocator {
  private static final long serialVersionUID=1L;
  /** 
 * Node being located by this locator. 
 */
  protected PNode node;
  /** 
 * Constructs a locator responsible for locating the given node.
 * @param node node to be located
 */
  public PNodeLocator(  final PNode node){
    setNode(node);
  }
  /** 
 * Returns the node being located by this locator.
 * @return node being located by this locator
 */
  public PNode getNode(){
    return node;
  }
  /** 
 * Changes the node being located by this locator.
 * @param node new node to have this locator locate.
 */
  public void setNode(  final PNode node){
    this.node=node;
  }
  /** 
 * Locates the left of the target node's bounds.
 * @return left of target node's bounds
 */
  public double locateX(){
    return node.getBoundsReference().getCenterX();
  }
  /** 
 * Locates the top of the target node's bounds.
 * @return top of target node's bounds
 */
  public double locateY(){
    return node.getBoundsReference().getCenterY();
  }
}
