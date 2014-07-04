package sun.net.www.protocol.https;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.CookieHandler;
import java.security.Principal;
import java.security.cert.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.security.AccessController;
import javax.security.auth.x500.X500Principal;
import javax.net.ssl.*;
import sun.net.www.http.HttpClient;
import sun.security.action.*;
import sun.security.util.HostnameChecker;
import sun.security.ssl.SSLSocketImpl;
/** 
 * This class provides HTTPS client URL support, building on the standard
 * "sun.net.www" HTTP protocol handler.  HTTPS is the same protocol as HTTP,
 * but differs in the transport layer which it uses:  <UL>
 * <LI>There's a <em>Secure Sockets Layer</em> between TCP
 * and the HTTP protocol code.
 * <LI>It uses a different default TCP port.
 * <LI>It doesn't use application level proxies, which can see and
 * manipulate HTTP user level data, compromising privacy.  It uses
 * low level tunneling instead, which hides HTTP protocol and data
 * from all third parties.  (Traffic analysis is still possible).
 * <LI>It does basic server authentication, to protect
 * against "URL spoofing" attacks.  This involves deciding
 * whether the X.509 certificate chain identifying the server
 * is trusted, and verifying that the name of the server is
 * found in the certificate.  (The application may enable an
 * anonymous SSL cipher suite, and such checks are not done
 * for anonymous ciphers.)
 * <LI>It exposes key SSL session attributes, specifically the
 * cipher suite in use and the server's X509 certificates, to
 * application software which knows about this protocol handler.
 * </UL>
 * <P> System properties used include:  <UL>
 * <LI><em>https.proxyHost</em> ... the host supporting SSL
 * tunneling using the conventional CONNECT syntax
 * <LI><em>https.proxyPort</em> ... port to use on proxyHost
 * <LI><em>https.cipherSuites</em> ... comma separated list of
 * SSL cipher suite names to enable.
 * <LI><em>http.nonProxyHosts</em> ...
 * </UL>
 * @author David Brownell
 * @author Bill Foote
 */
final class HttpsClient extends HttpClient implements HandshakeCompletedListener {
  private static final int httpsPortNumber=443;
  private static final String defaultHVCanonicalName="javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier";
  /** 
 * Returns the default HTTPS port (443) 
 */
  @Override protected int getDefaultPort(){
    return httpsPortNumber;
  }
  private HostnameVerifier hv;
  private SSLSocketFactory sslSocketFactory;
  private SSLSession session;
  private String[] getCipherSuites(){
    String ciphers[];
    String cipherString=AccessController.doPrivileged(new GetPropertyAction("https.cipherSuites"));
    if (cipherString == null || "".equals(cipherString)) {
      ciphers=null;
    }
 else {
      StringTokenizer tokenizer;
      Vector<String> v=new Vector<String>();
      tokenizer=new StringTokenizer(cipherString,",");
      while (tokenizer.hasMoreTokens())       v.addElement(tokenizer.nextToken());
      ciphers=new String[v.size()];
      for (int i=0; i < ciphers.length; i++)       ciphers[i]=v.elementAt(i);
    }
    return ciphers;
  }
  private String[] getProtocols(){
    String protocols[];
    String protocolString=AccessController.doPrivileged(new GetPropertyAction("https.protocols"));
    if (protocolString == null || "".equals(protocolString)) {
      protocols=null;
    }
 else {
      StringTokenizer tokenizer;
      Vector<String> v=new Vector<String>();
      tokenizer=new StringTokenizer(protocolString,",");
      while (tokenizer.hasMoreTokens())       v.addElement(tokenizer.nextToken());
      protocols=new String[v.size()];
      for (int i=0; i < protocols.length; i++) {
        protocols[i]=v.elementAt(i);
      }
    }
    return protocols;
  }
  private String getUserAgent(){
    String userAgent=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("https.agent"));
    if (userAgent == null || userAgent.length() == 0) {
      userAgent="JSSE";
    }
    return userAgent;
  }
  private static Proxy newHttpProxy(  String proxyHost,  int proxyPort){
    InetSocketAddress saddr=null;
    final String phost=proxyHost;
    final int pport=proxyPort < 0 ? httpsPortNumber : proxyPort;
    try {
      saddr=java.security.AccessController.doPrivileged(new java.security.PrivilegedExceptionAction<InetSocketAddress>(){
        public InetSocketAddress run(){
          return new InetSocketAddress(phost,pport);
        }
      }
);
    }
 catch (    java.security.PrivilegedActionException pae) {
    }
    return new Proxy(Proxy.Type.HTTP,saddr);
  }
  /** 
 * Create an HTTPS client URL.  Traffic will be tunneled through any
 * intermediate nodes rather than proxied, so that confidentiality
 * of data exchanged can be preserved.  However, note that all the
 * anonymous SSL flavors are subject to "person-in-the-middle"
 * attacks against confidentiality.  If you enable use of those
 * flavors, you may be giving up the protection you get through
 * SSL tunneling.
 * Use New to get new HttpsClient. This constructor is meant to be
 * used only by New method. New properly checks for URL spoofing.
 * @param URL https URL with which a connection must be established
 */
  private HttpsClient(  SSLSocketFactory sf,  URL url) throws IOException {
    this(sf,url,(String)null,-1);
  }
  /** 
 * Create an HTTPS client URL.  Traffic will be tunneled through
 * the specified proxy server.
 */
  HttpsClient(  SSLSocketFactory sf,  URL url,  String proxyHost,  int proxyPort) throws IOException {
    this(sf,url,proxyHost,proxyPort,-1);
  }
  /** 
 * Create an HTTPS client URL.  Traffic will be tunneled through
 * the specified proxy server, with a connect timeout
 */
  HttpsClient(  SSLSocketFactory sf,  URL url,  String proxyHost,  int proxyPort,  int connectTimeout) throws IOException {
    this(sf,url,(proxyHost == null ? null : HttpsClient.newHttpProxy(proxyHost,proxyPort)),connectTimeout);
  }
  /** 
 * Same as previous constructor except using a Proxy
 */
  HttpsClient(  SSLSocketFactory sf,  URL url,  Proxy proxy,  int connectTimeout) throws IOException {
    this.proxy=proxy;
    setSSLSocketFactory(sf);
    this.proxyDisabled=true;
    this.host=url.getHost();
    this.url=url;
    port=url.getPort();
    if (port == -1) {
      port=getDefaultPort();
    }
    setConnectTimeout(connectTimeout);
    cookieHandler=java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<CookieHandler>(){
      public CookieHandler run(){
        return CookieHandler.getDefault();
      }
    }
);
    openServer();
  }
  static HttpClient New(  SSLSocketFactory sf,  URL url,  HostnameVerifier hv) throws IOException {
    return HttpsClient.New(sf,url,hv,true);
  }
  /** 
 * See HttpClient for the model for this method. 
 */
  static HttpClient New(  SSLSocketFactory sf,  URL url,  HostnameVerifier hv,  boolean useCache) throws IOException {
    return HttpsClient.New(sf,url,hv,(String)null,-1,useCache);
  }
  /** 
 * Get a HTTPS client to the URL.  Traffic will be tunneled through
 * the specified proxy server.
 */
  static HttpClient New(  SSLSocketFactory sf,  URL url,  HostnameVerifier hv,  String proxyHost,  int proxyPort) throws IOException {
    return HttpsClient.New(sf,url,hv,proxyHost,proxyPort,true);
  }
  static HttpClient New(  SSLSocketFactory sf,  URL url,  HostnameVerifier hv,  String proxyHost,  int proxyPort,  boolean useCache) throws IOException {
    return HttpsClient.New(sf,url,hv,proxyHost,proxyPort,useCache,-1);
  }
  static HttpClient New(  SSLSocketFactory sf,  URL url,  HostnameVerifier hv,  String proxyHost,  int proxyPort,  boolean useCache,  int connectTimeout) throws IOException {
    return HttpsClient.New(sf,url,hv,(proxyHost == null ? null : HttpsClient.newHttpProxy(proxyHost,proxyPort)),useCache,connectTimeout);
  }
  static HttpClient New(  SSLSocketFactory sf,  URL url,  HostnameVerifier hv,  Proxy p,  boolean useCache,  int connectTimeout) throws IOException {
    HttpsClient ret=null;
    if (useCache) {
      ret=(HttpsClient)kac.get(url,sf);
      if (ret != null) {
        ret.cachedHttpClient=true;
      }
    }
    if (ret == null) {
      ret=new HttpsClient(sf,url,p,connectTimeout);
    }
 else {
      SecurityManager security=System.getSecurityManager();
      if (security != null) {
        security.checkConnect(url.getHost(),url.getPort());
      }
      ret.url=url;
    }
    ret.setHostnameVerifier(hv);
    return ret;
  }
  void setHostnameVerifier(  HostnameVerifier hv){
    this.hv=hv;
  }
  void setSSLSocketFactory(  SSLSocketFactory sf){
    sslSocketFactory=sf;
  }
  SSLSocketFactory getSSLSocketFactory(){
    return sslSocketFactory;
  }
  /** 
 * The following method, createSocket, is defined in NetworkClient
 * and overridden here so that the socket facroty is used to create
 * new sockets.
 */
  @Override protected Socket createSocket() throws IOException {
    try {
      return sslSocketFactory.createSocket();
    }
 catch (    SocketException se) {
      Throwable t=se.getCause();
      if (t != null && t instanceof UnsupportedOperationException) {
        return super.createSocket();
      }
 else {
        throw se;
      }
    }
  }
  @Override public boolean needsTunneling(){
    return (proxy != null && proxy.type() != Proxy.Type.DIRECT && proxy.type() != Proxy.Type.SOCKS);
  }
  @Override public void afterConnect() throws IOException, UnknownHostException {
    if (!isCachedConnection()) {
      SSLSocket s=null;
      SSLSocketFactory factory=sslSocketFactory;
      try {
        if (!(serverSocket instanceof SSLSocket)) {
          s=(SSLSocket)factory.createSocket(serverSocket,host,port,true);
        }
 else {
          s=(SSLSocket)serverSocket;
          if (s instanceof SSLSocketImpl) {
            ((SSLSocketImpl)s).setHost(host);
          }
        }
      }
 catch (      IOException ex) {
        try {
          s=(SSLSocket)factory.createSocket(host,port);
        }
 catch (        IOException ignored) {
          throw ex;
        }
      }
      String[] protocols=getProtocols();
      String[] ciphers=getCipherSuites();
      if (protocols != null) {
        s.setEnabledProtocols(protocols);
      }
      if (ciphers != null) {
        s.setEnabledCipherSuites(ciphers);
      }
      s.addHandshakeCompletedListener(this);
      boolean needToCheckSpoofing=true;
      String identification=s.getSSLParameters().getEndpointIdentificationAlgorithm();
      if (identification != null && identification.length() != 0) {
        if (identification.equalsIgnoreCase("HTTPS")) {
          needToCheckSpoofing=false;
        }
      }
 else {
        boolean isDefaultHostnameVerifier=false;
        if (hv != null) {
          String canonicalName=hv.getClass().getCanonicalName();
          if (canonicalName != null && canonicalName.equalsIgnoreCase(defaultHVCanonicalName)) {
            isDefaultHostnameVerifier=true;
          }
        }
 else {
          isDefaultHostnameVerifier=true;
        }
        if (isDefaultHostnameVerifier) {
          SSLParameters paramaters=s.getSSLParameters();
          paramaters.setEndpointIdentificationAlgorithm("HTTPS");
          s.setSSLParameters(paramaters);
          needToCheckSpoofing=false;
        }
      }
      s.startHandshake();
      session=s.getSession();
      serverSocket=s;
      try {
        serverOutput=new PrintStream(new BufferedOutputStream(serverSocket.getOutputStream()),false,encoding);
      }
 catch (      UnsupportedEncodingException e) {
        throw new InternalError(encoding + " encoding not found");
      }
      if (needToCheckSpoofing) {
        checkURLSpoofing(hv);
      }
    }
 else {
      session=((SSLSocket)serverSocket).getSession();
    }
  }
  private void checkURLSpoofing(  HostnameVerifier hostnameVerifier) throws IOException {
    String host=url.getHost();
    if (host != null && host.startsWith("[") && host.endsWith("]")) {
      host=host.substring(1,host.length() - 1);
    }
    Certificate[] peerCerts=null;
    String cipher=session.getCipherSuite();
    try {
      HostnameChecker checker=HostnameChecker.getInstance(HostnameChecker.TYPE_TLS);
      if (cipher.startsWith("TLS_KRB5")) {
        if (!HostnameChecker.match(host,getPeerPrincipal())) {
          throw new SSLPeerUnverifiedException("Hostname checker" + " failed for Kerberos");
        }
      }
 else {
        peerCerts=session.getPeerCertificates();
        X509Certificate peerCert;
        if (peerCerts[0] instanceof java.security.cert.X509Certificate) {
          peerCert=(java.security.cert.X509Certificate)peerCerts[0];
        }
 else {
          throw new SSLPeerUnverifiedException("");
        }
        checker.match(host,peerCert);
      }
      return;
    }
 catch (    SSLPeerUnverifiedException e) {
    }
catch (    java.security.cert.CertificateException cpe) {
    }
    if ((cipher != null) && (cipher.indexOf("_anon_") != -1)) {
      return;
    }
 else     if ((hostnameVerifier != null) && (hostnameVerifier.verify(host,session))) {
      return;
    }
    serverSocket.close();
    session.invalidate();
    throw new IOException("HTTPS hostname wrong:  should be <" + url.getHost() + ">");
  }
  @Override protected void putInKeepAliveCache(){
    kac.put(url,sslSocketFactory,this);
  }
  @Override public void closeIdleConnection(){
    HttpClient http=(HttpClient)kac.get(url,sslSocketFactory);
    if (http != null) {
      http.closeServer();
    }
  }
  /** 
 * Returns the cipher suite in use on this connection.
 */
  String getCipherSuite(){
    return session.getCipherSuite();
  }
  /** 
 * Returns the certificate chain the client sent to the
 * server, or null if the client did not authenticate.
 */
  public java.security.cert.Certificate[] getLocalCertificates(){
    return session.getLocalCertificates();
  }
  /** 
 * Returns the certificate chain with which the server
 * authenticated itself, or throw a SSLPeerUnverifiedException
 * if the server did not authenticate.
 */
  java.security.cert.Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
    return session.getPeerCertificates();
  }
  /** 
 * Returns the X.509 certificate chain with which the server
 * authenticated itself, or null if the server did not authenticate.
 */
  javax.security.cert.X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
    return session.getPeerCertificateChain();
  }
  /** 
 * Returns the principal with which the server authenticated
 * itself, or throw a SSLPeerUnverifiedException if the
 * server did not authenticate.
 */
  Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    Principal principal;
    try {
      principal=session.getPeerPrincipal();
    }
 catch (    AbstractMethodError e) {
      java.security.cert.Certificate[] certs=session.getPeerCertificates();
      principal=(X500Principal)((X509Certificate)certs[0]).getSubjectX500Principal();
    }
    return principal;
  }
  /** 
 * Returns the principal the client sent to the
 * server, or null if the client did not authenticate.
 */
  Principal getLocalPrincipal(){
    Principal principal;
    try {
      principal=session.getLocalPrincipal();
    }
 catch (    AbstractMethodError e) {
      principal=null;
      java.security.cert.Certificate[] certs=session.getLocalCertificates();
      if (certs != null) {
        principal=(X500Principal)((X509Certificate)certs[0]).getSubjectX500Principal();
      }
    }
    return principal;
  }
  /** 
 * This method implements the SSL HandshakeCompleted callback,
 * remembering the resulting session so that it may be queried
 * for the current cipher suite and peer certificates.  Servers
 * sometimes re-initiate handshaking, so the session in use on
 * a given connection may change.  When sessions change, so may
 * peer identities and cipher suites.
 */
  public void handshakeCompleted(  HandshakeCompletedEvent event){
    session=event.getSession();
  }
  /** 
 * @return the proxy host being used for this client, or null
 * if we're not going through a proxy
 */
  @Override public String getProxyHostUsed(){
    if (!needsTunneling()) {
      return null;
    }
 else {
      return super.getProxyHostUsed();
    }
  }
  /** 
 * @return the proxy port being used for this client.  Meaningless
 * if getProxyHostUsed() gives null.
 */
  @Override public int getProxyPortUsed(){
    return (proxy == null || proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.SOCKS) ? -1 : ((InetSocketAddress)proxy.address()).getPort();
  }
}
