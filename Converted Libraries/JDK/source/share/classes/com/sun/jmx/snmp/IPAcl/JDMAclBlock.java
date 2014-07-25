package com.sun.jmx.snmp.IPAcl;
import java.util.Hashtable;
class JDMAclBlock extends SimpleNode {
  JDMAclBlock(  int id){
    super(id);
  }
  JDMAclBlock(  Parser p,  int id){
    super(p,id);
  }
  public static Node jjtCreate(  int id){
    return new JDMAclBlock(id);
  }
  public static Node jjtCreate(  Parser p,  int id){
    return new JDMAclBlock(p,id);
  }
  /** 
 * Do no need to go through this part of the tree for
 * building TrapEntry.
 */
  public void buildTrapEntries(  Hashtable dest){
  }
  /** 
 * Do no need to go through this part of the tree for
 * building InformEntry.
 */
  public void buildInformEntries(  Hashtable dest){
  }
}
