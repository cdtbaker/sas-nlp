package com.sun.tools.example.debug.event;
import com.sun.jdi.*;
import com.sun.jdi.event.*;
public abstract class WatchpointEventSet extends LocatableEventSet {
  private static final long serialVersionUID=5606285209703845409L;
  WatchpointEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  /** 
 * Returns the field that is about to be accessed/modified.
 * @return a {@link Field} which mirrors the field
 * in the target VM.
 */
  public Field getField(){
    return ((WatchpointEvent)oneEvent).field();
  }
  /** 
 * Returns the object whose field is about to be accessed/modified.
 * Return null is the access is to a static field.
 * @return a {@link ObjectReference} which mirrors the event's
 * object in the target VM.
 */
  public ObjectReference getObject(){
    return ((WatchpointEvent)oneEvent).object();
  }
  /** 
 * Current value of the field.
 */
  public Value getValueCurrent(){
    return ((WatchpointEvent)oneEvent).valueCurrent();
  }
}
