package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.EventObject;
class ThreadAction extends EventObject {
  static final int THREAD_RESUMABLE=2;
  int id;
  ThreadAction(  ThreadReference thread,  int id){
    super(thread);
    this.id=id;
  }
  ThreadReference thread(){
    return (ThreadReference)getSource();
  }
  int id(){
    return id;
  }
}
