package com.sun.tools.example.debug.tty;
import com.sun.jdi.*;
import com.sun.jdi.request.ClassPrepareRequest;
interface ReferenceTypeSpec {
  /** 
 * Does the specified ReferenceType match this spec.
 */
  boolean matches(  ReferenceType refType);
  ClassPrepareRequest createPrepareRequest();
  @Override int hashCode();
  @Override boolean equals(  Object obj);
}
