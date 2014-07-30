package sun.jvmstat.perfdata.monitor;
import java.util.List;
import java.lang.reflect.*;
import java.io.*;
import sun.jvmstat.monitor.*;
import sun.jvmstat.monitor.remote.*;
import sun.jvmstat.monitor.event.VmListener;
/** 
 * Base class for all MonitoredVm implementations that utilize the
 * HotSpot PerfData instrumentation buffer as the communications
 * mechanism to the target Java Virtual Machine.
 * @author Brian Doherty
 * @since 1.5
 */
public abstract class AbstractMonitoredVm implements BufferedMonitoredVm {
  /** 
 * The VmIdentifier for the target.
 */
  protected VmIdentifier vmid;
  /** 
 * The shared memory instrumentation buffer for the target.
 */
  protected AbstractPerfDataBuffer pdb;
  /** 
 * The sampling interval, if the instrumentation buffer is acquired
 * by sampling instead of shared memory mechanisms.
 */
  protected int interval;
  /** 
 * Create an AbstractMonitoredVm instance.
 * @param vmid the VmIdentifier for the target
 * @param interval the initial sampling interval
 */
  public AbstractMonitoredVm(  VmIdentifier vmid,  int interval) throws MonitorException {
    this.vmid=vmid;
    this.interval=interval;
  }
  /** 
 * {@inheritDoc}
 */
  public VmIdentifier getVmIdentifier(){
    return vmid;
  }
  /** 
 * {@inheritDoc}
 */
  public Monitor findByName(  String name) throws MonitorException {
    return pdb.findByName(name);
  }
  /** 
 * {@inheritDoc}
 */
  public List<Monitor> findByPattern(  String patternString) throws MonitorException {
    return pdb.findByPattern(patternString);
  }
  /** 
 * {@inheritDoc}
 */
  public void detach(){
  }
  /** 
 * {@inheritDoc}
 */
  public void setInterval(  int interval){
    this.interval=interval;
  }
  /** 
 * {@inheritDoc}
 */
  public int getInterval(){
    return interval;
  }
  /** 
 * {@inheritDoc}
 */
  public void setLastException(  Exception e){
  }
  /** 
 * {@inheritDoc}
 */
  public Exception getLastException(){
    return null;
  }
  /** 
 * {@inheritDoc}
 */
  public void clearLastException(){
  }
  /** 
 * {@inheritDoc}
 */
  public boolean isErrored(){
    return false;
  }
  /** 
 * Get a list of the inserted and removed monitors since last called.
 * @return MonitorStatus - the status of available Monitors for the
 * target Java Virtual Machine.
 * @throws MonitorException Thrown if communications errors occur
 * while communicating with the target.
 */
  public MonitorStatus getMonitorStatus() throws MonitorException {
    return pdb.getMonitorStatus();
  }
  /** 
 * {@inheritDoc}
 */
  public abstract void addVmListener(  VmListener l);
  /** 
 * {@inheritDoc}
 */
  public abstract void removeVmListener(  VmListener l);
  /** 
 * {@inheritDoc}
 */
  public byte[] getBytes(){
    return pdb.getBytes();
  }
  /** 
 * {@inheritDoc}
 */
  public int getCapacity(){
    return pdb.getCapacity();
  }
}