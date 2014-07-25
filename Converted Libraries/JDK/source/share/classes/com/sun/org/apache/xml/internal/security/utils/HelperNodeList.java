package com.sun.org.apache.xml.internal.security.utils;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/** 
 * @author Christian Geuer-Pollmann
 */
public class HelperNodeList implements NodeList {
  /** 
 * Field nodes 
 */
  ArrayList nodes=new ArrayList(20);
  boolean _allNodesMustHaveSameParent=false;
  /** 
 */
  public HelperNodeList(){
    this(false);
  }
  /** 
 * @param allNodesMustHaveSameParent
 */
  public HelperNodeList(  boolean allNodesMustHaveSameParent){
    this._allNodesMustHaveSameParent=allNodesMustHaveSameParent;
  }
  /** 
 * Method item
 * @param index
 * @return node with inde i
 */
  public Node item(  int index){
    return (Node)nodes.get(index);
  }
  /** 
 * Method getLength
 * @return length of the list
 */
  public int getLength(){
    return nodes.size();
  }
  /** 
 * Method appendChild
 * @param node
 * @throws IllegalArgumentException
 */
  public void appendChild(  Node node) throws IllegalArgumentException {
    if (this._allNodesMustHaveSameParent && this.getLength() > 0) {
      if (this.item(0).getParentNode() != node.getParentNode()) {
        throw new IllegalArgumentException("Nodes have not the same Parent");
      }
    }
    nodes.add(node);
  }
  /** 
 * @return the document that contains this nodelist
 */
  public Document getOwnerDocument(){
    if (this.getLength() == 0) {
      return null;
    }
    return XMLUtils.getOwnerDocument(this.item(0));
  }
}
