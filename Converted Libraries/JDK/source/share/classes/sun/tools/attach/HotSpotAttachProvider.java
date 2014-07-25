package sun.tools.attach;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.AttachPermission;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.spi.AttachProvider;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.net.URISyntaxException;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.MonitorException;
public abstract class HotSpotAttachProvider extends AttachProvider {
  private static final String JVM_VERSION="java.property.java.vm.version";
  public HotSpotAttachProvider(){
  }
  public void checkAttachPermission(){
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(new AttachPermission("attachVirtualMachine"));
    }
  }
  public List<VirtualMachineDescriptor> listVirtualMachines(){
    ArrayList<VirtualMachineDescriptor> result=new ArrayList<VirtualMachineDescriptor>();
    MonitoredHost host;
    Set vms;
    try {
      host=MonitoredHost.getMonitoredHost(new HostIdentifier((String)null));
      vms=host.activeVms();
    }
 catch (    Throwable t) {
      if (t instanceof ExceptionInInitializerError) {
        t=t.getCause();
      }
      if (t instanceof ThreadDeath) {
        throw (ThreadDeath)t;
      }
      if (t instanceof SecurityException) {
        return result;
      }
      throw new InternalError();
    }
    for (    Object vmid : vms) {
      if (vmid instanceof Integer) {
        String pid=vmid.toString();
        String name=pid;
        boolean isAttachable=false;
        MonitoredVm mvm=null;
        try {
          mvm=host.getMonitoredVm(new VmIdentifier(pid));
          try {
            isAttachable=MonitoredVmUtil.isAttachable(mvm);
            name=MonitoredVmUtil.commandLine(mvm);
          }
 catch (          Exception e) {
          }
          if (isAttachable) {
            result.add(new HotSpotVirtualMachineDescriptor(this,pid,name));
          }
        }
 catch (        Throwable t) {
          if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
          }
        }
 finally {
          if (mvm != null) {
            mvm.detach();
          }
        }
      }
    }
    return result;
  }
  /** 
 * Test if a VM is attachable. If it's not attachable,
 * an AttachNotSupportedException will be thrown. For example,
 * 1.4.2 or 5.0 VM are not attachable. There are cases that
 * we can't determine if a VM is attachable or not and this method
 * will just return.
 * This method uses the jvmstat counter to determine if a VM
 * is attachable. If the target VM does not have a jvmstat
 * share memory buffer, this method returns.
 * @exception AttachNotSupportedException if it's not attachable
 */
  void testAttachable(  String id) throws AttachNotSupportedException {
    MonitoredVm mvm=null;
    try {
      VmIdentifier vmid=new VmIdentifier(id);
      MonitoredHost host=MonitoredHost.getMonitoredHost(vmid);
      mvm=host.getMonitoredVm(vmid);
      if (MonitoredVmUtil.isAttachable(mvm)) {
        return;
      }
    }
 catch (    Throwable t) {
      if (t instanceof ThreadDeath) {
        ThreadDeath td=(ThreadDeath)t;
        throw td;
      }
      return;
    }
 finally {
      if (mvm != null) {
        mvm.detach();
      }
    }
    throw new AttachNotSupportedException("The VM does not support the attach mechanism");
  }
  /** 
 * A virtual machine descriptor to describe a HotSpot virtual machine.
 */
static class HotSpotVirtualMachineDescriptor extends VirtualMachineDescriptor {
    HotSpotVirtualMachineDescriptor(    AttachProvider provider,    String id,    String displayName){
      super(provider,id,displayName);
    }
    public boolean isAttachable(){
      return true;
    }
  }
}
