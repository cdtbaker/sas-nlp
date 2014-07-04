package sun.jvmstat.perfdata.monitor.protocol.local;
import sun.jvmstat.monitor.*;
import sun.jvmstat.monitor.event.*;
import sun.jvmstat.perfdata.monitor.*;
import java.util.*;
import java.net.*;
/** 
 * Concrete implementation of the MonitoredHost interface for the
 * <em>local</em> protocol of the HotSpot PerfData monitoring implementation.
 * @author Brian Doherty
 * @since 1.5
 */
public class MonitoredHostProvider extends MonitoredHost {
  private static final int DEFAULT_POLLING_INTERVAL=1000;
  private ArrayList<HostListener> listeners;
  private NotifierTask task;
  private HashSet<Integer> activeVms;
  private LocalVmManager vmManager;
  /** 
 * Create a MonitoredHostProvider instance using the given HostIdentifier.
 * @param hostId the host identifier for this MonitoredHost
 */
  public MonitoredHostProvider(  HostIdentifier hostId){
    this.hostId=hostId;
    this.listeners=new ArrayList<HostListener>();
    this.interval=DEFAULT_POLLING_INTERVAL;
    this.activeVms=new HashSet<Integer>();
    this.vmManager=new LocalVmManager();
  }
  /** 
 * {@inheritDoc}
 */
  public MonitoredVm getMonitoredVm(  VmIdentifier vmid) throws MonitorException {
    return getMonitoredVm(vmid,DEFAULT_POLLING_INTERVAL);
  }
  /** 
 * {@inheritDoc}
 */
  public MonitoredVm getMonitoredVm(  VmIdentifier vmid,  int interval) throws MonitorException {
    try {
      VmIdentifier nvmid=hostId.resolve(vmid);
      return new LocalMonitoredVm(nvmid,interval);
    }
 catch (    URISyntaxException e) {
      throw new IllegalArgumentException("Malformed URI: " + vmid.toString(),e);
    }
  }
  /** 
 * {@inheritDoc}
 */
  public void detach(  MonitoredVm vm){
    vm.detach();
  }
  /** 
 * {@inheritDoc}
 */
  public void addHostListener(  HostListener listener){
synchronized (listeners) {
      listeners.add(listener);
      if (task == null) {
        task=new NotifierTask();
        LocalEventTimer timer=LocalEventTimer.getInstance();
        timer.schedule(task,interval,interval);
      }
    }
  }
  /** 
 * {@inheritDoc}
 */
  public void removeHostListener(  HostListener listener){
synchronized (listeners) {
      listeners.remove(listener);
      if (listeners.isEmpty() && (task != null)) {
        task.cancel();
        task=null;
      }
    }
  }
  /** 
 * {@inheritDoc}
 */
  public void setInterval(  int newInterval){
synchronized (listeners) {
      if (newInterval == interval) {
        return;
      }
      int oldInterval=interval;
      super.setInterval(newInterval);
      if (task != null) {
        task.cancel();
        NotifierTask oldTask=task;
        task=new NotifierTask();
        LocalEventTimer timer=LocalEventTimer.getInstance();
        CountedTimerTaskUtils.reschedule(timer,oldTask,task,oldInterval,newInterval);
      }
    }
  }
  /** 
 * {@inheritDoc}
 */
  public Set<Integer> activeVms(){
    return vmManager.activeVms();
  }
  /** 
 * Fire VmEvent events.
 * @param active a set of Integer objects containing the vmid of
 * the active Vms
 * @param started a set of Integer objects containing the vmid of
 * new Vms started since last interval.
 * @param terminated a set of Integer objects containing the vmid of
 * terminated Vms since last interval.
 */
  private void fireVmStatusChangedEvents(  Set active,  Set started,  Set terminated){
    ArrayList registered=null;
    VmStatusChangeEvent ev=null;
synchronized (listeners) {
      registered=(ArrayList)listeners.clone();
    }
    for (Iterator i=registered.iterator(); i.hasNext(); ) {
      HostListener l=(HostListener)i.next();
      if (ev == null) {
        ev=new VmStatusChangeEvent(this,active,started,terminated);
      }
      l.vmStatusChanged(ev);
    }
  }
  /** 
 * Class to poll the local system and generate event notifications.
 */
private class NotifierTask extends CountedTimerTask {
    public void run(){
      super.run();
      Set lastActiveVms=activeVms;
      activeVms=(HashSet<Integer>)vmManager.activeVms();
      if (activeVms.isEmpty()) {
        return;
      }
      Set<Integer> startedVms=new HashSet<Integer>();
      Set<Object> terminatedVms=new HashSet<Object>();
      for (Iterator i=activeVms.iterator(); i.hasNext(); ) {
        Integer vmid=(Integer)i.next();
        if (!lastActiveVms.contains(vmid)) {
          startedVms.add(vmid);
        }
      }
      for (Iterator i=lastActiveVms.iterator(); i.hasNext(); ) {
        Object o=i.next();
        if (!activeVms.contains(o)) {
          terminatedVms.add(o);
        }
      }
      if (!startedVms.isEmpty() || !terminatedVms.isEmpty()) {
        fireVmStatusChangedEvents(activeVms,startedVms,terminatedVms);
      }
    }
  }
}
