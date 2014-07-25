package com.sun.tools.example.debug.event;
import java.util.EventListener;
public interface JDIListener extends EventListener {
  void accessWatchpoint(  AccessWatchpointEventSet e);
  void classPrepare(  ClassPrepareEventSet e);
  void classUnload(  ClassUnloadEventSet e);
  void exception(  ExceptionEventSet e);
  void locationTrigger(  LocationTriggerEventSet e);
  void modificationWatchpoint(  ModificationWatchpointEventSet e);
  void threadDeath(  ThreadDeathEventSet e);
  void threadStart(  ThreadStartEventSet e);
  void vmDeath(  VMDeathEventSet e);
  void vmDisconnect(  VMDisconnectEventSet e);
  void vmStart(  VMStartEventSet e);
}
