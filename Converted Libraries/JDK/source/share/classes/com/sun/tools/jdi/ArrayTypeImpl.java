package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
public class ArrayTypeImpl extends ReferenceTypeImpl implements ArrayType {
  protected ArrayTypeImpl(  VirtualMachine aVm,  long aRef){
    super(aVm,aRef);
  }
  public ArrayReference newInstance(  int length){
    try {
      return (ArrayReference)JDWP.ArrayType.NewInstance.process(vm,this,length).newArray;
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  public String componentSignature(){
    return signature().substring(1);
  }
  public String componentTypeName(){
    JNITypeParser parser=new JNITypeParser(componentSignature());
    return parser.typeName();
  }
  Type type() throws ClassNotLoadedException {
    return findType(componentSignature());
  }
  void addVisibleMethods(  Map map){
  }
  public List<Method> allMethods(){
    return new ArrayList<Method>(0);
  }
  Type findComponentType(  String signature) throws ClassNotLoadedException {
    byte tag=(byte)signature.charAt(0);
    if (PacketStream.isObjectTag(tag)) {
      JNITypeParser parser=new JNITypeParser(componentSignature());
      List list=vm.classesByName(parser.typeName());
      Iterator iter=list.iterator();
      while (iter.hasNext()) {
        ReferenceType type=(ReferenceType)iter.next();
        ClassLoaderReference cl=type.classLoader();
        if ((cl == null) ? (classLoader() == null) : (cl.equals(classLoader()))) {
          return type;
        }
      }
      throw new ClassNotLoadedException(componentTypeName());
    }
 else {
      return vm.primitiveTypeMirror(tag);
    }
  }
  public Type componentType() throws ClassNotLoadedException {
    return findComponentType(componentSignature());
  }
  static boolean isComponentAssignable(  Type destination,  Type source){
    if (source instanceof PrimitiveType) {
      return source.equals(destination);
    }
 else {
      if (destination instanceof PrimitiveType) {
        return false;
      }
      ReferenceTypeImpl refSource=(ReferenceTypeImpl)source;
      ReferenceTypeImpl refDestination=(ReferenceTypeImpl)destination;
      return refSource.isAssignableTo(refDestination);
    }
  }
  boolean isAssignableTo(  ReferenceType destType){
    if (destType instanceof ArrayType) {
      try {
        Type destComponentType=((ArrayType)destType).componentType();
        return isComponentAssignable(destComponentType,componentType());
      }
 catch (      ClassNotLoadedException e) {
        return false;
      }
    }
 else     if (destType instanceof InterfaceType) {
      return destType.name().equals("java.lang.Cloneable");
    }
 else {
      return destType.name().equals("java.lang.Object");
    }
  }
  List<ReferenceType> inheritedTypes(){
    return new ArrayList<ReferenceType>(0);
  }
  void getModifiers(){
    if (modifiers != -1) {
      return;
    }
    try {
      Type t=componentType();
      if (t instanceof PrimitiveType) {
        modifiers=VMModifiers.FINAL | VMModifiers.PUBLIC;
      }
 else {
        ReferenceType rt=(ReferenceType)t;
        modifiers=rt.modifiers();
      }
    }
 catch (    ClassNotLoadedException cnle) {
      cnle.printStackTrace();
    }
  }
  public String toString(){
    return "array class " + name() + " ("+ loaderString()+ ")";
  }
  public boolean isPrepared(){
    return true;
  }
  public boolean isVerified(){
    return true;
  }
  public boolean isInitialized(){
    return true;
  }
  public boolean failedToInitialize(){
    return false;
  }
  public boolean isAbstract(){
    return false;
  }
  public boolean isFinal(){
    return true;
  }
}
