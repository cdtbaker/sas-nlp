package sun.security.ssl;
import java.io.*;
import java.util.*;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.net.ssl.*;
import sun.misc.HexDumpEncoder;
import sun.security.internal.spec.*;
import sun.security.internal.interfaces.TlsMasterSecret;
import sun.security.ssl.HandshakeMessage.*;
import sun.security.ssl.CipherSuite.*;
import static sun.security.ssl.CipherSuite.PRF.*;
/** 
 * Handshaker ... processes handshake records from an SSL V3.0
 * data stream, handling all the details of the handshake protocol.
 * Note that the real protocol work is done in two subclasses, the  base
 * class just provides the control flow and key generation framework.
 * @author David Brownell
 */
abstract class Handshaker {
  ProtocolVersion protocolVersion;
  ProtocolVersion activeProtocolVersion;
  boolean secureRenegotiation;
  byte[] clientVerifyData;
  byte[] serverVerifyData;
  boolean isInitialHandshake;
  private ProtocolList enabledProtocols;
  private CipherSuiteList enabledCipherSuites;
  String identificationProtocol;
  private AlgorithmConstraints algorithmConstraints=null;
  Collection<SignatureAndHashAlgorithm> localSupportedSignAlgs;
  Collection<SignatureAndHashAlgorithm> peerSupportedSignAlgs;
  private ProtocolList activeProtocols;
  private CipherSuiteList activeCipherSuites;
  private boolean isClient;
  private boolean needCertVerify;
  SSLSocketImpl conn=null;
  SSLEngineImpl engine=null;
  HandshakeHash handshakeHash;
  HandshakeInStream input;
  HandshakeOutStream output;
  int state;
  SSLContextImpl sslContext;
  RandomCookie clnt_random, svr_random;
  SSLSessionImpl session;
  CipherSuite cipherSuite;
  KeyExchange keyExchange;
  boolean resumingSession;
  boolean enableNewSession;
  private SecretKey clntWriteKey, svrWriteKey;
  private IvParameterSpec clntWriteIV, svrWriteIV;
  private SecretKey clntMacSecret, svrMacSecret;
  private volatile boolean taskDelegated=false;
  private volatile DelegatedTask delegatedTask=null;
  private volatile Exception thrown=null;
  private Object thrownLock=new Object();
  static final Debug debug=Debug.getInstance("ssl");
  static final boolean allowUnsafeRenegotiation=Debug.getBooleanProperty("sun.security.ssl.allowUnsafeRenegotiation",false);
  static final boolean allowLegacyHelloMessages=Debug.getBooleanProperty("sun.security.ssl.allowLegacyHelloMessages",true);
  boolean invalidated;
  Handshaker(  SSLSocketImpl c,  SSLContextImpl context,  ProtocolList enabledProtocols,  boolean needCertVerify,  boolean isClient,  ProtocolVersion activeProtocolVersion,  boolean isInitialHandshake,  boolean secureRenegotiation,  byte[] clientVerifyData,  byte[] serverVerifyData){
    this.conn=c;
    init(context,enabledProtocols,needCertVerify,isClient,activeProtocolVersion,isInitialHandshake,secureRenegotiation,clientVerifyData,serverVerifyData);
  }
  Handshaker(  SSLEngineImpl engine,  SSLContextImpl context,  ProtocolList enabledProtocols,  boolean needCertVerify,  boolean isClient,  ProtocolVersion activeProtocolVersion,  boolean isInitialHandshake,  boolean secureRenegotiation,  byte[] clientVerifyData,  byte[] serverVerifyData){
    this.engine=engine;
    init(context,enabledProtocols,needCertVerify,isClient,activeProtocolVersion,isInitialHandshake,secureRenegotiation,clientVerifyData,serverVerifyData);
  }
  private void init(  SSLContextImpl context,  ProtocolList enabledProtocols,  boolean needCertVerify,  boolean isClient,  ProtocolVersion activeProtocolVersion,  boolean isInitialHandshake,  boolean secureRenegotiation,  byte[] clientVerifyData,  byte[] serverVerifyData){
    if (debug != null && Debug.isOn("handshake")) {
      System.out.println("Allow unsafe renegotiation: " + allowUnsafeRenegotiation + "\nAllow legacy hello messages: "+ allowLegacyHelloMessages+ "\nIs initial handshake: "+ isInitialHandshake+ "\nIs secure renegotiation: "+ secureRenegotiation);
    }
    this.sslContext=context;
    this.isClient=isClient;
    this.needCertVerify=needCertVerify;
    this.activeProtocolVersion=activeProtocolVersion;
    this.isInitialHandshake=isInitialHandshake;
    this.secureRenegotiation=secureRenegotiation;
    this.clientVerifyData=clientVerifyData;
    this.serverVerifyData=serverVerifyData;
    enableNewSession=true;
    invalidated=false;
    setCipherSuite(CipherSuite.C_NULL);
    setEnabledProtocols(enabledProtocols);
    if (conn != null) {
      algorithmConstraints=new SSLAlgorithmConstraints(conn,true);
    }
 else {
      algorithmConstraints=new SSLAlgorithmConstraints(engine,true);
    }
    state=-2;
  }
  void fatalSE(  byte b,  String diagnostic) throws IOException {
    fatalSE(b,diagnostic,null);
  }
  void fatalSE(  byte b,  Throwable cause) throws IOException {
    fatalSE(b,null,cause);
  }
  void fatalSE(  byte b,  String diagnostic,  Throwable cause) throws IOException {
    if (conn != null) {
      conn.fatal(b,diagnostic,cause);
    }
 else {
      engine.fatal(b,diagnostic,cause);
    }
  }
  void warningSE(  byte b){
    if (conn != null) {
      conn.warning(b);
    }
 else {
      engine.warning(b);
    }
  }
  String getRawHostnameSE(){
    if (conn != null) {
      return conn.getRawHostname();
    }
 else {
      return engine.getPeerHost();
    }
  }
  String getHostSE(){
    if (conn != null) {
      return conn.getHost();
    }
 else {
      return engine.getPeerHost();
    }
  }
  String getHostAddressSE(){
    if (conn != null) {
      return conn.getInetAddress().getHostAddress();
    }
 else {
      return engine.getPeerHost();
    }
  }
  boolean isLoopbackSE(){
    if (conn != null) {
      return conn.getInetAddress().isLoopbackAddress();
    }
 else {
      return false;
    }
  }
  int getPortSE(){
    if (conn != null) {
      return conn.getPort();
    }
 else {
      return engine.getPeerPort();
    }
  }
  int getLocalPortSE(){
    if (conn != null) {
      return conn.getLocalPort();
    }
 else {
      return -1;
    }
  }
  AccessControlContext getAccSE(){
    if (conn != null) {
      return conn.getAcc();
    }
 else {
      return engine.getAcc();
    }
  }
  private void setVersionSE(  ProtocolVersion protocolVersion){
    if (conn != null) {
      conn.setVersion(protocolVersion);
    }
 else {
      engine.setVersion(protocolVersion);
    }
  }
  /** 
 * Set the active protocol version and propagate it to the SSLSocket
 * and our handshake streams. Called from ClientHandshaker
 * and ServerHandshaker with the negotiated protocol version.
 */
  void setVersion(  ProtocolVersion protocolVersion){
    this.protocolVersion=protocolVersion;
    setVersionSE(protocolVersion);
    output.r.setVersion(protocolVersion);
  }
  /** 
 * Set the enabled protocols. Called from the constructor or
 * SSLSocketImpl/SSLEngineImpl.setEnabledProtocols() (if the
 * handshake is not yet in progress).
 */
  void setEnabledProtocols(  ProtocolList enabledProtocols){
    activeCipherSuites=null;
    activeProtocols=null;
    this.enabledProtocols=enabledProtocols;
  }
  /** 
 * Set the enabled cipher suites. Called from
 * SSLSocketImpl/SSLEngineImpl.setEnabledCipherSuites() (if the
 * handshake is not yet in progress).
 */
  void setEnabledCipherSuites(  CipherSuiteList enabledCipherSuites){
    activeCipherSuites=null;
    activeProtocols=null;
    this.enabledCipherSuites=enabledCipherSuites;
  }
  /** 
 * Set the algorithm constraints. Called from the constructor or
 * SSLSocketImpl/SSLEngineImpl.setAlgorithmConstraints() (if the
 * handshake is not yet in progress).
 */
  void setAlgorithmConstraints(  AlgorithmConstraints algorithmConstraints){
    activeCipherSuites=null;
    activeProtocols=null;
    this.algorithmConstraints=new SSLAlgorithmConstraints(algorithmConstraints);
    this.localSupportedSignAlgs=null;
  }
  Collection<SignatureAndHashAlgorithm> getLocalSupportedSignAlgs(){
    if (localSupportedSignAlgs == null) {
      localSupportedSignAlgs=SignatureAndHashAlgorithm.getSupportedAlgorithms(algorithmConstraints);
    }
    return localSupportedSignAlgs;
  }
  void setPeerSupportedSignAlgs(  Collection<SignatureAndHashAlgorithm> algorithms){
    peerSupportedSignAlgs=new ArrayList<SignatureAndHashAlgorithm>(algorithms);
  }
  Collection<SignatureAndHashAlgorithm> getPeerSupportedSignAlgs(){
    return peerSupportedSignAlgs;
  }
  /** 
 * Set the identification protocol. Called from the constructor or
 * SSLSocketImpl/SSLEngineImpl.setIdentificationProtocol() (if the
 * handshake is not yet in progress).
 */
  void setIdentificationProtocol(  String protocol){
    this.identificationProtocol=protocol;
  }
  /** 
 * Prior to handshaking, activate the handshake and initialize the version,
 * input stream and output stream.
 */
  void activate(  ProtocolVersion helloVersion) throws IOException {
    if (activeProtocols == null) {
      activeProtocols=getActiveProtocols();
    }
    if (activeProtocols.collection().isEmpty() || activeProtocols.max.v == ProtocolVersion.NONE.v) {
      throw new SSLHandshakeException("No appropriate protocol");
    }
    if (activeCipherSuites == null) {
      activeCipherSuites=getActiveCipherSuites();
    }
    if (activeCipherSuites.collection().isEmpty()) {
      throw new SSLHandshakeException("No appropriate cipher suite");
    }
    if (!isInitialHandshake) {
      protocolVersion=activeProtocolVersion;
    }
 else {
      protocolVersion=activeProtocols.max;
    }
    if (helloVersion == null || helloVersion.v == ProtocolVersion.NONE.v) {
      helloVersion=activeProtocols.helloVersion;
    }
    Set<String> localSupportedHashAlgorithms=SignatureAndHashAlgorithm.getHashAlgorithmNames(getLocalSupportedSignAlgs());
    handshakeHash=new HandshakeHash(!isClient,needCertVerify,localSupportedHashAlgorithms);
    input=new HandshakeInStream(handshakeHash);
    if (conn != null) {
      output=new HandshakeOutStream(protocolVersion,helloVersion,handshakeHash,conn);
      conn.getAppInputStream().r.setHandshakeHash(handshakeHash);
      conn.getAppInputStream().r.setHelloVersion(helloVersion);
      conn.getAppOutputStream().r.setHelloVersion(helloVersion);
    }
 else {
      output=new HandshakeOutStream(protocolVersion,helloVersion,handshakeHash,engine);
      engine.inputRecord.setHandshakeHash(handshakeHash);
      engine.inputRecord.setHelloVersion(helloVersion);
      engine.outputRecord.setHelloVersion(helloVersion);
    }
    state=-1;
  }
  /** 
 * Set cipherSuite and keyExchange to the given CipherSuite.
 * Does not perform any verification that this is a valid selection,
 * this must be done before calling this method.
 */
  void setCipherSuite(  CipherSuite s){
    this.cipherSuite=s;
    this.keyExchange=s.keyExchange;
  }
  /** 
 * Check if the given ciphersuite is enabled and available.
 * Does not check if the required server certificates are available.
 */
  boolean isNegotiable(  CipherSuite s){
    if (activeCipherSuites == null) {
      activeCipherSuites=getActiveCipherSuites();
    }
    return activeCipherSuites.contains(s) && s.isNegotiable();
  }
  /** 
 * Check if the given protocol version is enabled and available.
 */
  boolean isNegotiable(  ProtocolVersion protocolVersion){
    if (activeProtocols == null) {
      activeProtocols=getActiveProtocols();
    }
    return activeProtocols.contains(protocolVersion);
  }
  /** 
 * Select a protocol version from the list. Called from
 * ServerHandshaker to negotiate protocol version.
 * Return the lower of the protocol version suggested in the
 * clien hello and the highest supported by the server.
 */
  ProtocolVersion selectProtocolVersion(  ProtocolVersion protocolVersion){
    if (activeProtocols == null) {
      activeProtocols=getActiveProtocols();
    }
    return activeProtocols.selectProtocolVersion(protocolVersion);
  }
  /** 
 * Get the active cipher suites.
 * In TLS 1.1, many weak or vulnerable cipher suites were obsoleted,
 * such as TLS_RSA_EXPORT_WITH_RC4_40_MD5. The implementation MUST NOT
 * negotiate these cipher suites in TLS 1.1 or later mode.
 * Therefore, when the active protocols only include TLS 1.1 or later,
 * the client cannot request to negotiate those obsoleted cipher
 * suites.  That is, the obsoleted suites should not be included in the
 * client hello. So we need to create a subset of the enabled cipher
 * suites, the active cipher suites, which does not contain obsoleted
 * cipher suites of the minimum active protocol.
 * Return empty list instead of null if no active cipher suites.
 */
  CipherSuiteList getActiveCipherSuites(){
    if (activeCipherSuites == null) {
      if (activeProtocols == null) {
        activeProtocols=getActiveProtocols();
      }
      ArrayList<CipherSuite> suites=new ArrayList<>();
      if (!(activeProtocols.collection().isEmpty()) && activeProtocols.min.v != ProtocolVersion.NONE.v) {
        for (        CipherSuite suite : enabledCipherSuites.collection()) {
          if (suite.obsoleted > activeProtocols.min.v && suite.supported <= activeProtocols.max.v) {
            if (algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT),suite.name,null)) {
              suites.add(suite);
            }
          }
 else           if (debug != null && Debug.isOn("verbose")) {
            if (suite.obsoleted <= activeProtocols.min.v) {
              System.out.println("Ignoring obsoleted cipher suite: " + suite);
            }
 else {
              System.out.println("Ignoring unsupported cipher suite: " + suite);
            }
          }
        }
      }
      activeCipherSuites=new CipherSuiteList(suites);
    }
    return activeCipherSuites;
  }
  ProtocolList getActiveProtocols(){
    if (activeProtocols == null) {
      ArrayList<ProtocolVersion> protocols=new ArrayList<>(4);
      for (      ProtocolVersion protocol : enabledProtocols.collection()) {
        boolean found=false;
        for (        CipherSuite suite : enabledCipherSuites.collection()) {
          if (suite.isAvailable() && suite.obsoleted > protocol.v && suite.supported <= protocol.v) {
            if (algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT),suite.name,null)) {
              protocols.add(protocol);
              found=true;
              break;
            }
 else             if (debug != null && Debug.isOn("verbose")) {
              System.out.println("Ignoring disabled cipher suite: " + suite + " for "+ protocol);
            }
          }
 else           if (debug != null && Debug.isOn("verbose")) {
            System.out.println("Ignoring unsupported cipher suite: " + suite + " for "+ protocol);
          }
        }
        if (!found && (debug != null) && Debug.isOn("handshake")) {
          System.out.println("No available cipher suite for " + protocol);
        }
      }
      activeProtocols=new ProtocolList(protocols);
    }
    return activeProtocols;
  }
  /** 
 * As long as handshaking has not activated, we can
 * change whether session creations are allowed.
 * Callers should do their own checking if handshaking
 * has activated.
 */
  void setEnableSessionCreation(  boolean newSessions){
    enableNewSession=newSessions;
  }
  /** 
 * Create a new read cipher and return it to caller.
 */
  CipherBox newReadCipher() throws NoSuchAlgorithmException {
    BulkCipher cipher=cipherSuite.cipher;
    CipherBox box;
    if (isClient) {
      box=cipher.newCipher(protocolVersion,svrWriteKey,svrWriteIV,sslContext.getSecureRandom(),false);
      svrWriteKey=null;
      svrWriteIV=null;
    }
 else {
      box=cipher.newCipher(protocolVersion,clntWriteKey,clntWriteIV,sslContext.getSecureRandom(),false);
      clntWriteKey=null;
      clntWriteIV=null;
    }
    return box;
  }
  /** 
 * Create a new write cipher and return it to caller.
 */
  CipherBox newWriteCipher() throws NoSuchAlgorithmException {
    BulkCipher cipher=cipherSuite.cipher;
    CipherBox box;
    if (isClient) {
      box=cipher.newCipher(protocolVersion,clntWriteKey,clntWriteIV,sslContext.getSecureRandom(),true);
      clntWriteKey=null;
      clntWriteIV=null;
    }
 else {
      box=cipher.newCipher(protocolVersion,svrWriteKey,svrWriteIV,sslContext.getSecureRandom(),true);
      svrWriteKey=null;
      svrWriteIV=null;
    }
    return box;
  }
  /** 
 * Create a new read MAC and return it to caller.
 */
  MAC newReadMAC() throws NoSuchAlgorithmException, InvalidKeyException {
    MacAlg macAlg=cipherSuite.macAlg;
    MAC mac;
    if (isClient) {
      mac=macAlg.newMac(protocolVersion,svrMacSecret);
      svrMacSecret=null;
    }
 else {
      mac=macAlg.newMac(protocolVersion,clntMacSecret);
      clntMacSecret=null;
    }
    return mac;
  }
  /** 
 * Create a new write MAC and return it to caller.
 */
  MAC newWriteMAC() throws NoSuchAlgorithmException, InvalidKeyException {
    MacAlg macAlg=cipherSuite.macAlg;
    MAC mac;
    if (isClient) {
      mac=macAlg.newMac(protocolVersion,clntMacSecret);
      clntMacSecret=null;
    }
 else {
      mac=macAlg.newMac(protocolVersion,svrMacSecret);
      svrMacSecret=null;
    }
    return mac;
  }
  boolean isDone(){
    return state == HandshakeMessage.ht_finished;
  }
  SSLSessionImpl getSession(){
    return session;
  }
  void setHandshakeSessionSE(  SSLSessionImpl handshakeSession){
    if (conn != null) {
      conn.setHandshakeSession(handshakeSession);
    }
 else {
      engine.setHandshakeSession(handshakeSession);
    }
  }
  boolean isSecureRenegotiation(){
    return secureRenegotiation;
  }
  byte[] getClientVerifyData(){
    return clientVerifyData;
  }
  byte[] getServerVerifyData(){
    return serverVerifyData;
  }
  void process_record(  InputRecord r,  boolean expectingFinished) throws IOException {
    checkThrown();
    input.incomingRecord(r);
    if ((conn != null) || expectingFinished) {
      processLoop();
    }
 else {
      delegateTask(new PrivilegedExceptionAction<Void>(){
        public Void run() throws Exception {
          processLoop();
          return null;
        }
      }
);
    }
  }
  void processLoop() throws IOException {
    while (input.available() >= 4) {
      byte messageType;
      int messageLen;
      input.mark(4);
      messageType=(byte)input.getInt8();
      messageLen=input.getInt24();
      if (input.available() < messageLen) {
        input.reset();
        return;
      }
      if (messageType == HandshakeMessage.ht_hello_request) {
        input.reset();
        processMessage(messageType,messageLen);
        input.ignore(4 + messageLen);
      }
 else {
        input.mark(messageLen);
        processMessage(messageType,messageLen);
        input.digestNow();
      }
    }
  }
  /** 
 * Returns true iff the handshaker has been activated.
 * In activated state, the handshaker may not send any messages out.
 */
  boolean activated(){
    return state >= -1;
  }
  /** 
 * Returns true iff the handshaker has sent any messages.
 */
  boolean started(){
    return state >= 0;
  }
  void kickstart() throws IOException {
    if (state >= 0) {
      return;
    }
    HandshakeMessage m=getKickstartMessage();
    if (debug != null && Debug.isOn("handshake")) {
      m.print(System.out);
    }
    m.write(output);
    output.flush();
    state=m.messageType();
  }
  /** 
 * Both client and server modes can start handshaking; but the
 * message they send to do so is different.
 */
  abstract HandshakeMessage getKickstartMessage() throws SSLException ;
  abstract void processMessage(  byte messageType,  int messageLen) throws IOException ;
  abstract void handshakeAlert(  byte description) throws SSLProtocolException ;
  void sendChangeCipherSpec(  Finished mesg,  boolean lastMessage) throws IOException {
    output.flush();
    OutputRecord r;
    if (conn != null) {
      r=new OutputRecord(Record.ct_change_cipher_spec);
    }
 else {
      r=new EngineOutputRecord(Record.ct_change_cipher_spec,engine);
    }
    r.setVersion(protocolVersion);
    r.write(1);
    if (conn != null) {
      conn.writeLock.lock();
      try {
        conn.writeRecord(r);
        conn.changeWriteCiphers();
        if (debug != null && Debug.isOn("handshake")) {
          mesg.print(System.out);
        }
        mesg.write(output);
        output.flush();
      }
  finally {
        conn.writeLock.unlock();
      }
    }
 else {
synchronized (engine.writeLock) {
        engine.writeRecord((EngineOutputRecord)r);
        engine.changeWriteCiphers();
        if (debug != null && Debug.isOn("handshake")) {
          mesg.print(System.out);
        }
        mesg.write(output);
        if (lastMessage) {
          output.setFinishedMsg();
        }
        output.flush();
      }
    }
  }
  void calculateKeys(  SecretKey preMasterSecret,  ProtocolVersion version){
    SecretKey master=calculateMasterSecret(preMasterSecret,version);
    session.setMasterSecret(master);
    calculateConnectionKeys(master);
  }
  private SecretKey calculateMasterSecret(  SecretKey preMasterSecret,  ProtocolVersion requestedVersion){
    if (debug != null && Debug.isOn("keygen")) {
      HexDumpEncoder dump=new HexDumpEncoder();
      System.out.println("SESSION KEYGEN:");
      System.out.println("PreMaster Secret:");
      printHex(dump,preMasterSecret.getEncoded());
    }
    String masterAlg;
    PRF prf;
    if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
      masterAlg="SunTls12MasterSecret";
      prf=cipherSuite.prfAlg;
    }
 else {
      masterAlg="SunTlsMasterSecret";
      prf=P_NONE;
    }
    String prfHashAlg=prf.getPRFHashAlg();
    int prfHashLength=prf.getPRFHashLength();
    int prfBlockSize=prf.getPRFBlockSize();
    TlsMasterSecretParameterSpec spec=new TlsMasterSecretParameterSpec(preMasterSecret,protocolVersion.major,protocolVersion.minor,clnt_random.random_bytes,svr_random.random_bytes,prfHashAlg,prfHashLength,prfBlockSize);
    SecretKey masterSecret;
    try {
      KeyGenerator kg=JsseJce.getKeyGenerator(masterAlg);
      kg.init(spec);
      masterSecret=kg.generateKey();
    }
 catch (    GeneralSecurityException e) {
      if (!preMasterSecret.getAlgorithm().equals("TlsRsaPremasterSecret")) {
        throw new ProviderException(e);
      }
      if (debug != null && Debug.isOn("handshake")) {
        System.out.println("RSA master secret generation error:");
        e.printStackTrace(System.out);
        System.out.println("Generating new random premaster secret");
      }
      if (requestedVersion != null) {
        preMasterSecret=RSAClientKeyExchange.generateDummySecret(requestedVersion);
      }
 else {
        preMasterSecret=RSAClientKeyExchange.generateDummySecret(protocolVersion);
      }
      return calculateMasterSecret(preMasterSecret,null);
    }
    if ((requestedVersion == null) || !(masterSecret instanceof TlsMasterSecret)) {
      return masterSecret;
    }
    TlsMasterSecret tlsKey=(TlsMasterSecret)masterSecret;
    int major=tlsKey.getMajorVersion();
    int minor=tlsKey.getMinorVersion();
    if ((major < 0) || (minor < 0)) {
      return masterSecret;
    }
    ProtocolVersion premasterVersion=ProtocolVersion.valueOf(major,minor);
    boolean versionMismatch=(premasterVersion.v != requestedVersion.v);
    if (versionMismatch && requestedVersion.v <= ProtocolVersion.TLS10.v) {
      versionMismatch=(premasterVersion.v != protocolVersion.v);
    }
    if (versionMismatch == false) {
      return masterSecret;
    }
    if (debug != null && Debug.isOn("handshake")) {
      System.out.println("RSA PreMasterSecret version error: expected" + protocolVersion + " or "+ requestedVersion+ ", decrypted: "+ premasterVersion);
      System.out.println("Generating new random premaster secret");
    }
    preMasterSecret=RSAClientKeyExchange.generateDummySecret(requestedVersion);
    return calculateMasterSecret(preMasterSecret,null);
  }
  void calculateConnectionKeys(  SecretKey masterKey){
    int hashSize=cipherSuite.macAlg.size;
    boolean is_exportable=cipherSuite.exportable;
    BulkCipher cipher=cipherSuite.cipher;
    int expandedKeySize=is_exportable ? cipher.expandedKeySize : 0;
    String keyMaterialAlg;
    PRF prf;
    if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
      keyMaterialAlg="SunTls12KeyMaterial";
      prf=cipherSuite.prfAlg;
    }
 else {
      keyMaterialAlg="SunTlsKeyMaterial";
      prf=P_NONE;
    }
    String prfHashAlg=prf.getPRFHashAlg();
    int prfHashLength=prf.getPRFHashLength();
    int prfBlockSize=prf.getPRFBlockSize();
    TlsKeyMaterialParameterSpec spec=new TlsKeyMaterialParameterSpec(masterKey,protocolVersion.major,protocolVersion.minor,clnt_random.random_bytes,svr_random.random_bytes,cipher.algorithm,cipher.keySize,expandedKeySize,cipher.ivSize,hashSize,prfHashAlg,prfHashLength,prfBlockSize);
    try {
      KeyGenerator kg=JsseJce.getKeyGenerator(keyMaterialAlg);
      kg.init(spec);
      TlsKeyMaterialSpec keySpec=(TlsKeyMaterialSpec)kg.generateKey();
      clntWriteKey=keySpec.getClientCipherKey();
      svrWriteKey=keySpec.getServerCipherKey();
      clntWriteIV=keySpec.getClientIv();
      svrWriteIV=keySpec.getServerIv();
      clntMacSecret=keySpec.getClientMacKey();
      svrMacSecret=keySpec.getServerMacKey();
    }
 catch (    GeneralSecurityException e) {
      throw new ProviderException(e);
    }
    if (debug != null && Debug.isOn("keygen")) {
synchronized (System.out) {
        HexDumpEncoder dump=new HexDumpEncoder();
        System.out.println("CONNECTION KEYGEN:");
        System.out.println("Client Nonce:");
        printHex(dump,clnt_random.random_bytes);
        System.out.println("Server Nonce:");
        printHex(dump,svr_random.random_bytes);
        System.out.println("Master Secret:");
        printHex(dump,masterKey.getEncoded());
        System.out.println("Client MAC write Secret:");
        printHex(dump,clntMacSecret.getEncoded());
        System.out.println("Server MAC write Secret:");
        printHex(dump,svrMacSecret.getEncoded());
        if (clntWriteKey != null) {
          System.out.println("Client write key:");
          printHex(dump,clntWriteKey.getEncoded());
          System.out.println("Server write key:");
          printHex(dump,svrWriteKey.getEncoded());
        }
 else {
          System.out.println("... no encryption keys used");
        }
        if (clntWriteIV != null) {
          System.out.println("Client write IV:");
          printHex(dump,clntWriteIV.getIV());
          System.out.println("Server write IV:");
          printHex(dump,svrWriteIV.getIV());
        }
 else {
          if (protocolVersion.v >= ProtocolVersion.TLS11.v) {
            System.out.println("... no IV derived for this protocol");
          }
 else {
            System.out.println("... no IV used for this cipher");
          }
        }
        System.out.flush();
      }
    }
  }
  private static void printHex(  HexDumpEncoder dump,  byte[] bytes){
    if (bytes == null) {
      System.out.println("(key bytes not available)");
    }
 else {
      try {
        dump.encodeBuffer(bytes,System.out);
      }
 catch (      IOException e) {
      }
    }
  }
  /** 
 * Throw an SSLException with the specified message and cause.
 * Shorthand until a new SSLException constructor is added.
 * This method never returns.
 */
  static void throwSSLException(  String msg,  Throwable cause) throws SSLException {
    SSLException e=new SSLException(msg);
    e.initCause(cause);
    throw e;
  }
class DelegatedTask<E> implements Runnable {
    private PrivilegedExceptionAction<E> pea;
    DelegatedTask(    PrivilegedExceptionAction<E> pea){
      this.pea=pea;
    }
    public void run(){
synchronized (engine) {
        try {
          AccessController.doPrivileged(pea,engine.getAcc());
        }
 catch (        PrivilegedActionException pae) {
          thrown=pae.getException();
        }
catch (        RuntimeException rte) {
          thrown=rte;
        }
        delegatedTask=null;
        taskDelegated=false;
      }
    }
  }
  private <T>void delegateTask(  PrivilegedExceptionAction<T> pea){
    delegatedTask=new DelegatedTask<T>(pea);
    taskDelegated=false;
    thrown=null;
  }
  DelegatedTask getTask(){
    if (!taskDelegated) {
      taskDelegated=true;
      return delegatedTask;
    }
 else {
      return null;
    }
  }
  boolean taskOutstanding(){
    return (delegatedTask != null);
  }
  void checkThrown() throws SSLException {
synchronized (thrownLock) {
      if (thrown != null) {
        String msg=thrown.getMessage();
        if (msg == null) {
          msg="Delegated task threw Exception/Error";
        }
        Exception e=thrown;
        thrown=null;
        if (e instanceof RuntimeException) {
          throw (RuntimeException)new RuntimeException(msg).initCause(e);
        }
 else         if (e instanceof SSLHandshakeException) {
          throw (SSLHandshakeException)new SSLHandshakeException(msg).initCause(e);
        }
 else         if (e instanceof SSLKeyException) {
          throw (SSLKeyException)new SSLKeyException(msg).initCause(e);
        }
 else         if (e instanceof SSLPeerUnverifiedException) {
          throw (SSLPeerUnverifiedException)new SSLPeerUnverifiedException(msg).initCause(e);
        }
 else         if (e instanceof SSLProtocolException) {
          throw (SSLProtocolException)new SSLProtocolException(msg).initCause(e);
        }
 else {
          throw (SSLException)new SSLException(msg).initCause(e);
        }
      }
    }
  }
}
