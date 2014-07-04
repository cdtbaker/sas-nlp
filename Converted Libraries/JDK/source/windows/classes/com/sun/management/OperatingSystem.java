package com.sun.management;
import sun.management.VMManagement;
/** 
 * Implementation class for the operating system.
 * Standard and committed hotspot-specific metrics if any.
 * ManagementFactory.getOperatingSystemMXBean() returns an instance
 * of this class.
 */
class OperatingSystem extends sun.management.OperatingSystemImpl implements OperatingSystemMXBean {
  private static Object psapiLock=new Object();
  OperatingSystem(  VMManagement vm){
    super(vm);
  }
  public long getCommittedVirtualMemorySize(){
synchronized (psapiLock) {
      return getCommittedVirtualMemorySize0();
    }
  }
  private native long getCommittedVirtualMemorySize0();
  public native long getTotalSwapSpaceSize();
  public native long getFreeSwapSpaceSize();
  public native long getProcessCpuTime();
  public native long getFreePhysicalMemorySize();
  public native long getTotalPhysicalMemorySize();
  public native double getSystemCpuLoad();
  public native double getProcessCpuLoad();
static {
    initialize();
  }
  private static native void initialize();
}
