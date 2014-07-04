package com.sun.tools.example.debug.event;
import com.sun.jdi.event.*;
public class AccessWatchpointEventSet extends WatchpointEventSet {
  private static final long serialVersionUID=-2620394219156607673L;
  AccessWatchpointEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  @Override public void notify(  JDIListener listener){
    listener.accessWatchpoint(this);
  }
}
