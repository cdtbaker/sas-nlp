package com.sun.tools.example.debug.event;
import com.sun.jdi.*;
import com.sun.jdi.event.*;
public class ThreadStartEventSet extends AbstractEventSet {
  private static final long serialVersionUID=-3802096132294933502L;
  ThreadStartEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  /** 
 * Returns the thread which has started.
 * @return a {@link ThreadReference} which mirrors the event's thread in
 * the target VM.
 */
  public ThreadReference getThread(){
    return ((ThreadStartEvent)oneEvent).thread();
  }
  @Override public void notify(  JDIListener listener){
    listener.threadStart(this);
  }
}
