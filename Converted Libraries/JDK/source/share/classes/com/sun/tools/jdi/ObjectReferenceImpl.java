package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.*;
import java.util.ArrayList;
public class ObjectReferenceImpl extends ValueImpl implements ObjectReference, VMListener {
  protected long ref;
  private ReferenceType type=null;
  private int gcDisableCount=0;
  boolean addedListener=false;
protected static class Cache {
    JDWP.ObjectReference.MonitorInfo monitorInfo=null;
  }
  private static final Cache noInitCache=new Cache();
  private static final Cache markerCache=new Cache();
  private Cache cache=noInitCache;
  private void disableCache(){
synchronized (vm.state()) {
      cache=null;
    }
  }
  private void enableCache(){
synchronized (vm.state()) {
      cache=markerCache;
    }
  }
  protected Cache newCache(){
    return new Cache();
  }
  protected Cache getCache(){
synchronized (vm.state()) {
      if (cache == noInitCache) {
        if (vm.state().isSuspended()) {
          enableCache();
        }
 else {
          disableCache();
        }
      }
      if (cache == markerCache) {
        cache=newCache();
      }
      return cache;
    }
  }
  protected ClassTypeImpl invokableReferenceType(  Method method){
    return (ClassTypeImpl)referenceType();
  }
  ObjectReferenceImpl(  VirtualMachine aVm,  long aRef){
    super(aVm);
    ref=aRef;
  }
  protected String description(){
    return "ObjectReference " + uniqueID();
  }
  public boolean vmSuspended(  VMAction action){
    enableCache();
    return true;
  }
  public boolean vmNotSuspended(  VMAction action){
synchronized (vm.state()) {
      if (cache != null && (vm.traceFlags & vm.TRACE_OBJREFS) != 0) {
        vm.printTrace("Clearing temporary cache for " + description());
      }
      disableCache();
      if (addedListener) {
        addedListener=false;
        return false;
      }
 else {
        return true;
      }
    }
  }
  public boolean equals(  Object obj){
    if ((obj != null) && (obj instanceof ObjectReferenceImpl)) {
      ObjectReferenceImpl other=(ObjectReferenceImpl)obj;
      return (ref() == other.ref()) && super.equals(obj);
    }
 else {
      return false;
    }
  }
  public int hashCode(){
    return (int)ref();
  }
  public Type type(){
    return referenceType();
  }
  public ReferenceType referenceType(){
    if (type == null) {
      try {
        JDWP.ObjectReference.ReferenceType rtinfo=JDWP.ObjectReference.ReferenceType.process(vm,this);
        type=vm.referenceType(rtinfo.typeID,rtinfo.refTypeTag);
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
    return type;
  }
  public Value getValue(  Field sig){
    List<Field> list=new ArrayList<Field>(1);
    list.add(sig);
    Map map=getValues(list);
    return (Value)map.get(sig);
  }
  public Map<Field,Value> getValues(  List<? extends Field> theFields){
    validateMirrors(theFields);
    List<Field> staticFields=new ArrayList<Field>(0);
    int size=theFields.size();
    List<Field> instanceFields=new ArrayList<Field>(size);
    for (int i=0; i < size; i++) {
      Field field=(Field)theFields.get(i);
      ((ReferenceTypeImpl)referenceType()).validateFieldAccess(field);
      if (field.isStatic())       staticFields.add(field);
 else {
        instanceFields.add(field);
      }
    }
    Map<Field,Value> map;
    if (staticFields.size() > 0) {
      map=referenceType().getValues(staticFields);
    }
 else {
      map=new HashMap<Field,Value>(size);
    }
    size=instanceFields.size();
    JDWP.ObjectReference.GetValues.Field[] queryFields=new JDWP.ObjectReference.GetValues.Field[size];
    for (int i=0; i < size; i++) {
      FieldImpl field=(FieldImpl)instanceFields.get(i);
      queryFields[i]=new JDWP.ObjectReference.GetValues.Field(field.ref());
    }
    ValueImpl[] values;
    try {
      values=JDWP.ObjectReference.GetValues.process(vm,this,queryFields).values;
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
    if (size != values.length) {
      throw new InternalException("Wrong number of values returned from target VM");
    }
    for (int i=0; i < size; i++) {
      FieldImpl field=(FieldImpl)instanceFields.get(i);
      map.put(field,values[i]);
    }
    return map;
  }
  public void setValue(  Field field,  Value value) throws InvalidTypeException, ClassNotLoadedException {
    validateMirror(field);
    validateMirrorOrNull(value);
    ((ReferenceTypeImpl)referenceType()).validateFieldSet(field);
    if (field.isStatic()) {
      ReferenceType type=referenceType();
      if (type instanceof ClassType) {
        ((ClassType)type).setValue(field,value);
        return;
      }
 else {
        throw new IllegalArgumentException("Invalid type for static field set");
      }
    }
    try {
      JDWP.ObjectReference.SetValues.FieldValue[] fvals=new JDWP.ObjectReference.SetValues.FieldValue[1];
      fvals[0]=new JDWP.ObjectReference.SetValues.FieldValue(((FieldImpl)field).ref(),ValueImpl.prepareForAssignment(value,(FieldImpl)field));
      try {
        JDWP.ObjectReference.SetValues.process(vm,this,fvals);
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
  void validateMethodInvocation(  Method method,  int options) throws InvalidTypeException, InvocationException {
    ReferenceTypeImpl declType=(ReferenceTypeImpl)method.declaringType();
    if (!declType.isAssignableFrom(this)) {
      throw new IllegalArgumentException("Invalid method");
    }
    ClassTypeImpl clazz=invokableReferenceType(method);
    if (method.isConstructor()) {
      throw new IllegalArgumentException("Cannot invoke constructor");
    }
    if ((options & INVOKE_NONVIRTUAL) != 0) {
      if (method.declaringType() instanceof InterfaceType) {
        throw new IllegalArgumentException("Interface method");
      }
 else       if (method.isAbstract()) {
        throw new IllegalArgumentException("Abstract method");
      }
    }
    ClassTypeImpl invokedClass;
    if ((options & INVOKE_NONVIRTUAL) != 0) {
      invokedClass=clazz;
    }
 else {
      Method invoker=clazz.concreteMethodByName(method.name(),method.signature());
      invokedClass=(ClassTypeImpl)invoker.declaringType();
    }
  }
  PacketStream sendInvokeCommand(  final ThreadReferenceImpl thread,  final ClassTypeImpl refType,  final MethodImpl method,  final ValueImpl[] args,  final int options){
    CommandSender sender=new CommandSender(){
      public PacketStream send(){
        return JDWP.ObjectReference.InvokeMethod.enqueueCommand(vm,ObjectReferenceImpl.this,thread,refType,method.ref(),args,options);
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
  public Value invokeMethod(  ThreadReference threadIntf,  Method methodIntf,  List<? extends Value> origArguments,  int options) throws InvalidTypeException, IncompatibleThreadStateException, InvocationException, ClassNotLoadedException {
    validateMirror(threadIntf);
    validateMirror(methodIntf);
    validateMirrorsOrNulls(origArguments);
    MethodImpl method=(MethodImpl)methodIntf;
    ThreadReferenceImpl thread=(ThreadReferenceImpl)threadIntf;
    if (method.isStatic()) {
      if (referenceType() instanceof ClassType) {
        ClassType type=(ClassType)referenceType();
        return type.invokeMethod(thread,method,origArguments,options);
      }
 else {
        throw new IllegalArgumentException("Invalid type for static method invocation");
      }
    }
    validateMethodInvocation(method,options);
    List<Value> arguments=method.validateAndPrepareArgumentsForInvoke(origArguments);
    ValueImpl[] args=arguments.toArray(new ValueImpl[0]);
    JDWP.ObjectReference.InvokeMethod ret;
    try {
      PacketStream stream=sendInvokeCommand(thread,invokableReferenceType(method),method,args,options);
      ret=JDWP.ObjectReference.InvokeMethod.waitForReply(vm,stream);
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
  public synchronized void disableCollection(){
    if (gcDisableCount == 0) {
      try {
        JDWP.ObjectReference.DisableCollection.process(vm,this);
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
    gcDisableCount++;
  }
  public synchronized void enableCollection(){
    gcDisableCount--;
    if (gcDisableCount == 0) {
      try {
        JDWP.ObjectReference.EnableCollection.process(vm,this);
      }
 catch (      JDWPException exc) {
        if (exc.errorCode() != JDWP.Error.INVALID_OBJECT) {
          throw exc.toJDIException();
        }
        return;
      }
    }
  }
  public boolean isCollected(){
    try {
      return JDWP.ObjectReference.IsCollected.process(vm,this).isCollected;
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  public long uniqueID(){
    return ref();
  }
  JDWP.ObjectReference.MonitorInfo jdwpMonitorInfo() throws IncompatibleThreadStateException {
    JDWP.ObjectReference.MonitorInfo info=null;
    try {
      Cache local;
synchronized (vm.state()) {
        local=getCache();
        if (local != null) {
          info=local.monitorInfo;
          if (info == null && !vm.state().hasListener(this)) {
            vm.state().addListener(this);
            addedListener=true;
          }
        }
      }
      if (info == null) {
        info=JDWP.ObjectReference.MonitorInfo.process(vm,this);
        if (local != null) {
          local.monitorInfo=info;
          if ((vm.traceFlags & vm.TRACE_OBJREFS) != 0) {
            vm.printTrace("ObjectReference " + uniqueID() + " temporarily caching monitor info");
          }
        }
      }
    }
 catch (    JDWPException exc) {
      if (exc.errorCode() == JDWP.Error.THREAD_NOT_SUSPENDED) {
        throw new IncompatibleThreadStateException();
      }
 else {
        throw exc.toJDIException();
      }
    }
    return info;
  }
  public List<ThreadReference> waitingThreads() throws IncompatibleThreadStateException {
    return Arrays.asList((ThreadReference[])jdwpMonitorInfo().waiters);
  }
  public ThreadReference owningThread() throws IncompatibleThreadStateException {
    return jdwpMonitorInfo().owner;
  }
  public int entryCount() throws IncompatibleThreadStateException {
    return jdwpMonitorInfo().entryCount;
  }
  public List<ObjectReference> referringObjects(  long maxReferrers){
    if (!vm.canGetInstanceInfo()) {
      throw new UnsupportedOperationException("target does not support getting referring objects");
    }
    if (maxReferrers < 0) {
      throw new IllegalArgumentException("maxReferrers is less than zero: " + maxReferrers);
    }
    int intMax=(maxReferrers > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)maxReferrers;
    try {
      return Arrays.asList((ObjectReference[])JDWP.ObjectReference.ReferringObjects.process(vm,this,intMax).referringObjects);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  long ref(){
    return ref;
  }
  boolean isClassObject(){
    return referenceType().name().equals("java.lang.Class");
  }
  ValueImpl prepareForAssignmentTo(  ValueContainer destination) throws InvalidTypeException, ClassNotLoadedException {
    validateAssignment(destination);
    return this;
  }
  void validateAssignment(  ValueContainer destination) throws InvalidTypeException, ClassNotLoadedException {
    if (destination.signature().length() == 1) {
      throw new InvalidTypeException("Can't assign object value to primitive");
    }
    if ((destination.signature().charAt(0) == '[') && (type().signature().charAt(0) != '[')) {
      throw new InvalidTypeException("Can't assign non-array value to an array");
    }
    if ("void".equals(destination.typeName())) {
      throw new InvalidTypeException("Can't assign object value to a void");
    }
    ReferenceType destType=(ReferenceTypeImpl)destination.type();
    ReferenceTypeImpl myType=(ReferenceTypeImpl)referenceType();
    if (!myType.isAssignableTo(destType)) {
      JNITypeParser parser=new JNITypeParser(destType.signature());
      String destTypeName=parser.typeName();
      throw new InvalidTypeException("Can't assign " + type().name() + " to "+ destTypeName);
    }
  }
  public String toString(){
    return "instance of " + referenceType().name() + "(id="+ uniqueID()+ ")";
  }
  byte typeValueKey(){
    return JDWP.Tag.OBJECT;
  }
}
