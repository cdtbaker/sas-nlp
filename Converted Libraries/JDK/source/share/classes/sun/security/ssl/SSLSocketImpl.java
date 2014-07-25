package sun.security.ssl;
import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.security.AlgorithmConstraints;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.BadPaddingException;
import javax.net.ssl.*;
import com.sun.net.ssl.internal.ssl.X509ExtendedTrustManager;
/** 
 * Implementation of an SSL socket.  This is a normal connection type
 * socket, implementing SSL over some lower level socket, such as TCP.
 * Because it is layered over some lower level socket, it MUST override
 * all default socket methods.
 * <P> This API offers a non-traditional option for establishing SSL
 * connections.  You may first establish the connection directly, then pass
 * that connection to the SSL socket constructor with a flag saying which
 * role should be taken in the handshake protocol.  (The two ends of the
 * connection must not choose the same role!)  This allows setup of SSL
 * proxying or tunneling, and also allows the kind of "role reversal"
 * that is required for most FTP data transfers.
 * @see javax.net.ssl.SSLSocket
 * @see SSLServerSocket
 * @author David Brownell
 */
final public class SSLSocketImpl extends BaseSSLSocketImpl {
  private static final int cs_START=0;
  private static final int cs_HANDSHAKE=1;
  private static final int cs_DATA=2;
  private static final int cs_RENEGOTIATE=3;
  private static final int cs_ERROR=4;
  private static final int cs_SENT_CLOSE=5;
  private static final int cs_CLOSED=6;
  private static final int cs_APP_CLOSED=7;
  private int connectionState;
  private boolean expectingFinished;
  private SSLException closeReason;
  private byte doClientAuth;
  private boolean roleIsServer;
  private boolean enableSessionCreation=true;
  private String host;
  private boolean autoClose=true;
  private AccessControlContext acc;
  private String rawHostname;
  private CipherSuiteList enabledCipherSuites;
  private String identificationProtocol=null;
  private AlgorithmConstraints algorithmConstraints=null;
  final private Object handshakeLock=new Object();
  final ReentrantLock writeLock=new ReentrantLock();
  final private Object readLock=new Object();
  private InputRecord inrec;
  private MAC readMAC, writeMAC;
  private CipherBox readCipher, writeCipher;
  private boolean secureRenegotiation;
  private byte[] clientVerifyData;
  private byte[] serverVerifyData;
  private SSLContextImpl sslContext;
  private Handshaker handshaker;
  private SSLSessionImpl sess;
  private volatile SSLSessionImpl handshakeSession;
  private HashMap<HandshakeCompletedListener,AccessControlContext> handshakeListeners;
  private InputStream sockInput;
  private OutputStream sockOutput;
  private AppInputStream input;
  private AppOutputStream output;
  private ProtocolList enabledProtocols;
  private ProtocolVersion protocolVersion=ProtocolVersion.DEFAULT;
  private static final Debug debug=Debug.getInstance("ssl");
  /** 
 * Constructs an SSL connection to a named host at a specified port,
 * using the authentication context provided.  This endpoint acts as
 * the client, and may rejoin an existing SSL session if appropriate.
 * @param context authentication context to use
 * @param host name of the host with which to connect
 * @param port number of the server's port
 */
  SSLSocketImpl(  SSLContextImpl context,  String host,  int port) throws IOException, UnknownHostException {
    super();
    this.host=host;
    this.rawHostname=host;
    init(context,false);
    SocketAddress socketAddress=host != null ? new InetSocketAddress(host,port) : new InetSocketAddress(InetAddress.getByName(null),port);
    connect(socketAddress,0);
  }
  /** 
 * Constructs an SSL connection to a server at a specified address.
 * and TCP port, using the authentication context provided.  This
 * endpoint acts as the client, and may rejoin an existing SSL session
 * if appropriate.
 * @param context authentication context to use
 * @param address the server's host
 * @param port its port
 */
  SSLSocketImpl(  SSLContextImpl context,  InetAddress host,  int port) throws IOException {
    super();
    init(context,false);
    SocketAddress socketAddress=new InetSocketAddress(host,port);
    connect(socketAddress,0);
  }
  /** 
 * Constructs an SSL connection to a named host at a specified port,
 * using the authentication context provided.  This endpoint acts as
 * the client, and may rejoin an existing SSL session if appropriate.
 * @param context authentication context to use
 * @param host name of the host with which to connect
 * @param port number of the server's port
 * @param localAddr the local address the socket is bound to
 * @param localPort the local port the socket is bound to
 */
  SSLSocketImpl(  SSLContextImpl context,  String host,  int port,  InetAddress localAddr,  int localPort) throws IOException, UnknownHostException {
    super();
    this.host=host;
    this.rawHostname=host;
    init(context,false);
    bind(new InetSocketAddress(localAddr,localPort));
    SocketAddress socketAddress=host != null ? new InetSocketAddress(host,port) : new InetSocketAddress(InetAddress.getByName(null),port);
    connect(socketAddress,0);
  }
  /** 
 * Constructs an SSL connection to a server at a specified address.
 * and TCP port, using the authentication context provided.  This
 * endpoint acts as the client, and may rejoin an existing SSL session
 * if appropriate.
 * @param context authentication context to use
 * @param address the server's host
 * @param port its port
 * @param localAddr the local address the socket is bound to
 * @param localPort the local port the socket is bound to
 */
  SSLSocketImpl(  SSLContextImpl context,  InetAddress host,  int port,  InetAddress localAddr,  int localPort) throws IOException {
    super();
    init(context,false);
    bind(new InetSocketAddress(localAddr,localPort));
    SocketAddress socketAddress=new InetSocketAddress(host,port);
    connect(socketAddress,0);
  }
  SSLSocketImpl(  SSLContextImpl context,  boolean serverMode,  CipherSuiteList suites,  byte clientAuth,  boolean sessionCreation,  ProtocolList protocols,  String identificationProtocol,  AlgorithmConstraints algorithmConstraints) throws IOException {
    super();
    doClientAuth=clientAuth;
    enableSessionCreation=sessionCreation;
    this.identificationProtocol=identificationProtocol;
    this.algorithmConstraints=algorithmConstraints;
    init(context,serverMode);
    enabledCipherSuites=suites;
    enabledProtocols=protocols;
  }
  /** 
 * Package-private constructor used to instantiate an unconnected
 * socket. The java.net package will connect it, either when the
 * connect() call is made by the application.  This instance is
 * meant to set handshake state to use "client mode".
 */
  SSLSocketImpl(  SSLContextImpl context){
    super();
    init(context,false);
  }
  /** 
 * Layer SSL traffic over an existing connection, rather than creating
 * a new connection.  The existing connection may be used only for SSL
 * traffic (using this SSLSocket) until the SSLSocket.close() call
 * returns. However, if a protocol error is detected, that existing
 * connection is automatically closed.
 * <P> This particular constructor always uses the socket in the
 * role of an SSL client. It may be useful in cases which start
 * using SSL after some initial data transfers, for example in some
 * SSL tunneling applications or as part of some kinds of application
 * protocols which negotiate use of a SSL based security.
 * @param sock the existing connection
 * @param context the authentication context to use
 */
  SSLSocketImpl(  SSLContextImpl context,  Socket sock,  String host,  int port,  boolean autoClose) throws IOException {
    super(sock);
    if (!sock.isConnected()) {
      throw new SocketException("Underlying socket is not connected");
    }
    this.host=host;
    this.rawHostname=host;
    init(context,false);
    this.autoClose=autoClose;
    doneConnect();
  }
  /** 
 * Initializes the client socket.
 */
  private void init(  SSLContextImpl context,  boolean isServer){
    sslContext=context;
    sess=SSLSessionImpl.nullSession;
    handshakeSession=null;
    roleIsServer=isServer;
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
    inrec=null;
    acc=AccessController.getContext();
    input=new AppInputStream(this);
    output=new AppOutputStream(this);
  }
  /** 
 * Connects this socket to the server with a specified timeout
 * value.
 * This method is either called on an unconnected SSLSocketImpl by the
 * application, or it is called in the constructor of a regular
 * SSLSocketImpl. If we are layering on top on another socket, then
 * this method should not be called, because we assume that the
 * underlying socket is already connected by the time it is passed to
 * us.
 * @param endpoint the <code>SocketAddress</code>
 * @param timeout  the timeout value to be used, 0 is no timeout
 * @throws IOException if an error occurs during the connection
 * @throws SocketTimeoutException if timeout expires before connecting
 */
  public void connect(  SocketAddress endpoint,  int timeout) throws IOException {
    if (self != this) {
      throw new SocketException("Already connected");
    }
    if (!(endpoint instanceof InetSocketAddress)) {
      throw new SocketException("Cannot handle non-Inet socket addresses.");
    }
    super.connect(endpoint,timeout);
    doneConnect();
  }
  /** 
 * Initialize the handshaker and socket streams.
 * Called by connect, the layered constructor, and SSLServerSocket.
 */
  void doneConnect() throws IOException {
    if (self == this) {
      sockInput=super.getInputStream();
      sockOutput=super.getOutputStream();
    }
 else {
      sockInput=self.getInputStream();
      sockOutput=self.getOutputStream();
    }
    initHandshaker();
  }
  synchronized private int getConnectionState(){
    return connectionState;
  }
  synchronized private void setConnectionState(  int state){
    connectionState=state;
  }
  AccessControlContext getAcc(){
    return acc;
  }
  void writeRecord(  OutputRecord r) throws IOException {
    loop:     while (r.contentType() == Record.ct_application_data) {
switch (getConnectionState()) {
case cs_HANDSHAKE:
        performInitialHandshake();
      break;
case cs_DATA:
case cs_RENEGOTIATE:
    break loop;
case cs_ERROR:
  fatal(Alerts.alert_close_notify,"error while writing to socket");
break;
case cs_SENT_CLOSE:
case cs_CLOSED:
case cs_APP_CLOSED:
if (closeReason != null) {
throw closeReason;
}
 else {
throw new SocketException("Socket closed");
}
default :
throw new SSLProtocolException("State error, send app data");
}
}
if (!r.isEmpty()) {
if (r.isAlert(Alerts.alert_close_notify) && getSoLinger() >= 0) {
boolean interrupted=Thread.interrupted();
try {
if (writeLock.tryLock(getSoLinger(),TimeUnit.SECONDS)) {
try {
writeRecordInternal(r);
}
  finally {
writeLock.unlock();
}
}
 else {
SSLException ssle=new SSLException("SO_LINGER timeout," + " close_notify message cannot be sent.");
if (self != this && !autoClose) {
fatal((byte)(-1),ssle);
}
 else if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", received Exception: " + ssle);
}
sess.invalidate();
}
}
 catch (InterruptedException ie) {
interrupted=true;
}
if (interrupted) {
Thread.currentThread().interrupt();
}
}
 else {
writeLock.lock();
try {
writeRecordInternal(r);
}
  finally {
writeLock.unlock();
}
}
}
}
private void writeRecordInternal(OutputRecord r) throws IOException {
r.addMAC(writeMAC);
r.encrypt(writeCipher);
r.write(sockOutput);
if (connectionState < cs_ERROR) {
checkSequenceNumber(writeMAC,r.contentType());
}
}
void readDataRecord(InputRecord r) throws IOException {
if (getConnectionState() == cs_HANDSHAKE) {
performInitialHandshake();
}
readRecord(r,true);
}
private void readRecord(InputRecord r,boolean needAppData) throws IOException {
int state;
synchronized (readLock) {
while (((state=getConnectionState()) != cs_CLOSED) && (state != cs_ERROR) && (state != cs_APP_CLOSED)) {
try {
r.setAppDataValid(false);
r.read(sockInput,sockOutput);
}
 catch (SSLProtocolException e) {
try {
fatal(Alerts.alert_unexpected_message,e);
}
 catch (IOException x) {
}
throw e;
}
catch (EOFException eof) {
boolean handshaking=(getConnectionState() <= cs_HANDSHAKE);
boolean rethrow=requireCloseNotify || handshaking;
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", received EOFException: " + (rethrow ? "error" : "ignored"));
}
if (rethrow) {
SSLException e;
if (handshaking) {
e=new SSLHandshakeException("Remote host closed connection during handshake");
}
 else {
e=new SSLProtocolException("Remote host closed connection incorrectly");
}
e.initCause(eof);
throw e;
}
 else {
closeInternal(false);
continue;
}
}
try {
r.decrypt(readCipher);
}
 catch (BadPaddingException e) {
r.checkMAC(readMAC);
byte alertType=(r.contentType() == Record.ct_handshake) ? Alerts.alert_handshake_failure : Alerts.alert_bad_record_mac;
fatal(alertType,"Invalid padding",e);
}
if (!r.checkMAC(readMAC)) {
if (r.contentType() == Record.ct_handshake) {
fatal(Alerts.alert_handshake_failure,"bad handshake record MAC");
}
 else {
fatal(Alerts.alert_bad_record_mac,"bad record MAC");
}
}
synchronized (this) {
switch (r.contentType()) {
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
handshaker.process_record(r,expectingFinished);
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
handshaker=null;
connectionState=cs_DATA;
if (handshakeListeners != null) {
HandshakeCompletedEvent event=new HandshakeCompletedEvent(this,sess);
Thread t=new NotifyHandshakeThread(handshakeListeners.entrySet(),event);
t.start();
}
}
if (needAppData || connectionState != cs_DATA) {
continue;
}
break;
case Record.ct_application_data:
if (connectionState != cs_DATA && connectionState != cs_RENEGOTIATE && connectionState != cs_SENT_CLOSE) {
throw new SSLProtocolException("Data received in non-data state: " + connectionState);
}
if (expectingFinished) {
throw new SSLProtocolException("Expecting finished message, received data");
}
if (!needAppData) {
throw new SSLException("Discarding app data");
}
r.setAppDataValid(true);
break;
case Record.ct_alert:
recvAlert(r);
continue;
case Record.ct_change_cipher_spec:
if ((connectionState != cs_HANDSHAKE && connectionState != cs_RENEGOTIATE) || r.available() != 1 || r.read() != 1) {
fatal(Alerts.alert_unexpected_message,"illegal change cipher spec msg, state = " + connectionState);
}
changeReadCiphers();
expectingFinished=true;
continue;
default :
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", Received record type: " + r.contentType());
}
continue;
}
if (connectionState < cs_ERROR) {
checkSequenceNumber(readMAC,r.contentType());
}
return;
}
}
r.close();
return;
}
}
/** 
 * Check the sequence number state
 * RFC 4346 states that, "Sequence numbers are of type uint64 and
 * may not exceed 2^64-1.  Sequence numbers do not wrap. If a TLS
 * implementation would need to wrap a sequence number, it must
 * renegotiate instead."
 */
private void checkSequenceNumber(MAC mac,byte type) throws IOException {
if (connectionState >= cs_ERROR || mac == MAC.NULL) {
return;
}
if (mac.seqNumOverflow()) {
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", sequence number extremely close to overflow " + "(2^64-1 packets). Closing connection.");
}
fatal(Alerts.alert_handshake_failure,"sequence number overflow");
}
if ((type != Record.ct_handshake) && mac.seqNumIsHuge()) {
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", request renegotiation " + "to avoid sequence number overflow");
}
startHandshake();
}
}
/** 
 * Return the AppInputStream. For use by Handshaker only.
 */
AppInputStream getAppInputStream(){
return input;
}
/** 
 * Return the AppOutputStream. For use by Handshaker only.
 */
AppOutputStream getAppOutputStream(){
return output;
}
/** 
 * Initialize the handshaker object. This means:
 * . if a handshake is already in progress (state is cs_HANDSHAKE
 * or cs_RENEGOTIATE), do nothing and return
 * . if the socket is already closed, throw an Exception (internal error)
 * . otherwise (cs_START or cs_DATA), create the appropriate handshaker
 * object, and advance the connection state (to cs_HANDSHAKE or
 * cs_RENEGOTIATE, respectively).
 * This method is called right after a new socket is created, when
 * starting renegotiation, or when changing client/ server mode of the
 * socket.
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
/** 
 * Synchronously perform the initial handshake.
 * If the handshake is already in progress, this method blocks until it
 * is completed. If the initial handshake has already been completed,
 * it returns immediately.
 */
private void performInitialHandshake() throws IOException {
synchronized (handshakeLock) {
if (getConnectionState() == cs_HANDSHAKE) {
kickstartHandshake();
if (inrec == null) {
inrec=new InputRecord();
inrec.setHandshakeHash(input.r.getHandshakeHash());
inrec.setHelloVersion(input.r.getHelloVersion());
inrec.enableFormatChecks();
}
readRecord(inrec,false);
inrec=null;
}
}
}
/** 
 * Starts an SSL handshake on this connection.
 */
public void startHandshake() throws IOException {
startHandshake(true);
}
/** 
 * Starts an ssl handshake on this connection.
 * @param resumable indicates the handshake process is resumable from a
 * certain exception. If <code>resumable</code>, the socket will
 * be reserved for exceptions like timeout; otherwise, the socket
 * will be closed, no further communications could be done.
 */
private void startHandshake(boolean resumable) throws IOException {
checkWrite();
try {
if (getConnectionState() == cs_HANDSHAKE) {
performInitialHandshake();
}
 else {
kickstartHandshake();
}
}
 catch (Exception e) {
handleException(e,resumable);
}
}
/** 
 * Kickstart the handshake if it is not already in progress.
 * This means:
 * . if handshaking is already underway, do nothing and return
 * . if the socket is not connected or already closed, throw an
 * Exception.
 * . otherwise, call initHandshake() to initialize the handshaker
 * object and progress the state. Then, send the initial
 * handshaking message if appropriate (always on clients and
 * on servers when renegotiating).
 */
private synchronized void kickstartHandshake() throws IOException {
switch (connectionState) {
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
case cs_START:
throw new SocketException("handshaking attempted on unconnected socket");
default :
throw new SocketException("connection is closed");
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
/** 
 * Return whether the socket has been explicitly closed by the application.
 */
public boolean isClosed(){
return getConnectionState() == cs_APP_CLOSED;
}
/** 
 * Return whether we have reached end-of-file.
 * If the socket is not connected, has been shutdown because of an error
 * or has been closed, throw an Exception.
 */
boolean checkEOF() throws IOException {
switch (getConnectionState()) {
case cs_START:
throw new SocketException("Socket is not connected");
case cs_HANDSHAKE:
case cs_DATA:
case cs_RENEGOTIATE:
case cs_SENT_CLOSE:
return false;
case cs_APP_CLOSED:
throw new SocketException("Socket is closed");
case cs_ERROR:
case cs_CLOSED:
default :
if (closeReason == null) {
return true;
}
IOException e=new SSLException("Connection has been shutdown: " + closeReason);
e.initCause(closeReason);
throw e;
}
}
/** 
 * Check if we can write data to this socket. If not, throw an IOException.
 */
void checkWrite() throws IOException {
if (checkEOF() || (getConnectionState() == cs_SENT_CLOSE)) {
throw new SocketException("Connection closed by remote host");
}
}
protected void closeSocket() throws IOException {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", called closeSocket()");
}
if (self == this) {
super.close();
}
 else {
self.close();
}
}
private void closeSocket(boolean selfInitiated) throws IOException {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", called closeSocket(selfInitiated)");
}
if (self == this) {
super.close();
}
 else if (autoClose) {
self.close();
}
 else if (selfInitiated) {
waitForClose(false);
}
}
/** 
 * Closes the SSL connection.  SSL includes an application level
 * shutdown handshake; you should close SSL sockets explicitly
 * rather than leaving it for finalization, so that your remote
 * peer does not experience a protocol error.
 */
public void close() throws IOException {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", called close()");
}
closeInternal(true);
setConnectionState(cs_APP_CLOSED);
}
/** 
 * Don't synchronize the whole method because waitForClose()
 * (which calls readRecord()) might be called.
 * @param selfInitiated Indicates which party initiated the close.
 * If selfInitiated, this side is initiating a close; for layered and
 * non-autoclose socket, wait for close_notify response.
 * If !selfInitiated, peer sent close_notify; we reciprocate but
 * no need to wait for response.
 */
private void closeInternal(boolean selfInitiated) throws IOException {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", called closeInternal(" + selfInitiated+ ")");
}
int state=getConnectionState();
boolean closeSocketCalled=false;
Throwable cachedThrowable=null;
try {
switch (state) {
case cs_START:
break;
case cs_ERROR:
closeSocket();
break;
case cs_CLOSED:
case cs_APP_CLOSED:
break;
default :
synchronized (this) {
if (((state=getConnectionState()) == cs_CLOSED) || (state == cs_ERROR) || (state == cs_APP_CLOSED)) {
return;
}
if (state != cs_SENT_CLOSE) {
try {
warning(Alerts.alert_close_notify);
connectionState=cs_SENT_CLOSE;
}
 catch (Throwable th) {
connectionState=cs_ERROR;
cachedThrowable=th;
closeSocketCalled=true;
closeSocket(selfInitiated);
}
}
}
if (state == cs_SENT_CLOSE) {
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", close invoked again; state = " + getConnectionState());
}
if (selfInitiated == false) {
return;
}
synchronized (this) {
while (connectionState < cs_CLOSED) {
try {
this.wait();
}
 catch (InterruptedException e) {
}
}
}
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", after primary close; state = " + getConnectionState());
}
return;
}
if (!closeSocketCalled) {
closeSocketCalled=true;
closeSocket(selfInitiated);
}
break;
}
}
  finally {
synchronized (this) {
connectionState=(connectionState == cs_APP_CLOSED) ? cs_APP_CLOSED : cs_CLOSED;
this.notifyAll();
}
if (closeSocketCalled) {
disposeCiphers();
}
if (cachedThrowable != null) {
if (cachedThrowable instanceof Error) throw (Error)cachedThrowable;
if (cachedThrowable instanceof RuntimeException) throw (RuntimeException)cachedThrowable;
}
}
}
/** 
 * Reads a close_notify or a fatal alert from the input stream.
 * Keep reading records until we get a close_notify or until
 * the connection is otherwise closed.  The close_notify or alert
 * might be read by another reader,
 * which will then process the close and set the connection state.
 */
void waitForClose(boolean rethrow) throws IOException {
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", waiting for close_notify or alert: state " + getConnectionState());
}
try {
int state;
while (((state=getConnectionState()) != cs_CLOSED) && (state != cs_ERROR) && (state != cs_APP_CLOSED)) {
if (inrec == null) {
inrec=new InputRecord();
}
try {
readRecord(inrec,true);
}
 catch (SocketTimeoutException e) {
}
}
inrec=null;
}
 catch (IOException e) {
if (debug != null && Debug.isOn("ssl")) {
System.out.println(threadName() + ", Exception while waiting for close " + e);
}
if (rethrow) {
throw e;
}
}
}
/** 
 * Called by closeInternal() only. Be sure to consider the
 * synchronization locks carefully before calling it elsewhere.
 */
private void disposeCiphers(){
synchronized (readLock) {
readCipher.dispose();
}
writeLock.lock();
try {
writeCipher.dispose();
}
  finally {
writeLock.unlock();
}
}
/** 
 * Handle an exception. This method is called by top level exception
 * handlers (in read(), write()) to make sure we always shutdown the
 * connection correctly and do not pass runtime exception to the
 * application.
 */
void handleException(Exception e) throws IOException {
handleException(e,true);
}
/** 
 * Handle an exception. This method is called by top level exception
 * handlers (in read(), write(), startHandshake()) to make sure we
 * always shutdown the connection correctly and do not pass runtime
 * exception to the application.
 * This method never returns normally, it always throws an IOException.
 * We first check if the socket has already been shutdown because of an
 * error. If so, we just rethrow the exception. If the socket has not
 * been shutdown, we sent a fatal alert and remember the exception.
 * @param e the Exception
 * @param resumable indicates the caller process is resumable from the
 * exception. If <code>resumable</code>, the socket will be
 * reserved for exceptions like timeout; otherwise, the socket
 * will be closed, no further communications could be done.
 */
synchronized private void handleException(Exception e,boolean resumable) throws IOException {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", handling exception: " + e.toString());
}
if (e instanceof InterruptedIOException && resumable) {
throw (IOException)e;
}
if (closeReason != null) {
if (e instanceof IOException) {
throw (IOException)e;
}
 else {
throw Alerts.getSSLException(Alerts.alert_internal_error,e,"Unexpected exception");
}
}
boolean isSSLException=(e instanceof SSLException);
if ((isSSLException == false) && (e instanceof IOException)) {
try {
fatal(Alerts.alert_unexpected_message,e);
}
 catch (IOException ee) {
}
throw (IOException)e;
}
byte alertType;
if (isSSLException) {
if (e instanceof SSLHandshakeException) {
alertType=Alerts.alert_handshake_failure;
}
 else {
alertType=Alerts.alert_unexpected_message;
}
}
 else {
alertType=Alerts.alert_internal_error;
}
fatal(alertType,e);
}
void warning(byte description){
sendAlert(Alerts.alert_warning,description);
}
synchronized void fatal(byte description,String diagnostic) throws IOException {
fatal(description,diagnostic,null);
}
synchronized void fatal(byte description,Throwable cause) throws IOException {
fatal(description,null,cause);
}
synchronized void fatal(byte description,String diagnostic,Throwable cause) throws IOException {
if ((input != null) && (input.r != null)) {
input.r.close();
}
sess.invalidate();
if (handshakeSession != null) {
handshakeSession.invalidate();
}
int oldState=connectionState;
if (connectionState < cs_ERROR) {
connectionState=cs_ERROR;
}
if (closeReason == null) {
if (oldState == cs_HANDSHAKE) {
sockInput.skip(sockInput.available());
}
if (description != -1) {
sendAlert(Alerts.alert_fatal,description);
}
if (cause instanceof SSLException) {
closeReason=(SSLException)cause;
}
 else {
closeReason=Alerts.getSSLException(description,cause,diagnostic);
}
}
closeSocket();
if (connectionState < cs_CLOSED) {
connectionState=(oldState == cs_APP_CLOSED) ? cs_APP_CLOSED : cs_CLOSED;
readCipher.dispose();
writeCipher.dispose();
}
throw closeReason;
}
private void recvAlert(InputRecord r) throws IOException {
byte level=(byte)r.read();
byte description=(byte)r.read();
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
closeInternal(false);
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
if (connectionState >= cs_SENT_CLOSE) {
return;
}
if (connectionState == cs_HANDSHAKE && (handshaker == null || !handshaker.started())) {
return;
}
OutputRecord r=new OutputRecord(Record.ct_alert);
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
output.r.setVersion(protocolVersion);
}
synchronized String getHost(){
if (host == null || host.length() == 0) {
host=getInetAddress().getHostName();
}
return host;
}
synchronized String getRawHostname(){
return rawHostname;
}
synchronized public void setHost(String host){
this.host=host;
this.rawHostname=host;
}
/** 
 * Gets an input stream to read from the peer on the other side.
 * Data read from this stream was always integrity protected in
 * transit, and will usually have been confidentiality protected.
 */
synchronized public InputStream getInputStream() throws IOException {
if (isClosed()) {
throw new SocketException("Socket is closed");
}
if (connectionState == cs_START) {
throw new SocketException("Socket is not connected");
}
return input;
}
/** 
 * Gets an output stream to write to the peer on the other side.
 * Data written on this stream is always integrity protected, and
 * will usually be confidentiality protected.
 */
synchronized public OutputStream getOutputStream() throws IOException {
if (isClosed()) {
throw new SocketException("Socket is closed");
}
if (connectionState == cs_START) {
throw new SocketException("Socket is not connected");
}
return output;
}
/** 
 * Returns the the SSL Session in use by this connection.  These can
 * be long lived, and frequently correspond to an entire login session
 * for some user.
 */
public SSLSession getSession(){
if (getConnectionState() == cs_HANDSHAKE) {
try {
startHandshake(false);
}
 catch (IOException e) {
if (debug != null && Debug.isOn("handshake")) {
System.out.println(threadName() + ", IOException in getSession():  " + e);
}
}
}
synchronized (this) {
return sess;
}
}
@Override synchronized public SSLSession getHandshakeSession(){
return handshakeSession;
}
synchronized void setHandshakeSession(SSLSessionImpl session){
handshakeSession=session;
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
 * Sets the flag controlling whether a server mode socket
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
 * Sets the flag controlling whether a server mode socket
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
 * Sets the flag controlling whether the socket is in SSL
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
 * for use on this connection.  When an SSL socket is first created,
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
 * Assigns the socket timeout.
 * @see java.net.Socket#setSoTimeout
 */
public void setSoTimeout(int timeout) throws SocketException {
if ((debug != null) && Debug.isOn("ssl")) {
System.out.println(threadName() + ", setSoTimeout(" + timeout+ ") called");
}
if (self == this) {
super.setSoTimeout(timeout);
}
 else {
self.setSoTimeout(timeout);
}
}
/** 
 * Registers an event listener to receive notifications that an
 * SSL handshake has completed on this connection.
 */
public synchronized void addHandshakeCompletedListener(HandshakeCompletedListener listener){
if (listener == null) {
throw new IllegalArgumentException("listener is null");
}
if (handshakeListeners == null) {
handshakeListeners=new HashMap<HandshakeCompletedListener,AccessControlContext>(4);
}
handshakeListeners.put(listener,AccessController.getContext());
}
/** 
 * Removes a previously registered handshake completion listener.
 */
public synchronized void removeHandshakeCompletedListener(HandshakeCompletedListener listener){
if (handshakeListeners == null) {
throw new IllegalArgumentException("no listeners");
}
if (handshakeListeners.remove(listener) == null) {
throw new IllegalArgumentException("listener not registered");
}
if (handshakeListeners.isEmpty()) {
handshakeListeners=null;
}
}
/** 
 * Returns the SSLParameters in effect for this SSLSocket.
 */
synchronized public SSLParameters getSSLParameters(){
SSLParameters params=super.getSSLParameters();
params.setEndpointIdentificationAlgorithm(identificationProtocol);
params.setAlgorithmConstraints(algorithmConstraints);
return params;
}
/** 
 * Applies SSLParameters to this socket.
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
private static class NotifyHandshakeThread extends Thread {
private Set<Map.Entry<HandshakeCompletedListener,AccessControlContext>> targets;
private HandshakeCompletedEvent event;
NotifyHandshakeThread(Set<Map.Entry<HandshakeCompletedListener,AccessControlContext>> entrySet,HandshakeCompletedEvent e){
super("HandshakeCompletedNotify-Thread");
targets=entrySet;
event=e;
}
public void run(){
for (Map.Entry<HandshakeCompletedListener,AccessControlContext> entry : targets) {
final HandshakeCompletedListener l=entry.getKey();
AccessControlContext acc=entry.getValue();
AccessController.doPrivileged(new PrivilegedAction<Void>(){
public Void run(){
l.handshakeCompleted(event);
return null;
}
}
,acc);
}
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
StringBuffer retval=new StringBuffer(80);
retval.append(Integer.toHexString(hashCode()));
retval.append("[");
retval.append(sess.getCipherSuite());
retval.append(": ");
if (self == this) {
retval.append(super.toString());
}
 else {
retval.append(self.toString());
}
retval.append("]");
return retval.toString();
}
}
