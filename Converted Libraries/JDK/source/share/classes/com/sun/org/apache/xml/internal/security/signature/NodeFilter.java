package com.sun.org.apache.xml.internal.security.signature;
import org.w3c.dom.Node;
/** 
 * An interface to tell to the c14n if a node is included or not in the output
 * @author raul
 */
public interface NodeFilter {
  /** 
 * Tells if a node must be outputed in c14n.
 * @param n
 * @return 1 if the node should be outputed.
 * 0 if node must not be outputed,
 * -1 if the node and all it's child must not be output.
 */
  public int isNodeInclude(  Node n);
  /** 
 * Tells if a node must be outputed in a c14n.
 * The caller must assured that this method is always call
 * in document order. The implementations can use this
 * restriction to optimize the transformation.
 * @param n
 * @param level the relative level in the tree
 * @return 1 if the node should be outputed.
 * 0 if node must not be outputed,
 * -1 if the node and all it's child must not be output.
 */
  public int isNodeIncludeDO(  Node n,  int level);
}
