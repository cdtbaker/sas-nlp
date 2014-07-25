package sun.management.snmp.jvminstr;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import sun.management.snmp.jvmmib.JvmMemGCTableMeta;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.JvmContextFactory;
/** 
 * The class is used for implementing the "JvmMemGCTable" table.
 */
public class JvmMemGCTableMetaImpl extends JvmMemGCTableMeta {
  /** 
 * This class acts as a filter over the SnmpTableHandler
 * used for the JvmMemoryManagerTable. It filters out
 * (skip) all MemoryManagerMXBean that are not instances of
 * GarbageCollectorMXBean so that only Garbage Collectors are
 * seen. This is a better solution than relying on
 * ManagementFactory.getGarbageCollectorMXBeans() because it makes it
 * possible to guarantee the consistency betwen the MemoryManager table
 * and the GCTable since both will be sharing the same cache.
 */
protected static class GCTableFilter {
    /** 
 * Returns the index that immediately follows the given
 * <var>index</var>. The returned index is strictly greater
 * than the given <var>index</var>, and is contained in the table.
 * <br>If the given <var>index</var> is null, returns the first
 * index in the table.
 * <br>If there are no index after the given <var>index</var>,
 * returns null.
 * This method is an optimization for the case where the
 * SnmpTableHandler is in fact an instance of SnmpCachedData.
 */
    public SnmpOid getNext(    SnmpCachedData datas,    SnmpOid index){
      final boolean dbg=log.isDebugOn();
      final int insertion=(index == null) ? -1 : datas.find(index);
      if (dbg)       log.debug("GCTableFilter","oid=" + index + " at insertion="+ insertion);
      int next;
      if (insertion > -1)       next=insertion + 1;
 else       next=-insertion - 1;
      for (; next < datas.indexes.length; next++) {
        if (dbg)         log.debug("GCTableFilter","next=" + next);
        final Object value=datas.datas[next];
        if (dbg)         log.debug("GCTableFilter","value[" + next + "]="+ ((MemoryManagerMXBean)value).getName());
        if (value instanceof GarbageCollectorMXBean) {
          if (dbg)           log.debug("GCTableFilter",((MemoryManagerMXBean)value).getName() + " is a  GarbageCollectorMXBean.");
          return datas.indexes[next];
        }
        if (dbg)         log.debug("GCTableFilter",((MemoryManagerMXBean)value).getName() + " is not a  GarbageCollectorMXBean: " + value.getClass().getName());
      }
      return null;
    }
    /** 
 * Returns the index that immediately follows the given
 * <var>index</var>. The returned index is strictly greater
 * than the given <var>index</var>, and is contained in the table.
 * <br>If the given <var>index</var> is null, returns the first
 * index in the table.
 * <br>If there are no index after the given <var>index</var>,
 * returns null.
 */
    public SnmpOid getNext(    SnmpTableHandler handler,    SnmpOid index){
      if (handler instanceof SnmpCachedData)       return getNext((SnmpCachedData)handler,index);
      SnmpOid next=index;
      do {
        next=handler.getNext(next);
        final Object value=handler.getData(next);
        if (value instanceof GarbageCollectorMXBean)         return next;
      }
 while (next != null);
      return null;
    }
    /** 
 * Returns the data associated with the given index.
 * If the given index is not found, null is returned.
 * Note that returning null does not necessarily means that
 * the index was not found.
 */
    public Object getData(    SnmpTableHandler handler,    SnmpOid index){
      final Object value=handler.getData(index);
      if (value instanceof GarbageCollectorMXBean)       return value;
      return null;
    }
    /** 
 * Returns true if the given <var>index</var> is present.
 */
    public boolean contains(    SnmpTableHandler handler,    SnmpOid index){
      if (handler.getData(index) instanceof GarbageCollectorMXBean)       return true;
      return false;
    }
  }
  private transient JvmMemManagerTableMetaImpl managers=null;
  private static GCTableFilter filter=new GCTableFilter();
  /** 
 * Constructor for the table. Initialize metadata for "JvmMemGCTableMeta".
 */
  public JvmMemGCTableMetaImpl(  SnmpMib myMib,  SnmpStandardObjectServer objserv){
    super(myMib,objserv);
  }
  private final JvmMemManagerTableMetaImpl getManagers(  SnmpMib mib){
    if (managers == null) {
      managers=(JvmMemManagerTableMetaImpl)mib.getRegisteredTableMeta("JvmMemManagerTable");
    }
    return managers;
  }
  /** 
 * Returns the JvmMemManagerTable SnmpTableHandler
 */
  protected SnmpTableHandler getHandler(  Object userData){
    JvmMemManagerTableMetaImpl managerTable=getManagers(theMib);
    return managerTable.getHandler(userData);
  }
  protected SnmpOid getNextOid(  Object userData) throws SnmpStatusException {
    return getNextOid(null,userData);
  }
  protected SnmpOid getNextOid(  SnmpOid oid,  Object userData) throws SnmpStatusException {
    final boolean dbg=log.isDebugOn();
    try {
      if (dbg)       log.debug("getNextOid","previous=" + oid);
      SnmpTableHandler handler=getHandler(userData);
      if (handler == null) {
        if (dbg)         log.debug("getNextOid","handler is null!");
        throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
      }
      final SnmpOid next=filter.getNext(handler,oid);
      if (dbg)       log.debug("getNextOid","next=" + next);
      if (next == null)       throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
      return next;
    }
 catch (    RuntimeException x) {
      if (dbg)       log.debug("getNextOid",x);
      throw x;
    }
  }
  protected boolean contains(  SnmpOid oid,  Object userData){
    SnmpTableHandler handler=getHandler(userData);
    if (handler == null)     return false;
    return filter.contains(handler,oid);
  }
  public Object getEntry(  SnmpOid oid) throws SnmpStatusException {
    if (oid == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    final Map<Object,Object> m=JvmContextFactory.getUserData();
    final long index=oid.getOidArc(0);
    final String entryTag=((m == null) ? null : ("JvmMemGCTable.entry." + index));
    if (m != null) {
      final Object entry=m.get(entryTag);
      if (entry != null)       return entry;
    }
    SnmpTableHandler handler=getHandler(m);
    if (handler == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    final Object data=filter.getData(handler,oid);
    if (data == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    final Object entry=new JvmMemGCEntryImpl((GarbageCollectorMXBean)data,(int)index);
    if (m != null && entry != null) {
      m.put(entryTag,entry);
    }
    return entry;
  }
  static final MibLogger log=new MibLogger(JvmMemGCTableMetaImpl.class);
}
