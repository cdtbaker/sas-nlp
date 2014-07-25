package sun.net.www.protocol.http;
import java.net.URL;
import java.io.IOException;
import java.net.Authenticator.RequestorType;
import java.util.HashMap;
import sun.net.www.HeaderParser;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import static sun.net.www.protocol.http.AuthScheme.NEGOTIATE;
import static sun.net.www.protocol.http.AuthScheme.KERBEROS;
/** 
 * NegotiateAuthentication:
 * @author weijun.wang@sun.com
 * @since 1.6
 */
class NegotiateAuthentication extends AuthenticationInfo {
  private static final long serialVersionUID=100L;
  final private HttpCallerInfo hci;
  static HashMap<String,Boolean> supported=null;
  static HashMap<String,Negotiator> cache=null;
  private Negotiator negotiator=null;
  /** 
 * Constructor used for both WWW and proxy entries.
 * @param hci a schemed object.
 */
  public NegotiateAuthentication(  HttpCallerInfo hci){
    super(RequestorType.PROXY == hci.authType ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,hci.scheme.equalsIgnoreCase("Negotiate") ? NEGOTIATE : KERBEROS,hci.url,"");
    this.hci=hci;
  }
  /** 
 * @return true if this authentication supports preemptive authorization
 */
  @Override public boolean supportsPreemptiveAuthorization(){
    return false;
  }
  /** 
 * Find out if the HttpCallerInfo supports Negotiate protocol. In order to
 * find out yes or no, an initialization of a Negotiator object against it
 * is tried. The generated object will be cached under the name of ths
 * hostname at a success try.<br>
 * If this method is called for the second time on an HttpCallerInfo with
 * the same hostname, the answer is retrieved from cache.
 * @return true if supported
 */
  synchronized public static boolean isSupported(  HttpCallerInfo hci){
    if (supported == null) {
      supported=new HashMap<String,Boolean>();
      cache=new HashMap<String,Negotiator>();
    }
    String hostname=hci.host;
    hostname=hostname.toLowerCase();
    if (supported.containsKey(hostname)) {
      return supported.get(hostname);
    }
    Negotiator neg=Negotiator.getNegotiator(hci);
    if (neg != null) {
      supported.put(hostname,true);
      cache.put(hostname,neg);
      return true;
    }
 else {
      supported.put(hostname,false);
      return false;
    }
  }
  /** 
 * Not supported. Must use the setHeaders() method
 */
  @Override public String getHeaderValue(  URL url,  String method){
    throw new RuntimeException("getHeaderValue not supported");
  }
  /** 
 * Check if the header indicates that the current auth. parameters are stale.
 * If so, then replace the relevant field with the new value
 * and return true. Otherwise return false.
 * returning true means the request can be retried with the same userid/password
 * returning false means we have to go back to the user to ask for a new
 * username password.
 */
  @Override public boolean isAuthorizationStale(  String header){
    return false;
  }
  /** 
 * Set header(s) on the given connection.
 * @param conn The connection to apply the header(s) to
 * @param p A source of header values for this connection, not used because
 * HeaderParser converts the fields to lower case, use raw instead
 * @param raw The raw header field.
 * @return true if all goes well, false if no headers were set.
 */
  @Override public synchronized boolean setHeaders(  HttpURLConnection conn,  HeaderParser p,  String raw){
    try {
      String response;
      byte[] incoming=null;
      String[] parts=raw.split("\\s+");
      if (parts.length > 1) {
        incoming=new BASE64Decoder().decodeBuffer(parts[1]);
      }
      response=hci.scheme + " " + new B64Encoder().encode(incoming == null ? firstToken() : nextToken(incoming));
      conn.setAuthenticationProperty(getHeaderName(),response);
      return true;
    }
 catch (    IOException e) {
      return false;
    }
  }
  /** 
 * return the first token.
 * @returns the token
 * @throws IOException if <code>Negotiator.getNegotiator()</code> or
 * <code>Negotiator.firstToken()</code> failed.
 */
  private byte[] firstToken() throws IOException {
    negotiator=null;
    if (cache != null) {
synchronized (cache) {
        negotiator=cache.get(getHost());
        if (negotiator != null) {
          cache.remove(getHost());
        }
      }
    }
    if (negotiator == null) {
      negotiator=Negotiator.getNegotiator(hci);
      if (negotiator == null) {
        IOException ioe=new IOException("Cannot initialize Negotiator");
        throw ioe;
      }
    }
    return negotiator.firstToken();
  }
  /** 
 * return more tokens
 * @param token the token to be fed into <code>negotiator.nextToken()</code>
 * @returns the token
 * @throws IOException if <code>negotiator.nextToken()</code> throws Exception.
 * May happen if the input token is invalid.
 */
  private byte[] nextToken(  byte[] token) throws IOException {
    return negotiator.nextToken(token);
  }
class B64Encoder extends BASE64Encoder {
    protected int bytesPerLine(){
      return 100000;
    }
  }
}
