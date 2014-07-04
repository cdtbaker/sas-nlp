package com.sun.tools.example.debug.bdi;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VMDisconnectedException;
/** 
 * Our repository of what we know about the state of one
 * running VM.
 */
class Session {
  final VirtualMachine vm;
  final ExecutionManager runtime;
  final OutputListener diagnostics;
  boolean running=true;
  boolean interrupted=false;
  private JDIEventSource eventSourceThread=null;
  private int traceFlags;
  private boolean dead=false;
  public Session(  VirtualMachine vm,  ExecutionManager runtime,  OutputListener diagnostics){
    this.vm=vm;
    this.runtime=runtime;
    this.diagnostics=diagnostics;
    this.traceFlags=VirtualMachine.TRACE_NONE;
  }
  /** 
 * Determine if VM is interrupted, i.e, present and not running.
 */
  public boolean isInterrupted(){
    return interrupted;
  }
  public void setTraceMode(  int traceFlags){
    this.traceFlags=traceFlags;
    if (!dead) {
      vm.setDebugTraceMode(traceFlags);
    }
  }
  public boolean attach(){
    vm.setDebugTraceMode(traceFlags);
    diagnostics.putString("Connected to VM");
    eventSourceThread=new JDIEventSource(this);
    eventSourceThread.start();
    return true;
  }
  public void detach(){
    if (!dead) {
      eventSourceThread.interrupt();
      eventSourceThread=null;
      try {
        vm.dispose();
      }
 catch (      VMDisconnectedException ee) {
      }
      dead=true;
      diagnostics.putString("Disconnected from VM");
    }
  }
}
