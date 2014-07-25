package com.sun.tools.example.debug.gui;
import java.util.EventObject;
public class SourcepathChangedEvent extends EventObject {
  private static final long serialVersionUID=8762169481005804121L;
  public SourcepathChangedEvent(  Object source){
    super(source);
  }
}
