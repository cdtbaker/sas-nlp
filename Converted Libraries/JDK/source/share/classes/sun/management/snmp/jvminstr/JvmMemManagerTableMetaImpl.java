package sun.management.snmp.jvminstr;
import com.sun.jmx.mbeanserver.Util;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.ManagementFactory;
import sun.management.snmp.jvmmib.JvmMemManagerTableMeta;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpNamedListTableCache;
import sun.management.snmp.util.SnmpTableHandler;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.JvmContextFactory;
/** 
 * The class is used for implementing the "JvmMemManagerTable" table.
 * This custom implementation show how to implement an SNMP table
 * over a weak cache, recomputing the cahed data when needed.
 */
public class JvmMemManagerTableMetaImpl extends JvmMemManagerTableMeta {
  /** 
 * A concrete implementation of {@link SnmpNamedListTableCache}, for the
 * jvmMemManagerTable.
 */
private static class JvmMemManagerTableCache extends SnmpNamedListTableCache {
    /** 
 * Create a weak cache for the jvmMemManagerTable.
 * @param validity validity of the cached data, in ms.
 */
    JvmMemManagerTableCache(    long validity){
      this.validity=validity;
    }
    /** 
 * Use the MemoryManagerMXBean name as key.
 * @param context A {@link TreeMap} as allocated by the parent{@link SnmpNamedListTableCache} class.
 * @param rawDatas List of {@link MemoryManagerMXBean}, as
 * returned by
 * <code>ManagementFactory.getMemoryMBean().getMemoryManagers()</code>
 * @param rank The <var>rank</var> of <var>item</var> in the list.
 * @param item The <var>rank</var><super>th</super>
 * <code>MemoryManagerMXBean</code> in the list.
 * @return  <code>((MemoryManagerMXBean)item).getName()</code>
 */
    protected String getKey(    Object context,    List rawDatas,    int rank,    Object item){
      if (item == null)       return null;
      final String name=((MemoryManagerMXBean)item).getName();
      log.debug("getKey","key=" + name);
      return name;
    }
    /** 
 * Call <code>getTableHandler(JvmContextFactory.getUserData())</code>.
 */
    public SnmpTableHandler getTableHandler(){
      final Map userData=JvmContextFactory.getUserData();
      return getTableDatas(userData);
    }
    /** 
 * Return the key used to cache the raw data of this table.
 */
    protected String getRawDatasKey(){
      return "JvmMemManagerTable.getMemoryManagers";
    }
    /** 
 * Call ManagementFactory.getMemoryManagerMXBeans() to
 * load the raw data of this table.
 */
    protected List loadRawDatas(    Map userData){
      return ManagementFactory.getMemoryManagerMXBeans();
    }
  }
  protected SnmpTableCache cache;
  /** 
 * Constructor for the table. Initialize metadata for
 * "JvmMemManagerTableMeta".
 * The reference on the MBean server is updated so the entries
 * created through an SNMP SET will be AUTOMATICALLY REGISTERED
 * in Java DMK.
 */
  public JvmMemManagerTableMetaImpl(  SnmpMib myMib,  SnmpStandardObjectServer objserv){
    super(myMib,objserv);
    this.cache=new JvmMemManagerTableCache(((JVM_MANAGEMENT_MIB_IMPL)myMib).validity());
  }
  protected SnmpOid getNextOid(  Object userData) throws SnmpStatusException {
    return getNextOid(null,userData);
  }
  protected SnmpOid getNextOid(  SnmpOid oid,  Object userData) throws SnmpStatusException {
    final boolean dbg=log.isDebugOn();
    if (dbg)     log.debug("getNextOid","previous=" + oid);
    SnmpTableHandler handler=getHandler(userData);
    if (handler == null) {
      if (dbg)       log.debug("getNextOid","handler is null!");
      throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    }
    final SnmpOid next=handler.getNext(oid);
    if (dbg)     log.debug("getNextOid","next=" + next);
    if (next == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    return next;
  }
  protected boolean contains(  SnmpOid oid,  Object userData){
    SnmpTableHandler handler=getHandler(userData);
    if (handler == null)     return false;
    return handler.contains(oid);
  }
  public Object getEntry(  SnmpOid oid) throws SnmpStatusException {
    if (oid == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    final Map<Object,Object> m=JvmContextFactory.getUserData();
    final long index=oid.getOidArc(0);
    final String entryTag=((m == null) ? null : ("JvmMemManagerTable.entry." + index));
    if (m != null) {
      final Object entry=m.get(entryTag);
      if (entry != null)       return entry;
    }
    SnmpTableHandler handler=getHandler(m);
    if (handler == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    final Object data=handler.getData(oid);
    if (data == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    final Object entry=new JvmMemManagerEntryImpl((MemoryManagerMXBean)data,(int)index);
    if (m != null && entry != null) {
      m.put(entryTag,entry);
    }
    return entry;
  }
  /** 
 * Get the SnmpTableHandler that holds the jvmMemManagerTable data.
 * First look it up in the request contextual cache, and if it is
 * not found, obtain it from the weak cache.
 * <br>The request contextual cache will be released at the end of the
 * current requests, and is used only to process this request.
 * <br>The weak cache is shared by all requests, and is only
 * recomputed when it is found to be obsolete.
 * <br>Note that the data put in the request contextual cache is
 * never considered to be obsolete, in order to preserve data
 * coherency.
 */
  protected SnmpTableHandler getHandler(  Object userData){
    final Map<Object,Object> m;
    if (userData instanceof Map)     m=Util.cast(userData);
 else     m=null;
    if (m != null) {
      final SnmpTableHandler handler=(SnmpTableHandler)m.get("JvmMemManagerTable.handler");
      if (handler != null)       return handler;
    }
    final SnmpTableHandler handler=cache.getTableHandler();
    if (m != null && handler != null)     m.put("JvmMemManagerTable.handler",handler);
    return handler;
  }
  static final MibLogger log=new MibLogger(JvmMemManagerTableMetaImpl.class);
}