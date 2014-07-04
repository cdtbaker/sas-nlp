package com.sun.tools.example.debug.tty;
import com.sun.jdi.*;
import com.sun.jdi.request.*;
import java.util.ArrayList;
import java.util.List;
class BreakpointSpec extends EventRequestSpec {
  String methodId;
  List<String> methodArgs;
  int lineNumber;
  BreakpointSpec(  ReferenceTypeSpec refSpec,  int lineNumber){
    super(refSpec);
    this.methodId=null;
    this.methodArgs=null;
    this.lineNumber=lineNumber;
  }
  BreakpointSpec(  ReferenceTypeSpec refSpec,  String methodId,  List<String> methodArgs) throws MalformedMemberNameException {
    super(refSpec);
    this.methodId=methodId;
    this.methodArgs=methodArgs;
    this.lineNumber=0;
    if (!isValidMethodName(methodId)) {
      throw new MalformedMemberNameException(methodId);
    }
  }
  /** 
 * The 'refType' is known to match, return the EventRequest.
 */
  @Override EventRequest resolveEventRequest(  ReferenceType refType) throws AmbiguousMethodException, AbsentInformationException, InvalidTypeException, NoSuchMethodException, LineNotFoundException {
    Location location=location(refType);
    if (location == null) {
      throw new InvalidTypeException();
    }
    EventRequestManager em=refType.virtualMachine().eventRequestManager();
    EventRequest bp=em.createBreakpointRequest(location);
    bp.setSuspendPolicy(suspendPolicy);
    bp.enable();
    return bp;
  }
  String methodName(){
    return methodId;
  }
  int lineNumber(){
    return lineNumber;
  }
  List<String> methodArgs(){
    return methodArgs;
  }
  boolean isMethodBreakpoint(){
    return (methodId != null);
  }
  @Override public int hashCode(){
    return refSpec.hashCode() + lineNumber + ((methodId != null) ? methodId.hashCode() : 0)+ ((methodArgs != null) ? methodArgs.hashCode() : 0);
  }
  @Override public boolean equals(  Object obj){
    if (obj instanceof BreakpointSpec) {
      BreakpointSpec breakpoint=(BreakpointSpec)obj;
      return ((methodId != null) ? methodId.equals(breakpoint.methodId) : methodId == breakpoint.methodId) && ((methodArgs != null) ? methodArgs.equals(breakpoint.methodArgs) : methodArgs == breakpoint.methodArgs) && refSpec.equals(breakpoint.refSpec)&& (lineNumber == breakpoint.lineNumber);
    }
 else {
      return false;
    }
  }
  @Override String errorMessageFor(  Exception e){
    if (e instanceof AmbiguousMethodException) {
      return (MessageOutput.format("Method is overloaded; specify arguments",methodName()));
    }
 else     if (e instanceof NoSuchMethodException) {
      return (MessageOutput.format("No method in",new Object[]{methodName(),refSpec.toString()}));
    }
 else     if (e instanceof AbsentInformationException) {
      return (MessageOutput.format("No linenumber information for",refSpec.toString()));
    }
 else     if (e instanceof LineNotFoundException) {
      return (MessageOutput.format("No code at line",new Object[]{new Long(lineNumber()),refSpec.toString()}));
    }
 else     if (e instanceof InvalidTypeException) {
      return (MessageOutput.format("Breakpoints can be located only in classes.",refSpec.toString()));
    }
 else {
      return super.errorMessageFor(e);
    }
  }
  @Override public String toString(){
    StringBuffer buffer=new StringBuffer(refSpec.toString());
    if (isMethodBreakpoint()) {
      buffer.append('.');
      buffer.append(methodId);
      if (methodArgs != null) {
        boolean first=true;
        buffer.append('(');
        for (        String arg : methodArgs) {
          if (!first) {
            buffer.append(',');
          }
          buffer.append(arg);
          first=false;
        }
        buffer.append(")");
      }
    }
 else {
      buffer.append(':');
      buffer.append(lineNumber);
    }
    return MessageOutput.format("breakpoint",buffer.toString());
  }
  private Location location(  ReferenceType refType) throws AmbiguousMethodException, AbsentInformationException, NoSuchMethodException, LineNotFoundException {
    Location location=null;
    if (isMethodBreakpoint()) {
      Method method=findMatchingMethod(refType);
      location=method.location();
    }
 else {
      List<Location> locs=refType.locationsOfLine(lineNumber());
      if (locs.size() == 0) {
        throw new LineNotFoundException();
      }
      location=locs.get(0);
      if (location.method() == null) {
        throw new LineNotFoundException();
      }
    }
    return location;
  }
  private boolean isValidMethodName(  String s){
    return isJavaIdentifier(s) || s.equals("<init>") || s.equals("<clinit>");
  }
  private boolean compareArgTypes(  Method method,  List<String> nameList){
    List<String> argTypeNames=method.argumentTypeNames();
    if (argTypeNames.size() != nameList.size()) {
      return false;
    }
    int nTypes=argTypeNames.size();
    for (int i=0; i < nTypes; ++i) {
      String comp1=argTypeNames.get(i);
      String comp2=nameList.get(i);
      if (!comp1.equals(comp2)) {
        if (i != nTypes - 1 || !method.isVarArgs() || !comp2.endsWith("...")) {
          return false;
        }
        int comp1Length=comp1.length();
        if (comp1Length + 1 != comp2.length()) {
          return false;
        }
        if (!comp1.regionMatches(0,comp2,0,comp1Length - 2)) {
          return false;
        }
        return true;
      }
    }
    return true;
  }
  private String normalizeArgTypeName(  String name){
    int i=0;
    StringBuffer typePart=new StringBuffer();
    StringBuffer arrayPart=new StringBuffer();
    name=name.trim();
    int nameLength=name.length();
    boolean isVarArgs=name.endsWith("...");
    if (isVarArgs) {
      nameLength-=3;
    }
    while (i < nameLength) {
      char c=name.charAt(i);
      if (Character.isWhitespace(c) || c == '[') {
        break;
      }
      typePart.append(c);
      i++;
    }
    while (i < nameLength) {
      char c=name.charAt(i);
      if ((c == '[') || (c == ']')) {
        arrayPart.append(c);
      }
 else       if (!Character.isWhitespace(c)) {
        throw new IllegalArgumentException(MessageOutput.format("Invalid argument type name"));
      }
      i++;
    }
    name=typePart.toString();
    if ((name.indexOf('.') == -1) || name.startsWith("*.")) {
      try {
        ReferenceType argClass=Env.getReferenceTypeFromToken(name);
        if (argClass != null) {
          name=argClass.name();
        }
      }
 catch (      IllegalArgumentException e) {
      }
    }
    name+=arrayPart.toString();
    if (isVarArgs) {
      name+="...";
    }
    return name;
  }
  private Method findMatchingMethod(  ReferenceType refType) throws AmbiguousMethodException, NoSuchMethodException {
    List<String> argTypeNames=null;
    if (methodArgs() != null) {
      argTypeNames=new ArrayList<String>(methodArgs().size());
      for (      String name : methodArgs()) {
        name=normalizeArgTypeName(name);
        argTypeNames.add(name);
      }
    }
    Method firstMatch=null;
    Method exactMatch=null;
    int matchCount=0;
    for (    Method candidate : refType.methods()) {
      if (candidate.name().equals(methodName())) {
        matchCount++;
        if (matchCount == 1) {
          firstMatch=candidate;
        }
        if ((argTypeNames != null) && compareArgTypes(candidate,argTypeNames) == true) {
          exactMatch=candidate;
          break;
        }
      }
    }
    Method method=null;
    if (exactMatch != null) {
      method=exactMatch;
    }
 else     if ((argTypeNames == null) && (matchCount > 0)) {
      if (matchCount == 1) {
        method=firstMatch;
      }
 else {
        throw new AmbiguousMethodException();
      }
    }
 else {
      throw new NoSuchMethodException(methodName());
    }
    return method;
  }
}
