package com.sun.jmx.snmp.daemon;
import java.util.logging.Level;
import java.util.Vector;
import static com.sun.jmx.defaults.JmxProperties.SNMP_ADAPTOR_LOGGER;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibRequest;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
class SnmpSubRequestHandler implements SnmpDefinitions, Runnable {
  protected SnmpIncomingRequest incRequest=null;
  protected SnmpEngine engine=null;
  /** 
 * V3 enabled Adaptor. Each Oid is added using updateRequest method.
 */
  protected SnmpSubRequestHandler(  SnmpEngine engine,  SnmpIncomingRequest incRequest,  SnmpMibAgent agent,  SnmpPdu req){
    this(agent,req);
    init(engine,incRequest);
  }
  /** 
 * V3 enabled Adaptor.
 */
  protected SnmpSubRequestHandler(  SnmpEngine engine,  SnmpIncomingRequest incRequest,  SnmpMibAgent agent,  SnmpPdu req,  boolean nouse){
    this(agent,req,nouse);
    init(engine,incRequest);
  }
  /** 
 * SNMP V1/V2 . To be called with updateRequest.
 */
  protected SnmpSubRequestHandler(  SnmpMibAgent agent,  SnmpPdu req){
    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"constructor","creating instance for request " + String.valueOf(req.requestId));
    }
    version=req.version;
    type=req.type;
    this.agent=agent;
    reqPdu=req;
    int length=req.varBindList.length;
    translation=new int[length];
    varBind=new NonSyncVector<SnmpVarBind>(length);
  }
  /** 
 * SNMP V1/V2 The constuctor initialize the subrequest with the whole varbind list contained
 * in the original request.
 */
  @SuppressWarnings("unchecked") protected SnmpSubRequestHandler(  SnmpMibAgent agent,  SnmpPdu req,  boolean nouse){
    this(agent,req);
    int max=translation.length;
    SnmpVarBind[] list=req.varBindList;
    for (int i=0; i < max; i++) {
      translation[i]=i;
      ((NonSyncVector<SnmpVarBind>)varBind).addNonSyncElement(list[i]);
    }
  }
  SnmpMibRequest createMibRequest(  Vector<SnmpVarBind> vblist,  int protocolVersion,  Object userData){
    if (type == pduSetRequestPdu && mibRequest != null)     return mibRequest;
    SnmpMibRequest result=null;
    if (incRequest != null) {
      result=SnmpMibAgent.newMibRequest(engine,reqPdu,vblist,protocolVersion,userData,incRequest.getPrincipal(),incRequest.getSecurityLevel(),incRequest.getSecurityModel(),incRequest.getContextName(),incRequest.getAccessContext());
    }
 else {
      result=SnmpMibAgent.newMibRequest(reqPdu,vblist,protocolVersion,userData);
    }
    if (type == pduWalkRequest)     mibRequest=result;
    return result;
  }
  void setUserData(  Object userData){
    data=userData;
  }
  public void run(){
    try {
      final ThreadContext oldContext=ThreadContext.push("SnmpUserData",data);
      try {
switch (type) {
case pduGetRequestPdu:
          if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:get operation on "+ agent.getMibName());
          }
        agent.get(createMibRequest(varBind,version,data));
      break;
case pduGetNextRequestPdu:
    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:getNext operation on "+ agent.getMibName());
    }
  agent.getNext(createMibRequest(varBind,version,data));
break;
case pduSetRequestPdu:
if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:set operation on "+ agent.getMibName());
}
agent.set(createMibRequest(varBind,version,data));
break;
case pduWalkRequest:
if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:check operation on "+ agent.getMibName());
}
agent.check(createMibRequest(varBind,version,data));
break;
default :
if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:unknown operation ("+ type+ ") on "+ agent.getMibName());
}
errorStatus=snmpRspGenErr;
errorIndex=1;
break;
}
}
  finally {
ThreadContext.restore(oldContext);
}
}
 catch (SnmpStatusException x) {
errorStatus=x.getStatus();
errorIndex=x.getErrorIndex();
if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:an Snmp error occured during the operation",x);
}
}
catch (Exception x) {
errorStatus=SnmpDefinitions.snmpRspGenErr;
if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:a generic error occured during the operation",x);
}
}
if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:operation completed");
}
}
static final int mapErrorStatusToV1(int errorStatus,int reqPduType){
if (errorStatus == SnmpDefinitions.snmpRspNoError) return SnmpDefinitions.snmpRspNoError;
if (errorStatus == SnmpDefinitions.snmpRspGenErr) return SnmpDefinitions.snmpRspGenErr;
if (errorStatus == SnmpDefinitions.snmpRspNoSuchName) return SnmpDefinitions.snmpRspNoSuchName;
if ((errorStatus == SnmpStatusException.noSuchInstance) || (errorStatus == SnmpStatusException.noSuchObject) || (errorStatus == SnmpDefinitions.snmpRspNoAccess)|| (errorStatus == SnmpDefinitions.snmpRspInconsistentName)|| (errorStatus == SnmpDefinitions.snmpRspAuthorizationError)) {
return SnmpDefinitions.snmpRspNoSuchName;
}
 else if ((errorStatus == SnmpDefinitions.snmpRspAuthorizationError) || (errorStatus == SnmpDefinitions.snmpRspNotWritable)) {
if (reqPduType == SnmpDefinitions.pduWalkRequest) return SnmpDefinitions.snmpRspReadOnly;
 else return SnmpDefinitions.snmpRspNoSuchName;
}
 else if ((errorStatus == SnmpDefinitions.snmpRspNoCreation)) {
return SnmpDefinitions.snmpRspNoSuchName;
}
 else if ((errorStatus == SnmpDefinitions.snmpRspWrongType) || (errorStatus == SnmpDefinitions.snmpRspWrongLength) || (errorStatus == SnmpDefinitions.snmpRspWrongEncoding)|| (errorStatus == SnmpDefinitions.snmpRspWrongValue)|| (errorStatus == SnmpDefinitions.snmpRspWrongLength)|| (errorStatus == SnmpDefinitions.snmpRspInconsistentValue)) {
if ((reqPduType == SnmpDefinitions.pduSetRequestPdu) || (reqPduType == SnmpDefinitions.pduWalkRequest)) return SnmpDefinitions.snmpRspBadValue;
 else return SnmpDefinitions.snmpRspNoSuchName;
}
 else if ((errorStatus == SnmpDefinitions.snmpRspResourceUnavailable) || (errorStatus == SnmpDefinitions.snmpRspCommitFailed) || (errorStatus == SnmpDefinitions.snmpRspUndoFailed)) {
return SnmpDefinitions.snmpRspGenErr;
}
if (errorStatus == SnmpDefinitions.snmpRspTooBig) return SnmpDefinitions.snmpRspTooBig;
if ((errorStatus == SnmpDefinitions.snmpRspBadValue) || (errorStatus == SnmpDefinitions.snmpRspReadOnly)) {
if ((reqPduType == SnmpDefinitions.pduSetRequestPdu) || (reqPduType == SnmpDefinitions.pduWalkRequest)) return errorStatus;
 else return SnmpDefinitions.snmpRspNoSuchName;
}
return SnmpDefinitions.snmpRspGenErr;
}
static final int mapErrorStatusToV2(int errorStatus,int reqPduType){
if (errorStatus == SnmpDefinitions.snmpRspNoError) return SnmpDefinitions.snmpRspNoError;
if (errorStatus == SnmpDefinitions.snmpRspGenErr) return SnmpDefinitions.snmpRspGenErr;
if (errorStatus == SnmpDefinitions.snmpRspTooBig) return SnmpDefinitions.snmpRspTooBig;
if ((reqPduType != SnmpDefinitions.pduSetRequestPdu) && (reqPduType != SnmpDefinitions.pduWalkRequest)) {
if (errorStatus == SnmpDefinitions.snmpRspAuthorizationError) return errorStatus;
 else return SnmpDefinitions.snmpRspGenErr;
}
if (errorStatus == SnmpDefinitions.snmpRspNoSuchName) return SnmpDefinitions.snmpRspNoAccess;
if (errorStatus == SnmpDefinitions.snmpRspReadOnly) return SnmpDefinitions.snmpRspNotWritable;
if (errorStatus == SnmpDefinitions.snmpRspBadValue) return SnmpDefinitions.snmpRspWrongValue;
if ((errorStatus == SnmpDefinitions.snmpRspNoAccess) || (errorStatus == SnmpDefinitions.snmpRspInconsistentName) || (errorStatus == SnmpDefinitions.snmpRspAuthorizationError)|| (errorStatus == SnmpDefinitions.snmpRspNotWritable)|| (errorStatus == SnmpDefinitions.snmpRspNoCreation)|| (errorStatus == SnmpDefinitions.snmpRspWrongType)|| (errorStatus == SnmpDefinitions.snmpRspWrongLength)|| (errorStatus == SnmpDefinitions.snmpRspWrongEncoding)|| (errorStatus == SnmpDefinitions.snmpRspWrongValue)|| (errorStatus == SnmpDefinitions.snmpRspWrongLength)|| (errorStatus == SnmpDefinitions.snmpRspInconsistentValue)|| (errorStatus == SnmpDefinitions.snmpRspResourceUnavailable)|| (errorStatus == SnmpDefinitions.snmpRspCommitFailed)|| (errorStatus == SnmpDefinitions.snmpRspUndoFailed)) return errorStatus;
return SnmpDefinitions.snmpRspGenErr;
}
static final int mapErrorStatus(int errorStatus,int protocolVersion,int reqPduType){
if (errorStatus == SnmpDefinitions.snmpRspNoError) return SnmpDefinitions.snmpRspNoError;
if (protocolVersion == SnmpDefinitions.snmpVersionOne) return mapErrorStatusToV1(errorStatus,reqPduType);
if (protocolVersion == SnmpDefinitions.snmpVersionTwo || protocolVersion == SnmpDefinitions.snmpVersionThree) return mapErrorStatusToV2(errorStatus,reqPduType);
return SnmpDefinitions.snmpRspGenErr;
}
/** 
 * The method returns the error status of the operation.
 * The method takes into account the protocol version.
 */
protected int getErrorStatus(){
if (errorStatus == snmpRspNoError) return snmpRspNoError;
return mapErrorStatus(errorStatus,version,type);
}
/** 
 * The method returns the error index as a position in the var bind list.
 * The value returned by the method corresponds to the index in the original
 * var bind list as received by the SNMP protocol adaptor.
 */
protected int getErrorIndex(){
if (errorStatus == snmpRspNoError) return -1;
if ((errorIndex == 0) || (errorIndex == -1)) errorIndex=1;
return translation[errorIndex - 1];
}
/** 
 * The method updates the varbind list of the subrequest.
 */
protected void updateRequest(SnmpVarBind var,int pos){
int size=varBind.size();
translation[size]=pos;
varBind.addElement(var);
}
/** 
 * The method updates a given var bind list with the result of a
 * previsouly invoked operation.
 * Prior to calling the method, one must make sure that the operation was
 * successful. As such the method getErrorIndex or getErrorStatus should be
 * called.
 */
protected void updateResult(SnmpVarBind[] result){
if (result == null) return;
final int max=varBind.size();
final int len=result.length;
for (int i=0; i < max; i++) {
final int pos=translation[i];
if (pos < len) {
result[pos]=(SnmpVarBind)((NonSyncVector)varBind).elementAtNonSync(i);
}
 else {
if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"updateResult","Position `" + pos + "' is out of bound...");
}
}
}
}
private void init(SnmpEngine engine,SnmpIncomingRequest incRequest){
this.incRequest=incRequest;
this.engine=engine;
}
/** 
 * Store the protocol version to handle
 */
protected int version=snmpVersionOne;
/** 
 * Store the operation type. Remember if the type is Walk, it means
 * that we have to invoke the check method ...
 */
protected int type=0;
/** 
 * Agent directly handled by the sub-request handler.
 */
protected SnmpMibAgent agent;
/** 
 * Error status.
 */
protected int errorStatus=snmpRspNoError;
/** 
 * Index of error.
 * A value of -1 means no error.
 */
protected int errorIndex=-1;
/** 
 * The varbind list specific to the current sub request.
 * The vector must contain object of type SnmpVarBind.
 */
protected Vector<SnmpVarBind> varBind;
/** 
 * The array giving the index translation between the content of
 * <VAR>varBind</VAR> and the varbind list as specified in the request.
 */
protected int[] translation;
/** 
 * Contextual object allocated by the SnmpUserDataFactory.
 */
protected Object data;
/** 
 * The SnmpMibRequest that will be passed to the agent.
 */
private SnmpMibRequest mibRequest=null;
/** 
 * The SnmpPdu that will be passed to the request.
 */
private SnmpPdu reqPdu=null;
@SuppressWarnings("serial") class NonSyncVector<E> extends Vector<E> {
public NonSyncVector(int size){
super(size);
}
final void addNonSyncElement(E obj){
ensureCapacity(elementCount + 1);
elementData[elementCount++]=obj;
}
@SuppressWarnings("unchecked") final E elementAtNonSync(int index){
return (E)elementData[index];
}
}
}
