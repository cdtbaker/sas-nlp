package com.sun.tools.jdi;
import com.sun.jdi.*;
public class LocalVariableImpl extends MirrorImpl implements LocalVariable, ValueContainer {
  private final Method method;
  private final int slot;
  private final Location scopeStart;
  private final Location scopeEnd;
  private final String name;
  private final String signature;
  private String genericSignature=null;
  LocalVariableImpl(  VirtualMachine vm,  Method method,  int slot,  Location scopeStart,  Location scopeEnd,  String name,  String signature,  String genericSignature){
    super(vm);
    this.method=method;
    this.slot=slot;
    this.scopeStart=scopeStart;
    this.scopeEnd=scopeEnd;
    this.name=name;
    this.signature=signature;
    if (genericSignature != null && genericSignature.length() > 0) {
      this.genericSignature=genericSignature;
    }
 else {
      this.genericSignature=null;
    }
  }
  public boolean equals(  Object obj){
    if ((obj != null) && (obj instanceof LocalVariableImpl)) {
      LocalVariableImpl other=(LocalVariableImpl)obj;
      return ((slot() == other.slot()) && (scopeStart != null) && (scopeStart.equals(other.scopeStart))&& (super.equals(obj)));
    }
 else {
      return false;
    }
  }
  public int hashCode(){
    return ((scopeStart.hashCode() << 4) + slot());
  }
  public int compareTo(  LocalVariable object){
    LocalVariableImpl other=(LocalVariableImpl)object;
    int rc=scopeStart.compareTo(other.scopeStart);
    if (rc == 0) {
      rc=slot() - other.slot();
    }
    return rc;
  }
  public String name(){
    return name;
  }
  /** 
 * @return a text representation of the declared type
 * of this variable.
 */
  public String typeName(){
    JNITypeParser parser=new JNITypeParser(signature);
    return parser.typeName();
  }
  public Type type() throws ClassNotLoadedException {
    return findType(signature());
  }
  public Type findType(  String signature) throws ClassNotLoadedException {
    ReferenceTypeImpl enclosing=(ReferenceTypeImpl)method.declaringType();
    return enclosing.findType(signature);
  }
  public String signature(){
    return signature;
  }
  public String genericSignature(){
    return genericSignature;
  }
  public boolean isVisible(  StackFrame frame){
    validateMirror(frame);
    Method frameMethod=frame.location().method();
    if (!frameMethod.equals(method)) {
      throw new IllegalArgumentException("frame method different than variable's method");
    }
    if (frameMethod.isNative()) {
      return false;
    }
    return ((scopeStart.compareTo(frame.location()) <= 0) && (scopeEnd.compareTo(frame.location()) >= 0));
  }
  public boolean isArgument(){
    try {
      MethodImpl method=(MethodImpl)scopeStart.method();
      return (slot < method.argSlotCount());
    }
 catch (    AbsentInformationException e) {
      throw new InternalException();
    }
  }
  int slot(){
    return slot;
  }
  boolean hides(  LocalVariable other){
    LocalVariableImpl otherImpl=(LocalVariableImpl)other;
    if (!method.equals(otherImpl.method) || !name.equals(otherImpl.name)) {
      return false;
    }
 else {
      return (scopeStart.compareTo(otherImpl.scopeStart) > 0);
    }
  }
  public String toString(){
    return name() + " in " + method.toString()+ "@"+ scopeStart.toString();
  }
}
