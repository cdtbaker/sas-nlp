package com.sun.tools.jdi;
import com.sun.jdi.*;
abstract class PrimitiveTypeImpl extends TypeImpl implements PrimitiveType {
  PrimitiveTypeImpl(  VirtualMachine vm){
    super(vm);
  }
  abstract PrimitiveValue convert(  PrimitiveValue value) throws InvalidTypeException ;
  public String toString(){
    return name();
  }
}
