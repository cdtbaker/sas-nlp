package com.sun.tools.example.debug.event;
import com.sun.jdi.*;
import com.sun.jdi.event.*;
/** 
 * Abstract event set for events with location and thread.
 */
public abstract class LocatableEventSet extends AbstractEventSet {
  private static final long serialVersionUID=1027131209997915620L;
  LocatableEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  /** 
 * Returns the {@link Location} of this mirror. Depending on context
 * and on available debug information, this location will have
 * varying precision.
 * @return the {@link Location} of this mirror.
 */
  public Location getLocation(){
    return ((LocatableEvent)oneEvent).location();
  }
  /** 
 * Returns the thread in which this event has occurred.
 * @return a {@link ThreadReference} which mirrors the event's thread in
 * the target VM.
 */
  public ThreadReference getThread(){
    return ((LocatableEvent)oneEvent).thread();
  }
}
