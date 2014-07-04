package com.sun.tools.example.debug.bdi;
import java.util.EventObject;
import java.util.EventListener;
public interface SessionListener extends EventListener {
  void sessionStart(  EventObject e);
  void sessionInterrupt(  EventObject e);
  void sessionContinue(  EventObject e);
}
