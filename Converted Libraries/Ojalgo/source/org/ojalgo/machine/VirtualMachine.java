package org.ojalgo.machine;
import java.lang.management.ManagementFactory;
import org.ojalgo.ProgrammingError;
import org.ojalgo.netio.ASCII;
import org.ojalgo.netio.BasicLogger;
public final class VirtualMachine extends AbstractMachine {
  private static final String AMD64="amd64";
  private static final String I386="i386";
  private static final String X86="x86";
  private static final String X86_64="x86_64";
  public static String getArchitecture(){
    final String tmpProperty=ManagementFactory.getOperatingSystemMXBean().getArch().toLowerCase();
    if (tmpProperty.equals(I386)) {
      return X86;
    }
 else     if (tmpProperty.equals(AMD64)) {
      return X86_64;
    }
 else {
      return tmpProperty;
    }
  }
  public static long getMemory(){
    return Runtime.getRuntime().maxMemory();
  }
  public static int getThreads(){
    return Runtime.getRuntime().availableProcessors();
  }
  private final Hardware myHardware;
  private final Runtime myRuntime;
  private VirtualMachine(  final String anArchitecture,  final BasicMachine[] someLevels){
    super(anArchitecture,someLevels);
    myHardware=null;
    myRuntime=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  VirtualMachine(  final Hardware aHardware,  final Runtime aRuntime){
    super(aHardware,aRuntime);
    myHardware=aHardware;
    myRuntime=aRuntime;
  }
  public void collectGarbage(){
    myRuntime.runFinalization();
    long tmpIsFree=myRuntime.freeMemory();
    long tmpWasFree;
    do {
      tmpWasFree=tmpIsFree;
      myRuntime.gc();
      try {
        Thread.sleep(8L);
      }
 catch (      final InterruptedException anException) {
        BasicLogger.logError(anException.getMessage());
      }
      tmpIsFree=myRuntime.freeMemory();
    }
 while (tmpIsFree > tmpWasFree);
    myRuntime.runFinalization();
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof VirtualMachine)) {
      return false;
    }
    final VirtualMachine other=(VirtualMachine)obj;
    if (myHardware == null) {
      if (other.myHardware != null) {
        return false;
      }
    }
 else     if (!myHardware.equals(other.myHardware)) {
      return false;
    }
    return true;
  }
  public int getAvailableDim1D(  final long elementSize){
    return (int)AbstractMachine.elements(this.getAvailableMemory(),elementSize);
  }
  public int getAvailableDim2D(  final long elementSize){
    return (int)Math.sqrt(AbstractMachine.elements(this.getAvailableMemory(),elementSize));
  }
  public long getAvailableMemory(){
    final long tmpMax=myRuntime.maxMemory();
    final long tmpTotal=myRuntime.totalMemory();
    final long tmpFree=myRuntime.freeMemory();
    final long tmpAvailable=(tmpMax - tmpTotal) + tmpFree;
    return tmpAvailable;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=super.hashCode();
    result=(prime * result) + ((myHardware == null) ? 0 : myHardware.hashCode());
    return result;
  }
  @Override public String toString(){
    return super.toString() + ASCII.SP + myHardware.toString();
  }
}
