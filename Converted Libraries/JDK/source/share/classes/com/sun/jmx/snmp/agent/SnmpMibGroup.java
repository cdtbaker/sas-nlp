package com.sun.jmx.snmp.agent;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMibOid;
import com.sun.jmx.snmp.agent.SnmpMibNode;
/** 
 * Represents a node in an SNMP MIB which corresponds to a group.
 * This class allows subnodes to be registered below a group, providing
 * support for nested groups. The subnodes are registered at run time
 * when registering the nested groups in the global MIB OID tree.
 * <P>
 * This class is used by the class generated by <CODE>mibgen</CODE>.
 * You should not need to use this class directly.
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 */
public abstract class SnmpMibGroup extends SnmpMibOid implements Serializable {
  protected Hashtable<Long,Long> subgroups=null;
  /** 
 * Tells whether the given arc identifies a table in this group.
 * @param arc An OID arc.
 * @return <CODE>true</CODE> if `arc' leads to a table.
 */
  public abstract boolean isTable(  long arc);
  /** 
 * Tells whether the given arc identifies a variable (scalar object) in
 * this group.
 * @param arc An OID arc.
 * @return <CODE>true</CODE> if `arc' leads to a variable.
 */
  public abstract boolean isVariable(  long arc);
  /** 
 * Tells whether the given arc identifies a readable scalar object in
 * this group.
 * @param arc An OID arc.
 * @return <CODE>true</CODE> if `arc' leads to a readable variable.
 */
  public abstract boolean isReadable(  long arc);
  /** 
 * Gets the table identified by the given `arc'.
 * @param arc An OID arc.
 * @return The <CODE>SnmpMibTable</CODE> identified by `arc', or
 * <CODE>null</CODE> if `arc' does not identify any table.
 */
  public abstract SnmpMibTable getTable(  long arc);
  /** 
 * Checks whether the given OID arc identifies a variable (scalar
 * object).
 * @exception If the given `arc' does not identify any variable in this
 * group, throws an SnmpStatusException.
 */
  public void validateVarId(  long arc,  Object userData) throws SnmpStatusException {
    if (isVariable(arc) == false)     throw noSuchObjectException;
  }
  /** 
 * Tell whether the given OID arc identifies a sub-tree
 * leading to a nested SNMP sub-group. This method is used internally.
 * You shouldn't need to call it directly.
 * @param arc An OID arc.
 * @return <CODE>true</CODE> if the given OID arc identifies a subtree
 * leading to a nested SNMP sub-group.
 */
  public boolean isNestedArc(  long arc){
    if (subgroups == null)     return false;
    Object obj=subgroups.get(new Long(arc));
    return (obj != null);
  }
  /** 
 * Generic handling of the <CODE>get</CODE> operation.
 * <p>The actual implementation of this method will be generated
 * by mibgen. Usually, this implementation only delegates the
 * job to some other provided runtime class, which knows how to
 * access the MBean. The current toolkit thus provides two
 * implementations:
 * <ul><li>The standard implementation will directly access the
 * MBean through a java reference,</li>
 * <li>The generic implementation will access the MBean through
 * the MBean server.</li>
 * </ul>
 * <p>Both implementations rely upon specific - and distinct, set of
 * mibgen generated methods.
 * <p> You can override this method if you need to implement some
 * specific policies for minimizing the accesses made to some remote
 * underlying resources.
 * <p>
 * @param req   The sub-request that must be handled by this node.
 * @param depth The depth reached in the OID tree.
 * @exception SnmpStatusException An error occurred while accessing
 * the MIB node.
 */
  abstract public void get(  SnmpMibSubRequest req,  int depth) throws SnmpStatusException ;
  /** 
 * Generic handling of the <CODE>set</CODE> operation.
 * <p>The actual implementation of this method will be generated
 * by mibgen. Usually, this implementation only delegates the
 * job to some other provided runtime class, which knows how to
 * access the MBean. The current toolkit thus provides two
 * implementations:
 * <ul><li>The standard implementation will directly access the
 * MBean through a java reference,</li>
 * <li>The generic implementation will access the MBean through
 * the MBean server.</li>
 * </ul>
 * <p>Both implementations rely upon specific - and distinct, set of
 * mibgen generated methods.
 * <p> You can override this method if you need to implement some
 * specific policies for minimizing the accesses made to some remote
 * underlying resources.
 * <p>
 * @param req   The sub-request that must be handled by this node.
 * @param depth The depth reached in the OID tree.
 * @exception SnmpStatusException An error occurred while accessing
 * the MIB node.
 */
  abstract public void set(  SnmpMibSubRequest req,  int depth) throws SnmpStatusException ;
  /** 
 * Generic handling of the <CODE>check</CODE> operation.
 * <p>The actual implementation of this method will be generated
 * by mibgen. Usually, this implementation only delegates the
 * job to some other provided runtime class, which knows how to
 * access the MBean. The current toolkit thus provides two
 * implementations:
 * <ul><li>The standard implementation will directly access the
 * MBean through a java reference,</li>
 * <li>The generic implementation will access the MBean through
 * the MBean server.</li>
 * </ul>
 * <p>Both implementations rely upon specific - and distinct, set of
 * mibgen generated methods.
 * <p> You can override this method if you need to implement some
 * specific policies for minimizing the accesses made to some remote
 * underlying resources, or if you need to implement some consistency
 * checks between the different values provided in the varbind list.
 * <p>
 * @param req   The sub-request that must be handled by this node.
 * @param depth The depth reached in the OID tree.
 * @exception SnmpStatusException An error occurred while accessing
 * the MIB node.
 */
  abstract public void check(  SnmpMibSubRequest req,  int depth) throws SnmpStatusException ;
  public void getRootOid(  Vector result){
    return;
  }
  /** 
 * Register an OID arc that identifies a sub-tree
 * leading to a nested SNMP sub-group. This method is used internally.
 * You shouldn't ever call it directly.
 * @param arc An OID arc.
 */
  void registerNestedArc(  long arc){
    Long obj=new Long(arc);
    if (subgroups == null)     subgroups=new Hashtable<Long,Long>();
    subgroups.put(obj,obj);
  }
  /** 
 * Register an OID arc that identifies a scalar object or a table.
 * This method is used internally. You shouldn't ever call it directly.
 * @param arc An OID arc.
 */
  protected void registerObject(  long arc) throws IllegalAccessException {
    long[] oid=new long[1];
    oid[0]=arc;
    super.registerNode(oid,0,null);
  }
  /** 
 * Register a child node of this node in the OID tree.
 * This method is used internally. You shouldn't ever call it directly.
 * @param oid The oid of the node being registered.
 * @param cursor The position reached in the oid.
 * @param node The node being registered.
 */
  void registerNode(  long[] oid,  int cursor,  SnmpMibNode node) throws IllegalAccessException {
    super.registerNode(oid,cursor,node);
    if (cursor < 0)     return;
    if (cursor >= oid.length)     return;
    registerNestedArc(oid[cursor]);
  }
  void findHandlingNode(  SnmpVarBind varbind,  long[] oid,  int depth,  SnmpRequestTree handlers) throws SnmpStatusException {
    int length=oid.length;
    SnmpMibNode node=null;
    if (handlers == null)     throw new SnmpStatusException(SnmpStatusException.snmpRspGenErr);
    final Object data=handlers.getUserData();
    if (depth >= length) {
      throw new SnmpStatusException(SnmpStatusException.noAccess);
    }
    long arc=oid[depth];
    if (isNestedArc(arc)) {
      super.findHandlingNode(varbind,oid,depth,handlers);
      return;
    }
 else     if (isTable(arc)) {
      SnmpMibTable table=getTable(arc);
      table.findHandlingNode(varbind,oid,depth + 1,handlers);
    }
 else {
      validateVarId(arc,data);
      if (depth + 2 > length)       throw noSuchInstanceException;
      if (depth + 2 < length)       throw noSuchInstanceException;
      if (oid[depth + 1] != 0L)       throw noSuchInstanceException;
      handlers.add(this,depth,varbind);
    }
  }
  long[] findNextHandlingNode(  SnmpVarBind varbind,  long[] oid,  int pos,  int depth,  SnmpRequestTree handlers,  AcmChecker checker) throws SnmpStatusException {
    int length=oid.length;
    SnmpMibNode node=null;
    if (handlers == null)     throw noSuchObjectException;
    final Object data=handlers.getUserData();
    final int pduVersion=handlers.getRequestPduVersion();
    if (pos >= length)     return super.findNextHandlingNode(varbind,oid,pos,depth,handlers,checker);
    long arc=oid[pos];
    long[] result=null;
    try {
      if (isTable(arc)) {
        SnmpMibTable table=getTable(arc);
        checker.add(depth,arc);
        try {
          result=table.findNextHandlingNode(varbind,oid,pos + 1,depth + 1,handlers,checker);
        }
 catch (        SnmpStatusException ex) {
          throw noSuchObjectException;
        }
 finally {
          checker.remove(depth);
        }
        result[depth]=arc;
        return result;
      }
 else       if (isReadable(arc)) {
        if (pos == (length - 1)) {
          result=new long[depth + 2];
          result[depth + 1]=0L;
          result[depth]=arc;
          checker.add(depth,result,depth,2);
          try {
            checker.checkCurrentOid();
          }
 catch (          SnmpStatusException e) {
            throw noSuchObjectException;
          }
 finally {
            checker.remove(depth,2);
          }
          handlers.add(this,depth,varbind);
          return result;
        }
      }
 else       if (isNestedArc(arc)) {
        final SnmpMibNode child=getChild(arc);
        if (child != null) {
          checker.add(depth,arc);
          try {
            result=child.findNextHandlingNode(varbind,oid,pos + 1,depth + 1,handlers,checker);
            result[depth]=arc;
            return result;
          }
  finally {
            checker.remove(depth);
          }
        }
      }
      throw noSuchObjectException;
    }
 catch (    SnmpStatusException e) {
      long[] newOid=new long[1];
      newOid[0]=getNextVarId(arc,data,pduVersion);
      return findNextHandlingNode(varbind,newOid,0,depth,handlers,checker);
    }
  }
}
