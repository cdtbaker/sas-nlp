package com.sun.tools.example.debug.bdi;
import com.sun.jdi.*;
public class AccessWatchpointSpec extends WatchpointSpec {
  AccessWatchpointSpec(  EventRequestSpecList specs,  ReferenceTypeSpec refSpec,  String fieldId){
    super(specs,refSpec,fieldId);
  }
  /** 
 * The 'refType' is known to match.
 */
  @Override void resolve(  ReferenceType refType) throws InvalidTypeException, NoSuchFieldException {
    if (!(refType instanceof ClassType)) {
      throw new InvalidTypeException();
    }
    Field field=refType.fieldByName(fieldId);
    if (field == null) {
      throw new NoSuchFieldException(fieldId);
    }
    setRequest(refType.virtualMachine().eventRequestManager().createAccessWatchpointRequest(field));
  }
  @Override public boolean equals(  Object obj){
    return (obj instanceof AccessWatchpointSpec) && super.equals(obj);
  }
}
