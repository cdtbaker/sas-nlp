package com.sun.tools.jdi;
import com.sun.jdi.*;
import com.sun.jdi.request.BreakpointRequest;
import java.util.*;
import java.lang.ref.WeakReference;
public class ThreadReferenceImpl extends ObjectReferenceImpl implements ThreadReference, VMListener {
  static final int SUSPEND_STATUS_SUSPENDED=0x1;
  static final int SUSPEND_STATUS_BREAK=0x2;
  private int suspendedZombieCount=0;
  private ThreadGroupReference threadGroup;
private static class LocalCache {
    JDWP.ThreadReference.Status status=null;
    List<StackFrame> frames=null;
    int framesStart=-1;
    int framesLength=0;
    int frameCount=-1;
    List<ObjectReference> ownedMonitors=null;
    List<MonitorInfo> ownedMonitorsInfo=null;
    ObjectReference contendedMonitor=null;
    boolean triedCurrentContended=false;
  }
  private LocalCache localCache;
  private void resetLocalCache(){
    localCache=new LocalCache();
  }
private static class Cache extends ObjectReferenceImpl.Cache {
    String name=null;
  }
  protected ObjectReferenceImpl.Cache newCache(){
    return new Cache();
  }
  private List<WeakReference<ThreadListener>> listeners=new ArrayList<WeakReference<ThreadListener>>();
  ThreadReferenceImpl(  VirtualMachine aVm,  long aRef){
    super(aVm,aRef);
    resetLocalCache();
    vm.state().addListener(this);
  }
  protected String description(){
    return "ThreadReference " + uniqueID();
  }
  public boolean vmNotSuspended(  VMAction action){
    if (action.resumingThread() == null) {
synchronized (vm.state()) {
        processThreadAction(new ThreadAction(this,ThreadAction.THREAD_RESUMABLE));
      }
    }
    return super.vmNotSuspended(action);
  }
  /** 
 * Note that we only cache the name string while the entire VM is suspended
 * because the name can change via Thread.setName arbitrarily while this
 * thread is running.
 */
  public String name(){
    String name=null;
    try {
      Cache local=(Cache)getCache();
      if (local != null) {
        name=local.name;
      }
      if (name == null) {
        name=JDWP.ThreadReference.Name.process(vm,this).threadName;
        if (local != null) {
          local.name=name;
        }
      }
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
    return name;
  }
  PacketStream sendResumingCommand(  CommandSender sender){
synchronized (vm.state()) {
      processThreadAction(new ThreadAction(this,ThreadAction.THREAD_RESUMABLE));
      return sender.send();
    }
  }
  public void suspend(){
    try {
      JDWP.ThreadReference.Suspend.process(vm,this);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  public void resume(){
    if (suspendedZombieCount > 0) {
      suspendedZombieCount--;
      return;
    }
    PacketStream stream;
synchronized (vm.state()) {
      processThreadAction(new ThreadAction(this,ThreadAction.THREAD_RESUMABLE));
      stream=JDWP.ThreadReference.Resume.enqueueCommand(vm,this);
    }
    try {
      JDWP.ThreadReference.Resume.waitForReply(vm,stream);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  public int suspendCount(){
    if (suspendedZombieCount > 0) {
      return suspendedZombieCount;
    }
    try {
      return JDWP.ThreadReference.SuspendCount.process(vm,this).suspendCount;
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  public void stop(  ObjectReference throwable) throws InvalidTypeException {
    validateMirror(throwable);
    List list=vm.classesByName("java.lang.Throwable");
    ClassTypeImpl throwableClass=(ClassTypeImpl)list.get(0);
    if ((throwable == null) || !throwableClass.isAssignableFrom(throwable)) {
      throw new InvalidTypeException("Not an instance of Throwable");
    }
    try {
      JDWP.ThreadReference.Stop.process(vm,this,(ObjectReferenceImpl)throwable);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  public void interrupt(){
    try {
      JDWP.ThreadReference.Interrupt.process(vm,this);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
  }
  private JDWP.ThreadReference.Status jdwpStatus(){
    LocalCache snapshot=localCache;
    JDWP.ThreadReference.Status myStatus=snapshot.status;
    try {
      if (myStatus == null) {
        myStatus=JDWP.ThreadReference.Status.process(vm,this);
        if ((myStatus.suspendStatus & SUSPEND_STATUS_SUSPENDED) != 0) {
          snapshot.status=myStatus;
        }
      }
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
    return myStatus;
  }
  public int status(){
    return jdwpStatus().threadStatus;
  }
  public boolean isSuspended(){
    return ((suspendedZombieCount > 0) || ((jdwpStatus().suspendStatus & SUSPEND_STATUS_SUSPENDED) != 0));
  }
  public boolean isAtBreakpoint(){
    try {
      StackFrame frame=frame(0);
      Location location=frame.location();
      List requests=vm.eventRequestManager().breakpointRequests();
      Iterator iter=requests.iterator();
      while (iter.hasNext()) {
        BreakpointRequest request=(BreakpointRequest)iter.next();
        if (location.equals(request.location())) {
          return true;
        }
      }
      return false;
    }
 catch (    IndexOutOfBoundsException iobe) {
      return false;
    }
catch (    IncompatibleThreadStateException itse) {
      return false;
    }
  }
  public ThreadGroupReference threadGroup(){
    if (threadGroup == null) {
      try {
        threadGroup=JDWP.ThreadReference.ThreadGroup.process(vm,this).group;
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
    }
    return threadGroup;
  }
  public int frameCount() throws IncompatibleThreadStateException {
    LocalCache snapshot=localCache;
    try {
      if (snapshot.frameCount == -1) {
        snapshot.frameCount=JDWP.ThreadReference.FrameCount.process(vm,this).frameCount;
      }
    }
 catch (    JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.THREAD_NOT_SUSPENDED:
case JDWP.Error.INVALID_THREAD:
        throw new IncompatibleThreadStateException();
default :
      throw exc.toJDIException();
  }
}
return snapshot.frameCount;
}
public List<StackFrame> frames() throws IncompatibleThreadStateException {
return privateFrames(0,-1);
}
public StackFrame frame(int index) throws IncompatibleThreadStateException {
List list=privateFrames(index,1);
return (StackFrame)list.get(0);
}
/** 
 * Is the requested subrange within what has been retrieved?
 * local is known to be non-null.  Should only be called from
 * a sync method.
 */
private boolean isSubrange(LocalCache snapshot,int start,int length){
if (start < snapshot.framesStart) {
  return false;
}
if (length == -1) {
  return (snapshot.framesLength == -1);
}
if (snapshot.framesLength == -1) {
  if ((start + length) > (snapshot.framesStart + snapshot.frames.size())) {
    throw new IndexOutOfBoundsException();
  }
  return true;
}
return ((start + length) <= (snapshot.framesStart + snapshot.framesLength));
}
public List<StackFrame> frames(int start,int length) throws IncompatibleThreadStateException {
if (length < 0) {
  throw new IndexOutOfBoundsException("length must be greater than or equal to zero");
}
return privateFrames(start,length);
}
/** 
 * Private version of frames() allows "-1" to specify all
 * remaining frames.
 */
synchronized private List<StackFrame> privateFrames(int start,int length) throws IncompatibleThreadStateException {
LocalCache snapshot=localCache;
try {
  if (snapshot.frames == null || !isSubrange(snapshot,start,length)) {
    JDWP.ThreadReference.Frames.Frame[] jdwpFrames=JDWP.ThreadReference.Frames.process(vm,this,start,length).frames;
    int count=jdwpFrames.length;
    snapshot.frames=new ArrayList<StackFrame>(count);
    for (int i=0; i < count; i++) {
      if (jdwpFrames[i].location == null) {
        throw new InternalException("Invalid frame location");
      }
      StackFrame frame=new StackFrameImpl(vm,this,jdwpFrames[i].frameID,jdwpFrames[i].location);
      snapshot.frames.add(frame);
    }
    snapshot.framesStart=start;
    snapshot.framesLength=length;
    return Collections.unmodifiableList(snapshot.frames);
  }
 else {
    int fromIndex=start - snapshot.framesStart;
    int toIndex;
    if (length == -1) {
      toIndex=snapshot.frames.size() - fromIndex;
    }
 else {
      toIndex=fromIndex + length;
    }
    return Collections.unmodifiableList(snapshot.frames.subList(fromIndex,toIndex));
  }
}
 catch (JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.THREAD_NOT_SUSPENDED:
case JDWP.Error.INVALID_THREAD:
    throw new IncompatibleThreadStateException();
default :
  throw exc.toJDIException();
}
}
}
public List<ObjectReference> ownedMonitors() throws IncompatibleThreadStateException {
LocalCache snapshot=localCache;
try {
if (snapshot.ownedMonitors == null) {
snapshot.ownedMonitors=Arrays.asList((ObjectReference[])JDWP.ThreadReference.OwnedMonitors.process(vm,this).owned);
if ((vm.traceFlags & vm.TRACE_OBJREFS) != 0) {
  vm.printTrace(description() + " temporarily caching owned monitors" + " (count = "+ snapshot.ownedMonitors.size()+ ")");
}
}
}
 catch (JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.THREAD_NOT_SUSPENDED:
case JDWP.Error.INVALID_THREAD:
throw new IncompatibleThreadStateException();
default :
throw exc.toJDIException();
}
}
return snapshot.ownedMonitors;
}
public ObjectReference currentContendedMonitor() throws IncompatibleThreadStateException {
LocalCache snapshot=localCache;
try {
if (snapshot.contendedMonitor == null && !snapshot.triedCurrentContended) {
snapshot.contendedMonitor=JDWP.ThreadReference.CurrentContendedMonitor.process(vm,this).monitor;
snapshot.triedCurrentContended=true;
if ((snapshot.contendedMonitor != null) && ((vm.traceFlags & vm.TRACE_OBJREFS) != 0)) {
vm.printTrace(description() + " temporarily caching contended monitor" + " (id = "+ snapshot.contendedMonitor.uniqueID()+ ")");
}
}
}
 catch (JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.THREAD_NOT_SUSPENDED:
case JDWP.Error.INVALID_THREAD:
throw new IncompatibleThreadStateException();
default :
throw exc.toJDIException();
}
}
return snapshot.contendedMonitor;
}
public List<MonitorInfo> ownedMonitorsAndFrames() throws IncompatibleThreadStateException {
LocalCache snapshot=localCache;
try {
if (snapshot.ownedMonitorsInfo == null) {
JDWP.ThreadReference.OwnedMonitorsStackDepthInfo.monitor[] minfo;
minfo=JDWP.ThreadReference.OwnedMonitorsStackDepthInfo.process(vm,this).owned;
snapshot.ownedMonitorsInfo=new ArrayList<MonitorInfo>(minfo.length);
for (int i=0; i < minfo.length; i++) {
JDWP.ThreadReference.OwnedMonitorsStackDepthInfo.monitor mi=minfo[i];
MonitorInfo mon=new MonitorInfoImpl(vm,minfo[i].monitor,this,minfo[i].stack_depth);
snapshot.ownedMonitorsInfo.add(mon);
}
if ((vm.traceFlags & vm.TRACE_OBJREFS) != 0) {
vm.printTrace(description() + " temporarily caching owned monitors" + " (count = "+ snapshot.ownedMonitorsInfo.size()+ ")");
}
}
}
 catch (JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.THREAD_NOT_SUSPENDED:
case JDWP.Error.INVALID_THREAD:
throw new IncompatibleThreadStateException();
default :
throw exc.toJDIException();
}
}
return snapshot.ownedMonitorsInfo;
}
public void popFrames(StackFrame frame) throws IncompatibleThreadStateException {
if (!frame.thread().equals(this)) {
throw new IllegalArgumentException("frame does not belong to this thread");
}
if (!vm.canPopFrames()) {
throw new UnsupportedOperationException("target does not support popping frames");
}
((StackFrameImpl)frame).pop();
}
public void forceEarlyReturn(Value returnValue) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException {
if (!vm.canForceEarlyReturn()) {
throw new UnsupportedOperationException("target does not support the forcing of a method to return early");
}
validateMirrorOrNull(returnValue);
StackFrameImpl sf;
try {
sf=(StackFrameImpl)frame(0);
}
 catch (IndexOutOfBoundsException exc) {
throw new InvalidStackFrameException("No more frames on the stack");
}
sf.validateStackFrame();
MethodImpl meth=(MethodImpl)sf.location().method();
ValueImpl convertedValue=ValueImpl.prepareForAssignment(returnValue,meth.getReturnValueContainer());
try {
JDWP.ThreadReference.ForceEarlyReturn.process(vm,this,convertedValue);
}
 catch (JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.OPAQUE_FRAME:
throw new NativeMethodException();
case JDWP.Error.THREAD_NOT_SUSPENDED:
throw new IncompatibleThreadStateException("Thread not suspended");
case JDWP.Error.THREAD_NOT_ALIVE:
throw new IncompatibleThreadStateException("Thread has not started or has finished");
case JDWP.Error.NO_MORE_FRAMES:
throw new InvalidStackFrameException("No more frames on the stack");
default :
throw exc.toJDIException();
}
}
}
public String toString(){
return "instance of " + referenceType().name() + "(name='"+ name()+ "', "+ "id="+ uniqueID()+ ")";
}
byte typeValueKey(){
return JDWP.Tag.THREAD;
}
void addListener(ThreadListener listener){
synchronized (vm.state()) {
listeners.add(new WeakReference<ThreadListener>(listener));
}
}
void removeListener(ThreadListener listener){
synchronized (vm.state()) {
Iterator iter=listeners.iterator();
while (iter.hasNext()) {
WeakReference ref=(WeakReference)iter.next();
if (listener.equals(ref.get())) {
iter.remove();
break;
}
}
}
}
/** 
 * Propagate the the thread state change information
 * to registered listeners.
 * Must be entered while synchronized on vm.state()
 */
private void processThreadAction(ThreadAction action){
synchronized (vm.state()) {
Iterator iter=listeners.iterator();
while (iter.hasNext()) {
WeakReference ref=(WeakReference)iter.next();
ThreadListener listener=(ThreadListener)ref.get();
if (listener != null) {
switch (action.id()) {
case ThreadAction.THREAD_RESUMABLE:
if (!listener.threadResumable(action)) {
iter.remove();
}
break;
}
}
 else {
iter.remove();
}
}
resetLocalCache();
}
}
}
