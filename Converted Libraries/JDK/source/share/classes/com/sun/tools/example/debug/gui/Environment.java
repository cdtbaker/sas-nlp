package com.sun.tools.example.debug.gui;
import java.io.*;
import com.sun.jdi.*;
import com.sun.tools.example.debug.bdi.*;
public class Environment {
  private SourceManager sourceManager;
  private ClassManager classManager;
  private ContextManager contextManager;
  private MonitorListModel monitorListModel;
  private ExecutionManager runtime;
  private PrintWriter typeScript;
  private boolean verbose;
  public Environment(){
    this.classManager=new ClassManager(this);
    this.runtime=new ExecutionManager();
    this.sourceManager=new SourceManager(this);
    this.contextManager=new ContextManager(this);
    this.monitorListModel=new MonitorListModel(this);
  }
  public SourceManager getSourceManager(){
    return sourceManager;
  }
  public ClassManager getClassManager(){
    return classManager;
  }
  public ContextManager getContextManager(){
    return contextManager;
  }
  public MonitorListModel getMonitorListModel(){
    return monitorListModel;
  }
  public ExecutionManager getExecutionManager(){
    return runtime;
  }
  public void terminate(){
    System.exit(0);
  }
  public void setTypeScript(  PrintWriter writer){
    typeScript=writer;
  }
  public void error(  String message){
    if (typeScript != null) {
      typeScript.println(message);
    }
 else {
      System.out.println(message);
    }
  }
  public void failure(  String message){
    if (typeScript != null) {
      typeScript.println(message);
    }
 else {
      System.out.println(message);
    }
  }
  public void notice(  String message){
    if (typeScript != null) {
      typeScript.println(message);
    }
 else {
      System.out.println(message);
    }
  }
  public OutputSink getOutputSink(){
    return new OutputSink(typeScript);
  }
  public void viewSource(  String fileName){
    com.sun.tools.example.debug.gui.GUI.srcTool.showSourceFile(fileName);
  }
  public void viewLocation(  Location locn){
    com.sun.tools.example.debug.gui.GUI.srcTool.showSourceForLocation(locn);
  }
  public boolean getVerboseFlag(){
    return verbose;
  }
  public void setVerboseFlag(  boolean verbose){
    this.verbose=verbose;
  }
}
