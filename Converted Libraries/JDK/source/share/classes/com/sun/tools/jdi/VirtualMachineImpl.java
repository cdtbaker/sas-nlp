package com.sun.tools.jdi;
import com.sun.jdi.*;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.event.EventQueue;
import java.util.*;
import java.text.MessageFormat;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
class VirtualMachineImpl extends MirrorImpl implements PathSearchingVirtualMachine, ThreadListener {
  public final int sizeofFieldRef;
  public final int sizeofMethodRef;
  public final int sizeofObjectRef;
  public final int sizeofClassRef;
  public final int sizeofFrameRef;
  final int sequenceNumber;
  private final TargetVM target;
  private final EventQueueImpl eventQueue;
  private final EventRequestManagerImpl internalEventRequestManager;
  private final EventRequestManagerImpl eventRequestManager;
  final VirtualMachineManagerImpl vmManager;
  private final ThreadGroup threadGroupForJDI;
  int traceFlags=TRACE_NONE;
  static int TRACE_RAW_SENDS=0x01000000;
  static int TRACE_RAW_RECEIVES=0x02000000;
  boolean traceReceives=false;
  private Map<Long,ReferenceType> typesByID;
  private TreeSet<ReferenceType> typesBySignature;
  private boolean retrievedAllTypes=false;
  private String defaultStratum=null;
  private final Map<Long,SoftObjectReference> objectsByID=new HashMap<Long,SoftObjectReference>();
  private final ReferenceQueue<ObjectReferenceImpl> referenceQueue=new ReferenceQueue<ObjectReferenceImpl>();
  static private final int DISPOSE_THRESHOLD=50;
  private final List<SoftObjectReference> batchedDisposeRequests=Collections.synchronizedList(new ArrayList<SoftObjectReference>(DISPOSE_THRESHOLD + 10));
  private JDWP.VirtualMachine.Version versionInfo;
  private JDWP.VirtualMachine.ClassPaths pathInfo;
  private JDWP.VirtualMachine.Capabilities capabilities=null;
  private JDWP.VirtualMachine.CapabilitiesNew capabilitiesNew=null;
  private BooleanType theBooleanType;
  private ByteType theByteType;
  private CharType theCharType;
  private ShortType theShortType;
  private IntegerType theIntegerType;
  private LongType theLongType;
  private FloatType theFloatType;
  private DoubleType theDoubleType;
  private VoidType theVoidType;
  private VoidValue voidVal;
  private Process process;
  private VMState state=new VMState(this);
  private Object initMonitor=new Object();
  private boolean initComplete=false;
  private boolean shutdown=false;
  private void notifyInitCompletion(){
synchronized (initMonitor) {
      initComplete=true;
      initMonitor.notifyAll();
    }
  }
  void waitInitCompletion(){
synchronized (initMonitor) {
      while (!initComplete) {
        try {
          initMonitor.wait();
        }
 catch (        InterruptedException e) {
        }
      }
    }
  }
  VMState state(){
    return state;
  }
  public boolean threadResumable(  ThreadAction action){
    state.thaw(action.thread());
    return true;
  }
  VirtualMachineImpl(  VirtualMachineManager manager,  Connection connection,  Process process,  int sequenceNumber){
    super(null);
    vm=this;
    this.vmManager=(VirtualMachineManagerImpl)manager;
    this.process=process;
    this.sequenceNumber=sequenceNumber;
    threadGroupForJDI=new ThreadGroup(vmManager.mainGroupForJDI(),"JDI [" + this.hashCode() + "]");
    target=new TargetVM(this,connection);
    EventQueueImpl internalEventQueue=new EventQueueImpl(this,target);
    new InternalEventHandler(this,internalEventQueue);
    eventQueue=new EventQueueImpl(this,target);
    eventRequestManager=new EventRequestManagerImpl(this);
    target.start();
    JDWP.VirtualMachine.IDSizes idSizes;
    try {
      idSizes=JDWP.VirtualMachine.IDSizes.process(vm);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
    sizeofFieldRef=idSizes.fieldIDSize;
    sizeofMethodRef=idSizes.methodIDSize;
    sizeofObjectRef=idSizes.objectIDSize;
    sizeofClassRef=idSizes.referenceTypeIDSize;
    sizeofFrameRef=idSizes.frameIDSize;
    internalEventRequestManager=new EventRequestManagerImpl(this);
    EventRequest er=internalEventRequestManager.createClassPrepareRequest();
    er.setSuspendPolicy(EventRequest.SUSPEND_NONE);
    er.enable();
    er=internalEventRequestManager.createClassUnloadRequest();
    er.setSuspendPolicy(EventRequest.SUSPEND_NONE);
    er.enable();
    notifyInitCompletion();
  }
  EventRequestManagerImpl getInternalEventRequestManager(){
    return internalEventRequestManager;
  }
  void validateVM(){
  }
  public boolean equals(  Object obj){
    return this == obj;
  }
  public int hashCode(){
    return System.identityHashCode(this);
  }
  public List<ReferenceType> classesByName(  String className){
    validateVM();
    String signature=JNITypeParser.typeNameToSignature(className);
    List<ReferenceType> list;
    if (retrievedAllTypes) {
      list=findReferenceTypes(signature);
    }
 else {
      list=retrieveClassesBySignature(signature);
    }
    return Collections.unmodifiableList(list);
  }
  public List<ReferenceType> allClasses(){
    validateVM();
    if (!retrievedAllTypes) {
      retrieveAllClasses();
    }
    ArrayList<ReferenceType> a;
synchronized (this) {
      a=new ArrayList<ReferenceType>(typesBySignature);
    }
    return Collections.unmodifiableList(a);
  }
  public void redefineClasses(  Map<? extends ReferenceType,byte[]> classToBytes){
    int cnt=classToBytes.size();
    JDWP.VirtualMachine.RedefineClasses.ClassDef[] defs=new JDWP.VirtualMachine.RedefineClasses.ClassDef[cnt];
    validateVM();
    if (!canRedefineClasses()) {
      throw new UnsupportedOperationException();
    }
    Iterator it=classToBytes.entrySet().iterator();
    for (int i=0; it.hasNext(); i++) {
      Map.Entry entry=(Map.Entry)it.next();
      ReferenceTypeImpl refType=(ReferenceTypeImpl)entry.getKey();
      validateMirror(refType);
      defs[i]=new JDWP.VirtualMachine.RedefineClasses.ClassDef(refType,(byte[])entry.getValue());
    }
    vm.state().thaw();
    try {
      JDWP.VirtualMachine.RedefineClasses.process(vm,defs);
    }
 catch (    JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.INVALID_CLASS_FORMAT:
        throw new ClassFormatError("class not in class file format");
case JDWP.Error.CIRCULAR_CLASS_DEFINITION:
      throw new ClassCircularityError("circularity has been detected while initializing a class");
case JDWP.Error.FAILS_VERIFICATION:
    throw new VerifyError("verifier detected internal inconsistency or security problem");
case JDWP.Error.UNSUPPORTED_VERSION:
  throw new UnsupportedClassVersionError("version numbers of class are not supported");
case JDWP.Error.ADD_METHOD_NOT_IMPLEMENTED:
throw new UnsupportedOperationException("add method not implemented");
case JDWP.Error.SCHEMA_CHANGE_NOT_IMPLEMENTED:
throw new UnsupportedOperationException("schema change not implemented");
case JDWP.Error.HIERARCHY_CHANGE_NOT_IMPLEMENTED:
throw new UnsupportedOperationException("hierarchy change not implemented");
case JDWP.Error.DELETE_METHOD_NOT_IMPLEMENTED:
throw new UnsupportedOperationException("delete method not implemented");
case JDWP.Error.CLASS_MODIFIERS_CHANGE_NOT_IMPLEMENTED:
throw new UnsupportedOperationException("changes to class modifiers not implemented");
case JDWP.Error.METHOD_MODIFIERS_CHANGE_NOT_IMPLEMENTED:
throw new UnsupportedOperationException("changes to method modifiers not implemented");
case JDWP.Error.NAMES_DONT_MATCH:
throw new NoClassDefFoundError("class names do not match");
default :
throw exc.toJDIException();
}
}
List<BreakpointRequest> toDelete=new ArrayList<BreakpointRequest>();
EventRequestManager erm=eventRequestManager();
it=erm.breakpointRequests().iterator();
while (it.hasNext()) {
BreakpointRequest req=(BreakpointRequest)it.next();
if (classToBytes.containsKey(req.location().declaringType())) {
toDelete.add(req);
}
}
erm.deleteEventRequests(toDelete);
it=classToBytes.keySet().iterator();
while (it.hasNext()) {
ReferenceTypeImpl rti=(ReferenceTypeImpl)it.next();
rti.noticeRedefineClass();
}
}
public List<ThreadReference> allThreads(){
validateVM();
return state.allThreads();
}
public List<ThreadGroupReference> topLevelThreadGroups(){
validateVM();
return state.topLevelThreadGroups();
}
PacketStream sendResumingCommand(CommandSender sender){
return state.thawCommand(sender);
}
void notifySuspend(){
state.freeze();
}
public void suspend(){
validateVM();
try {
JDWP.VirtualMachine.Suspend.process(vm);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
notifySuspend();
}
public void resume(){
validateVM();
CommandSender sender=new CommandSender(){
public PacketStream send(){
return JDWP.VirtualMachine.Resume.enqueueCommand(vm);
}
}
;
try {
PacketStream stream=state.thawCommand(sender);
JDWP.VirtualMachine.Resume.waitForReply(vm,stream);
}
 catch (VMDisconnectedException exc) {
}
catch (JDWPException exc) {
switch (exc.errorCode()) {
case JDWP.Error.VM_DEAD:
return;
default :
throw exc.toJDIException();
}
}
}
public EventQueue eventQueue(){
return eventQueue;
}
public EventRequestManager eventRequestManager(){
validateVM();
return eventRequestManager;
}
EventRequestManagerImpl eventRequestManagerImpl(){
return eventRequestManager;
}
public BooleanValue mirrorOf(boolean value){
validateVM();
return new BooleanValueImpl(this,value);
}
public ByteValue mirrorOf(byte value){
validateVM();
return new ByteValueImpl(this,value);
}
public CharValue mirrorOf(char value){
validateVM();
return new CharValueImpl(this,value);
}
public ShortValue mirrorOf(short value){
validateVM();
return new ShortValueImpl(this,value);
}
public IntegerValue mirrorOf(int value){
validateVM();
return new IntegerValueImpl(this,value);
}
public LongValue mirrorOf(long value){
validateVM();
return new LongValueImpl(this,value);
}
public FloatValue mirrorOf(float value){
validateVM();
return new FloatValueImpl(this,value);
}
public DoubleValue mirrorOf(double value){
validateVM();
return new DoubleValueImpl(this,value);
}
public StringReference mirrorOf(String value){
validateVM();
try {
return (StringReference)JDWP.VirtualMachine.CreateString.process(vm,value).stringObject;
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
}
public VoidValue mirrorOfVoid(){
if (voidVal == null) {
voidVal=new VoidValueImpl(this);
}
return voidVal;
}
public long[] instanceCounts(List<? extends ReferenceType> classes){
if (!canGetInstanceInfo()) {
throw new UnsupportedOperationException("target does not support getting instances");
}
long[] retValue;
ReferenceTypeImpl[] rtArray=new ReferenceTypeImpl[classes.size()];
int ii=0;
for (ReferenceType rti : classes) {
validateMirror(rti);
rtArray[ii++]=(ReferenceTypeImpl)rti;
}
try {
retValue=JDWP.VirtualMachine.InstanceCounts.process(vm,rtArray).counts;
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
return retValue;
}
public void dispose(){
validateVM();
shutdown=true;
try {
JDWP.VirtualMachine.Dispose.process(vm);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
target.stopListening();
}
public void exit(int exitCode){
validateVM();
shutdown=true;
try {
JDWP.VirtualMachine.Exit.process(vm,exitCode);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
target.stopListening();
}
public Process process(){
validateVM();
return process;
}
private JDWP.VirtualMachine.Version versionInfo(){
try {
if (versionInfo == null) {
versionInfo=JDWP.VirtualMachine.Version.process(vm);
}
return versionInfo;
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
}
public String description(){
validateVM();
return MessageFormat.format(vmManager.getString("version_format"),"" + vmManager.majorInterfaceVersion(),"" + vmManager.minorInterfaceVersion(),versionInfo().description);
}
public String version(){
validateVM();
return versionInfo().vmVersion;
}
public String name(){
validateVM();
return versionInfo().vmName;
}
public boolean canWatchFieldModification(){
validateVM();
return capabilities().canWatchFieldModification;
}
public boolean canWatchFieldAccess(){
validateVM();
return capabilities().canWatchFieldAccess;
}
public boolean canGetBytecodes(){
validateVM();
return capabilities().canGetBytecodes;
}
public boolean canGetSyntheticAttribute(){
validateVM();
return capabilities().canGetSyntheticAttribute;
}
public boolean canGetOwnedMonitorInfo(){
validateVM();
return capabilities().canGetOwnedMonitorInfo;
}
public boolean canGetCurrentContendedMonitor(){
validateVM();
return capabilities().canGetCurrentContendedMonitor;
}
public boolean canGetMonitorInfo(){
validateVM();
return capabilities().canGetMonitorInfo;
}
private boolean hasNewCapabilities(){
return versionInfo().jdwpMajor > 1 || versionInfo().jdwpMinor >= 4;
}
boolean canGet1_5LanguageFeatures(){
return versionInfo().jdwpMajor > 1 || versionInfo().jdwpMinor >= 5;
}
public boolean canUseInstanceFilters(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canUseInstanceFilters;
}
public boolean canRedefineClasses(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canRedefineClasses;
}
public boolean canAddMethod(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canAddMethod;
}
public boolean canUnrestrictedlyRedefineClasses(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canUnrestrictedlyRedefineClasses;
}
public boolean canPopFrames(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canPopFrames;
}
public boolean canGetMethodReturnValues(){
return versionInfo().jdwpMajor > 1 || versionInfo().jdwpMinor >= 6;
}
public boolean canGetInstanceInfo(){
if (versionInfo().jdwpMajor < 1 || versionInfo().jdwpMinor < 6) {
return false;
}
validateVM();
return hasNewCapabilities() && capabilitiesNew().canGetInstanceInfo;
}
public boolean canUseSourceNameFilters(){
if (versionInfo().jdwpMajor < 1 || versionInfo().jdwpMinor < 6) {
return false;
}
return true;
}
public boolean canForceEarlyReturn(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canForceEarlyReturn;
}
public boolean canBeModified(){
return true;
}
public boolean canGetSourceDebugExtension(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canGetSourceDebugExtension;
}
public boolean canGetClassFileVersion(){
if (versionInfo().jdwpMajor < 1 && versionInfo().jdwpMinor < 6) {
return false;
}
 else {
return true;
}
}
public boolean canGetConstantPool(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canGetConstantPool;
}
public boolean canRequestVMDeathEvent(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canRequestVMDeathEvent;
}
public boolean canRequestMonitorEvents(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canRequestMonitorEvents;
}
public boolean canGetMonitorFrameInfo(){
validateVM();
return hasNewCapabilities() && capabilitiesNew().canGetMonitorFrameInfo;
}
public void setDebugTraceMode(int traceFlags){
validateVM();
this.traceFlags=traceFlags;
this.traceReceives=(traceFlags & TRACE_RECEIVES) != 0;
}
void printTrace(String string){
System.err.println("[JDI: " + string + "]");
}
void printReceiveTrace(int depth,String string){
StringBuffer sb=new StringBuffer("Receiving:");
for (int i=depth; i > 0; --i) {
sb.append("    ");
}
sb.append(string);
printTrace(sb.toString());
}
private synchronized ReferenceTypeImpl addReferenceType(long id,int tag,String signature){
if (typesByID == null) {
initReferenceTypes();
}
ReferenceTypeImpl type=null;
switch (tag) {
case JDWP.TypeTag.CLASS:
type=new ClassTypeImpl(vm,id);
break;
case JDWP.TypeTag.INTERFACE:
type=new InterfaceTypeImpl(vm,id);
break;
case JDWP.TypeTag.ARRAY:
type=new ArrayTypeImpl(vm,id);
break;
default :
throw new InternalException("Invalid reference type tag");
}
if (signature != null) {
type.setSignature(signature);
}
typesByID.put(new Long(id),type);
typesBySignature.add(type);
if ((vm.traceFlags & VirtualMachine.TRACE_REFTYPES) != 0) {
vm.printTrace("Caching new ReferenceType, sig=" + signature + ", id="+ id);
}
return type;
}
synchronized void removeReferenceType(String signature){
if (typesByID == null) {
return;
}
Iterator iter=typesBySignature.iterator();
int matches=0;
while (iter.hasNext()) {
ReferenceTypeImpl type=(ReferenceTypeImpl)iter.next();
int comp=signature.compareTo(type.signature());
if (comp == 0) {
matches++;
iter.remove();
typesByID.remove(new Long(type.ref()));
if ((vm.traceFlags & VirtualMachine.TRACE_REFTYPES) != 0) {
vm.printTrace("Uncaching ReferenceType, sig=" + signature + ", id="+ type.ref());
}
}
}
if (matches > 1) {
retrieveClassesBySignature(signature);
}
}
private synchronized List<ReferenceType> findReferenceTypes(String signature){
if (typesByID == null) {
return new ArrayList<ReferenceType>(0);
}
Iterator iter=typesBySignature.iterator();
List<ReferenceType> list=new ArrayList<ReferenceType>();
while (iter.hasNext()) {
ReferenceTypeImpl type=(ReferenceTypeImpl)iter.next();
int comp=signature.compareTo(type.signature());
if (comp == 0) {
list.add(type);
}
}
return list;
}
private void initReferenceTypes(){
typesByID=new HashMap<Long,ReferenceType>(300);
typesBySignature=new TreeSet<ReferenceType>();
}
ReferenceTypeImpl referenceType(long ref,byte tag){
return referenceType(ref,tag,null);
}
ClassTypeImpl classType(long ref){
return (ClassTypeImpl)referenceType(ref,JDWP.TypeTag.CLASS,null);
}
InterfaceTypeImpl interfaceType(long ref){
return (InterfaceTypeImpl)referenceType(ref,JDWP.TypeTag.INTERFACE,null);
}
ArrayTypeImpl arrayType(long ref){
return (ArrayTypeImpl)referenceType(ref,JDWP.TypeTag.ARRAY,null);
}
ReferenceTypeImpl referenceType(long id,int tag,String signature){
if ((vm.traceFlags & VirtualMachine.TRACE_REFTYPES) != 0) {
StringBuffer sb=new StringBuffer();
sb.append("Looking up ");
if (tag == JDWP.TypeTag.CLASS) {
sb.append("Class");
}
 else if (tag == JDWP.TypeTag.INTERFACE) {
sb.append("Interface");
}
 else if (tag == JDWP.TypeTag.ARRAY) {
sb.append("ArrayType");
}
 else {
sb.append("UNKNOWN TAG: " + tag);
}
if (signature != null) {
sb.append(", signature='" + signature + "'");
}
sb.append(", id=" + id);
vm.printTrace(sb.toString());
}
if (id == 0) {
return null;
}
 else {
ReferenceTypeImpl retType=null;
synchronized (this) {
if (typesByID != null) {
retType=(ReferenceTypeImpl)typesByID.get(new Long(id));
}
if (retType == null) {
retType=addReferenceType(id,tag,signature);
}
}
return retType;
}
}
private JDWP.VirtualMachine.Capabilities capabilities(){
if (capabilities == null) {
try {
capabilities=JDWP.VirtualMachine.Capabilities.process(vm);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
}
return capabilities;
}
private JDWP.VirtualMachine.CapabilitiesNew capabilitiesNew(){
if (capabilitiesNew == null) {
try {
capabilitiesNew=JDWP.VirtualMachine.CapabilitiesNew.process(vm);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
}
return capabilitiesNew;
}
private List<ReferenceType> retrieveClassesBySignature(String signature){
if ((vm.traceFlags & VirtualMachine.TRACE_REFTYPES) != 0) {
vm.printTrace("Retrieving matching ReferenceTypes, sig=" + signature);
}
JDWP.VirtualMachine.ClassesBySignature.ClassInfo[] cinfos;
try {
cinfos=JDWP.VirtualMachine.ClassesBySignature.process(vm,signature).classes;
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
int count=cinfos.length;
List<ReferenceType> list=new ArrayList<ReferenceType>(count);
synchronized (this) {
for (int i=0; i < count; i++) {
JDWP.VirtualMachine.ClassesBySignature.ClassInfo ci=cinfos[i];
ReferenceTypeImpl type=referenceType(ci.typeID,ci.refTypeTag,signature);
type.setStatus(ci.status);
list.add(type);
}
}
return list;
}
private void retrieveAllClasses1_4(){
JDWP.VirtualMachine.AllClasses.ClassInfo[] cinfos;
try {
cinfos=JDWP.VirtualMachine.AllClasses.process(vm).classes;
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
synchronized (this) {
if (!retrievedAllTypes) {
int count=cinfos.length;
for (int i=0; i < count; i++) {
JDWP.VirtualMachine.AllClasses.ClassInfo ci=cinfos[i];
ReferenceTypeImpl type=referenceType(ci.typeID,ci.refTypeTag,ci.signature);
type.setStatus(ci.status);
}
retrievedAllTypes=true;
}
}
}
private void retrieveAllClasses(){
if ((vm.traceFlags & VirtualMachine.TRACE_REFTYPES) != 0) {
vm.printTrace("Retrieving all ReferenceTypes");
}
if (!vm.canGet1_5LanguageFeatures()) {
retrieveAllClasses1_4();
return;
}
JDWP.VirtualMachine.AllClassesWithGeneric.ClassInfo[] cinfos;
try {
cinfos=JDWP.VirtualMachine.AllClassesWithGeneric.process(vm).classes;
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
synchronized (this) {
if (!retrievedAllTypes) {
int count=cinfos.length;
for (int i=0; i < count; i++) {
JDWP.VirtualMachine.AllClassesWithGeneric.ClassInfo ci=cinfos[i];
ReferenceTypeImpl type=referenceType(ci.typeID,ci.refTypeTag,ci.signature);
type.setGenericSignature(ci.genericSignature);
type.setStatus(ci.status);
}
retrievedAllTypes=true;
}
}
}
void sendToTarget(Packet packet){
target.send(packet);
}
void waitForTargetReply(Packet packet){
target.waitForReply(packet);
processBatchedDisposes();
}
Type findBootType(String signature) throws ClassNotLoadedException {
List types=allClasses();
Iterator iter=types.iterator();
while (iter.hasNext()) {
ReferenceType type=(ReferenceType)iter.next();
if ((type.classLoader() == null) && (type.signature().equals(signature))) {
return type;
}
}
JNITypeParser parser=new JNITypeParser(signature);
throw new ClassNotLoadedException(parser.typeName(),"Type " + parser.typeName() + " not loaded");
}
BooleanType theBooleanType(){
if (theBooleanType == null) {
synchronized (this) {
if (theBooleanType == null) {
theBooleanType=new BooleanTypeImpl(this);
}
}
}
return theBooleanType;
}
ByteType theByteType(){
if (theByteType == null) {
synchronized (this) {
if (theByteType == null) {
theByteType=new ByteTypeImpl(this);
}
}
}
return theByteType;
}
CharType theCharType(){
if (theCharType == null) {
synchronized (this) {
if (theCharType == null) {
theCharType=new CharTypeImpl(this);
}
}
}
return theCharType;
}
ShortType theShortType(){
if (theShortType == null) {
synchronized (this) {
if (theShortType == null) {
theShortType=new ShortTypeImpl(this);
}
}
}
return theShortType;
}
IntegerType theIntegerType(){
if (theIntegerType == null) {
synchronized (this) {
if (theIntegerType == null) {
theIntegerType=new IntegerTypeImpl(this);
}
}
}
return theIntegerType;
}
LongType theLongType(){
if (theLongType == null) {
synchronized (this) {
if (theLongType == null) {
theLongType=new LongTypeImpl(this);
}
}
}
return theLongType;
}
FloatType theFloatType(){
if (theFloatType == null) {
synchronized (this) {
if (theFloatType == null) {
theFloatType=new FloatTypeImpl(this);
}
}
}
return theFloatType;
}
DoubleType theDoubleType(){
if (theDoubleType == null) {
synchronized (this) {
if (theDoubleType == null) {
theDoubleType=new DoubleTypeImpl(this);
}
}
}
return theDoubleType;
}
VoidType theVoidType(){
if (theVoidType == null) {
synchronized (this) {
if (theVoidType == null) {
theVoidType=new VoidTypeImpl(this);
}
}
}
return theVoidType;
}
PrimitiveType primitiveTypeMirror(byte tag){
switch (tag) {
case JDWP.Tag.BOOLEAN:
return theBooleanType();
case JDWP.Tag.BYTE:
return theByteType();
case JDWP.Tag.CHAR:
return theCharType();
case JDWP.Tag.SHORT:
return theShortType();
case JDWP.Tag.INT:
return theIntegerType();
case JDWP.Tag.LONG:
return theLongType();
case JDWP.Tag.FLOAT:
return theFloatType();
case JDWP.Tag.DOUBLE:
return theDoubleType();
default :
throw new IllegalArgumentException("Unrecognized primitive tag " + tag);
}
}
private void processBatchedDisposes(){
if (shutdown) {
return;
}
JDWP.VirtualMachine.DisposeObjects.Request[] requests=null;
synchronized (batchedDisposeRequests) {
int size=batchedDisposeRequests.size();
if (size >= DISPOSE_THRESHOLD) {
if ((traceFlags & TRACE_OBJREFS) != 0) {
printTrace("Dispose threashold reached. Will dispose " + size + " object references...");
}
requests=new JDWP.VirtualMachine.DisposeObjects.Request[size];
for (int i=0; i < requests.length; i++) {
SoftObjectReference ref=batchedDisposeRequests.get(i);
if ((traceFlags & TRACE_OBJREFS) != 0) {
printTrace("Disposing object " + ref.key().longValue() + " (ref count = "+ ref.count()+ ")");
}
requests[i]=new JDWP.VirtualMachine.DisposeObjects.Request(new ObjectReferenceImpl(this,ref.key().longValue()),ref.count());
}
batchedDisposeRequests.clear();
}
}
if (requests != null) {
try {
JDWP.VirtualMachine.DisposeObjects.process(vm,requests);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
}
}
private void batchForDispose(SoftObjectReference ref){
if ((traceFlags & TRACE_OBJREFS) != 0) {
printTrace("Batching object " + ref.key().longValue() + " for dispose (ref count = "+ ref.count()+ ")");
}
batchedDisposeRequests.add(ref);
}
private void processQueue(){
Reference ref;
while ((ref=referenceQueue.poll()) != null) {
SoftObjectReference softRef=(SoftObjectReference)ref;
removeObjectMirror(softRef);
batchForDispose(softRef);
}
}
synchronized ObjectReferenceImpl objectMirror(long id,int tag){
processQueue();
if (id == 0) {
return null;
}
ObjectReferenceImpl object=null;
Long key=new Long(id);
SoftObjectReference ref=objectsByID.get(key);
if (ref != null) {
object=ref.object();
}
if (object == null) {
switch (tag) {
case JDWP.Tag.OBJECT:
object=new ObjectReferenceImpl(vm,id);
break;
case JDWP.Tag.STRING:
object=new StringReferenceImpl(vm,id);
break;
case JDWP.Tag.ARRAY:
object=new ArrayReferenceImpl(vm,id);
break;
case JDWP.Tag.THREAD:
ThreadReferenceImpl thread=new ThreadReferenceImpl(vm,id);
thread.addListener(this);
object=thread;
break;
case JDWP.Tag.THREAD_GROUP:
object=new ThreadGroupReferenceImpl(vm,id);
break;
case JDWP.Tag.CLASS_LOADER:
object=new ClassLoaderReferenceImpl(vm,id);
break;
case JDWP.Tag.CLASS_OBJECT:
object=new ClassObjectReferenceImpl(vm,id);
break;
default :
throw new IllegalArgumentException("Invalid object tag: " + tag);
}
ref=new SoftObjectReference(key,object,referenceQueue);
objectsByID.put(key,ref);
if ((traceFlags & TRACE_OBJREFS) != 0) {
printTrace("Creating new " + object.getClass().getName() + " (id = "+ id+ ")");
}
}
 else {
ref.incrementCount();
}
return object;
}
synchronized void removeObjectMirror(ObjectReferenceImpl object){
processQueue();
SoftObjectReference ref=objectsByID.remove(new Long(object.ref()));
if (ref != null) {
batchForDispose(ref);
}
 else {
throw new InternalException("ObjectReference " + object.ref() + " not found in object cache");
}
}
synchronized void removeObjectMirror(SoftObjectReference ref){
objectsByID.remove(ref.key());
}
ObjectReferenceImpl objectMirror(long id){
return objectMirror(id,JDWP.Tag.OBJECT);
}
StringReferenceImpl stringMirror(long id){
return (StringReferenceImpl)objectMirror(id,JDWP.Tag.STRING);
}
ArrayReferenceImpl arrayMirror(long id){
return (ArrayReferenceImpl)objectMirror(id,JDWP.Tag.ARRAY);
}
ThreadReferenceImpl threadMirror(long id){
return (ThreadReferenceImpl)objectMirror(id,JDWP.Tag.THREAD);
}
ThreadGroupReferenceImpl threadGroupMirror(long id){
return (ThreadGroupReferenceImpl)objectMirror(id,JDWP.Tag.THREAD_GROUP);
}
ClassLoaderReferenceImpl classLoaderMirror(long id){
return (ClassLoaderReferenceImpl)objectMirror(id,JDWP.Tag.CLASS_LOADER);
}
ClassObjectReferenceImpl classObjectMirror(long id){
return (ClassObjectReferenceImpl)objectMirror(id,JDWP.Tag.CLASS_OBJECT);
}
private JDWP.VirtualMachine.ClassPaths getClasspath(){
if (pathInfo == null) {
try {
pathInfo=JDWP.VirtualMachine.ClassPaths.process(vm);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
}
return pathInfo;
}
public List<String> classPath(){
return Arrays.asList(getClasspath().classpaths);
}
public List<String> bootClassPath(){
return Arrays.asList(getClasspath().bootclasspaths);
}
public String baseDirectory(){
return getClasspath().baseDir;
}
public void setDefaultStratum(String stratum){
defaultStratum=stratum;
if (stratum == null) {
stratum="";
}
try {
JDWP.VirtualMachine.SetDefaultStratum.process(vm,stratum);
}
 catch (JDWPException exc) {
throw exc.toJDIException();
}
}
public String getDefaultStratum(){
return defaultStratum;
}
ThreadGroup threadGroupForJDI(){
return threadGroupForJDI;
}
static private class SoftObjectReference extends SoftReference<ObjectReferenceImpl> {
int count;
Long key;
SoftObjectReference(Long key,ObjectReferenceImpl mirror,ReferenceQueue<ObjectReferenceImpl> queue){
super(mirror,queue);
this.count=1;
this.key=key;
}
int count(){
return count;
}
void incrementCount(){
count++;
}
Long key(){
return key;
}
ObjectReferenceImpl object(){
return get();
}
}
}
