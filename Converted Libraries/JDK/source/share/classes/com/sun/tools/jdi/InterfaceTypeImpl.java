package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.lang.ref.SoftReference;
public class InterfaceTypeImpl extends ReferenceTypeImpl implements InterfaceType {
  private SoftReference<List<InterfaceType>> superinterfacesRef=null;
  protected InterfaceTypeImpl(  VirtualMachine aVm,  long aRef){
    super(aVm,aRef);
  }
  public List<InterfaceType> superinterfaces(){
    List<InterfaceType> superinterfaces=(superinterfacesRef == null) ? null : superinterfacesRef.get();
    if (superinterfaces == null) {
      superinterfaces=getInterfaces();
      superinterfaces=Collections.unmodifiableList(superinterfaces);
      superinterfacesRef=new SoftReference<List<InterfaceType>>(superinterfaces);
    }
    return superinterfaces;
  }
  public List<InterfaceType> subinterfaces(){
    List<InterfaceType> subs=new ArrayList<InterfaceType>();
    for (    ReferenceType refType : vm.allClasses()) {
      if (refType instanceof InterfaceType) {
        InterfaceType interfaze=(InterfaceType)refType;
        if (interfaze.isPrepared() && interfaze.superinterfaces().contains(this)) {
          subs.add(interfaze);
        }
      }
    }
    return subs;
  }
  public List<ClassType> implementors(){
    List<ClassType> implementors=new ArrayList<ClassType>();
    for (    ReferenceType refType : vm.allClasses()) {
      if (refType instanceof ClassType) {
        ClassType clazz=(ClassType)refType;
        if (clazz.isPrepared() && clazz.interfaces().contains(this)) {
          implementors.add(clazz);
        }
      }
    }
    return implementors;
  }
  void addVisibleMethods(  Map<String,Method> methodMap){
    for (    InterfaceType interfaze : superinterfaces()) {
      ((InterfaceTypeImpl)interfaze).addVisibleMethods(methodMap);
    }
    addToMethodMap(methodMap,methods());
  }
  public List<Method> allMethods(){
    ArrayList<Method> list=new ArrayList<Method>(methods());
    for (    InterfaceType interfaze : allSuperinterfaces()) {
      list.addAll(interfaze.methods());
    }
    return list;
  }
  List<InterfaceType> allSuperinterfaces(){
    ArrayList<InterfaceType> list=new ArrayList<InterfaceType>();
    addSuperinterfaces(list);
    return list;
  }
  void addSuperinterfaces(  List<InterfaceType> list){
    List<InterfaceType> immediate=new ArrayList<InterfaceType>(superinterfaces());
    Iterator iter=immediate.iterator();
    while (iter.hasNext()) {
      InterfaceType interfaze=(InterfaceType)iter.next();
      if (list.contains(interfaze)) {
        iter.remove();
      }
    }
    list.addAll(immediate);
    iter=immediate.iterator();
    while (iter.hasNext()) {
      InterfaceTypeImpl interfaze=(InterfaceTypeImpl)iter.next();
      interfaze.addSuperinterfaces(list);
    }
  }
  boolean isAssignableTo(  ReferenceType type){
    if (this.equals(type)) {
      return true;
    }
 else {
      for (      InterfaceType interfaze : superinterfaces()) {
        if (((InterfaceTypeImpl)interfaze).isAssignableTo(type)) {
          return true;
        }
      }
      return false;
    }
  }
  List<InterfaceType> inheritedTypes(){
    return superinterfaces();
  }
  public boolean isInitialized(){
    return isPrepared();
  }
  public String toString(){
    return "interface " + name() + " ("+ loaderString()+ ")";
  }
}
