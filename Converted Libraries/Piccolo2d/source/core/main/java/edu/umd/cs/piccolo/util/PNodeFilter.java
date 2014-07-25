package edu.umd.cs.piccolo.util;
import edu.umd.cs.piccolo.PNode;
/** 
 * <b>PNodeFilter</b> is a interface that filters (accepts or rejects) nodes.
 * Its main use is to retrieve all the children of a node the meet some criteria
 * by using the method PNode.getAllNodes(collection, filter);
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public interface PNodeFilter {
  /** 
 * Return true if the filter should accept the given node.
 * @param aNode node under test
 * @return true if node should be accepted
 */
  boolean accept(  PNode aNode);
  /** 
 * Return true if the filter should test the children of the given node for
 * acceptance.
 * @param aNode parent being tested
 * @return true if children should be tested for acceptance
 */
  boolean acceptChildrenOf(  PNode aNode);
}
