package sun.net.www.protocol.http;
import java.net.URL;
import java.net.URLConnection;
import java.net.ProtocolException;
import java.net.HttpRetryException;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.InetSocketAddress;
import java.net.CookieHandler;
import java.net.ResponseCache;
import java.net.CacheResponse;
import java.net.SecureCacheResponse;
import java.net.CacheRequest;
import java.net.Authenticator.RequestorType;
import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import sun.net.*;
import sun.net.www.*;
import sun.net.www.http.HttpClient;
import sun.net.www.http.PosterOutputStream;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.http.ChunkedOutputStream;
import sun.util.logging.PlatformLogger;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import static sun.net.www.protocol.http.AuthScheme.BASIC;
import static sun.net.www.protocol.http.AuthScheme.DIGEST;
import static sun.net.www.protocol.http.AuthScheme.NTLM;
import static sun.net.www.protocol.http.AuthScheme.NEGOTIATE;
import static sun.net.www.protocol.http.AuthScheme.KERBEROS;
import static sun.net.www.protocol.http.AuthScheme.UNKNOWN;
/** 
 * A class to represent an HTTP connection to a remote object.
 */
public class HttpURLConnection extends java.net.HttpURLConnection {
  static String HTTP_CONNECT="CONNECT";
  static final String version;
  public static final String userAgent;
  static final int defaultmaxRedirects=20;
  static final int maxRedirects;
  static final boolean validateProxy;
  static final boolean validateServer;
  private StreamingOutputStream strOutputStream;
  private final static String RETRY_MSG1="cannot retry due to proxy authentication, in streaming mode";
  private final static String RETRY_MSG2="cannot retry due to server authentication, in streaming mode";
  private final static String RETRY_MSG3="cannot retry due to redirection, in streaming mode";
  private static boolean enableESBuffer=false;
  private static int timeout4ESBuffer=0;
  private static int bufSize4ES=0;
  private static final boolean allowRestrictedHeaders;
  private static final Set<String> restrictedHeaderSet;
  private static final String[] restrictedHeaders={"Access-Control-Request-Headers","Access-Control-Request-Method","Connection","Content-Length","Content-Transfer-Encoding","Host","Keep-Alive","Origin","Trailer","Transfer-Encoding","Upgrade","Via"};
static {
    maxRedirects=java.security.AccessController.doPrivileged(new sun.security.action.GetIntegerAction("http.maxRedirects",defaultmaxRedirects)).intValue();
    version=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("java.version"));
    String agent=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("http.agent"));
    if (agent == null) {
      agent="Java/" + version;
    }
 else {
      agent=agent + " Java/" + version;
    }
    userAgent=agent;
    validateProxy=java.security.AccessController.doPrivileged(new sun.security.action.GetBooleanAction("http.auth.digest.validateProxy")).booleanValue();
    validateServer=java.security.AccessController.doPrivileged(new sun.security.action.GetBooleanAction("http.auth.digest.validateServer")).booleanValue();
    enableESBuffer=java.security.AccessController.doPrivileged(new sun.security.action.GetBooleanAction("sun.net.http.errorstream.enableBuffering")).booleanValue();
    timeout4ESBuffer=java.security.AccessController.doPrivileged(new sun.security.action.GetIntegerAction("sun.net.http.errorstream.timeout",300)).intValue();
    if (timeout4ESBuffer <= 0) {
      timeout4ESBuffer=300;
    }
    bufSize4ES=java.security.AccessController.doPrivileged(new sun.security.action.GetIntegerAction("sun.net.http.errorstream.bufferSize",4096)).intValue();
    if (bufSize4ES <= 0) {
      bufSize4ES=4096;
    }
    allowRestrictedHeaders=((Boolean)java.security.AccessController.doPrivileged(new sun.security.action.GetBooleanAction("sun.net.http.allowRestrictedHeaders"))).booleanValue();
    if (!allowRestrictedHeaders) {
      restrictedHeaderSet=new HashSet<String>(restrictedHeaders.length);
      for (int i=0; i < restrictedHeaders.length; i++) {
        restrictedHeaderSet.add(restrictedHeaders[i].toLowerCase());
      }
    }
 else {
      restrictedHeaderSet=null;
    }
  }
  static final String httpVersion="HTTP/1.1";
  static final String acceptString="text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
  private static final String[] EXCLUDE_HEADERS={"Proxy-Authorization","Authorization"};
  private static final String[] EXCLUDE_HEADERS2={"Proxy-Authorization","Authorization","Cookie","Cookie2"};
  protected HttpClient http;
  protected Handler handler;
  protected Proxy instProxy;
  private CookieHandler cookieHandler;
  private ResponseCache cacheHandler;
  protected CacheResponse cachedResponse;
  private MessageHeader cachedHeaders;
  private InputStream cachedInputStream;
  protected PrintStream ps=null;
  private InputStream errorStream=null;
  private boolean setUserCookies=true;
  private String userCookies=null;
  private String userCookies2=null;
  private static HttpAuthenticator defaultAuth;
  private MessageHeader requests;
  String domain;
  DigestAuthentication.Parameters digestparams;
  AuthenticationInfo currentProxyCredentials=null;
  AuthenticationInfo currentServerCredentials=null;
  boolean needToCheck=true;
  private boolean doingNTLM2ndStage=false;
  private boolean doingNTLMp2ndStage=false;
  private boolean tryTransparentNTLMServer=true;
  private boolean tryTransparentNTLMProxy=true;
  private Object authObj;
  boolean isUserServerAuth;
  boolean isUserProxyAuth;
  String serverAuthKey, proxyAuthKey;
  protected ProgressSource pi;
  private MessageHeader responses;
  private InputStream inputStream=null;
  private PosterOutputStream poster=null;
  private boolean setRequests=false;
  private boolean failedOnce=false;
  private Exception rememberedException=null;
  private HttpClient reuseClient=null;
  enum TunnelState {  NONE,   SETUP,   TUNNELING}
  private TunnelState tunnelState=TunnelState.NONE;
  private int connectTimeout=NetworkClient.DEFAULT_CONNECT_TIMEOUT;
  private int readTimeout=NetworkClient.DEFAULT_READ_TIMEOUT;
  private static final PlatformLogger logger=PlatformLogger.getLogger("sun.net.www.protocol.http.HttpURLConnection");
  private static PasswordAuthentication privilegedRequestPasswordAuthentication(  final String host,  final InetAddress addr,  final int port,  final String protocol,  final String prompt,  final String scheme,  final URL url,  final RequestorType authType){
    return java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<PasswordAuthentication>(){
      public PasswordAuthentication run(){
        if (logger.isLoggable(PlatformLogger.FINEST)) {
          logger.finest("Requesting Authentication: host =" + host + " url = "+ url);
        }
        PasswordAuthentication pass=Authenticator.requestPasswordAuthentication(host,addr,port,protocol,prompt,scheme,url,authType);
        if (logger.isLoggable(PlatformLogger.FINEST)) {
          logger.finest("Authentication returned: " + (pass != null ? pass.toString() : "null"));
        }
        return pass;
      }
    }
);
  }
  private boolean isRestrictedHeader(  String key,  String value){
    if (allowRestrictedHeaders) {
      return false;
    }
    key=key.toLowerCase();
    if (restrictedHeaderSet.contains(key)) {
      if (key.equals("connection") && value.equalsIgnoreCase("close")) {
        return false;
      }
      return true;
    }
 else     if (key.startsWith("sec-")) {
      return true;
    }
    return false;
  }
  private boolean isExternalMessageHeaderAllowed(  String key,  String value){
    checkMessageHeader(key,value);
    if (!isRestrictedHeader(key,value)) {
      return true;
    }
    return false;
  }
  public static PlatformLogger getHttpLogger(){
    return logger;
  }
  public Object authObj(){
    return authObj;
  }
  public void authObj(  Object authObj){
    this.authObj=authObj;
  }
  private void checkMessageHeader(  String key,  String value){
    char LF='\n';
    int index=key.indexOf(LF);
    if (index != -1) {
      throw new IllegalArgumentException("Illegal character(s) in message header field: " + key);
    }
 else {
      if (value == null) {
        return;
      }
      index=value.indexOf(LF);
      while (index != -1) {
        index++;
        if (index < value.length()) {
          char c=value.charAt(index);
          if ((c == ' ') || (c == '\t')) {
            index=value.indexOf(LF,index);
            continue;
          }
        }
        throw new IllegalArgumentException("Illegal character(s) in message header value: " + value);
      }
    }
  }
  private void writeRequests() throws IOException {
    if (http.usingProxy && tunnelState() != TunnelState.TUNNELING) {
      setPreemptiveProxyAuthentication(requests);
    }
    if (!setRequests) {
      if (!failedOnce)       requests.prepend(method + " " + getRequestURI()+ " "+ httpVersion,null);
      if (!getUseCaches()) {
        requests.setIfNotSet("Cache-Control","no-cache");
        requests.setIfNotSet("Pragma","no-cache");
      }
      requests.setIfNotSet("User-Agent",userAgent);
      int port=url.getPort();
      String host=url.getHost();
      if (port != -1 && port != url.getDefaultPort()) {
        host+=":" + String.valueOf(port);
      }
      requests.setIfNotSet("Host",host);
      requests.setIfNotSet("Accept",acceptString);
      if (!failedOnce && http.getHttpKeepAliveSet()) {
        if (http.usingProxy && tunnelState() != TunnelState.TUNNELING) {
          requests.setIfNotSet("Proxy-Connection","keep-alive");
        }
 else {
          requests.setIfNotSet("Connection","keep-alive");
        }
      }
 else {
        requests.setIfNotSet("Connection","close");
      }
      long modTime=getIfModifiedSince();
      if (modTime != 0) {
        Date date=new Date(modTime);
        SimpleDateFormat fo=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.US);
        fo.setTimeZone(TimeZone.getTimeZone("GMT"));
        requests.setIfNotSet("If-Modified-Since",fo.format(date));
      }
      AuthenticationInfo sauth=AuthenticationInfo.getServerAuth(url);
      if (sauth != null && sauth.supportsPreemptiveAuthorization()) {
        requests.setIfNotSet(sauth.getHeaderName(),sauth.getHeaderValue(url,method));
        currentServerCredentials=sauth;
      }
      if (!method.equals("PUT") && (poster != null || streaming())) {
        requests.setIfNotSet("Content-type","application/x-www-form-urlencoded");
      }
      boolean chunked=false;
      if (streaming()) {
        if (chunkLength != -1) {
          requests.set("Transfer-Encoding","chunked");
          chunked=true;
        }
 else {
          if (fixedContentLengthLong != -1) {
            requests.set("Content-Length",String.valueOf(fixedContentLengthLong));
          }
 else           if (fixedContentLength != -1) {
            requests.set("Content-Length",String.valueOf(fixedContentLength));
          }
        }
      }
 else       if (poster != null) {
synchronized (poster) {
          poster.close();
          requests.set("Content-Length",String.valueOf(poster.size()));
        }
      }
      if (!chunked) {
        if (requests.findValue("Transfer-Encoding") != null) {
          requests.remove("Transfer-Encoding");
          if (logger.isLoggable(PlatformLogger.WARNING)) {
            logger.warning("use streaming mode for chunked encoding");
          }
        }
      }
      setCookieHeader();
      setRequests=true;
    }
    if (logger.isLoggable(PlatformLogger.FINE)) {
      logger.fine(requests.toString());
    }
    http.writeRequests(requests,poster,streaming());
    if (ps.checkError()) {
      String proxyHost=http.getProxyHostUsed();
      int proxyPort=http.getProxyPortUsed();
      disconnectInternal();
      if (failedOnce) {
        throw new IOException("Error writing to server");
      }
 else {
        failedOnce=true;
        if (proxyHost != null) {
          setProxiedClient(url,proxyHost,proxyPort);
        }
 else {
          setNewClient(url);
        }
        ps=(PrintStream)http.getOutputStream();
        connected=true;
        responses=new MessageHeader();
        setRequests=false;
        writeRequests();
      }
    }
  }
  /** 
 * Create a new HttpClient object, bypassing the cache of
 * HTTP client objects/connections.
 * @param url       the URL being accessed
 */
  protected void setNewClient(  URL url) throws IOException {
    setNewClient(url,false);
  }
  /** 
 * Obtain a HttpsClient object. Use the cached copy if specified.
 * @param url       the URL being accessed
 * @param useCache  whether the cached connection should be used
 * if present
 */
  protected void setNewClient(  URL url,  boolean useCache) throws IOException {
    http=HttpClient.New(url,null,-1,useCache,connectTimeout);
    http.setReadTimeout(readTimeout);
  }
  /** 
 * Create a new HttpClient object, set up so that it uses
 * per-instance proxying to the given HTTP proxy.  This
 * bypasses the cache of HTTP client objects/connections.
 * @param url       the URL being accessed
 * @param proxyHost the proxy host to use
 * @param proxyPort the proxy port to use
 */
  protected void setProxiedClient(  URL url,  String proxyHost,  int proxyPort) throws IOException {
    setProxiedClient(url,proxyHost,proxyPort,false);
  }
  /** 
 * Obtain a HttpClient object, set up so that it uses per-instance
 * proxying to the given HTTP proxy. Use the cached copy of HTTP
 * client objects/connections if specified.
 * @param url       the URL being accessed
 * @param proxyHost the proxy host to use
 * @param proxyPort the proxy port to use
 * @param useCache  whether the cached connection should be used
 * if present
 */
  protected void setProxiedClient(  URL url,  String proxyHost,  int proxyPort,  boolean useCache) throws IOException {
    proxiedConnect(url,proxyHost,proxyPort,useCache);
  }
  protected void proxiedConnect(  URL url,  String proxyHost,  int proxyPort,  boolean useCache) throws IOException {
    http=HttpClient.New(url,proxyHost,proxyPort,useCache,connectTimeout);
    http.setReadTimeout(readTimeout);
  }
  protected HttpURLConnection(  URL u,  Handler handler) throws IOException {
    this(u,null,handler);
  }
  public HttpURLConnection(  URL u,  String host,  int port){
    this(u,new Proxy(Proxy.Type.HTTP,InetSocketAddress.createUnresolved(host,port)));
  }
  /** 
 * this constructor is used by other protocol handlers such as ftp
 * that want to use http to fetch urls on their behalf.
 */
  public HttpURLConnection(  URL u,  Proxy p){
    this(u,p,new Handler());
  }
  protected HttpURLConnection(  URL u,  Proxy p,  Handler handler){
    super(u);
    requests=new MessageHeader();
    responses=new MessageHeader();
    this.handler=handler;
    instProxy=p;
    if (instProxy instanceof sun.net.ApplicationProxy) {
      try {
        cookieHandler=CookieHandler.getDefault();
      }
 catch (      SecurityException se) {
      }
    }
 else {
      cookieHandler=java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<CookieHandler>(){
        public CookieHandler run(){
          return CookieHandler.getDefault();
        }
      }
);
    }
    cacheHandler=java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<ResponseCache>(){
      public ResponseCache run(){
        return ResponseCache.getDefault();
      }
    }
);
  }
  /** 
 *   Use java.net.Authenticator.setDefault() instead.
 */
  public static void setDefaultAuthenticator(  HttpAuthenticator a){
    defaultAuth=a;
  }
  /** 
 * opens a stream allowing redirects only to the same host.
 */
  public static InputStream openConnectionCheckRedirects(  URLConnection c) throws IOException {
    boolean redir;
    int redirects=0;
    InputStream in;
    do {
      if (c instanceof HttpURLConnection) {
        ((HttpURLConnection)c).setInstanceFollowRedirects(false);
      }
      in=c.getInputStream();
      redir=false;
      if (c instanceof HttpURLConnection) {
        HttpURLConnection http=(HttpURLConnection)c;
        int stat=http.getResponseCode();
        if (stat >= 300 && stat <= 307 && stat != 306 && stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
          URL base=http.getURL();
          String loc=http.getHeaderField("Location");
          URL target=null;
          if (loc != null) {
            target=new URL(base,loc);
          }
          http.disconnect();
          if (target == null || !base.getProtocol().equals(target.getProtocol()) || base.getPort() != target.getPort() || !hostsEqual(base,target) || redirects >= 5) {
            throw new SecurityException("illegal URL redirect");
          }
          redir=true;
          c=target.openConnection();
          redirects++;
        }
      }
    }
 while (redir);
    return in;
  }
  private static boolean hostsEqual(  URL u1,  URL u2){
    final String h1=u1.getHost();
    final String h2=u2.getHost();
    if (h1 == null) {
      return h2 == null;
    }
 else     if (h2 == null) {
      return false;
    }
 else     if (h1.equalsIgnoreCase(h2)) {
      return true;
    }
    final boolean result[]={false};
    java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Void>(){
      public Void run(){
        try {
          InetAddress a1=InetAddress.getByName(h1);
          InetAddress a2=InetAddress.getByName(h2);
          result[0]=a1.equals(a2);
        }
 catch (        UnknownHostException e) {
        }
catch (        SecurityException e) {
        }
        return null;
      }
    }
);
    return result[0];
  }
  public void connect() throws IOException {
    plainConnect();
  }
  private boolean checkReuseConnection(){
    if (connected) {
      return true;
    }
    if (reuseClient != null) {
      http=reuseClient;
      http.setReadTimeout(getReadTimeout());
      http.reuse=false;
      reuseClient=null;
      connected=true;
      return true;
    }
    return false;
  }
  protected void plainConnect() throws IOException {
    if (connected) {
      return;
    }
    if (cacheHandler != null && getUseCaches()) {
      try {
        URI uri=ParseUtil.toURI(url);
        if (uri != null) {
          cachedResponse=cacheHandler.get(uri,getRequestMethod(),requests.getHeaders(EXCLUDE_HEADERS));
          if ("https".equalsIgnoreCase(uri.getScheme()) && !(cachedResponse instanceof SecureCacheResponse)) {
            cachedResponse=null;
          }
          if (logger.isLoggable(PlatformLogger.FINEST)) {
            logger.finest("Cache Request for " + uri + " / "+ getRequestMethod());
            logger.finest("From cache: " + (cachedResponse != null ? cachedResponse.toString() : "null"));
          }
          if (cachedResponse != null) {
            cachedHeaders=mapToMessageHeader(cachedResponse.getHeaders());
            cachedInputStream=cachedResponse.getBody();
          }
        }
      }
 catch (      IOException ioex) {
      }
      if (cachedHeaders != null && cachedInputStream != null) {
        connected=true;
        return;
      }
 else {
        cachedResponse=null;
      }
    }
    try {
      if (instProxy == null) {
        ProxySelector sel=java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<ProxySelector>(){
          public ProxySelector run(){
            return ProxySelector.getDefault();
          }
        }
);
        if (sel != null) {
          URI uri=sun.net.www.ParseUtil.toURI(url);
          if (logger.isLoggable(PlatformLogger.FINEST)) {
            logger.finest("ProxySelector Request for " + uri);
          }
          Iterator<Proxy> it=sel.select(uri).iterator();
          Proxy p;
          while (it.hasNext()) {
            p=it.next();
            try {
              if (!failedOnce) {
                http=getNewHttpClient(url,p,connectTimeout);
                http.setReadTimeout(readTimeout);
              }
 else {
                http=getNewHttpClient(url,p,connectTimeout,false);
                http.setReadTimeout(readTimeout);
              }
              if (logger.isLoggable(PlatformLogger.FINEST)) {
                if (p != null) {
                  logger.finest("Proxy used: " + p.toString());
                }
              }
              break;
            }
 catch (            IOException ioex) {
              if (p != Proxy.NO_PROXY) {
                sel.connectFailed(uri,p.address(),ioex);
                if (!it.hasNext()) {
                  http=getNewHttpClient(url,null,connectTimeout,false);
                  http.setReadTimeout(readTimeout);
                  break;
                }
              }
 else {
                throw ioex;
              }
              continue;
            }
          }
        }
 else {
          if (!failedOnce) {
            http=getNewHttpClient(url,null,connectTimeout);
            http.setReadTimeout(readTimeout);
          }
 else {
            http=getNewHttpClient(url,null,connectTimeout,false);
            http.setReadTimeout(readTimeout);
          }
        }
      }
 else {
        if (!failedOnce) {
          http=getNewHttpClient(url,instProxy,connectTimeout);
          http.setReadTimeout(readTimeout);
        }
 else {
          http=getNewHttpClient(url,instProxy,connectTimeout,false);
          http.setReadTimeout(readTimeout);
        }
      }
      ps=(PrintStream)http.getOutputStream();
    }
 catch (    IOException e) {
      throw e;
    }
    connected=true;
  }
  protected HttpClient getNewHttpClient(  URL url,  Proxy p,  int connectTimeout) throws IOException {
    return HttpClient.New(url,p,connectTimeout);
  }
  protected HttpClient getNewHttpClient(  URL url,  Proxy p,  int connectTimeout,  boolean useCache) throws IOException {
    return HttpClient.New(url,p,connectTimeout,useCache);
  }
  private void expect100Continue() throws IOException {
    int oldTimeout=http.getReadTimeout();
    boolean enforceTimeOut=false;
    boolean timedOut=false;
    if (oldTimeout <= 0) {
      http.setReadTimeout(5000);
      enforceTimeOut=true;
    }
    try {
      http.parseHTTP(responses,pi,this);
    }
 catch (    SocketTimeoutException se) {
      if (!enforceTimeOut) {
        throw se;
      }
      timedOut=true;
      http.setIgnoreContinue(true);
    }
    if (!timedOut) {
      String resp=responses.getValue(0);
      if (resp != null && resp.startsWith("HTTP/")) {
        String[] sa=resp.split("\\s+");
        responseCode=-1;
        try {
          if (sa.length > 1)           responseCode=Integer.parseInt(sa[1]);
        }
 catch (        NumberFormatException numberFormatException) {
        }
      }
      if (responseCode != 100) {
        throw new ProtocolException("Server rejected operation");
      }
    }
    http.setReadTimeout(oldTimeout);
    responseCode=-1;
    responses.reset();
  }
  @Override public synchronized OutputStream getOutputStream() throws IOException {
    try {
      if (!doOutput) {
        throw new ProtocolException("cannot write to a URLConnection" + " if doOutput=false - call setDoOutput(true)");
      }
      if (method.equals("GET")) {
        method="POST";
      }
      if (!"POST".equals(method) && !"PUT".equals(method) && "http".equals(url.getProtocol())) {
        throw new ProtocolException("HTTP method " + method + " doesn't support output");
      }
      if (inputStream != null) {
        throw new ProtocolException("Cannot write output after reading input.");
      }
      if (!checkReuseConnection())       connect();
      boolean expectContinue=false;
      String expects=requests.findValue("Expect");
      if ("100-Continue".equalsIgnoreCase(expects)) {
        http.setIgnoreContinue(false);
        expectContinue=true;
      }
      if (streaming() && strOutputStream == null) {
        writeRequests();
      }
      if (expectContinue) {
        expect100Continue();
      }
      ps=(PrintStream)http.getOutputStream();
      if (streaming()) {
        if (strOutputStream == null) {
          if (chunkLength != -1) {
            strOutputStream=new StreamingOutputStream(new ChunkedOutputStream(ps,chunkLength),-1L);
          }
 else {
            long length=0L;
            if (fixedContentLengthLong != -1) {
              length=fixedContentLengthLong;
            }
 else             if (fixedContentLength != -1) {
              length=fixedContentLength;
            }
            strOutputStream=new StreamingOutputStream(ps,length);
          }
        }
        return strOutputStream;
      }
 else {
        if (poster == null) {
          poster=new PosterOutputStream();
        }
        return poster;
      }
    }
 catch (    RuntimeException e) {
      disconnectInternal();
      throw e;
    }
catch (    ProtocolException e) {
      int i=responseCode;
      disconnectInternal();
      responseCode=i;
      throw e;
    }
catch (    IOException e) {
      disconnectInternal();
      throw e;
    }
  }
  private boolean streaming(){
    return (fixedContentLength != -1) || (fixedContentLengthLong != -1) || (chunkLength != -1);
  }
  private void setCookieHeader() throws IOException {
    if (cookieHandler != null) {
synchronized (this) {
        if (setUserCookies) {
          int k=requests.getKey("Cookie");
          if (k != -1)           userCookies=requests.getValue(k);
          k=requests.getKey("Cookie2");
          if (k != -1)           userCookies2=requests.getValue(k);
          setUserCookies=false;
        }
      }
      requests.remove("Cookie");
      requests.remove("Cookie2");
      URI uri=ParseUtil.toURI(url);
      if (uri != null) {
        if (logger.isLoggable(PlatformLogger.FINEST)) {
          logger.finest("CookieHandler request for " + uri);
        }
        Map<String,List<String>> cookies=cookieHandler.get(uri,requests.getHeaders(EXCLUDE_HEADERS));
        if (!cookies.isEmpty()) {
          if (logger.isLoggable(PlatformLogger.FINEST)) {
            logger.finest("Cookies retrieved: " + cookies.toString());
          }
          for (          Map.Entry<String,List<String>> entry : cookies.entrySet()) {
            String key=entry.getKey();
            if (!"Cookie".equalsIgnoreCase(key) && !"Cookie2".equalsIgnoreCase(key)) {
              continue;
            }
            List<String> l=entry.getValue();
            if (l != null && !l.isEmpty()) {
              StringBuilder cookieValue=new StringBuilder();
              for (              String value : l) {
                cookieValue.append(value).append("; ");
              }
              try {
                requests.add(key,cookieValue.substring(0,cookieValue.length() - 2));
              }
 catch (              StringIndexOutOfBoundsException ignored) {
              }
            }
          }
        }
      }
      if (userCookies != null) {
        int k;
        if ((k=requests.getKey("Cookie")) != -1)         requests.set("Cookie",requests.getValue(k) + ";" + userCookies);
 else         requests.set("Cookie",userCookies);
      }
      if (userCookies2 != null) {
        int k;
        if ((k=requests.getKey("Cookie2")) != -1)         requests.set("Cookie2",requests.getValue(k) + ";" + userCookies2);
 else         requests.set("Cookie2",userCookies2);
      }
    }
  }
  @Override @SuppressWarnings("empty-statement") public synchronized InputStream getInputStream() throws IOException {
    if (!doInput) {
      throw new ProtocolException("Cannot read from URLConnection" + " if doInput=false (call setDoInput(true))");
    }
    if (rememberedException != null) {
      if (rememberedException instanceof RuntimeException)       throw new RuntimeException(rememberedException);
 else {
        throw getChainedException((IOException)rememberedException);
      }
    }
    if (inputStream != null) {
      return inputStream;
    }
    if (streaming()) {
      if (strOutputStream == null) {
        getOutputStream();
      }
      strOutputStream.close();
      if (!strOutputStream.writtenOK()) {
        throw new IOException("Incomplete output stream");
      }
    }
    int redirects=0;
    int respCode=0;
    long cl=-1;
    AuthenticationInfo serverAuthentication=null;
    AuthenticationInfo proxyAuthentication=null;
    AuthenticationHeader srvHdr=null;
    boolean inNegotiate=false;
    boolean inNegotiateProxy=false;
    isUserServerAuth=requests.getKey("Authorization") != -1;
    isUserProxyAuth=requests.getKey("Proxy-Authorization") != -1;
    try {
      do {
        if (!checkReuseConnection())         connect();
        if (cachedInputStream != null) {
          return cachedInputStream;
        }
        boolean meteredInput=ProgressMonitor.getDefault().shouldMeterInput(url,method);
        if (meteredInput) {
          pi=new ProgressSource(url,method);
          pi.beginTracking();
        }
        ps=(PrintStream)http.getOutputStream();
        if (!streaming()) {
          writeRequests();
        }
        http.parseHTTP(responses,pi,this);
        if (logger.isLoggable(PlatformLogger.FINE)) {
          logger.fine(responses.toString());
        }
        inputStream=http.getInputStream();
        respCode=getResponseCode();
        if (respCode == -1) {
          disconnectInternal();
          throw new IOException("Invalid Http response");
        }
        if (respCode == HTTP_PROXY_AUTH) {
          if (streaming()) {
            disconnectInternal();
            throw new HttpRetryException(RETRY_MSG1,HTTP_PROXY_AUTH);
          }
          boolean dontUseNegotiate=false;
          Iterator iter=responses.multiValueIterator("Proxy-Authenticate");
          while (iter.hasNext()) {
            String value=((String)iter.next()).trim();
            if (value.equalsIgnoreCase("Negotiate") || value.equalsIgnoreCase("Kerberos")) {
              if (!inNegotiateProxy) {
                inNegotiateProxy=true;
              }
 else {
                dontUseNegotiate=true;
                doingNTLMp2ndStage=false;
                proxyAuthentication=null;
              }
              break;
            }
          }
          AuthenticationHeader authhdr=new AuthenticationHeader("Proxy-Authenticate",responses,new HttpCallerInfo(url,http.getProxyHostUsed(),http.getProxyPortUsed()),dontUseNegotiate);
          if (!doingNTLMp2ndStage) {
            proxyAuthentication=resetProxyAuthentication(proxyAuthentication,authhdr);
            if (proxyAuthentication != null) {
              redirects++;
              disconnectInternal();
              continue;
            }
          }
 else {
            String raw=responses.findValue("Proxy-Authenticate");
            reset();
            if (!proxyAuthentication.setHeaders(this,authhdr.headerParser(),raw)) {
              disconnectInternal();
              throw new IOException("Authentication failure");
            }
            if (serverAuthentication != null && srvHdr != null && !serverAuthentication.setHeaders(this,srvHdr.headerParser(),raw)) {
              disconnectInternal();
              throw new IOException("Authentication failure");
            }
            authObj=null;
            doingNTLMp2ndStage=false;
            continue;
          }
        }
 else {
          inNegotiateProxy=false;
          doingNTLMp2ndStage=false;
          if (!isUserProxyAuth)           requests.remove("Proxy-Authorization");
        }
        if (proxyAuthentication != null) {
          proxyAuthentication.addToCache();
        }
        if (respCode == HTTP_UNAUTHORIZED) {
          if (streaming()) {
            disconnectInternal();
            throw new HttpRetryException(RETRY_MSG2,HTTP_UNAUTHORIZED);
          }
          boolean dontUseNegotiate=false;
          Iterator iter=responses.multiValueIterator("WWW-Authenticate");
          while (iter.hasNext()) {
            String value=((String)iter.next()).trim();
            if (value.equalsIgnoreCase("Negotiate") || value.equalsIgnoreCase("Kerberos")) {
              if (!inNegotiate) {
                inNegotiate=true;
              }
 else {
                dontUseNegotiate=true;
                doingNTLM2ndStage=false;
                serverAuthentication=null;
              }
              break;
            }
          }
          srvHdr=new AuthenticationHeader("WWW-Authenticate",responses,new HttpCallerInfo(url),dontUseNegotiate);
          String raw=srvHdr.raw();
          if (!doingNTLM2ndStage) {
            if ((serverAuthentication != null) && serverAuthentication.getAuthScheme() != NTLM) {
              if (serverAuthentication.isAuthorizationStale(raw)) {
                disconnectWeb();
                redirects++;
                requests.set(serverAuthentication.getHeaderName(),serverAuthentication.getHeaderValue(url,method));
                currentServerCredentials=serverAuthentication;
                setCookieHeader();
                continue;
              }
 else {
                serverAuthentication.removeFromCache();
              }
            }
            serverAuthentication=getServerAuthentication(srvHdr);
            currentServerCredentials=serverAuthentication;
            if (serverAuthentication != null) {
              disconnectWeb();
              redirects++;
              setCookieHeader();
              continue;
            }
          }
 else {
            reset();
            if (!serverAuthentication.setHeaders(this,null,raw)) {
              disconnectWeb();
              throw new IOException("Authentication failure");
            }
            doingNTLM2ndStage=false;
            authObj=null;
            setCookieHeader();
            continue;
          }
        }
        if (serverAuthentication != null) {
          if (!(serverAuthentication instanceof DigestAuthentication) || (domain == null)) {
            if (serverAuthentication instanceof BasicAuthentication) {
              String npath=AuthenticationInfo.reducePath(url.getPath());
              String opath=serverAuthentication.path;
              if (!opath.startsWith(npath) || npath.length() >= opath.length()) {
                npath=BasicAuthentication.getRootPath(opath,npath);
              }
              BasicAuthentication a=(BasicAuthentication)serverAuthentication.clone();
              serverAuthentication.removeFromCache();
              a.path=npath;
              serverAuthentication=a;
            }
            serverAuthentication.addToCache();
          }
 else {
            DigestAuthentication srv=(DigestAuthentication)serverAuthentication;
            StringTokenizer tok=new StringTokenizer(domain," ");
            String realm=srv.realm;
            PasswordAuthentication pw=srv.pw;
            digestparams=srv.params;
            while (tok.hasMoreTokens()) {
              String path=tok.nextToken();
              try {
                URL u=new URL(url,path);
                DigestAuthentication d=new DigestAuthentication(false,u,realm,"Digest",pw,digestparams);
                d.addToCache();
              }
 catch (              Exception e) {
              }
            }
          }
        }
        inNegotiate=false;
        inNegotiateProxy=false;
        doingNTLMp2ndStage=false;
        doingNTLM2ndStage=false;
        if (!isUserServerAuth)         requests.remove("Authorization");
        if (!isUserProxyAuth)         requests.remove("Proxy-Authorization");
        if (respCode == HTTP_OK) {
          checkResponseCredentials(false);
        }
 else {
          needToCheck=false;
        }
        needToCheck=true;
        if (followRedirect()) {
          redirects++;
          setCookieHeader();
          continue;
        }
        try {
          cl=Long.parseLong(responses.findValue("content-length"));
        }
 catch (        Exception exc) {
        }
        ;
        if (method.equals("HEAD") || cl == 0 || respCode == HTTP_NOT_MODIFIED || respCode == HTTP_NO_CONTENT) {
          if (pi != null) {
            pi.finishTracking();
            pi=null;
          }
          http.finished();
          http=null;
          inputStream=new EmptyInputStream();
          connected=false;
        }
        if (respCode == 200 || respCode == 203 || respCode == 206 || respCode == 300 || respCode == 301 || respCode == 410) {
          if (cacheHandler != null) {
            URI uri=ParseUtil.toURI(url);
            if (uri != null) {
              URLConnection uconn=this;
              if ("https".equalsIgnoreCase(uri.getScheme())) {
                try {
                  uconn=(URLConnection)this.getClass().getField("httpsURLConnection").get(this);
                }
 catch (                IllegalAccessException iae) {
                }
catch (                NoSuchFieldException nsfe) {
                }
              }
              CacheRequest cacheRequest=cacheHandler.put(uri,uconn);
              if (cacheRequest != null && http != null) {
                http.setCacheRequest(cacheRequest);
                inputStream=new HttpInputStream(inputStream,cacheRequest);
              }
            }
          }
        }
        if (!(inputStream instanceof HttpInputStream)) {
          inputStream=new HttpInputStream(inputStream);
        }
        if (respCode >= 400) {
          if (respCode == 404 || respCode == 410) {
            throw new FileNotFoundException(url.toString());
          }
 else {
            throw new java.io.IOException("Server returned HTTP" + " response code: " + respCode + " for URL: "+ url.toString());
          }
        }
        poster=null;
        strOutputStream=null;
        return inputStream;
      }
 while (redirects < maxRedirects);
      throw new ProtocolException("Server redirected too many " + " times (" + redirects + ")");
    }
 catch (    RuntimeException e) {
      disconnectInternal();
      rememberedException=e;
      throw e;
    }
catch (    IOException e) {
      rememberedException=e;
      String te=responses.findValue("Transfer-Encoding");
      if (http != null && http.isKeepingAlive() && enableESBuffer && (cl > 0 || (te != null && te.equalsIgnoreCase("chunked")))) {
        errorStream=ErrorStream.getErrorStream(inputStream,cl,http);
      }
      throw e;
    }
 finally {
      if (proxyAuthKey != null) {
        AuthenticationInfo.endAuthRequest(proxyAuthKey);
      }
      if (serverAuthKey != null) {
        AuthenticationInfo.endAuthRequest(serverAuthKey);
      }
    }
  }
  private IOException getChainedException(  final IOException rememberedException){
    try {
      final Object[] args={rememberedException.getMessage()};
      IOException chainedException=java.security.AccessController.doPrivileged(new java.security.PrivilegedExceptionAction<IOException>(){
        public IOException run() throws Exception {
          return (IOException)rememberedException.getClass().getConstructor(new Class[]{String.class}).newInstance(args);
        }
      }
);
      chainedException.initCause(rememberedException);
      return chainedException;
    }
 catch (    Exception ignored) {
      return rememberedException;
    }
  }
  @Override public InputStream getErrorStream(){
    if (connected && responseCode >= 400) {
      if (errorStream != null) {
        return errorStream;
      }
 else       if (inputStream != null) {
        return inputStream;
      }
    }
    return null;
  }
  /** 
 * set or reset proxy authentication info in request headers
 * after receiving a 407 error. In the case of NTLM however,
 * receiving a 407 is normal and we just skip the stale check
 * because ntlm does not support this feature.
 */
  private AuthenticationInfo resetProxyAuthentication(  AuthenticationInfo proxyAuthentication,  AuthenticationHeader auth) throws IOException {
    if ((proxyAuthentication != null) && proxyAuthentication.getAuthScheme() != NTLM) {
      String raw=auth.raw();
      if (proxyAuthentication.isAuthorizationStale(raw)) {
        String value;
        if (proxyAuthentication instanceof DigestAuthentication) {
          DigestAuthentication digestProxy=(DigestAuthentication)proxyAuthentication;
          if (tunnelState() == TunnelState.SETUP) {
            value=digestProxy.getHeaderValue(connectRequestURI(url),HTTP_CONNECT);
          }
 else {
            value=digestProxy.getHeaderValue(getRequestURI(),method);
          }
        }
 else {
          value=proxyAuthentication.getHeaderValue(url,method);
        }
        requests.set(proxyAuthentication.getHeaderName(),value);
        currentProxyCredentials=proxyAuthentication;
        return proxyAuthentication;
      }
 else {
        proxyAuthentication.removeFromCache();
      }
    }
    proxyAuthentication=getHttpProxyAuthentication(auth);
    currentProxyCredentials=proxyAuthentication;
    return proxyAuthentication;
  }
  /** 
 * Returns the tunnel state.
 * @return  the state
 */
  TunnelState tunnelState(){
    return tunnelState;
  }
  /** 
 * Set the tunneling status.
 * @param the state
 */
  void setTunnelState(  TunnelState tunnelState){
    this.tunnelState=tunnelState;
  }
  /** 
 * establish a tunnel through proxy server
 */
  public synchronized void doTunneling() throws IOException {
    int retryTunnel=0;
    String statusLine="";
    int respCode=0;
    AuthenticationInfo proxyAuthentication=null;
    String proxyHost=null;
    int proxyPort=-1;
    MessageHeader savedRequests=requests;
    requests=new MessageHeader();
    boolean inNegotiateProxy=false;
    try {
      setTunnelState(TunnelState.SETUP);
      do {
        if (!checkReuseConnection()) {
          proxiedConnect(url,proxyHost,proxyPort,false);
        }
        sendCONNECTRequest();
        responses.reset();
        http.parseHTTP(responses,null,this);
        if (logger.isLoggable(PlatformLogger.FINE)) {
          logger.fine(responses.toString());
        }
        statusLine=responses.getValue(0);
        StringTokenizer st=new StringTokenizer(statusLine);
        st.nextToken();
        respCode=Integer.parseInt(st.nextToken().trim());
        if (respCode == HTTP_PROXY_AUTH) {
          boolean dontUseNegotiate=false;
          Iterator iter=responses.multiValueIterator("Proxy-Authenticate");
          while (iter.hasNext()) {
            String value=((String)iter.next()).trim();
            if (value.equalsIgnoreCase("Negotiate") || value.equalsIgnoreCase("Kerberos")) {
              if (!inNegotiateProxy) {
                inNegotiateProxy=true;
              }
 else {
                dontUseNegotiate=true;
                doingNTLMp2ndStage=false;
                proxyAuthentication=null;
              }
              break;
            }
          }
          AuthenticationHeader authhdr=new AuthenticationHeader("Proxy-Authenticate",responses,new HttpCallerInfo(url,http.getProxyHostUsed(),http.getProxyPortUsed()),dontUseNegotiate);
          if (!doingNTLMp2ndStage) {
            proxyAuthentication=resetProxyAuthentication(proxyAuthentication,authhdr);
            if (proxyAuthentication != null) {
              proxyHost=http.getProxyHostUsed();
              proxyPort=http.getProxyPortUsed();
              disconnectInternal();
              retryTunnel++;
              continue;
            }
          }
 else {
            String raw=responses.findValue("Proxy-Authenticate");
            reset();
            if (!proxyAuthentication.setHeaders(this,authhdr.headerParser(),raw)) {
              disconnectInternal();
              throw new IOException("Authentication failure");
            }
            authObj=null;
            doingNTLMp2ndStage=false;
            continue;
          }
        }
        if (proxyAuthentication != null) {
          proxyAuthentication.addToCache();
        }
        if (respCode == HTTP_OK) {
          setTunnelState(TunnelState.TUNNELING);
          break;
        }
        disconnectInternal();
        setTunnelState(TunnelState.NONE);
        break;
      }
 while (retryTunnel < maxRedirects);
      if (retryTunnel >= maxRedirects || (respCode != HTTP_OK)) {
        throw new IOException("Unable to tunnel through proxy." + " Proxy returns \"" + statusLine + "\"");
      }
    }
  finally {
      if (proxyAuthKey != null) {
        AuthenticationInfo.endAuthRequest(proxyAuthKey);
      }
    }
    requests=savedRequests;
    responses.reset();
  }
  static String connectRequestURI(  URL url){
    String host=url.getHost();
    int port=url.getPort();
    port=port != -1 ? port : url.getDefaultPort();
    return host + ":" + port;
  }
  /** 
 * send a CONNECT request for establishing a tunnel to proxy server
 */
  private void sendCONNECTRequest() throws IOException {
    int port=url.getPort();
    if (setRequests)     requests.set(0,null,null);
    requests.prepend(HTTP_CONNECT + " " + connectRequestURI(url)+ " "+ httpVersion,null);
    requests.setIfNotSet("User-Agent",userAgent);
    String host=url.getHost();
    if (port != -1 && port != url.getDefaultPort()) {
      host+=":" + String.valueOf(port);
    }
    requests.setIfNotSet("Host",host);
    requests.setIfNotSet("Accept",acceptString);
    if (http.getHttpKeepAliveSet()) {
      requests.setIfNotSet("Proxy-Connection","keep-alive");
    }
    setPreemptiveProxyAuthentication(requests);
    if (logger.isLoggable(PlatformLogger.FINE)) {
      logger.fine(requests.toString());
    }
    http.writeRequests(requests,null);
    requests.set(0,null,null);
  }
  /** 
 * Sets pre-emptive proxy authentication in header
 */
  private void setPreemptiveProxyAuthentication(  MessageHeader requests) throws IOException {
    AuthenticationInfo pauth=AuthenticationInfo.getProxyAuth(http.getProxyHostUsed(),http.getProxyPortUsed());
    if (pauth != null && pauth.supportsPreemptiveAuthorization()) {
      String value;
      if (pauth instanceof DigestAuthentication) {
        DigestAuthentication digestProxy=(DigestAuthentication)pauth;
        if (tunnelState() == TunnelState.SETUP) {
          value=digestProxy.getHeaderValue(connectRequestURI(url),HTTP_CONNECT);
        }
 else {
          value=digestProxy.getHeaderValue(getRequestURI(),method);
        }
      }
 else {
        value=pauth.getHeaderValue(url,method);
      }
      requests.set(pauth.getHeaderName(),value);
      currentProxyCredentials=pauth;
    }
  }
  /** 
 * Gets the authentication for an HTTP proxy, and applies it to
 * the connection.
 */
  private AuthenticationInfo getHttpProxyAuthentication(  AuthenticationHeader authhdr){
    AuthenticationInfo ret=null;
    String raw=authhdr.raw();
    String host=http.getProxyHostUsed();
    int port=http.getProxyPortUsed();
    if (host != null && authhdr.isPresent()) {
      HeaderParser p=authhdr.headerParser();
      String realm=p.findValue("realm");
      String scheme=authhdr.scheme();
      AuthScheme authScheme=UNKNOWN;
      if ("basic".equalsIgnoreCase(scheme)) {
        authScheme=BASIC;
      }
 else       if ("digest".equalsIgnoreCase(scheme)) {
        authScheme=DIGEST;
      }
 else       if ("ntlm".equalsIgnoreCase(scheme)) {
        authScheme=NTLM;
        doingNTLMp2ndStage=true;
      }
 else       if ("Kerberos".equalsIgnoreCase(scheme)) {
        authScheme=KERBEROS;
        doingNTLMp2ndStage=true;
      }
 else       if ("Negotiate".equalsIgnoreCase(scheme)) {
        authScheme=NEGOTIATE;
        doingNTLMp2ndStage=true;
      }
      if (realm == null)       realm="";
      proxyAuthKey=AuthenticationInfo.getProxyAuthKey(host,port,realm,authScheme);
      ret=AuthenticationInfo.getProxyAuth(proxyAuthKey);
      if (ret == null) {
switch (authScheme) {
case BASIC:
          InetAddress addr=null;
        try {
          final String finalHost=host;
          addr=java.security.AccessController.doPrivileged(new java.security.PrivilegedExceptionAction<InetAddress>(){
            public InetAddress run() throws java.net.UnknownHostException {
              return InetAddress.getByName(finalHost);
            }
          }
);
        }
 catch (        java.security.PrivilegedActionException ignored) {
        }
      PasswordAuthentication a=privilegedRequestPasswordAuthentication(host,addr,port,"http",realm,scheme,url,RequestorType.PROXY);
    if (a != null) {
      ret=new BasicAuthentication(true,host,port,realm,a);
    }
  break;
case DIGEST:
a=privilegedRequestPasswordAuthentication(host,null,port,url.getProtocol(),realm,scheme,url,RequestorType.PROXY);
if (a != null) {
DigestAuthentication.Parameters params=new DigestAuthentication.Parameters();
ret=new DigestAuthentication(true,host,port,realm,scheme,a,params);
}
break;
case NTLM:
if (NTLMAuthenticationProxy.proxy.supported) {
if (tryTransparentNTLMProxy) {
tryTransparentNTLMProxy=NTLMAuthenticationProxy.proxy.supportsTransparentAuth;
}
a=null;
if (tryTransparentNTLMProxy) {
logger.finest("Trying Transparent NTLM authentication");
}
 else {
a=privilegedRequestPasswordAuthentication(host,null,port,url.getProtocol(),"",scheme,url,RequestorType.PROXY);
}
if (tryTransparentNTLMProxy || (!tryTransparentNTLMProxy && a != null)) {
ret=NTLMAuthenticationProxy.proxy.create(true,host,port,a);
}
tryTransparentNTLMProxy=false;
}
break;
case NEGOTIATE:
ret=new NegotiateAuthentication(new HttpCallerInfo(authhdr.getHttpCallerInfo(),"Negotiate"));
break;
case KERBEROS:
ret=new NegotiateAuthentication(new HttpCallerInfo(authhdr.getHttpCallerInfo(),"Kerberos"));
break;
case UNKNOWN:
logger.finest("Unknown/Unsupported authentication scheme: " + scheme);
default :
throw new AssertionError("should not reach here");
}
}
if (ret == null && defaultAuth != null && defaultAuth.schemeSupported(scheme)) {
try {
URL u=new URL("http",host,port,"/");
String a=defaultAuth.authString(u,scheme,realm);
if (a != null) {
ret=new BasicAuthentication(true,host,port,realm,a);
}
}
 catch (java.net.MalformedURLException ignored) {
}
}
if (ret != null) {
if (!ret.setHeaders(this,p,raw)) {
ret=null;
}
}
}
if (logger.isLoggable(PlatformLogger.FINER)) {
logger.finer("Proxy Authentication for " + authhdr.toString() + " returned "+ (ret != null ? ret.toString() : "null"));
}
return ret;
}
/** 
 * Gets the authentication for an HTTP server, and applies it to
 * the connection.
 * @param authHdr the AuthenticationHeader which tells what auth scheme is
 * prefered.
 */
private AuthenticationInfo getServerAuthentication(AuthenticationHeader authhdr){
AuthenticationInfo ret=null;
String raw=authhdr.raw();
if (authhdr.isPresent()) {
HeaderParser p=authhdr.headerParser();
String realm=p.findValue("realm");
String scheme=authhdr.scheme();
AuthScheme authScheme=UNKNOWN;
if ("basic".equalsIgnoreCase(scheme)) {
authScheme=BASIC;
}
 else if ("digest".equalsIgnoreCase(scheme)) {
authScheme=DIGEST;
}
 else if ("ntlm".equalsIgnoreCase(scheme)) {
authScheme=NTLM;
doingNTLM2ndStage=true;
}
 else if ("Kerberos".equalsIgnoreCase(scheme)) {
authScheme=KERBEROS;
doingNTLM2ndStage=true;
}
 else if ("Negotiate".equalsIgnoreCase(scheme)) {
authScheme=NEGOTIATE;
doingNTLM2ndStage=true;
}
domain=p.findValue("domain");
if (realm == null) realm="";
serverAuthKey=AuthenticationInfo.getServerAuthKey(url,realm,authScheme);
ret=AuthenticationInfo.getServerAuth(serverAuthKey);
InetAddress addr=null;
if (ret == null) {
try {
addr=InetAddress.getByName(url.getHost());
}
 catch (java.net.UnknownHostException ignored) {
}
}
int port=url.getPort();
if (port == -1) {
port=url.getDefaultPort();
}
if (ret == null) {
switch (authScheme) {
case KERBEROS:
ret=new NegotiateAuthentication(new HttpCallerInfo(authhdr.getHttpCallerInfo(),"Kerberos"));
break;
case NEGOTIATE:
ret=new NegotiateAuthentication(new HttpCallerInfo(authhdr.getHttpCallerInfo(),"Negotiate"));
break;
case BASIC:
PasswordAuthentication a=privilegedRequestPasswordAuthentication(url.getHost(),addr,port,url.getProtocol(),realm,scheme,url,RequestorType.SERVER);
if (a != null) {
ret=new BasicAuthentication(false,url,realm,a);
}
break;
case DIGEST:
a=privilegedRequestPasswordAuthentication(url.getHost(),addr,port,url.getProtocol(),realm,scheme,url,RequestorType.SERVER);
if (a != null) {
digestparams=new DigestAuthentication.Parameters();
ret=new DigestAuthentication(false,url,realm,scheme,a,digestparams);
}
break;
case NTLM:
if (NTLMAuthenticationProxy.proxy.supported) {
URL url1;
try {
url1=new URL(url,"/");
}
 catch (Exception e) {
url1=url;
}
if (tryTransparentNTLMServer) {
tryTransparentNTLMServer=NTLMAuthenticationProxy.proxy.supportsTransparentAuth;
if (tryTransparentNTLMServer) {
tryTransparentNTLMServer=NTLMAuthenticationProxy.proxy.isTrustedSite(url);
}
}
a=null;
if (tryTransparentNTLMServer) {
logger.finest("Trying Transparent NTLM authentication");
}
 else {
a=privilegedRequestPasswordAuthentication(url.getHost(),addr,port,url.getProtocol(),"",scheme,url,RequestorType.SERVER);
}
if (tryTransparentNTLMServer || (!tryTransparentNTLMServer && a != null)) {
ret=NTLMAuthenticationProxy.proxy.create(false,url1,a);
}
tryTransparentNTLMServer=false;
}
break;
case UNKNOWN:
logger.finest("Unknown/Unsupported authentication scheme: " + scheme);
default :
throw new AssertionError("should not reach here");
}
}
if (ret == null && defaultAuth != null && defaultAuth.schemeSupported(scheme)) {
String a=defaultAuth.authString(url,scheme,realm);
if (a != null) {
ret=new BasicAuthentication(false,url,realm,a);
}
}
if (ret != null) {
if (!ret.setHeaders(this,p,raw)) {
ret=null;
}
}
}
if (logger.isLoggable(PlatformLogger.FINER)) {
logger.finer("Server Authentication for " + authhdr.toString() + " returned "+ (ret != null ? ret.toString() : "null"));
}
return ret;
}
private void checkResponseCredentials(boolean inClose) throws IOException {
try {
if (!needToCheck) return;
if ((validateProxy && currentProxyCredentials != null) && (currentProxyCredentials instanceof DigestAuthentication)) {
String raw=responses.findValue("Proxy-Authentication-Info");
if (inClose || (raw != null)) {
DigestAuthentication da=(DigestAuthentication)currentProxyCredentials;
da.checkResponse(raw,method,getRequestURI());
currentProxyCredentials=null;
}
}
if ((validateServer && currentServerCredentials != null) && (currentServerCredentials instanceof DigestAuthentication)) {
String raw=responses.findValue("Authentication-Info");
if (inClose || (raw != null)) {
DigestAuthentication da=(DigestAuthentication)currentServerCredentials;
da.checkResponse(raw,method,url);
currentServerCredentials=null;
}
}
if ((currentServerCredentials == null) && (currentProxyCredentials == null)) {
needToCheck=false;
}
}
 catch (IOException e) {
disconnectInternal();
connected=false;
throw e;
}
}
String requestURI=null;
String getRequestURI() throws IOException {
if (requestURI == null) {
requestURI=http.getURLFile();
}
return requestURI;
}
private boolean followRedirect() throws IOException {
if (!getInstanceFollowRedirects()) {
return false;
}
int stat=getResponseCode();
if (stat < 300 || stat > 307 || stat == 306 || stat == HTTP_NOT_MODIFIED) {
return false;
}
String loc=getHeaderField("Location");
if (loc == null) {
return false;
}
URL locUrl;
try {
locUrl=new URL(loc);
if (!url.getProtocol().equalsIgnoreCase(locUrl.getProtocol())) {
return false;
}
}
 catch (MalformedURLException mue) {
locUrl=new URL(url,loc);
}
disconnectInternal();
if (streaming()) {
throw new HttpRetryException(RETRY_MSG3,stat,loc);
}
if (logger.isLoggable(PlatformLogger.FINE)) {
logger.fine("Redirected from " + url + " to "+ locUrl);
}
responses=new MessageHeader();
if (stat == HTTP_USE_PROXY) {
String proxyHost=locUrl.getHost();
int proxyPort=locUrl.getPort();
SecurityManager security=System.getSecurityManager();
if (security != null) {
security.checkConnect(proxyHost,proxyPort);
}
setProxiedClient(url,proxyHost,proxyPort);
requests.set(0,method + " " + getRequestURI()+ " "+ httpVersion,null);
connected=true;
}
 else {
url=locUrl;
requestURI=null;
if (method.equals("POST") && !Boolean.getBoolean("http.strictPostRedirect") && (stat != 307)) {
requests=new MessageHeader();
setRequests=false;
setRequestMethod("GET");
poster=null;
if (!checkReuseConnection()) connect();
}
 else {
if (!checkReuseConnection()) connect();
if (http != null) {
requests.set(0,method + " " + getRequestURI()+ " "+ httpVersion,null);
int port=url.getPort();
String host=url.getHost();
if (port != -1 && port != url.getDefaultPort()) {
host+=":" + String.valueOf(port);
}
requests.set("Host",host);
}
}
}
return true;
}
byte[] cdata=new byte[128];
/** 
 * Reset (without disconnecting the TCP conn) in order to do another transaction with this instance
 */
private void reset() throws IOException {
http.reuse=true;
reuseClient=http;
InputStream is=http.getInputStream();
if (!method.equals("HEAD")) {
try {
if ((is instanceof ChunkedInputStream) || (is instanceof MeteredStream)) {
while (is.read(cdata) > 0) {
}
}
 else {
long cl=0;
int n=0;
String cls=responses.findValue("Content-Length");
if (cls != null) {
try {
cl=Long.parseLong(cls);
}
 catch (NumberFormatException e) {
cl=0;
}
}
for (long i=0; i < cl; ) {
if ((n=is.read(cdata)) == -1) {
break;
}
 else {
i+=n;
}
}
}
}
 catch (IOException e) {
http.reuse=false;
reuseClient=null;
disconnectInternal();
return;
}
try {
if (is instanceof MeteredStream) {
is.close();
}
}
 catch (IOException e) {
}
}
responseCode=-1;
responses=new MessageHeader();
connected=false;
}
/** 
 * Disconnect from the web server at the first 401 error. Do not
 * disconnect when using a proxy, a good proxy should have already
 * closed the connection to the web server.
 */
private void disconnectWeb() throws IOException {
if (usingProxy() && http.isKeepingAlive()) {
responseCode=-1;
reset();
}
 else {
disconnectInternal();
}
}
/** 
 * Disconnect from the server (for internal use)
 */
private void disconnectInternal(){
responseCode=-1;
inputStream=null;
if (pi != null) {
pi.finishTracking();
pi=null;
}
if (http != null) {
http.closeServer();
http=null;
connected=false;
}
}
/** 
 * Disconnect from the server (public API)
 */
public void disconnect(){
responseCode=-1;
if (pi != null) {
pi.finishTracking();
pi=null;
}
if (http != null) {
if (inputStream != null) {
HttpClient hc=http;
boolean ka=hc.isKeepingAlive();
try {
inputStream.close();
}
 catch (IOException ioe) {
}
if (ka) {
hc.closeIdleConnection();
}
}
 else {
http.setDoNotRetry(true);
http.closeServer();
}
http=null;
connected=false;
}
cachedInputStream=null;
if (cachedHeaders != null) {
cachedHeaders.reset();
}
}
public boolean usingProxy(){
if (http != null) {
return (http.getProxyHostUsed() != null);
}
return false;
}
/** 
 * Gets a header field by name. Returns null if not known.
 * @param name the name of the header field
 */
@Override public String getHeaderField(String name){
try {
getInputStream();
}
 catch (IOException e) {
}
if (cachedHeaders != null) {
return cachedHeaders.findValue(name);
}
return responses.findValue(name);
}
/** 
 * Returns an unmodifiable Map of the header fields.
 * The Map keys are Strings that represent the
 * response-header field names. Each Map value is an
 * unmodifiable List of Strings that represents
 * the corresponding field values.
 * @return a Map of header fields
 * @since 1.4
 */
@Override public Map<String,List<String>> getHeaderFields(){
try {
getInputStream();
}
 catch (IOException e) {
}
if (cachedHeaders != null) {
return cachedHeaders.getHeaders();
}
return responses.getHeaders();
}
/** 
 * Gets a header field by index. Returns null if not known.
 * @param n the index of the header field
 */
@Override public String getHeaderField(int n){
try {
getInputStream();
}
 catch (IOException e) {
}
if (cachedHeaders != null) {
return cachedHeaders.getValue(n);
}
return responses.getValue(n);
}
/** 
 * Gets a header field by index. Returns null if not known.
 * @param n the index of the header field
 */
@Override public String getHeaderFieldKey(int n){
try {
getInputStream();
}
 catch (IOException e) {
}
if (cachedHeaders != null) {
return cachedHeaders.getKey(n);
}
return responses.getKey(n);
}
/** 
 * Sets request property. If a property with the key already
 * exists, overwrite its value with the new value.
 * @param value the value to be set
 */
@Override public void setRequestProperty(String key,String value){
if (connected) throw new IllegalStateException("Already connected");
if (key == null) throw new NullPointerException("key is null");
if (isExternalMessageHeaderAllowed(key,value)) {
requests.set(key,value);
}
}
/** 
 * Adds a general request property specified by a
 * key-value pair.  This method will not overwrite
 * existing values associated with the same key.
 * @param key     the keyword by which the request is known
 * (e.g., "<code>accept</code>").
 * @param value  the value associated with it.
 * @see #getRequestProperties(java.lang.String)
 * @since 1.4
 */
@Override public void addRequestProperty(String key,String value){
if (connected) throw new IllegalStateException("Already connected");
if (key == null) throw new NullPointerException("key is null");
if (isExternalMessageHeaderAllowed(key,value)) {
requests.add(key,value);
}
}
public void setAuthenticationProperty(String key,String value){
checkMessageHeader(key,value);
requests.set(key,value);
}
@Override public synchronized String getRequestProperty(String key){
if (key == null) {
return null;
}
for (int i=0; i < EXCLUDE_HEADERS.length; i++) {
if (key.equalsIgnoreCase(EXCLUDE_HEADERS[i])) {
return null;
}
}
if (!setUserCookies) {
if (key.equalsIgnoreCase("Cookie")) {
return userCookies;
}
if (key.equalsIgnoreCase("Cookie2")) {
return userCookies2;
}
}
return requests.findValue(key);
}
/** 
 * Returns an unmodifiable Map of general request
 * properties for this connection. The Map keys
 * are Strings that represent the request-header
 * field names. Each Map value is a unmodifiable List
 * of Strings that represents the corresponding
 * field values.
 * @return  a Map of the general request properties for this connection.
 * @throws IllegalStateException if already connected
 * @since 1.4
 */
@Override public synchronized Map<String,List<String>> getRequestProperties(){
if (connected) throw new IllegalStateException("Already connected");
if (setUserCookies) {
return requests.getHeaders(EXCLUDE_HEADERS);
}
Map userCookiesMap=null;
if (userCookies != null || userCookies2 != null) {
userCookiesMap=new HashMap();
if (userCookies != null) {
userCookiesMap.put("Cookie",userCookies);
}
if (userCookies2 != null) {
userCookiesMap.put("Cookie2",userCookies2);
}
}
return requests.filterAndAddHeaders(EXCLUDE_HEADERS2,userCookiesMap);
}
@Override public void setConnectTimeout(int timeout){
if (timeout < 0) throw new IllegalArgumentException("timeouts can't be negative");
connectTimeout=timeout;
}
/** 
 * Returns setting for connect timeout.
 * <p>
 * 0 return implies that the option is disabled
 * (i.e., timeout of infinity).
 * @return an <code>int</code> that indicates the connect timeout
 * value in milliseconds
 * @see java.net.URLConnection#setConnectTimeout(int)
 * @see java.net.URLConnection#connect()
 * @since 1.5
 */
@Override public int getConnectTimeout(){
return (connectTimeout < 0 ? 0 : connectTimeout);
}
/** 
 * Sets the read timeout to a specified timeout, in
 * milliseconds. A non-zero value specifies the timeout when
 * reading from Input stream when a connection is established to a
 * resource. If the timeout expires before there is data available
 * for read, a java.net.SocketTimeoutException is raised. A
 * timeout of zero is interpreted as an infinite timeout.
 * <p> Some non-standard implementation of this method ignores the
 * specified timeout. To see the read timeout set, please call
 * getReadTimeout().
 * @param timeout an <code>int</code> that specifies the timeout
 * value to be used in milliseconds
 * @throws IllegalArgumentException if the timeout parameter is negative
 * @see java.net.URLConnectiongetReadTimeout()
 * @see java.io.InputStream#read()
 * @since 1.5
 */
@Override public void setReadTimeout(int timeout){
if (timeout < 0) throw new IllegalArgumentException("timeouts can't be negative");
readTimeout=timeout;
}
/** 
 * Returns setting for read timeout. 0 return implies that the
 * option is disabled (i.e., timeout of infinity).
 * @return an <code>int</code> that indicates the read timeout
 * value in milliseconds
 * @see java.net.URLConnection#setReadTimeout(int)
 * @see java.io.InputStream#read()
 * @since 1.5
 */
@Override public int getReadTimeout(){
return readTimeout < 0 ? 0 : readTimeout;
}
String getMethod(){
return method;
}
private MessageHeader mapToMessageHeader(Map<String,List<String>> map){
MessageHeader headers=new MessageHeader();
if (map == null || map.isEmpty()) {
return headers;
}
for (Map.Entry<String,List<String>> entry : map.entrySet()) {
String key=entry.getKey();
List<String> values=entry.getValue();
for (String value : values) {
if (key == null) {
headers.prepend(key,value);
}
 else {
headers.add(key,value);
}
}
}
return headers;
}
class HttpInputStream extends FilterInputStream {
private CacheRequest cacheRequest;
private OutputStream outputStream;
private boolean marked=false;
private int inCache=0;
private int markCount=0;
public HttpInputStream(InputStream is){
super(is);
this.cacheRequest=null;
this.outputStream=null;
}
public HttpInputStream(InputStream is,CacheRequest cacheRequest){
super(is);
this.cacheRequest=cacheRequest;
try {
this.outputStream=cacheRequest.getBody();
}
 catch (IOException ioex) {
this.cacheRequest.abort();
this.cacheRequest=null;
this.outputStream=null;
}
}
/** 
 * Marks the current position in this input stream. A subsequent
 * call to the <code>reset</code> method repositions this stream at
 * the last marked position so that subsequent reads re-read the same
 * bytes.
 * <p>
 * The <code>readlimit</code> argument tells this input stream to
 * allow that many bytes to be read before the mark position gets
 * invalidated.
 * <p>
 * This method simply performs <code>in.mark(readlimit)</code>.
 * @param readlimit   the maximum limit of bytes that can be read before
 * the mark position becomes invalid.
 * @see java.io.FilterInputStream#in
 * @see java.io.FilterInputStream#reset()
 */
@Override public synchronized void mark(int readlimit){
super.mark(readlimit);
if (cacheRequest != null) {
marked=true;
markCount=0;
}
}
/** 
 * Repositions this stream to the position at the time the
 * <code>mark</code> method was last called on this input stream.
 * <p>
 * This method
 * simply performs <code>in.reset()</code>.
 * <p>
 * Stream marks are intended to be used in
 * situations where you need to read ahead a little to see what's in
 * the stream. Often this is most easily done by invoking some
 * general parser. If the stream is of the type handled by the
 * parse, it just chugs along happily. If the stream is not of
 * that type, the parser should toss an exception when it fails.
 * If this happens within readlimit bytes, it allows the outer
 * code to reset the stream and try another parser.
 * @exception IOException  if the stream has not been marked or if the
 * mark has been invalidated.
 * @see java.io.FilterInputStream#in
 * @see java.io.FilterInputStream#mark(int)
 */
@Override public synchronized void reset() throws IOException {
super.reset();
if (cacheRequest != null) {
marked=false;
inCache+=markCount;
}
}
@Override public int read() throws IOException {
try {
byte[] b=new byte[1];
int ret=read(b);
return (ret == -1 ? ret : (b[0] & 0x00FF));
}
 catch (IOException ioex) {
if (cacheRequest != null) {
cacheRequest.abort();
}
throw ioex;
}
}
@Override public int read(byte[] b) throws IOException {
return read(b,0,b.length);
}
@Override public int read(byte[] b,int off,int len) throws IOException {
try {
int newLen=super.read(b,off,len);
int nWrite;
if (inCache > 0) {
if (inCache >= newLen) {
inCache-=newLen;
nWrite=0;
}
 else {
nWrite=newLen - inCache;
inCache=0;
}
}
 else {
nWrite=newLen;
}
if (nWrite > 0 && outputStream != null) outputStream.write(b,off + (newLen - nWrite),nWrite);
if (marked) {
markCount+=newLen;
}
return newLen;
}
 catch (IOException ioex) {
if (cacheRequest != null) {
cacheRequest.abort();
}
throw ioex;
}
}
private byte[] skipBuffer;
private static final int SKIP_BUFFER_SIZE=8096;
@Override public long skip(long n) throws IOException {
long remaining=n;
int nr;
if (skipBuffer == null) skipBuffer=new byte[SKIP_BUFFER_SIZE];
byte[] localSkipBuffer=skipBuffer;
if (n <= 0) {
return 0;
}
while (remaining > 0) {
nr=read(localSkipBuffer,0,(int)Math.min(SKIP_BUFFER_SIZE,remaining));
if (nr < 0) {
break;
}
remaining-=nr;
}
return n - remaining;
}
@Override public void close() throws IOException {
try {
if (outputStream != null) {
if (read() != -1) {
cacheRequest.abort();
}
 else {
outputStream.close();
}
}
super.close();
}
 catch (IOException ioex) {
if (cacheRequest != null) {
cacheRequest.abort();
}
throw ioex;
}
 finally {
HttpURLConnection.this.http=null;
checkResponseCredentials(true);
}
}
}
class StreamingOutputStream extends FilterOutputStream {
long expected;
long written;
boolean closed;
boolean error;
IOException errorExcp;
/** 
 * expectedLength == -1 if the stream is chunked
 * expectedLength > 0 if the stream is fixed content-length
 * In the 2nd case, we make sure the expected number of
 * of bytes are actually written
 */
StreamingOutputStream(OutputStream os,long expectedLength){
super(os);
expected=expectedLength;
written=0L;
closed=false;
error=false;
}
@Override public void write(int b) throws IOException {
checkError();
written++;
if (expected != -1L && written > expected) {
throw new IOException("too many bytes written");
}
out.write(b);
}
@Override public void write(byte[] b) throws IOException {
write(b,0,b.length);
}
@Override public void write(byte[] b,int off,int len) throws IOException {
checkError();
written+=len;
if (expected != -1L && written > expected) {
out.close();
throw new IOException("too many bytes written");
}
out.write(b,off,len);
}
void checkError() throws IOException {
if (closed) {
throw new IOException("Stream is closed");
}
if (error) {
throw errorExcp;
}
if (((PrintStream)out).checkError()) {
throw new IOException("Error writing request body to server");
}
}
boolean writtenOK(){
return closed && !error;
}
@Override public void close() throws IOException {
if (closed) {
return;
}
closed=true;
if (expected != -1L) {
if (written != expected) {
error=true;
errorExcp=new IOException("insufficient data written");
out.close();
throw errorExcp;
}
super.flush();
}
 else {
super.close();
OutputStream o=http.getOutputStream();
o.write('\r');
o.write('\n');
o.flush();
}
}
}
static class ErrorStream extends InputStream {
ByteBuffer buffer;
InputStream is;
private ErrorStream(ByteBuffer buf){
buffer=buf;
is=null;
}
private ErrorStream(ByteBuffer buf,InputStream is){
buffer=buf;
this.is=is;
}
public static InputStream getErrorStream(InputStream is,long cl,HttpClient http){
if (cl == 0) {
return null;
}
try {
int oldTimeout=http.getReadTimeout();
http.setReadTimeout(timeout4ESBuffer / 5);
long expected=0;
boolean isChunked=false;
if (cl < 0) {
expected=bufSize4ES;
isChunked=true;
}
 else {
expected=cl;
}
if (expected <= bufSize4ES) {
int exp=(int)expected;
byte[] buffer=new byte[exp];
int count=0, time=0, len=0;
do {
try {
len=is.read(buffer,count,buffer.length - count);
if (len < 0) {
if (isChunked) {
break;
}
throw new IOException("the server closes" + " before sending " + cl + " bytes of data");
}
count+=len;
}
 catch (SocketTimeoutException ex) {
time+=timeout4ESBuffer / 5;
}
}
 while (count < exp && time < timeout4ESBuffer);
http.setReadTimeout(oldTimeout);
if (count == 0) {
return null;
}
 else if ((count == expected && !(isChunked)) || (isChunked && len < 0)) {
is.close();
return new ErrorStream(ByteBuffer.wrap(buffer,0,count));
}
 else {
return new ErrorStream(ByteBuffer.wrap(buffer,0,count),is);
}
}
return null;
}
 catch (IOException ioex) {
return null;
}
}
@Override public int available() throws IOException {
if (is == null) {
return buffer.remaining();
}
 else {
return buffer.remaining() + is.available();
}
}
public int read() throws IOException {
byte[] b=new byte[1];
int ret=read(b);
return (ret == -1 ? ret : (b[0] & 0x00FF));
}
@Override public int read(byte[] b) throws IOException {
return read(b,0,b.length);
}
@Override public int read(byte[] b,int off,int len) throws IOException {
int rem=buffer.remaining();
if (rem > 0) {
int ret=rem < len ? rem : len;
buffer.get(b,off,ret);
return ret;
}
 else {
if (is == null) {
return -1;
}
 else {
return is.read(b,off,len);
}
}
}
@Override public void close() throws IOException {
buffer=null;
if (is != null) {
is.close();
}
}
}
}
/** 
 * An input stream that just returns EOF.  This is for
 * HTTP URLConnections that are KeepAlive && use the
 * HEAD method - i.e., stream not dead, but nothing to be read.
 */
class EmptyInputStream extends InputStream {
@Override public int available(){
return 0;
}
public int read(){
return -1;
}
}
