package com.sun.tools.jdi;
import com.sun.jdi.*;
public abstract class PrimitiveValueImpl extends ValueImpl implements PrimitiveValue {
  PrimitiveValueImpl(  VirtualMachine aVm){
    super(aVm);
  }
  abstract public boolean booleanValue();
  abstract public byte byteValue();
  abstract public char charValue();
  abstract public short shortValue();
  abstract public int intValue();
  abstract public long longValue();
  abstract public float floatValue();
  abstract public double doubleValue();
  byte checkedByteValue() throws InvalidTypeException {
    return byteValue();
  }
  char checkedCharValue() throws InvalidTypeException {
    return charValue();
  }
  short checkedShortValue() throws InvalidTypeException {
    return shortValue();
  }
  int checkedIntValue() throws InvalidTypeException {
    return intValue();
  }
  long checkedLongValue() throws InvalidTypeException {
    return longValue();
  }
  float checkedFloatValue() throws InvalidTypeException {
    return floatValue();
  }
  final boolean checkedBooleanValue() throws InvalidTypeException {
    if (this instanceof BooleanValue) {
      return booleanValue();
    }
 else {
      throw new InvalidTypeException("Can't convert non-boolean value to boolean");
    }
  }
  final double checkedDoubleValue() throws InvalidTypeException {
    return doubleValue();
  }
  ValueImpl prepareForAssignmentTo(  ValueContainer destination) throws InvalidTypeException {
    return convertForAssignmentTo(destination);
  }
  ValueImpl convertForAssignmentTo(  ValueContainer destination) throws InvalidTypeException {
    if (destination.signature().length() > 1) {
      throw new InvalidTypeException("Can't assign primitive value to object");
    }
    if ((destination.signature().charAt(0) == 'Z') && (type().signature().charAt(0) != 'Z')) {
      throw new InvalidTypeException("Can't assign non-boolean value to a boolean");
    }
    if ((destination.signature().charAt(0) != 'Z') && (type().signature().charAt(0) == 'Z')) {
      throw new InvalidTypeException("Can't assign boolean value to an non-boolean");
    }
    if ("void".equals(destination.typeName())) {
      throw new InvalidTypeException("Can't assign primitive value to a void");
    }
    try {
      PrimitiveTypeImpl primitiveType=(PrimitiveTypeImpl)destination.type();
      return (ValueImpl)(primitiveType.convert(this));
    }
 catch (    ClassNotLoadedException e) {
      throw new InternalException("Signature and type inconsistent for: " + destination.typeName());
    }
  }
}
