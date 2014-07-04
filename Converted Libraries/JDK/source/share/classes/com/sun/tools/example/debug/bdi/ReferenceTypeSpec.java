package com.sun.tools.example.debug.bdi;
import com.sun.jdi.*;
interface ReferenceTypeSpec {
  /** 
 * Does the specified ReferenceType match this spec.
 */
  boolean matches(  ReferenceType refType);
  @Override int hashCode();
  @Override boolean equals(  Object obj);
}
