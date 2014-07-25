package com.sun.tools.example.debug.event;
import com.sun.jdi.*;
import com.sun.jdi.event.*;
public class ThreadDeathEventSet extends AbstractEventSet {
  private static final long serialVersionUID=-8801604712308151331L;
  ThreadDeathEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  /** 
 * Returns the thread which is terminating.
 * @return a {@link ThreadReference} which mirrors the event's thread in
 * the target VM.
 */
  public ThreadReference getThread(){
    return ((ThreadDeathEvent)oneEvent).thread();
  }
  @Override public void notify(  JDIListener listener){
    listener.threadDeath(this);
  }
}
