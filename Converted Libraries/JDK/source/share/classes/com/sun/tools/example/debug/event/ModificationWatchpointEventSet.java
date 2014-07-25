package com.sun.tools.example.debug.event;
import com.sun.jdi.*;
import com.sun.jdi.event.*;
public class ModificationWatchpointEventSet extends WatchpointEventSet {
  private static final long serialVersionUID=-680889300856154719L;
  ModificationWatchpointEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  /** 
 * Value that will be assigned to the field when the instruction
 * completes.
 */
  public Value getValueToBe(){
    return ((ModificationWatchpointEvent)oneEvent).valueToBe();
  }
  @Override public void notify(  JDIListener listener){
    listener.modificationWatchpoint(this);
  }
}
