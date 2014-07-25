package com.sun.tools.example.debug.event;
import com.sun.jdi.event.*;
public class ClassUnloadEventSet extends AbstractEventSet {
  private static final long serialVersionUID=8370341450345835866L;
  ClassUnloadEventSet(  EventSet jdiEventSet){
    super(jdiEventSet);
  }
  /** 
 * Returns the name of the class that has been unloaded.
 */
  public String getClassName(){
    return ((ClassUnloadEvent)oneEvent).className();
  }
  /** 
 * Returns the JNI-style signature of the class that has been unloaded.
 */
  public String getClassSignature(){
    return ((ClassUnloadEvent)oneEvent).classSignature();
  }
  @Override public void notify(  JDIListener listener){
    listener.classUnload(this);
  }
}
