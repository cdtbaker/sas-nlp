package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.*;
public class ThreadGroupReferenceImpl extends ObjectReferenceImpl implements ThreadGroupReference, VMListener {
  String name;
  ThreadGroupReference parent;
  boolean triedParent;
private static class Cache extends ObjectReferenceImpl.Cache {
    JDWP.ThreadGroupReference.Children kids=null;
  }
  protected ObjectReferenceImpl.Cache newCache(){
    return new Cache();
  }
  ThreadGroupReferenceImpl(  VirtualMachine aVm,  long aRef){
    super(aVm,aRef);
    vm.state().addListener(this);
  }
  protected String description(){
    return "ThreadGroupReference " + uniqueID();
  }
  public String name(){
    if (name == null) {
      try {
        name=JDWP.ThreadGroupReference.Name.process(vm,this).groupName;
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
    return name;
  }
  public ThreadGroupReference parent(){
    if (!triedParent) {
      try {
        parent=JDWP.ThreadGroupReference.Parent.process(vm,this).parentGroup;
        triedParent=true;
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
    return parent;
  }
  public void suspend(){
    for (    ThreadReference thread : threads()) {
      thread.suspend();
    }
    for (    ThreadGroupReference threadGroup : threadGroups()) {
      threadGroup.suspend();
    }
  }
  public void resume(){
    for (    ThreadReference thread : threads()) {
      thread.resume();
    }
    for (    ThreadGroupReference threadGroup : threadGroups()) {
      threadGroup.resume();
    }
  }
  private JDWP.ThreadGroupReference.Children kids(){
    JDWP.ThreadGroupReference.Children kids=null;
    try {
      Cache local=(Cache)getCache();
      if (local != null) {
        kids=local.kids;
      }
      if (kids == null) {
        kids=JDWP.ThreadGroupReference.Children.process(vm,this);
        if (local != null) {
          local.kids=kids;
          if ((vm.traceFlags & vm.TRACE_OBJREFS) != 0) {
            vm.printTrace(description() + " temporarily caching children ");
          }
        }
      }
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
    return kids;
  }
  public List<ThreadReference> threads(){
    return Arrays.asList((ThreadReference[])kids().childThreads);
  }
  public List<ThreadGroupReference> threadGroups(){
    return Arrays.asList((ThreadGroupReference[])kids().childGroups);
  }
  public String toString(){
    return "instance of " + referenceType().name() + "(name='"+ name()+ "', "+ "id="+ uniqueID()+ ")";
  }
  byte typeValueKey(){
    return JDWP.Tag.THREAD_GROUP;
  }
}
