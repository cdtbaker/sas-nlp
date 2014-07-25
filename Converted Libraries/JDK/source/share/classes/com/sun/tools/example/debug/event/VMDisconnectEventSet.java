package com.sun.tools.example.debug.event;
import com.sun.jdi.event.*;
public class VMDisconnectEventSet extends AbstractEventSet {
  private static final long serialVersionUID=7968123152344675342L;
  VMDisconnectEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  @Override public void notify(  JDIListener listener){
    listener.vmDisconnect(this);
  }
}
