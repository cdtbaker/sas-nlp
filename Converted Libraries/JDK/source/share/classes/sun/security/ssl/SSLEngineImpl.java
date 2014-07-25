package sun.security.ssl;
import java.io.*;
import java.nio.*;
import java.nio.ReadOnlyBufferException;
import java.util.LinkedList;
import java.security.*;
import javax.crypto.BadPaddingException;
import javax.net.ssl.*;
import javax.net.ssl.SSLEngineResult.*;
import com.sun.net.ssl.internal.ssl.X509ExtendedTrustManager;
/** 
 * Implementation of an non-blocking SSLEngine.
 * *Currently*, the SSLEngine code exists in parallel with the current
 * SSLSocket.  As such, the current implementation is using legacy code
 * with many of the same abstractions.  However, it varies in many
 * areas, most dramatically in the IO handling.
 * There are three main I/O threads that can be existing in parallel:
 * wrap(), unwrap(), and beginHandshake().  We are encouraging users to
 * not call multiple instances of wrap or unwrap, because the data could
 * appear to flow out of the SSLEngine in a non-sequential order.  We
 * take all steps we can to at least make sure the ordering remains
 * consistent, but once the calls returns, anything can happen.  For
 * example, thread1 and thread2 both call wrap, thread1 gets the first
 * packet, thread2 gets the second packet, but thread2 gets control back
 * before thread1, and sends the data.  The receiving side would see an
 * out-of-order error.
 * Handshaking is still done the same way as SSLSocket using the normal
 * InputStream/OutputStream abstactions.  We create
 * ClientHandshakers/ServerHandshakers, which produce/consume the
 * handshaking data.  The transfer of the data is largely handled by the
 * HandshakeInStream/HandshakeOutStreams.  Lastly, the
 * InputRecord/OutputRecords still have the same functionality, except
 * that they are overridden with EngineInputRecord/EngineOutputRecord,
 * which provide SSLEngine-specific functionality.
 * Some of the major differences are:
 * EngineInputRecord/EngineOutputRecord/EngineWriter:
 * In order to avoid writing whole new control flows for
 * handshaking, and to reuse most of the same code, we kept most
 * of the actual handshake code the same.  As usual, reading
 * handshake data may trigger output of more handshake data, so
 * what we do is write this data to internal buffers, and wait for
 * wrap() to be called to give that data a ride.
 * All data is routed through
 * EngineInputRecord/EngineOutputRecord.  However, all handshake
 * data (ct_alert/ct_change_cipher_spec/ct_handshake) are passed
 * through to the the underlying InputRecord/OutputRecord, and
 * the data uses the internal buffers.
 * Application data is handled slightly different, we copy the data
 * directly from the src to the dst buffers, and do all operations
 * on those buffers, saving the overhead of multiple copies.
 * In the case of an inbound record, unwrap passes the inbound
 * ByteBuffer to the InputRecord.  If the data is handshake data,
 * the data is read into the InputRecord's internal buffer.  If
 * the data is application data, the data is decoded directly into
 * the dst buffer.
 * In the case of an outbound record, when the write to the
 * "real" OutputStream's would normally take place, instead we
 * call back up to the EngineOutputRecord's version of
 * writeBuffer, at which time we capture the resulting output in a
 * ByteBuffer, and send that back to the EngineWriter for internal
 * storage.
 * EngineWriter is responsible for "handling" all outbound
 * data, be it handshake or app data, and for returning the data
 * to wrap() in the proper order.
 * ClientHandshaker/ServerHandshaker/Handshaker:
 * Methods which relied on SSLSocket now have work on either
 * SSLSockets or SSLEngines.
 * @author Brad Wetmore
 */
final public class SSLEngineImpl extends SSLEngine {
  private int connectionState;
  private static final int cs_START=0;
  private static final int cs_HANDSHAKE=1;
  private static final int cs_DATA=2;
  private static final int cs_RENEGOTIATE=3;
  private static final int cs_ERROR=4;
  private static final int cs_CLOSED=6;
  private boolean inboundDone=false;
  EngineWriter writer;
  private SSLContextImpl sslContext;
  private Handshaker handshaker;
  private SSLSessionImpl sess;
  private volatile SSLSessionImpl handshakeSession;
  static final byte clauth_none=0;
  static final byte clauth_requested=1;
  static final byte clauth_required=2;
  private boolean expectingFinished;
  private boolean recvCN;
  private SSLException closeReason;
  private byte doClientAuth;
  private boolean enableSessionCreation=true;
  EngineInputRecord inputRecord;
  EngineOutputRecord outputRecord;
  private AccessControlContext acc;
  private CipherSuiteList enabledCipherSuites;
  private String identificationProtocol=null;
  private AlgorithmConstraints algorithmConstraints=null;
  private boolean serverModeSet=false;
  private boolean roleIsServer;
  private ProtocolList enabledProtocols;
  private ProtocolVersion protocolVersion=ProtocolVersion.DEFAULT;
  private MAC readMAC, writeMAC;
  private CipherBox readCipher, writeCipher;
  private boolean secureRenegotiation;
  private byte[] clientVerifyData;
  private byte[] serverVerifyData;
  private Object wrapLock;
  private Object unwrapLock;
  Object writeLock;
  private static final Debug debug=Debug.getInstance("ssl");
  /** 
 * Constructor for an SSLEngine from SSLContext, without
 * host/port hints.  This Engine will not be able to cache
 * sessions, but must renegotiate everything by hand.
 */
  SSLEngineImpl(  SSLContextImpl ctx){
    super();
    init(ctx);
  }
  /** 
 * Constructor for an SSLEngine from SSLContext.
 */
  SSLEngineImpl(  SSLContextImpl ctx,  String host,  int port){
    super(host,port);
    init(ctx);
  }
  /** 
 * Initializes the Engine
 */
  private void init(  SSLContextImpl ctx){
    if (debug != null && Debug.isOn("ssl")) {
      System.out.println("Using SSLEngineImpl.");
    }
    sslContext=ctx;
    sess=SSLSessionImpl.nullSession;
    handshakeSession=null;
    roleIsServer=true;
    connectionState=cs_START;
    readCipher=CipherBox.NULL;
    readMAC=MAC.NULL;
    writeCipher=CipherBox.NULL;
    writeMAC=MAC.NULL;
    secureRenegotiation=false;
    clientVerifyData=new byte[0];
    serverVerifyData=new byte[0];
    enabledCipherSuites=sslContext.getDefaultCipherSuiteList(roleIsServer);
    enabledProtocols=sslContext.getDefaultProtocolList(roleIsServer);
    wrapLock=new Object();
    unwrapLock=new Object();
    writeLock=new Object();
    acc=AccessController.getContext();
    outputRecord=new EngineOutputRecord(Record.ct_application_data,this);
    inputRecord=new EngineInputRecord(this);
    inputRecord.enableFormatChecks();
    writer=new EngineWriter();
  }
  /** 
 * Initialize the handshaker object. This means:
 * . if a handshake is already in progress (state is cs_HANDSHAKE
 * or cs_RENEGOTIATE), do nothing and return
 * . if the engine is already closed, throw an Exception (internal error)
 * . otherwise (cs_START or cs_DATA), create the appropriate handshaker
 * object and advance the connection state (to cs_HANDSHAKE or
 * cs_RENEGOTIATE, respectively).
 * This method is called right after a new engine is created, when
 * starting renegotiation, or when changing client/server mode of the
 * engine.
 */
  private void initHandshaker(){
switch (connectionState) {
case cs_START:
case cs_DATA:
      break;
case cs_HANDSHAKE:
case cs_RENEGOTIATE:
    return;
default :
  throw new IllegalStateException("Internal error");
}
if (connectionState == cs_START) {
connectionState=cs_HANDSHAKE;
}
 else {
connectionState=cs_RENEGOTIATE;
}
if (roleIsServer) {
handshaker=new ServerHandshaker(this,sslContext,enabledProtocols,doClientAuth,protocolVersion,connectionState == cs_HANDSHAKE,secureRenegotiation,clientVerifyData,serverVerifyData);
}
 else {
handshaker=new ClientHandshaker(this,sslContext,enabledProtocols,protocolVersion,connectionState == cs_HANDSHAKE,secureRenegotiation,clientVerifyData,serverVerifyData);
}
handshaker.setEnabledCipherSuites(enabledCipherSuites);
handshaker.setEnableSessionCreation(enableSessionCreation);
}
private HandshakeStatus getHSStatus(HandshakeStatus hss){
if (hss != null) {
return hss;
}
synchronized (this) {
if (writer.hasOutboundData()) {
  return HandshakeStatus.NEED_WRAP;
}
 else if (handshaker != null) {
  if (handshaker.taskOutstanding()) {
    return HandshakeStatus.NEED_TASK;
  }
 else {
    return HandshakeStatus.NEED_UNWRAP;
  }
}
 else if (connectionState == cs_CLOSED) {
  if (!isInboundDone()) {
    return HandshakeStatus.NEED_UNWRAP;
  }
}
return HandshakeStatus.NOT_HANDSHAKING;
}
}
synchronized private void checkTaskThrown() throws SSLException {
if (handshaker != null) {
handshaker.checkThrown();
}
}
synchronized private int getConnectionState(){
return connectionState;
}
synchronized private void setConnectionState(int state){
connectionState=state;
}
AccessControlContext getAcc(){
return acc;
}
public SSLEngineResult.HandshakeStatus getHandshakeStatus(){
return getHSStatus(null);
}
private void changeReadCiphers() throws SSLException {
if (connectionState != cs_HANDSHAKE && connectionState != cs_RENEGOTIATE) {
throw new SSLProtocolException("State error, change cipher specs");
}
CipherBox oldCipher=readCipher;
try {
readCipher=handshaker.newReadCipher();
readMAC=handshaker.newReadMAC();
}
 catch (GeneralSecurityException e) {
throw (SSLException)new SSLException("Algorithm missing:  ").initCause(e);
}
oldCipher.dispose();
}
void changeWriteCiphers() throws SSLException {
if (connectionState != cs_HANDSHAKE && connectionState != cs_RENEGOTIATE) {
throw new SSLProtocolException("State error, change cipher specs");
}
CipherBox oldCipher=writeCipher;
try {
writeCipher=handshaker.newWriteCipher();
writeMAC=handshaker.newWriteMAC();
}
 catch (GeneralSecurityException e) {
throw (SSLException)new SSLException("Algorithm missing:  ").initCause(e);
}
oldCipher.dispose();
}
synchronized void setVersion(ProtocolVersion protocolVersion){
this.protocolVersion=protocolVersion;
outputRecord.setVersion(protocolVersion);
}
/** 
 * Kickstart the handshake if it is not already in progress.
 * This means:
 * . if handshaking is already underway, do nothing and return
 * . if the engine is not connected or already closed, throw an
 * Exception.
 * . otherwise, call initHandshake() to initialize the handshaker
 * object and progress the state. Then, send the initial
 * handshaking message if appropriate (always on clients and
 * on servers when renegotiating).
 */
private synchronized void kickstartHandshake() throws IOException {
switch (connectionState) {
case cs_START:
if (!serverModeSet) {
  throw new IllegalStateException("Client/Server mode not yet set.");
}
initHandshaker();
break;
case cs_HANDSHAKE:
break;
case cs_DATA:
if (!secureRenegotiation && !Handshaker.allowUnsafeRenegotiation) {
throw new SSLHandshakeException("Insecure renegotiation is not allowed");
}
if (!secureRenegotiation) {
if (debug != null && Debug.isOn("handshake")) {
System.out.println("Warning: Using insecure renegotiation");
}
}
initHandshaker();
break;
case cs_RENEGOTIATE:
return;
default :
throw new SSLException("SSLEngine is closing/closed");
}
if (!handshaker.activated()) {
if (connectionState == cs_RENEGOTIATE) {
handshaker.activate(protocolVersion);
}
 else {
handshaker.activate(null);
}
if (handshaker instanceof ClientHandshaker) {
handshaker.kickstart();
}
 else {
if (connectionState == cs_HANDSHAKE) {
}
 else {
handshaker.kickstart();
handshaker.handshakeHash.reset();
}
}
}
}
public void beginHandshake() throws SSLException {
try {
kickstartHandshake();
}
 catch (Exception e) {
fatal(Alerts.alert_handshake_failure,"Couldn't kickstart handshaking",e);
}
}
/** 
 * Unwraps a buffer.  Does a variety of checks before grabbing
 * the unwrapLock, which blocks multiple unwraps from occuring.
 */
public SSLEngineResult unwrap(ByteBuffer netData,ByteBuffer[] appData,int offset,int length) throws SSLException {
EngineArgs ea=new EngineArgs(netData,appData,offset,length);
try {
synchronized (unwrapLock) {
return readNetRecord(ea);
}
}
 catch (Exception e) {
fatal(Alerts.alert_internal_error,"problem unwrapping net record",e);
return null;
}
 finally {
ea.resetLim();
}
}
private SSLEngineResult readNetRecord(EngineArgs ea) throws IOException {
Status status=null;
HandshakeStatus hsStatus=null;
checkTaskThrown();
if (isInboundDone()) {
return new SSLEngineResult(Status.CLOSED,getHSStatus(null),0,0);
}
synchronized (this) {
if ((connectionState == cs_HANDSHAKE) || (connectionState == cs_START)) {
kickstartHandshake();
hsStatus=getHSStatus(null);
if (hsStatus == HandshakeStatus.NEED_WRAP) {
return new SSLEngineResult(Status.OK,hsStatus,0,0);
}
}
}
if (hsStatus == null) {
hsStatus=getHSStatus(null);
}
if (hsStatus == HandshakeStatus.NEED_TASK) {
return new SSLEngineResult(Status.OK,hsStatus,0,0);
}
int packetLen=inputRecord.bytesInCompletePacket(ea.netData);
if (packetLen > sess.getPacketBufferSize()) {
if (packetLen > Record.maxLargeRecordSize) {
throw new SSLProtocolException("Input SSL/TLS record too big: max = " + Record.maxLargeRecordSize + " len = "+ packetLen);
}
 else {
sess.expandBufferSizes();
}
}
if ((packetLen - Record.headerSize) > ea.getAppRemaining()) {
return new SSLEngineResult(Status.BUFFER_OVERFLOW,hsStatus,0,0);
}
if ((packetLen == -1) || (ea.netData.remaining() < packetLen)) {
return new SSLEngineResult(Status.BUFFER_UNDERFLOW,hsStatus,0,0);
}
try {
hsStatus=readRecord(ea);
}
 catch (SSLException e) {
throw e;
}
catch (IOException e) {
SSLException ex=new SSLException("readRecord");
ex.initCause(e);
throw ex;
}
status=(isInboundDone() ? Status.CLOSED : Status.OK);
hsStatus=getHSStatus(hsStatus);
return new SSLEngineResult(status,hsStatus,ea.deltaNet(),ea.deltaApp());
}
private HandshakeStatus readRecord(EngineArgs ea) throws IOException {
HandshakeStatus hsStatus=null;
ByteBuffer readBB=null;
ByteBuffer decryptedBB=null;
if (getConnectionState() != cs_ERROR) {
try {
readBB=inputRecord.read(ea.netData);
}
 catch (IOException e) {
fatal(Alerts.alert_unexpected_message,e);
}
try {
decryptedBB=inputRecord.decrypt(readCipher,readBB);
}
 catch (BadPaddingException e) {
readBB.rewind();
inputRecord.checkMAC(readMAC,readBB);
byte alertType=(inputRecord.contentType() == Record.ct_handshake) ? Alerts.alert_handshake_failure : Alerts.alert_bad_record_mac;
fatal(alertType,"Invalid padding",e);
}
if (!inputRecord.checkMAC(readMAC,decryptedBB)) {
if (inputRecord.contentType() == Record.ct_handshake) {
fatal(Alerts.alert_handshake_failure,"bad handshake record MAC");
}
 else {
fatal(Alerts.alert_bad_record_mac,"bad record MAC");
}
}
synchronized (this) {
switch (inputRecord.contentType()) {
case Record.ct_handshake:
initHandshaker();
if (!handshaker.activated()) {
if (connectionState == cs_RENEGOTIATE) {
handshaker.activate(protocolVersion);
}
 else {
handshaker.activate(null);
}
}
handshaker.process_record(inputRecord,expectingFinished);
expectingFinished=false;
if (handshaker.invalidated) {
handshaker=null;
if (connectionState == cs_RENEGOTIATE) {
connectionState=cs_DATA;
}
}
 else if (handshaker.isDone()) {
secureRenegotiation=handshaker.isSecureRenegotiation();
clientVerifyData=handshaker.getClientVerifyData();
serverVerifyData=handshaker.getServerVerifyData();
sess=handshaker.getSession();
handshakeSession=null;
if (!writer.hasOutboundData()) {
hsStatus=HandshakeStatus.FINISHED;
}
handshaker=null;
connectionState=cs_DATA;
}
 else if (handshaker.taskOutstanding()) {
hsStatus=HandshakeStatus.NEED_TASK;
}
break;
case Record.ct_application_data:
if ((connectionState != cs_DATA) && (connectionState != cs_RENEGOTIATE) && (connectionState != cs_CLOSED)) {
throw new SSLProtocolException("Data received in non-data state: " + connectionState);
}
if (expectingFinished) {
throw new SSLProtocolException("Expecting finished message, received data");
}
if (!inboundDone) {
ea.scatter(decryptedBB.slice());
}
break;
case Record.ct_alert:
recvAlert();
break;
case Record.ct_change_cipher_spec:
if ((connectionState != cs_HANDSHAKE && connectionState != cs_RENEGOTIATE) || inputRecord.available() != 1 || inputRecord.read() != 1) {
fatal(Alerts.alert_unexpected_message,"illegal change cipher spec msg, state = " + connectionState);
}
changeReadCiphers();
expectingFinished=true;
break;
default :
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", Received record type: " + inputRecord.contentType());
}
break;
}
if (connectionState < cs_ERROR && !isInboundDone() && (hsStatus == HandshakeStatus.NOT_HANDSHAKING)) {
if (checkSequenceNumber(readMAC,inputRecord.contentType())) {
hsStatus=getHSStatus(null);
}
}
}
}
return hsStatus;
}
/** 
 * Wraps a buffer.  Does a variety of checks before grabbing
 * the wrapLock, which blocks multiple wraps from occuring.
 */
public SSLEngineResult wrap(ByteBuffer[] appData,int offset,int length,ByteBuffer netData) throws SSLException {
EngineArgs ea=new EngineArgs(appData,offset,length,netData);
if (netData.remaining() < outputRecord.maxRecordSize) {
return new SSLEngineResult(Status.BUFFER_OVERFLOW,getHSStatus(null),0,0);
}
try {
synchronized (wrapLock) {
return writeAppRecord(ea);
}
}
 catch (Exception e) {
ea.resetPos();
fatal(Alerts.alert_internal_error,"problem unwrapping net record",e);
return null;
}
 finally {
ea.resetLim();
}
}
private SSLEngineResult writeAppRecord(EngineArgs ea) throws IOException {
Status status=null;
HandshakeStatus hsStatus=null;
checkTaskThrown();
if (writer.isOutboundDone()) {
return new SSLEngineResult(Status.CLOSED,getHSStatus(null),0,0);
}
synchronized (this) {
if ((connectionState == cs_HANDSHAKE) || (connectionState == cs_START)) {
kickstartHandshake();
hsStatus=getHSStatus(null);
if (hsStatus == HandshakeStatus.NEED_UNWRAP) {
return new SSLEngineResult(Status.OK,hsStatus,0,0);
}
}
}
if (hsStatus == null) {
hsStatus=getHSStatus(null);
}
if (hsStatus == HandshakeStatus.NEED_TASK) {
return new SSLEngineResult(Status.OK,hsStatus,0,0);
}
try {
synchronized (writeLock) {
hsStatus=writeRecord(outputRecord,ea);
}
}
 catch (SSLException e) {
throw e;
}
catch (IOException e) {
SSLException ex=new SSLException("Write problems");
ex.initCause(e);
throw ex;
}
status=(isOutboundDone() ? Status.CLOSED : Status.OK);
hsStatus=getHSStatus(hsStatus);
return new SSLEngineResult(status,hsStatus,ea.deltaApp(),ea.deltaNet());
}
private HandshakeStatus writeRecord(EngineOutputRecord eor,EngineArgs ea) throws IOException {
HandshakeStatus hsStatus=writer.writeRecord(eor,ea,writeMAC,writeCipher);
if (connectionState < cs_ERROR && !isOutboundDone() && (hsStatus == HandshakeStatus.NOT_HANDSHAKING)) {
if (checkSequenceNumber(writeMAC,eor.contentType())) {
hsStatus=getHSStatus(null);
}
}
return hsStatus;
}
void writeRecord(EngineOutputRecord eor) throws IOException {
writer.writeRecord(eor,writeMAC,writeCipher);
if ((connectionState < cs_ERROR) && !isOutboundDone()) {
checkSequenceNumber(writeMAC,eor.contentType());
}
}
/** 
 * Check the sequence number state
 * RFC 4346 states that, "Sequence numbers are of type uint64 and
 * may not exceed 2^64-1.  Sequence numbers do not wrap. If a TLS
 * implementation would need to wrap a sequence number, it must
 * renegotiate instead."
 * Return true if the handshake status may be changed.
 */
private boolean checkSequenceNumber(MAC mac,byte type) throws IOException {
if (connectionState >= cs_ERROR || mac == MAC.NULL) {
return false;
}
if (mac.seqNumOverflow()) {
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", sequence number extremely close to overflow " + "(2^64-1 packets). Closing connection.");
}
fatal(Alerts.alert_handshake_failure,"sequence number overflow");
return true;
}
if ((type != Record.ct_handshake) && mac.seqNumIsHuge()) {
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", request renegotiation " + "to avoid sequence number overflow");
}
beginHandshake();
return true;
}
return false;
}
/** 
 * Signals that no more outbound application data will be sent
 * on this <code>SSLEngine</code>.
 */
private void closeOutboundInternal(){
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", closeOutboundInternal()");
}
if (writer.isOutboundDone()) {
return;
}
switch (connectionState) {
case cs_START:
writer.closeOutbound();
inboundDone=true;
break;
case cs_ERROR:
case cs_CLOSED:
break;
default :
warning(Alerts.alert_close_notify);
writer.closeOutbound();
break;
}
writeCipher.dispose();
connectionState=cs_CLOSED;
}
synchronized public void closeOutbound(){
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", called closeOutbound()");
}
closeOutboundInternal();
}
/** 
 * Returns the outbound application data closure state
 */
public boolean isOutboundDone(){
return writer.isOutboundDone();
}
/** 
 * Signals that no more inbound network data will be sent
 * to this <code>SSLEngine</code>.
 */
private void closeInboundInternal(){
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", closeInboundInternal()");
}
if (inboundDone) {
return;
}
closeOutboundInternal();
inboundDone=true;
readCipher.dispose();
connectionState=cs_CLOSED;
}
synchronized public void closeInbound() throws SSLException {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", called closeInbound()");
}
if ((connectionState != cs_START) && !recvCN) {
recvCN=true;
fatal(Alerts.alert_internal_error,"Inbound closed before receiving peer's close_notify: " + "possible truncation attack?");
}
 else {
closeInboundInternal();
}
}
/** 
 * Returns the network inbound data closure state
 */
synchronized public boolean isInboundDone(){
return inboundDone;
}
/** 
 * Returns the current <code>SSLSession</code> for this
 * <code>SSLEngine</code>
 * <P>
 * These can be long lived, and frequently correspond to an
 * entire login session for some user.
 */
synchronized public SSLSession getSession(){
return sess;
}
@Override synchronized public SSLSession getHandshakeSession(){
return handshakeSession;
}
synchronized void setHandshakeSession(SSLSessionImpl session){
handshakeSession=session;
}
/** 
 * Returns a delegated <code>Runnable</code> task for
 * this <code>SSLEngine</code>.
 */
synchronized public Runnable getDelegatedTask(){
if (handshaker != null) {
return handshaker.getTask();
}
return null;
}
void warning(byte description){
sendAlert(Alerts.alert_warning,description);
}
synchronized void fatal(byte description,String diagnostic) throws SSLException {
fatal(description,diagnostic,null);
}
synchronized void fatal(byte description,Throwable cause) throws SSLException {
fatal(description,null,cause);
}
synchronized void fatal(byte description,String diagnostic,Throwable cause) throws SSLException {
if (diagnostic == null) {
diagnostic="General SSLEngine problem";
}
if (cause == null) {
cause=Alerts.getSSLException(description,cause,diagnostic);
}
if (closeReason != null) {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", fatal: engine already closed.  Rethrowing " + cause.toString());
}
if (cause instanceof RuntimeException) {
throw (RuntimeException)cause;
}
 else if (cause instanceof SSLException) {
throw (SSLException)cause;
}
 else if (cause instanceof Exception) {
SSLException ssle=new SSLException("fatal SSLEngine condition");
ssle.initCause(cause);
throw ssle;
}
}
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", fatal error: " + description+ ": "+ diagnostic+ "\n"+ cause.toString());
}
int oldState=connectionState;
connectionState=cs_ERROR;
inboundDone=true;
sess.invalidate();
if (handshakeSession != null) {
handshakeSession.invalidate();
}
if (oldState != cs_START) {
sendAlert(Alerts.alert_fatal,description);
}
if (cause instanceof SSLException) {
closeReason=(SSLException)cause;
}
 else {
closeReason=Alerts.getSSLException(description,cause,diagnostic);
}
writer.closeOutbound();
connectionState=cs_CLOSED;
readCipher.dispose();
writeCipher.dispose();
if (cause instanceof RuntimeException) {
throw (RuntimeException)cause;
}
 else {
throw closeReason;
}
}
private void recvAlert() throws IOException {
byte level=(byte)inputRecord.read();
byte description=(byte)inputRecord.read();
if (description == -1) {
fatal(Alerts.alert_illegal_parameter,"Short alert message");
}
if (debug != null && (Debug.isOn("record") || Debug.isOn("handshake"))) {
synchronized (System.out) {
System.out.print(threadName());
System.out.print(", RECV " + protocolVersion + " ALERT:  ");
if (level == Alerts.alert_fatal) {
System.out.print("fatal, ");
}
 else if (level == Alerts.alert_warning) {
System.out.print("warning, ");
}
 else {
System.out.print("<level " + (0x0ff & level) + ">, ");
}
System.out.println(Alerts.alertDescription(description));
}
}
if (level == Alerts.alert_warning) {
if (description == Alerts.alert_close_notify) {
if (connectionState == cs_HANDSHAKE) {
fatal(Alerts.alert_unexpected_message,"Received close_notify during handshake");
}
 else {
recvCN=true;
closeInboundInternal();
}
}
 else {
if (handshaker != null) {
handshaker.handshakeAlert(description);
}
}
}
 else {
String reason="Received fatal alert: " + Alerts.alertDescription(description);
if (closeReason == null) {
closeReason=Alerts.getSSLException(description,reason);
}
fatal(Alerts.alert_unexpected_message,reason);
}
}
private void sendAlert(byte level,byte description){
if (connectionState >= cs_CLOSED) {
return;
}
if (connectionState == cs_HANDSHAKE && (handshaker == null || !handshaker.started())) {
return;
}
EngineOutputRecord r=new EngineOutputRecord(Record.ct_alert,this);
r.setVersion(protocolVersion);
boolean useDebug=debug != null && Debug.isOn("ssl");
if (useDebug) {
synchronized (System.out) {
System.out.print(threadName());
System.out.print(", SEND " + protocolVersion + " ALERT:  ");
if (level == Alerts.alert_fatal) {
System.out.print("fatal, ");
}
 else if (level == Alerts.alert_warning) {
System.out.print("warning, ");
}
 else {
System.out.print("<level = " + (0x0ff & level) + ">, ");
}
System.out.println("description = " + Alerts.alertDescription(description));
}
}
r.write(level);
r.write(description);
try {
writeRecord(r);
}
 catch (IOException e) {
if (useDebug) {
System.out.println(threadName() + ", Exception sending alert: " + e);
}
}
}
/** 
 * Controls whether new connections may cause creation of new SSL
 * sessions.
 * As long as handshaking has not started, we can change
 * whether we enable session creations.  Otherwise,
 * we will need to wait for the next handshake.
 */
synchronized public void setEnableSessionCreation(boolean flag){
enableSessionCreation=flag;
if ((handshaker != null) && !handshaker.activated()) {
handshaker.setEnableSessionCreation(enableSessionCreation);
}
}
/** 
 * Returns true if new connections may cause creation of new SSL
 * sessions.
 */
synchronized public boolean getEnableSessionCreation(){
return enableSessionCreation;
}
/** 
 * Sets the flag controlling whether a server mode engine
 * *REQUIRES* SSL client authentication.
 * As long as handshaking has not started, we can change
 * whether client authentication is needed.  Otherwise,
 * we will need to wait for the next handshake.
 */
synchronized public void setNeedClientAuth(boolean flag){
doClientAuth=(flag ? SSLEngineImpl.clauth_required : SSLEngineImpl.clauth_none);
if ((handshaker != null) && (handshaker instanceof ServerHandshaker) && !handshaker.activated()) {
((ServerHandshaker)handshaker).setClientAuth(doClientAuth);
}
}
synchronized public boolean getNeedClientAuth(){
return (doClientAuth == SSLEngineImpl.clauth_required);
}
/** 
 * Sets the flag controlling whether a server mode engine
 * *REQUESTS* SSL client authentication.
 * As long as handshaking has not started, we can change
 * whether client authentication is requested.  Otherwise,
 * we will need to wait for the next handshake.
 */
synchronized public void setWantClientAuth(boolean flag){
doClientAuth=(flag ? SSLEngineImpl.clauth_requested : SSLEngineImpl.clauth_none);
if ((handshaker != null) && (handshaker instanceof ServerHandshaker) && !handshaker.activated()) {
((ServerHandshaker)handshaker).setClientAuth(doClientAuth);
}
}
synchronized public boolean getWantClientAuth(){
return (doClientAuth == SSLEngineImpl.clauth_requested);
}
/** 
 * Sets the flag controlling whether the engine is in SSL
 * client or server mode.  Must be called before any SSL
 * traffic has started.
 */
synchronized public void setUseClientMode(boolean flag){
switch (connectionState) {
case cs_START:
if (roleIsServer != (!flag) && sslContext.isDefaultProtocolList(enabledProtocols)) {
enabledProtocols=sslContext.getDefaultProtocolList(!flag);
}
roleIsServer=!flag;
serverModeSet=true;
break;
case cs_HANDSHAKE:
assert (handshaker != null);
if (!handshaker.activated()) {
if (roleIsServer != (!flag) && sslContext.isDefaultProtocolList(enabledProtocols)) {
enabledProtocols=sslContext.getDefaultProtocolList(!flag);
}
roleIsServer=!flag;
connectionState=cs_START;
initHandshaker();
break;
}
default :
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", setUseClientMode() invoked in state = " + connectionState);
}
throw new IllegalArgumentException("Cannot change mode after SSL traffic has started");
}
}
synchronized public boolean getUseClientMode(){
return !roleIsServer;
}
/** 
 * Returns the names of the cipher suites which could be enabled for use
 * on an SSL connection.  Normally, only a subset of these will actually
 * be enabled by default, since this list may include cipher suites which
 * do not support the mutual authentication of servers and clients, or
 * which do not protect data confidentiality.  Servers may also need
 * certain kinds of certificates to use certain cipher suites.
 * @return an array of cipher suite names
 */
public String[] getSupportedCipherSuites(){
return sslContext.getSuportedCipherSuiteList().toStringArray();
}
/** 
 * Controls which particular cipher suites are enabled for use on
 * this connection.  The cipher suites must have been listed by
 * getCipherSuites() as being supported.  Even if a suite has been
 * enabled, it might never be used if no peer supports it or the
 * requisite certificates (and private keys) are not available.
 * @param suites Names of all the cipher suites to enable.
 */
synchronized public void setEnabledCipherSuites(String[] suites){
enabledCipherSuites=new CipherSuiteList(suites);
if ((handshaker != null) && !handshaker.activated()) {
handshaker.setEnabledCipherSuites(enabledCipherSuites);
}
}
/** 
 * Returns the names of the SSL cipher suites which are currently enabled
 * for use on this connection.  When an SSL engine is first created,
 * all enabled cipher suites <em>(a)</em> protect data confidentiality,
 * by traffic encryption, and <em>(b)</em> can mutually authenticate
 * both clients and servers.  Thus, in some environments, this value
 * might be empty.
 * @return an array of cipher suite names
 */
synchronized public String[] getEnabledCipherSuites(){
return enabledCipherSuites.toStringArray();
}
/** 
 * Returns the protocols that are supported by this implementation.
 * A subset of the supported protocols may be enabled for this connection
 * @return an array of protocol names.
 */
public String[] getSupportedProtocols(){
return sslContext.getSuportedProtocolList().toStringArray();
}
/** 
 * Controls which protocols are enabled for use on
 * this connection.  The protocols must have been listed by
 * getSupportedProtocols() as being supported.
 * @param protocols protocols to enable.
 * @exception IllegalArgumentException when one of the protocols
 * named by the parameter is not supported.
 */
synchronized public void setEnabledProtocols(String[] protocols){
enabledProtocols=new ProtocolList(protocols);
if ((handshaker != null) && !handshaker.activated()) {
handshaker.setEnabledProtocols(enabledProtocols);
}
}
synchronized public String[] getEnabledProtocols(){
return enabledProtocols.toStringArray();
}
/** 
 * Returns the SSLParameters in effect for this SSLEngine.
 */
synchronized public SSLParameters getSSLParameters(){
SSLParameters params=super.getSSLParameters();
params.setEndpointIdentificationAlgorithm(identificationProtocol);
params.setAlgorithmConstraints(algorithmConstraints);
return params;
}
/** 
 * Applies SSLParameters to this engine.
 */
synchronized public void setSSLParameters(SSLParameters params){
super.setSSLParameters(params);
identificationProtocol=params.getEndpointIdentificationAlgorithm();
algorithmConstraints=params.getAlgorithmConstraints();
if ((handshaker != null) && !handshaker.started()) {
handshaker.setIdentificationProtocol(identificationProtocol);
handshaker.setAlgorithmConstraints(algorithmConstraints);
}
}
/** 
 * Return the name of the current thread. Utility method.
 */
private static String threadName(){
return Thread.currentThread().getName();
}
/** 
 * Returns a printable representation of this end of the connection.
 */
public String toString(){
StringBuilder retval=new StringBuilder(80);
retval.append(Integer.toHexString(hashCode()));
retval.append("[");
retval.append("SSLEngine[hostname=");
String host=getPeerHost();
retval.append((host == null) ? "null" : host);
retval.append(" port=");
retval.append(Integer.toString(getPeerPort()));
retval.append("] ");
retval.append(getSession().getCipherSuite());
retval.append("]");
return retval.toString();
}
}
