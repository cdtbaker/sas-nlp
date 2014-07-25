package com.sun.tools.example.debug.bdi;
import java.util.EventListener;
public interface SpecListener extends EventListener {
  void breakpointSet(  SpecEvent e);
  void breakpointDeferred(  SpecEvent e);
  void breakpointDeleted(  SpecEvent e);
  void breakpointResolved(  SpecEvent e);
  void breakpointError(  SpecErrorEvent e);
  void watchpointSet(  SpecEvent e);
  void watchpointDeferred(  SpecEvent e);
  void watchpointDeleted(  SpecEvent e);
  void watchpointResolved(  SpecEvent e);
  void watchpointError(  SpecErrorEvent e);
  void exceptionInterceptSet(  SpecEvent e);
  void exceptionInterceptDeferred(  SpecEvent e);
  void exceptionInterceptDeleted(  SpecEvent e);
  void exceptionInterceptResolved(  SpecEvent e);
  void exceptionInterceptError(  SpecErrorEvent e);
}
