package com.sun.jndi.dns;
import java.util.Hashtable;
/** 
 * A NameNode represents a node in the DNS namespace.  Each node
 * has a label, which is its name relative to its parent (so the
 * node at Sun.COM has label "Sun").  Each node has a hashtable of
 * children indexed by their labels converted to lower-case.
 * <p> A node may be addressed from another by giving a DnsName
 * consisting of the sequence of labels from one node to the other.
 * <p> Each node also has an <tt>isZoneCut</tt> flag, used to indicate
 * if the node is a zone cut.  A zone cut is a node with an NS record
 * that is contained in one zone, but that actually belongs to a child zone.
 * <p> All access is unsynchronized.
 * @author Scott Seligman
 */
class NameNode {
  private String label;
  private Hashtable children=null;
  private boolean isZoneCut=false;
  private int depth=0;
  NameNode(  String label){
    this.label=label;
  }
  protected NameNode newNameNode(  String label){
    return new NameNode(label);
  }
  String getLabel(){
    return label;
  }
  int depth(){
    return depth;
  }
  boolean isZoneCut(){
    return isZoneCut;
  }
  void setZoneCut(  boolean isZoneCut){
    this.isZoneCut=isZoneCut;
  }
  Hashtable getChildren(){
    return children;
  }
  NameNode get(  String key){
    return (children != null) ? (NameNode)children.get(key) : null;
  }
  NameNode get(  DnsName name,  int idx){
    NameNode node=this;
    for (int i=idx; i < name.size() && node != null; i++) {
      node=node.get(name.getKey(i));
    }
    return node;
  }
  NameNode add(  DnsName name,  int idx){
    NameNode node=this;
    for (int i=idx; i < name.size(); i++) {
      String label=name.get(i);
      String key=name.getKey(i);
      NameNode child=null;
      if (node.children == null) {
        node.children=new Hashtable();
      }
 else {
        child=(NameNode)node.children.get(key);
      }
      if (child == null) {
        child=newNameNode(label);
        child.depth=node.depth + 1;
        node.children.put(key,child);
      }
      node=child;
    }
    return node;
  }
}
