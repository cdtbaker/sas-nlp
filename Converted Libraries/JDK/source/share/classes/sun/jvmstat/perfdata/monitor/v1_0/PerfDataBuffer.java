package sun.jvmstat.perfdata.monitor.v1_0;
import sun.jvmstat.monitor.*;
import sun.jvmstat.perfdata.monitor.*;
import java.util.*;
import java.util.regex.*;
import java.nio.*;
/** 
 * The concrete implementation of version 1.0 of the HotSpot PerfData
 * Instrumentation buffer. This class is responsible for parsing the
 * instrumentation memory and constructing the necessary objects to
 * represent and access the instrumentation objects contained in the
 * memory buffer.
 * @author Brian Doherty
 * @since 1.5
 * @see AbstractPerfDataBuffer
 */
public class PerfDataBuffer extends PerfDataBufferImpl {
  private static final boolean DEBUG=false;
  private static final int syncWaitMs=Integer.getInteger("sun.jvmstat.perdata.syncWaitMs",5000);
  private static final ArrayList EMPTY_LIST=new ArrayList(0);
  private final static int PERFDATA_ENTRYLENGTH_OFFSET=0;
  private final static int PERFDATA_ENTRYLENGTH_SIZE=4;
  private final static int PERFDATA_NAMELENGTH_OFFSET=4;
  private final static int PERFDATA_NAMELENGTH_SIZE=4;
  private final static int PERFDATA_VECTORLENGTH_OFFSET=8;
  private final static int PERFDATA_VECTORLENGTH_SIZE=4;
  private final static int PERFDATA_DATATYPE_OFFSET=12;
  private final static int PERFDATA_DATATYPE_SIZE=1;
  private final static int PERFDATA_FLAGS_OFFSET=13;
  private final static int PERFDATA_FLAGS_SIZE=1;
  private final static int PERFDATA_DATAUNITS_OFFSET=14;
  private final static int PERFDATA_DATAUNITS_SIZE=1;
  private final static int PERFDATA_DATAATTR_OFFSET=15;
  private final static int PERFDATA_DATAATTR_SIZE=1;
  private final static int PERFDATA_NAME_OFFSET=16;
  PerfDataBufferPrologue prologue;
  int nextEntry;
  int pollForEntry;
  int perfDataItem;
  long lastModificationTime;
  int lastUsed;
  IntegerMonitor overflow;
  ArrayList<Monitor> insertedMonitors;
  /** 
 * Construct a PerfDataBufferImpl instance.
 * <p>
 * This class is dynamically loaded by{@link AbstractPerfDataBuffer#createPerfDataBuffer}, and this
 * constructor is called to instantiate the instance.
 * @param buffer the buffer containing the instrumentation data
 * @param lvmid the Local Java Virtual Machine Identifier for this
 * instrumentation buffer.
 */
  public PerfDataBuffer(  ByteBuffer buffer,  int lvmid) throws MonitorException {
    super(buffer,lvmid);
    prologue=new PerfDataBufferPrologue(buffer);
    this.buffer.order(prologue.getByteOrder());
  }
  /** 
 * {@inheritDoc}
 */
  protected void buildMonitorMap(  Map<String,Monitor> map) throws MonitorException {
    assert Thread.holdsLock(this);
    buffer.rewind();
    buildPseudoMonitors(map);
    buffer.position(prologue.getSize());
    nextEntry=buffer.position();
    perfDataItem=0;
    int used=prologue.getUsed();
    long modificationTime=prologue.getModificationTimeStamp();
    Monitor m=getNextMonitorEntry();
    while (m != null) {
      map.put(m.getName(),m);
      m=getNextMonitorEntry();
    }
    lastUsed=used;
    lastModificationTime=modificationTime;
    synchWithTarget(map);
    kludge(map);
    insertedMonitors=new ArrayList<Monitor>(map.values());
  }
  /** 
 * {@inheritDoc}
 */
  protected void getNewMonitors(  Map<String,Monitor> map) throws MonitorException {
    assert Thread.holdsLock(this);
    int used=prologue.getUsed();
    long modificationTime=prologue.getModificationTimeStamp();
    if ((used > lastUsed) || (lastModificationTime > modificationTime)) {
      lastUsed=used;
      lastModificationTime=modificationTime;
      Monitor monitor=getNextMonitorEntry();
      while (monitor != null) {
        String name=monitor.getName();
        if (!map.containsKey(name)) {
          map.put(name,monitor);
          if (insertedMonitors != null) {
            insertedMonitors.add(monitor);
          }
        }
        monitor=getNextMonitorEntry();
      }
    }
  }
  /** 
 * {@inheritDoc}
 */
  protected MonitorStatus getMonitorStatus(  Map<String,Monitor> map) throws MonitorException {
    assert Thread.holdsLock(this);
    assert insertedMonitors != null;
    getNewMonitors(map);
    ArrayList removed=EMPTY_LIST;
    ArrayList inserted=insertedMonitors;
    insertedMonitors=new ArrayList<Monitor>();
    return new MonitorStatus(inserted,removed);
  }
  /** 
 * Build the pseudo monitors used to map the prolog data into counters.
 */
  protected void buildPseudoMonitors(  Map<String,Monitor> map){
    Monitor monitor=null;
    String name=null;
    IntBuffer ib=null;
    name=PerfDataBufferPrologue.PERFDATA_MAJOR_NAME;
    ib=prologue.majorVersionBuffer();
    monitor=new PerfIntegerMonitor(name,Units.NONE,Variability.CONSTANT,false,ib);
    map.put(name,monitor);
    name=PerfDataBufferPrologue.PERFDATA_MINOR_NAME;
    ib=prologue.minorVersionBuffer();
    monitor=new PerfIntegerMonitor(name,Units.NONE,Variability.CONSTANT,false,ib);
    map.put(name,monitor);
    name=PerfDataBufferPrologue.PERFDATA_BUFFER_SIZE_NAME;
    ib=prologue.sizeBuffer();
    monitor=new PerfIntegerMonitor(name,Units.BYTES,Variability.MONOTONIC,false,ib);
    map.put(name,monitor);
    name=PerfDataBufferPrologue.PERFDATA_BUFFER_USED_NAME;
    ib=prologue.usedBuffer();
    monitor=new PerfIntegerMonitor(name,Units.BYTES,Variability.MONOTONIC,false,ib);
    map.put(name,monitor);
    name=PerfDataBufferPrologue.PERFDATA_OVERFLOW_NAME;
    ib=prologue.overflowBuffer();
    monitor=new PerfIntegerMonitor(name,Units.BYTES,Variability.MONOTONIC,false,ib);
    map.put(name,monitor);
    this.overflow=(IntegerMonitor)monitor;
    name=PerfDataBufferPrologue.PERFDATA_MODTIMESTAMP_NAME;
    LongBuffer lb=prologue.modificationTimeStampBuffer();
    monitor=new PerfLongMonitor(name,Units.TICKS,Variability.MONOTONIC,false,lb);
    map.put(name,monitor);
  }
  /** 
 * Method to provide a gross level of synchronization with the
 * target monitored jvm.
 * gross synchronization works by polling for the hotspot.rt.hrt.ticks
 * counter, which is the last counter created by the StatSampler
 * initialization code. The counter is updated when the watcher thread
 * starts scheduling tasks, which is the last thing done in vm
 * initialization.
 */
  protected void synchWithTarget(  Map<String,Monitor> map) throws MonitorException {
    long timeLimit=System.currentTimeMillis() + syncWaitMs;
    String name="hotspot.rt.hrt.ticks";
    LongMonitor ticks=(LongMonitor)pollFor(map,name,timeLimit);
    log("synchWithTarget: " + lvmid + " ");
    while (ticks.longValue() == 0) {
      log(".");
      try {
        Thread.sleep(20);
      }
 catch (      InterruptedException e) {
      }
      if (System.currentTimeMillis() > timeLimit) {
        lognl("failed: " + lvmid);
        throw new MonitorException("Could Not Synchronize with target");
      }
    }
    lognl("success: " + lvmid);
  }
  /** 
 * Method to poll the instrumentation memory for a counter with
 * the given name. The polling period is bounded by the timeLimit
 * argument.
 */
  protected Monitor pollFor(  Map<String,Monitor> map,  String name,  long timeLimit) throws MonitorException {
    Monitor monitor=null;
    log("polling for: " + lvmid + ","+ name+ " ");
    pollForEntry=nextEntry;
    while ((monitor=map.get(name)) == null) {
      log(".");
      try {
        Thread.sleep(20);
      }
 catch (      InterruptedException e) {
      }
      long t=System.currentTimeMillis();
      if ((t > timeLimit) || (overflow.intValue() > 0)) {
        lognl("failed: " + lvmid + ","+ name);
        dumpAll(map,lvmid);
        throw new MonitorException("Could not find expected counter");
      }
      getNewMonitors(map);
    }
    lognl("success: " + lvmid + ","+ name);
    return monitor;
  }
  /** 
 * method to make adjustments for known counter problems. This
 * method depends on the availability of certain counters, which
 * is generally guaranteed by the synchWithTarget() method.
 */
  protected void kludge(  Map<String,Monitor> map){
    if (Boolean.getBoolean("sun.jvmstat.perfdata.disableKludge")) {
      return;
    }
    String name="java.vm.version";
    StringMonitor jvm_version=(StringMonitor)map.get(name);
    if (jvm_version == null) {
      jvm_version=(StringMonitor)findByAlias(name);
    }
    name="java.vm.name";
    StringMonitor jvm_name=(StringMonitor)map.get(name);
    if (jvm_name == null) {
      jvm_name=(StringMonitor)findByAlias(name);
    }
    name="hotspot.vm.args";
    StringMonitor args=(StringMonitor)map.get(name);
    if (args == null) {
      args=(StringMonitor)findByAlias(name);
    }
    assert ((jvm_name != null) && (jvm_version != null) && (args != null));
    if (jvm_name.stringValue().indexOf("HotSpot") >= 0) {
      if (jvm_version.stringValue().startsWith("1.4.2")) {
        kludgeMantis(map,args);
      }
    }
  }
  /** 
 * method to repair the 1.4.2 parallel scavenge counters that are
 * incorrectly initialized by the JVM when UseAdaptiveSizePolicy
 * is set. This bug couldn't be fixed for 1.4.2 FCS due to putback
 * restrictions.
 */
  private void kludgeMantis(  Map<String,Monitor> map,  StringMonitor args){
    String cname="hotspot.gc.collector.0.name";
    StringMonitor collector=(StringMonitor)map.get(cname);
    if (collector.stringValue().compareTo("PSScavenge") == 0) {
      boolean adaptiveSizePolicy=true;
      cname="hotspot.vm.flags";
      StringMonitor flags=(StringMonitor)map.get(cname);
      String allArgs=flags.stringValue() + " " + args.stringValue();
      int ahi=allArgs.lastIndexOf("+AggressiveHeap");
      int aspi=allArgs.lastIndexOf("-UseAdaptiveSizePolicy");
      if (ahi != -1) {
        if ((aspi != -1) && (aspi > ahi)) {
          adaptiveSizePolicy=false;
        }
      }
 else {
        if (aspi != -1) {
          adaptiveSizePolicy=false;
        }
      }
      if (adaptiveSizePolicy) {
        String eden_size="hotspot.gc.generation.0.space.0.size";
        String s0_size="hotspot.gc.generation.0.space.1.size";
        String s1_size="hotspot.gc.generation.0.space.2.size";
        map.remove(eden_size);
        map.remove(s0_size);
        map.remove(s1_size);
        String new_max_name="hotspot.gc.generation.0.capacity.max";
        LongMonitor new_max=(LongMonitor)map.get(new_max_name);
        Monitor monitor=null;
        LongBuffer lb=LongBuffer.allocate(1);
        lb.put(new_max.longValue());
        monitor=new PerfLongMonitor(eden_size,Units.BYTES,Variability.CONSTANT,false,lb);
        map.put(eden_size,monitor);
        monitor=new PerfLongMonitor(s0_size,Units.BYTES,Variability.CONSTANT,false,lb);
        map.put(s0_size,monitor);
        monitor=new PerfLongMonitor(s1_size,Units.BYTES,Variability.CONSTANT,false,lb);
        map.put(s1_size,monitor);
      }
    }
  }
  /** 
 * method to extract the next monitor entry from the instrumentation memory.
 * assumes that nextEntry is the offset into the byte array
 * at which to start the search for the next entry. method leaves
 * next entry pointing to the next entry or to the end of data.
 */
  protected Monitor getNextMonitorEntry() throws MonitorException {
    Monitor monitor=null;
    if ((nextEntry % 4) != 0) {
      throw new MonitorStructureException("Entry index not properly aligned: " + nextEntry);
    }
    if ((nextEntry < 0) || (nextEntry > buffer.limit())) {
      throw new MonitorStructureException("Entry index out of bounds: nextEntry = " + nextEntry + ", limit = "+ buffer.limit());
    }
    if (nextEntry == buffer.limit()) {
      lognl("getNextMonitorEntry():" + " nextEntry == buffer.limit(): returning");
      return null;
    }
    buffer.position(nextEntry);
    int entryStart=buffer.position();
    int entryLength=buffer.getInt();
    if ((entryLength < 0) || (entryLength > buffer.limit())) {
      throw new MonitorStructureException("Invalid entry length: entryLength = " + entryLength);
    }
    if ((entryStart + entryLength) > buffer.limit()) {
      throw new MonitorStructureException("Entry extends beyond end of buffer: " + " entryStart = " + entryStart + " entryLength = "+ entryLength+ " buffer limit = "+ buffer.limit());
    }
    if (entryLength == 0) {
      return null;
    }
    int nameLength=buffer.getInt();
    int vectorLength=buffer.getInt();
    byte dataType=buffer.get();
    byte flags=buffer.get();
    Units u=Units.toUnits(buffer.get());
    Variability v=Variability.toVariability(buffer.get());
    boolean supported=(flags & 0x01) != 0;
    if ((nameLength <= 0) || (nameLength > entryLength)) {
      throw new MonitorStructureException("Invalid Monitor name length: " + nameLength);
    }
    if ((vectorLength < 0) || (vectorLength > entryLength)) {
      throw new MonitorStructureException("Invalid Monitor vector length: " + vectorLength);
    }
    byte[] nameBytes=new byte[nameLength - 1];
    for (int i=0; i < nameLength - 1; i++) {
      nameBytes[i]=buffer.get();
    }
    String name=new String(nameBytes,0,nameLength - 1);
    if (v == Variability.INVALID) {
      throw new MonitorDataException("Invalid variability attribute:" + " entry index = " + perfDataItem + " name = "+ name);
    }
    if (u == Units.INVALID) {
      throw new MonitorDataException("Invalid units attribute: " + " entry index = " + perfDataItem + " name = "+ name);
    }
    int offset;
    if (vectorLength == 0) {
      if (dataType == BasicType.LONG.intValue()) {
        offset=entryStart + entryLength - 8;
        buffer.position(offset);
        LongBuffer lb=buffer.asLongBuffer();
        lb.limit(1);
        monitor=new PerfLongMonitor(name,u,v,supported,lb);
        perfDataItem++;
      }
 else {
        throw new MonitorTypeException("Invalid Monitor type:" + " entry index = " + perfDataItem + " name = "+ name+ " type = "+ dataType);
      }
    }
 else {
      if (dataType == BasicType.BYTE.intValue()) {
        if (u != Units.STRING) {
          throw new MonitorTypeException("Invalid Monitor type:" + " entry index = " + perfDataItem + " name = "+ name+ " type = "+ dataType);
        }
        offset=entryStart + PERFDATA_NAME_OFFSET + nameLength;
        buffer.position(offset);
        ByteBuffer bb=buffer.slice();
        bb.limit(vectorLength);
        bb.position(0);
        if (v == Variability.CONSTANT) {
          monitor=new PerfStringConstantMonitor(name,supported,bb);
        }
 else         if (v == Variability.VARIABLE) {
          monitor=new PerfStringVariableMonitor(name,supported,bb,vectorLength - 1);
        }
 else {
          throw new MonitorDataException("Invalid variability attribute:" + " entry index = " + perfDataItem + " name = "+ name+ " variability = "+ v);
        }
        perfDataItem++;
      }
 else {
        throw new MonitorTypeException("Invalid Monitor type:" + " entry index = " + perfDataItem + " name = "+ name+ " type = "+ dataType);
      }
    }
    nextEntry=entryStart + entryLength;
    return monitor;
  }
  /** 
 * Method to dump debugging information
 */
  private void dumpAll(  Map map,  int lvmid){
    if (DEBUG) {
      Set keys=map.keySet();
      System.err.println("Dump for " + lvmid);
      int j=0;
      for (Iterator i=keys.iterator(); i.hasNext(); j++) {
        Monitor monitor=(Monitor)map.get(i.next());
        System.err.println(j + "\t" + monitor.getName()+ "="+ monitor.getValue());
      }
      System.err.println("nextEntry = " + nextEntry + " pollForEntry = "+ pollForEntry);
      System.err.println("Buffer info:");
      System.err.println("buffer = " + buffer);
    }
  }
  private void lognl(  String s){
    if (DEBUG) {
      System.err.println(s);
    }
  }
  private void log(  String s){
    if (DEBUG) {
      System.err.print(s);
    }
  }
}
