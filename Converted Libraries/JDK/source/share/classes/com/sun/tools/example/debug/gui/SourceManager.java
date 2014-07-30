package com.sun.tools.example.debug.gui;
import java.io.*;
import java.util.*;
import com.sun.jdi.*;
import com.sun.tools.example.debug.event.*;
/** 
 * Manage the list of source files.
 * Origin of SourceListener events.
 */
public class SourceManager {
  private List<SourceModel> sourceList;
  private SearchPath sourcePath;
  private ArrayList<SourceListener> sourceListeners=new ArrayList<SourceListener>();
  private Map<ReferenceType,SourceModel> classToSource=new HashMap<ReferenceType,SourceModel>();
  private Environment env;
  /** 
 * Hold on to it so it can be removed.
 */
  private SMClassListener classListener=new SMClassListener();
  public SourceManager(  Environment env){
    this(env,new SearchPath(""));
  }
  public SourceManager(  Environment env,  SearchPath sourcePath){
    this.env=env;
    this.sourceList=new LinkedList<SourceModel>();
    this.sourcePath=sourcePath;
    env.getExecutionManager().addJDIListener(classListener);
  }
  /** 
 * Set path for access to source code.
 */
  public void setSourcePath(  SearchPath sp){
    sourcePath=sp;
    sourceList=new LinkedList<SourceModel>();
    notifySourcepathChanged();
    classToSource=new HashMap<ReferenceType,SourceModel>();
  }
  public void addSourceListener(  SourceListener l){
    sourceListeners.add(l);
  }
  public void removeSourceListener(  SourceListener l){
    sourceListeners.remove(l);
  }
  private void notifySourcepathChanged(){
    ArrayList<SourceListener> l=new ArrayList<SourceListener>(sourceListeners);
    SourcepathChangedEvent evt=new SourcepathChangedEvent(this);
    for (int i=0; i < l.size(); i++) {
      l.get(i).sourcepathChanged(evt);
    }
  }
  /** 
 * Get path for access to source code.
 */
  public SearchPath getSourcePath(){
    return sourcePath;
  }
  /** 
 * Get source object associated with a Location.
 */
  public SourceModel sourceForLocation(  Location loc){
    return sourceForClass(loc.declaringType());
  }
  /** 
 * Get source object associated with a class or interface.
 * Returns null if not available.
 */
  public SourceModel sourceForClass(  ReferenceType refType){
    SourceModel sm=classToSource.get(refType);
    if (sm != null) {
      return sm;
    }
    try {
      String filename=refType.sourceName();
      String refName=refType.name();
      int iDot=refName.lastIndexOf('.');
      String pkgName=(iDot >= 0) ? refName.substring(0,iDot + 1) : "";
      String full=pkgName.replace('.',File.separatorChar) + filename;
      File path=sourcePath.resolve(full);
      if (path != null) {
        sm=sourceForFile(path);
        classToSource.put(refType,sm);
        return sm;
      }
      return null;
    }
 catch (    AbsentInformationException e) {
      return null;
    }
  }
  /** 
 * Get source object associated with an absolute file path.
 */
  public SourceModel sourceForFile(  File path){
    Iterator<SourceModel> iter=sourceList.iterator();
    SourceModel sm=null;
    while (iter.hasNext()) {
      SourceModel candidate=iter.next();
      if (candidate.fileName().equals(path)) {
        sm=candidate;
        iter.remove();
        break;
      }
    }
    if (sm == null && path.exists()) {
      sm=new SourceModel(env,path);
    }
    if (sm != null) {
      sourceList.add(0,sm);
    }
    return sm;
  }
private class SMClassListener extends JDIAdapter implements JDIListener {
    @Override public void classPrepare(    ClassPrepareEventSet e){
      ReferenceType refType=e.getReferenceType();
      SourceModel sm=sourceForClass(refType);
      if (sm != null) {
        sm.addClass(refType);
      }
    }
    @Override public void classUnload(    ClassUnloadEventSet e){
    }
  }
}