package com.sun.tools.hat.internal.model;
/** 
 * Represents a stack frame.
 */
public class StackFrame {
  public final static int LINE_NUMBER_UNKNOWN=-1;
  public final static int LINE_NUMBER_COMPILED=-2;
  public final static int LINE_NUMBER_NATIVE=-3;
  private String methodName;
  private String methodSignature;
  private String className;
  private String sourceFileName;
  private int lineNumber;
  public StackFrame(  String methodName,  String methodSignature,  String className,  String sourceFileName,  int lineNumber){
    this.methodName=methodName;
    this.methodSignature=methodSignature;
    this.className=className;
    this.sourceFileName=sourceFileName;
    this.lineNumber=lineNumber;
  }
  public void resolve(  Snapshot snapshot){
  }
  public String getMethodName(){
    return methodName;
  }
  public String getMethodSignature(){
    return methodSignature;
  }
  public String getClassName(){
    return className;
  }
  public String getSourceFileName(){
    return sourceFileName;
  }
  public String getLineNumber(){
switch (lineNumber) {
case LINE_NUMBER_UNKNOWN:
      return "(unknown)";
case LINE_NUMBER_COMPILED:
    return "(compiled method)";
case LINE_NUMBER_NATIVE:
  return "(native method)";
default :
return Integer.toString(lineNumber,10);
}
}
}
