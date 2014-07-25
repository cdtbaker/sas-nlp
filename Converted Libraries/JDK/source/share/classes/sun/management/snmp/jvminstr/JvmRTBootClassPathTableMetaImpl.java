package sun.management.snmp.jvminstr;
import com.sun.jmx.mbeanserver.Util;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.sun.jmx.snmp.SnmpCounter;
import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpGauge;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpUnsignedInt;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpOpaque;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpStringFixed;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpNull;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpIndex;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import sun.management.snmp.jvmmib.JvmRTBootClassPathTableMeta;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.JvmContextFactory;
/** 
 * The class is used for implementing the "JvmRTBootClassPathTable".
 */
public class JvmRTBootClassPathTableMetaImpl extends JvmRTBootClassPathTableMeta {
  private SnmpTableCache cache;
  /** 
 * A concrete implementation of {@link SnmpTableCache}, for the
 * JvmRTBootClassPathTable.
 */
private static class JvmRTBootClassPathTableCache extends SnmpTableCache {
    private JvmRTBootClassPathTableMetaImpl meta;
    JvmRTBootClassPathTableCache(    JvmRTBootClassPathTableMetaImpl meta,    long validity){
      this.meta=meta;
      this.validity=validity;
    }
    /** 
 * Call <code>getTableDatas(JvmContextFactory.getUserData())</code>.
 */
    public SnmpTableHandler getTableHandler(){
      final Map userData=JvmContextFactory.getUserData();
      return getTableDatas(userData);
    }
    /** 
 * Return a table handler containing the Thread indexes.
 * Indexes are computed from the ThreadId.
 */
    protected SnmpCachedData updateCachedDatas(    Object userData){
      final String[] path=JvmRuntimeImpl.getBootClassPath(userData);
      final long time=System.currentTimeMillis();
      final int len=path.length;
      SnmpOid indexes[]=new SnmpOid[len];
      for (int i=0; i < len; i++) {
        indexes[i]=new SnmpOid(i + 1);
      }
      return new SnmpCachedData(time,indexes,path);
    }
  }
  /** 
 * Constructor for the table. Initialize metadata for
 * "JvmRTBootClassPathTableMeta".
 * The reference on the MBean server is updated so the entries
 * created through an SNMP SET will be AUTOMATICALLY REGISTERED
 * in Java DMK.
 */
  public JvmRTBootClassPathTableMetaImpl(  SnmpMib myMib,  SnmpStandardObjectServer objserv){
    super(myMib,objserv);
    cache=new JvmRTBootClassPathTableCache(this,-1);
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
    if (dbg)     log.debug("*** **** **** **** getNextOid","next=" + next);
    if (next == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    return next;
  }
  protected boolean contains(  SnmpOid oid,  Object userData){
    SnmpTableHandler handler=getHandler(userData);
    if (handler == null)     return false;
    return handler.contains(oid);
  }
  public Object getEntry(  SnmpOid oid) throws SnmpStatusException {
    final boolean dbg=log.isDebugOn();
    if (dbg)     log.debug("getEntry","oid [" + oid + "]");
    if (oid == null || oid.getLength() != 1) {
      if (dbg)       log.debug("getEntry","Invalid oid [" + oid + "]");
      throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    }
    final Map<Object,Object> m=JvmContextFactory.getUserData();
    final String entryTag=((m == null) ? null : ("JvmRTBootClassPathTable.entry." + oid.toString()));
    if (m != null) {
      final Object entry=m.get(entryTag);
      if (entry != null) {
        if (dbg)         log.debug("getEntry","Entry is already in the cache");
        return entry;
      }
 else       if (dbg)       log.debug("getEntry","Entry is not in the cache");
    }
    SnmpTableHandler handler=getHandler(m);
    if (handler == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    final Object data=handler.getData(oid);
    if (data == null)     throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
    if (dbg)     log.debug("getEntry","data is a: " + data.getClass().getName());
    final Object entry=new JvmRTBootClassPathEntryImpl((String)data,(int)oid.getOidArc(0));
    if (m != null && entry != null) {
      m.put(entryTag,entry);
    }
    return entry;
  }
  /** 
 * Get the SnmpTableHandler that holds the jvmThreadInstanceTable data.
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
      final SnmpTableHandler handler=(SnmpTableHandler)m.get("JvmRTBootClassPathTable.handler");
      if (handler != null)       return handler;
    }
    final SnmpTableHandler handler=cache.getTableHandler();
    if (m != null && handler != null)     m.put("JvmRTBootClassPathTable.handler",handler);
    return handler;
  }
  static final MibLogger log=new MibLogger(JvmRTBootClassPathTableMetaImpl.class);
}
