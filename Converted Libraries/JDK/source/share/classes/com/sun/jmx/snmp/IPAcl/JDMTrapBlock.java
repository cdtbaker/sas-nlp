package com.sun.jmx.snmp.IPAcl;
import java.util.Hashtable;
class JDMTrapBlock extends SimpleNode {
  JDMTrapBlock(  int id){
    super(id);
  }
  JDMTrapBlock(  Parser p,  int id){
    super(p,id);
  }
  public static Node jjtCreate(  int id){
    return new JDMTrapBlock(id);
  }
  public static Node jjtCreate(  Parser p,  int id){
    return new JDMTrapBlock(p,id);
  }
  /** 
 * Do no need to go through this part of the tree for
 * building AclEntry.
 */
  public void buildAclEntries(  PrincipalImpl owner,  AclImpl acl){
  }
  /** 
 * Do no need to go through this part of the tree for
 * building InformEntry.
 */
  public void buildInformEntries(  Hashtable dest){
  }
}
