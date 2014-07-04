package com.sun.tools.example.debug.event;
import com.sun.jdi.*;
import com.sun.jdi.event.*;
public class VMStartEventSet extends AbstractEventSet {
  private static final long serialVersionUID=-3384957227835478191L;
  VMStartEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  /** 
 * Returns the initial thread of the VM which has started.
 * @return a {@link ThreadReference} which mirrors the event's
 * thread in the target VM.
 */
  public ThreadReference getThread(){
    return ((VMStartEvent)oneEvent).thread();
  }
  @Override public void notify(  JDIListener listener){
    listener.vmStart(this);
  }
}
