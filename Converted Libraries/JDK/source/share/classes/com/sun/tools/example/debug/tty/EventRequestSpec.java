package com.sun.tools.example.debug.tty;
import com.sun.jdi.*;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.event.ClassPrepareEvent;
import java.util.ArrayList;
abstract class EventRequestSpec {
  final ReferenceTypeSpec refSpec;
  int suspendPolicy=EventRequest.SUSPEND_ALL;
  EventRequest resolved=null;
  ClassPrepareRequest prepareRequest=null;
  EventRequestSpec(  ReferenceTypeSpec refSpec){
    this.refSpec=refSpec;
  }
  /** 
 * The 'refType' is known to match, return the EventRequest.
 */
  abstract EventRequest resolveEventRequest(  ReferenceType refType) throws Exception ;
  /** 
 * @return If this EventRequestSpec matches the 'refType'
 * return the cooresponding EventRequest.  Otherwise
 * return null.
 */
  synchronized EventRequest resolve(  ClassPrepareEvent event) throws Exception {
    if ((resolved == null) && (prepareRequest != null) && prepareRequest.equals(event.request())) {
      resolved=resolveEventRequest(event.referenceType());
      prepareRequest.disable();
      Env.vm().eventRequestManager().deleteEventRequest(prepareRequest);
      prepareRequest=null;
      if (refSpec instanceof PatternReferenceTypeSpec) {
        PatternReferenceTypeSpec prs=(PatternReferenceTypeSpec)refSpec;
        if (!prs.isUnique()) {
          resolved=null;
          prepareRequest=refSpec.createPrepareRequest();
          prepareRequest.enable();
        }
      }
    }
    return resolved;
  }
  synchronized void remove(){
    if (isResolved()) {
      Env.vm().eventRequestManager().deleteEventRequest(resolved());
    }
    if (refSpec instanceof PatternReferenceTypeSpec) {
      PatternReferenceTypeSpec prs=(PatternReferenceTypeSpec)refSpec;
      if (!prs.isUnique()) {
        ArrayList<ExceptionRequest> deleteList=new ArrayList<ExceptionRequest>();
        for (        ExceptionRequest er : Env.vm().eventRequestManager().exceptionRequests()) {
          if (prs.matches(er.exception())) {
            deleteList.add(er);
          }
        }
        Env.vm().eventRequestManager().deleteEventRequests(deleteList);
      }
    }
  }
  private EventRequest resolveAgainstPreparedClasses() throws Exception {
    for (    ReferenceType refType : Env.vm().allClasses()) {
      if (refType.isPrepared() && refSpec.matches(refType)) {
        resolved=resolveEventRequest(refType);
      }
    }
    return resolved;
  }
  synchronized EventRequest resolveEagerly() throws Exception {
    try {
      if (resolved == null) {
        prepareRequest=refSpec.createPrepareRequest();
        prepareRequest.enable();
        resolveAgainstPreparedClasses();
        if (resolved != null) {
          prepareRequest.disable();
          Env.vm().eventRequestManager().deleteEventRequest(prepareRequest);
          prepareRequest=null;
        }
      }
      if (refSpec instanceof PatternReferenceTypeSpec) {
        PatternReferenceTypeSpec prs=(PatternReferenceTypeSpec)refSpec;
        if (!prs.isUnique()) {
          resolved=null;
          if (prepareRequest == null) {
            prepareRequest=refSpec.createPrepareRequest();
            prepareRequest.enable();
          }
        }
      }
    }
 catch (    VMNotConnectedException e) {
    }
    return resolved;
  }
  /** 
 * @return the eventRequest this spec has been resolved to,
 * null if so far unresolved.
 */
  EventRequest resolved(){
    return resolved;
  }
  /** 
 * @return true if this spec has been resolved.
 */
  boolean isResolved(){
    return resolved != null;
  }
  protected boolean isJavaIdentifier(  String s){
    if (s.length() == 0) {
      return false;
    }
    int cp=s.codePointAt(0);
    if (!Character.isJavaIdentifierStart(cp)) {
      return false;
    }
    for (int i=Character.charCount(cp); i < s.length(); i+=Character.charCount(cp)) {
      cp=s.codePointAt(i);
      if (!Character.isJavaIdentifierPart(cp)) {
        return false;
      }
    }
    return true;
  }
  String errorMessageFor(  Exception e){
    if (e instanceof IllegalArgumentException) {
      return (MessageOutput.format("Invalid command syntax"));
    }
 else     if (e instanceof RuntimeException) {
      throw (RuntimeException)e;
    }
 else {
      return (MessageOutput.format("Internal error; unable to set",this.refSpec.toString()));
    }
  }
}
