package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
public class ArrayReferenceImpl extends ObjectReferenceImpl implements ArrayReference {
  int length=-1;
  ArrayReferenceImpl(  VirtualMachine aVm,  long aRef){
    super(aVm,aRef);
  }
  protected ClassTypeImpl invokableReferenceType(  Method method){
    return (ClassTypeImpl)method.declaringType();
  }
  ArrayTypeImpl arrayType(){
    return (ArrayTypeImpl)type();
  }
  /** 
 * Return array length.
 * Need not be synchronized since it cannot be provably stale.
 */
  public int length(){
    if (length == -1) {
      try {
        length=JDWP.ArrayReference.Length.process(vm,this).arrayLength;
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
    return length;
  }
  public Value getValue(  int index){
    List list=getValues(index,1);
    return (Value)list.get(0);
  }
  public List<Value> getValues(){
    return getValues(0,-1);
  }
  /** 
 * Validate that the range to set/get is valid.
 * length of -1 (meaning rest of array) has been converted
 * before entry.
 */
  private void validateArrayAccess(  int index,  int length){
    if ((index < 0) || (index > length())) {
      throw new IndexOutOfBoundsException("Invalid array index: " + index);
    }
    if (length < 0) {
      throw new IndexOutOfBoundsException("Invalid array range length: " + length);
    }
    if (index + length > length()) {
      throw new IndexOutOfBoundsException("Invalid array range: " + index + " to "+ (index + length - 1));
    }
  }
  @SuppressWarnings("unchecked") private static <T>T cast(  Object x){
    return (T)x;
  }
  public List<Value> getValues(  int index,  int length){
    if (length == -1) {
      length=length() - index;
    }
    validateArrayAccess(index,length);
    if (length == 0) {
      return new ArrayList<Value>();
    }
    List<Value> vals;
    try {
      vals=cast(JDWP.ArrayReference.GetValues.process(vm,this,index,length).values);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
    return vals;
  }
  public void setValue(  int index,  Value value) throws InvalidTypeException, ClassNotLoadedException {
    List<Value> list=new ArrayList<Value>(1);
    list.add(value);
    setValues(index,list,0,1);
  }
  public void setValues(  List<? extends Value> values) throws InvalidTypeException, ClassNotLoadedException {
    setValues(0,values,0,-1);
  }
  public void setValues(  int index,  List<? extends Value> values,  int srcIndex,  int length) throws InvalidTypeException, ClassNotLoadedException {
    if (length == -1) {
      length=Math.min(length() - index,values.size() - srcIndex);
    }
    validateMirrorsOrNulls(values);
    validateArrayAccess(index,length);
    if ((srcIndex < 0) || (srcIndex > values.size())) {
      throw new IndexOutOfBoundsException("Invalid source index: " + srcIndex);
    }
    if (srcIndex + length > values.size()) {
      throw new IndexOutOfBoundsException("Invalid source range: " + srcIndex + " to "+ (srcIndex + length - 1));
    }
    boolean somethingToSet=false;
    ;
    ValueImpl[] setValues=new ValueImpl[length];
    for (int i=0; i < length; i++) {
      ValueImpl value=(ValueImpl)values.get(srcIndex + i);
      try {
        setValues[i]=ValueImpl.prepareForAssignment(value,new Component());
        somethingToSet=true;
      }
 catch (      ClassNotLoadedException e) {
        if (value != null) {
          throw e;
        }
      }
    }
    if (somethingToSet) {
      try {
        JDWP.ArrayReference.SetValues.process(vm,this,index,setValues);
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
  }
  public String toString(){
    return "instance of " + arrayType().componentTypeName() + "["+ length()+ "] (id="+ uniqueID()+ ")";
  }
  byte typeValueKey(){
    return JDWP.Tag.ARRAY;
  }
  void validateAssignment(  ValueContainer destination) throws InvalidTypeException, ClassNotLoadedException {
    try {
      super.validateAssignment(destination);
    }
 catch (    ClassNotLoadedException e) {
      boolean valid=false;
      JNITypeParser destParser=new JNITypeParser(destination.signature());
      JNITypeParser srcParser=new JNITypeParser(arrayType().signature());
      int destDims=destParser.dimensionCount();
      if (destDims <= srcParser.dimensionCount()) {
        String destComponentSignature=destParser.componentSignature(destDims);
        Type destComponentType=destination.findType(destComponentSignature);
        String srcComponentSignature=srcParser.componentSignature(destDims);
        Type srcComponentType=arrayType().findComponentType(srcComponentSignature);
        valid=ArrayTypeImpl.isComponentAssignable(destComponentType,srcComponentType);
      }
      if (!valid) {
        throw new InvalidTypeException("Cannot assign " + arrayType().name() + " to "+ destination.typeName());
      }
    }
  }
class Component implements ValueContainer {
    public Type type() throws ClassNotLoadedException {
      return arrayType().componentType();
    }
    public String typeName(){
      return arrayType().componentTypeName();
    }
    public String signature(){
      return arrayType().componentSignature();
    }
    public Type findType(    String signature) throws ClassNotLoadedException {
      return arrayType().findComponentType(signature);
    }
  }
}
