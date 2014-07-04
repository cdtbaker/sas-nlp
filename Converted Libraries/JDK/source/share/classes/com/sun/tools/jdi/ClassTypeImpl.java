package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.*;
public class ClassTypeImpl extends ReferenceTypeImpl implements ClassType {
  private boolean cachedSuperclass=false;
  private ClassType superclass=null;
  private int lastLine=-1;
  private List<InterfaceType> interfaces=null;
  protected ClassTypeImpl(  VirtualMachine aVm,  long aRef){
    super(aVm,aRef);
  }
  public ClassType superclass(){
    if (!cachedSuperclass) {
      ClassTypeImpl sup=null;
      try {
        sup=JDWP.ClassType.Superclass.process(vm,this).superclass;
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
      if (sup != null) {
        superclass=sup;
      }
      cachedSuperclass=true;
    }
    return superclass;
  }
  public List<InterfaceType> interfaces(){
    if (interfaces == null) {
      interfaces=getInterfaces();
    }
    return interfaces;
  }
  void addInterfaces(  List<InterfaceType> list){
    List<InterfaceType> immediate=interfaces();
    list.addAll(interfaces());
    Iterator iter=immediate.iterator();
    while (iter.hasNext()) {
      InterfaceTypeImpl interfaze=(InterfaceTypeImpl)iter.next();
      interfaze.addSuperinterfaces(list);
    }
    ClassTypeImpl superclass=(ClassTypeImpl)superclass();
    if (superclass != null) {
      superclass.addInterfaces(list);
    }
  }
  public List<InterfaceType> allInterfaces(){
    List<InterfaceType> all=new ArrayList<InterfaceType>();
    addInterfaces(all);
    return all;
  }
  public List<ClassType> subclasses(){
    List<ClassType> subs=new ArrayList<ClassType>();
    for (    ReferenceType refType : vm.allClasses()) {
      if (refType instanceof ClassType) {
        ClassType clazz=(ClassType)refType;
        ClassType superclass=clazz.superclass();
        if ((superclass != null) && superclass.equals(this)) {
          subs.add((ClassType)refType);
        }
      }
    }
    return subs;
  }
  public boolean isEnum(){
    ClassType superclass=superclass();
    if (superclass != null && superclass.name().equals("java.lang.Enum")) {
      return true;
    }
    return false;
  }
  public void setValue(  Field field,  Value value) throws InvalidTypeException, ClassNotLoadedException {
    validateMirror(field);
    validateMirrorOrNull(value);
    validateFieldSet(field);
    if (!field.isStatic()) {
      throw new IllegalArgumentException("Must set non-static field through an instance");
    }
    try {
      JDWP.ClassType.SetValues.FieldValue[] values=new JDWP.ClassType.SetValues.FieldValue[1];
      values[0]=new JDWP.ClassType.SetValues.FieldValue(((FieldImpl)field).ref(),ValueImpl.prepareForAssignment(value,(FieldImpl)field));
      try {
        JDWP.ClassType.SetValues.process(vm,this,values);
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
 catch (    ClassNotLoadedException e) {
      if (value != null) {
        throw e;
      }
    }
  }
  PacketStream sendInvokeCommand(  final ThreadReferenceImpl thread,  final MethodImpl method,  final ValueImpl[] args,  final int options){
    CommandSender sender=new CommandSender(){
      public PacketStream send(){
        return JDWP.ClassType.InvokeMethod.enqueueCommand(vm,ClassTypeImpl.this,thread,method.ref(),args,options);
      }
    }
;
    PacketStream stream;
    if ((options & INVOKE_SINGLE_THREADED) != 0) {
      stream=thread.sendResumingCommand(sender);
    }
 else {
      stream=vm.sendResumingCommand(sender);
    }
    return stream;
  }
  PacketStream sendNewInstanceCommand(  final ThreadReferenceImpl thread,  final MethodImpl method,  final ValueImpl[] args,  final int options){
    CommandSender sender=new CommandSender(){
      public PacketStream send(){
        return JDWP.ClassType.NewInstance.enqueueCommand(vm,ClassTypeImpl.this,thread,method.ref(),args,options);
      }
    }
;
    PacketStream stream;
    if ((options & INVOKE_SINGLE_THREADED) != 0) {
      stream=thread.sendResumingCommand(sender);
    }
 else {
      stream=vm.sendResumingCommand(sender);
    }
    return stream;
  }
  public Value invokeMethod(  ThreadReference threadIntf,  Method methodIntf,  List<? extends Value> origArguments,  int options) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
    validateMirror(threadIntf);
    validateMirror(methodIntf);
    validateMirrorsOrNulls(origArguments);
    MethodImpl method=(MethodImpl)methodIntf;
    ThreadReferenceImpl thread=(ThreadReferenceImpl)threadIntf;
    validateMethodInvocation(method);
    List<? extends Value> arguments=method.validateAndPrepareArgumentsForInvoke(origArguments);
    ValueImpl[] args=arguments.toArray(new ValueImpl[0]);
    JDWP.ClassType.InvokeMethod ret;
    try {
      PacketStream stream=sendInvokeCommand(thread,method,args,options);
      ret=JDWP.ClassType.InvokeMethod.waitForReply(vm,stream);
    }
 catch (    JDWPException exc) {
      if (exc.errorCode() == JDWP.Error.INVALID_THREAD) {
        throw new IncompatibleThreadStateException();
      }
 else {
        throw exc.toJDIException();
      }
    }
    if ((options & INVOKE_SINGLE_THREADED) == 0) {
      vm.notifySuspend();
    }
    if (ret.exception != null) {
      throw new InvocationException(ret.exception);
    }
 else {
      return ret.returnValue;
    }
  }
  public ObjectReference newInstance(  ThreadReference threadIntf,  Method methodIntf,  List<? extends Value> origArguments,  int options) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
    validateMirror(threadIntf);
    validateMirror(methodIntf);
    validateMirrorsOrNulls(origArguments);
    MethodImpl method=(MethodImpl)methodIntf;
    ThreadReferenceImpl thread=(ThreadReferenceImpl)threadIntf;
    validateConstructorInvocation(method);
    List<Value> arguments=method.validateAndPrepareArgumentsForInvoke(origArguments);
    ValueImpl[] args=arguments.toArray(new ValueImpl[0]);
    JDWP.ClassType.NewInstance ret=null;
    try {
      PacketStream stream=sendNewInstanceCommand(thread,method,args,options);
      ret=JDWP.ClassType.NewInstance.waitForReply(vm,stream);
    }
 catch (    JDWPException exc) {
      if (exc.errorCode() == JDWP.Error.INVALID_THREAD) {
        throw new IncompatibleThreadStateException();
      }
 else {
        throw exc.toJDIException();
      }
    }
    if ((options & INVOKE_SINGLE_THREADED) == 0) {
      vm.notifySuspend();
    }
    if (ret.exception != null) {
      throw new InvocationException(ret.exception);
    }
 else {
      return ret.newObject;
    }
  }
  public Method concreteMethodByName(  String name,  String signature){
    Method method=null;
    for (    Method candidate : visibleMethods()) {
      if (candidate.name().equals(name) && candidate.signature().equals(signature) && !candidate.isAbstract()) {
        method=candidate;
        break;
      }
    }
    return method;
  }
  public List<Method> allMethods(){
    ArrayList<Method> list=new ArrayList<Method>(methods());
    ClassType clazz=superclass();
    while (clazz != null) {
      list.addAll(clazz.methods());
      clazz=clazz.superclass();
    }
    for (    InterfaceType interfaze : allInterfaces()) {
      list.addAll(interfaze.methods());
    }
    return list;
  }
  List<ReferenceType> inheritedTypes(){
    List<ReferenceType> inherited=new ArrayList<ReferenceType>();
    if (superclass() != null) {
      inherited.add(0,(ReferenceType)superclass());
    }
    for (    ReferenceType rt : interfaces()) {
      inherited.add(rt);
    }
    return inherited;
  }
  void validateMethodInvocation(  Method method) throws InvalidTypeException, InvocationException {
    ReferenceTypeImpl declType=(ReferenceTypeImpl)method.declaringType();
    if (!declType.isAssignableFrom(this)) {
      throw new IllegalArgumentException("Invalid method");
    }
    if (!method.isStatic()) {
      throw new IllegalArgumentException("Cannot invoke instance method on a class type");
    }
 else     if (method.isStaticInitializer()) {
      throw new IllegalArgumentException("Cannot invoke static initializer");
    }
  }
  void validateConstructorInvocation(  Method method) throws InvalidTypeException, InvocationException {
    ReferenceTypeImpl declType=(ReferenceTypeImpl)method.declaringType();
    if (!declType.equals(this)) {
      throw new IllegalArgumentException("Invalid constructor");
    }
    if (!method.isConstructor()) {
      throw new IllegalArgumentException("Cannot create instance with non-constructor");
    }
  }
  void addVisibleMethods(  Map<String,Method> methodMap){
    Iterator iter=interfaces().iterator();
    while (iter.hasNext()) {
      InterfaceTypeImpl interfaze=(InterfaceTypeImpl)iter.next();
      interfaze.addVisibleMethods(methodMap);
    }
    ClassTypeImpl clazz=(ClassTypeImpl)superclass();
    if (clazz != null) {
      clazz.addVisibleMethods(methodMap);
    }
    addToMethodMap(methodMap,methods());
  }
  boolean isAssignableTo(  ReferenceType type){
    ClassTypeImpl superclazz=(ClassTypeImpl)superclass();
    if (this.equals(type)) {
      return true;
    }
 else     if ((superclazz != null) && superclazz.isAssignableTo(type)) {
      return true;
    }
 else {
      List<InterfaceType> interfaces=interfaces();
      Iterator iter=interfaces.iterator();
      while (iter.hasNext()) {
        InterfaceTypeImpl interfaze=(InterfaceTypeImpl)iter.next();
        if (interfaze.isAssignableTo(type)) {
          return true;
        }
      }
      return false;
    }
  }
  public String toString(){
    return "class " + name() + " ("+ loaderString()+ ")";
  }
}
