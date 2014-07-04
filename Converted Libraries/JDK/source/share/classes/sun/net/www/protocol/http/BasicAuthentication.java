package sun.net.www.protocol.http;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.PasswordAuthentication;
import java.io.IOException;
import java.io.OutputStream;
import sun.net.www.HeaderParser;
import sun.misc.BASE64Encoder;
/** 
 * BasicAuthentication: Encapsulate an http server authentication using
 * the "basic" scheme.
 * @author Bill Foote
 */
class BasicAuthentication extends AuthenticationInfo {
  private static final long serialVersionUID=100L;
  /** 
 * The authentication string for this host, port, and realm.  This is
 * a simple BASE64 encoding of "login:password".    
 */
  String auth;
  /** 
 * Create a BasicAuthentication
 */
  public BasicAuthentication(  boolean isProxy,  String host,  int port,  String realm,  PasswordAuthentication pw){
    super(isProxy ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,AuthScheme.BASIC,host,port,realm);
    String plain=pw.getUserName() + ":";
    byte[] nameBytes=null;
    try {
      nameBytes=plain.getBytes("ISO-8859-1");
    }
 catch (    java.io.UnsupportedEncodingException uee) {
      assert false;
    }
    char[] passwd=pw.getPassword();
    byte[] passwdBytes=new byte[passwd.length];
    for (int i=0; i < passwd.length; i++)     passwdBytes[i]=(byte)passwd[i];
    byte[] concat=new byte[nameBytes.length + passwdBytes.length];
    System.arraycopy(nameBytes,0,concat,0,nameBytes.length);
    System.arraycopy(passwdBytes,0,concat,nameBytes.length,passwdBytes.length);
    this.auth="Basic " + (new BasicBASE64Encoder()).encode(concat);
    this.pw=pw;
  }
  /** 
 * Create a BasicAuthentication
 */
  public BasicAuthentication(  boolean isProxy,  String host,  int port,  String realm,  String auth){
    super(isProxy ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,AuthScheme.BASIC,host,port,realm);
    this.auth="Basic " + auth;
  }
  /** 
 * Create a BasicAuthentication
 */
  public BasicAuthentication(  boolean isProxy,  URL url,  String realm,  PasswordAuthentication pw){
    super(isProxy ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,AuthScheme.BASIC,url,realm);
    String plain=pw.getUserName() + ":";
    byte[] nameBytes=null;
    try {
      nameBytes=plain.getBytes("ISO-8859-1");
    }
 catch (    java.io.UnsupportedEncodingException uee) {
      assert false;
    }
    char[] passwd=pw.getPassword();
    byte[] passwdBytes=new byte[passwd.length];
    for (int i=0; i < passwd.length; i++)     passwdBytes[i]=(byte)passwd[i];
    byte[] concat=new byte[nameBytes.length + passwdBytes.length];
    System.arraycopy(nameBytes,0,concat,0,nameBytes.length);
    System.arraycopy(passwdBytes,0,concat,nameBytes.length,passwdBytes.length);
    this.auth="Basic " + (new BasicBASE64Encoder()).encode(concat);
    this.pw=pw;
  }
  /** 
 * Create a BasicAuthentication
 */
  public BasicAuthentication(  boolean isProxy,  URL url,  String realm,  String auth){
    super(isProxy ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,AuthScheme.BASIC,url,realm);
    this.auth="Basic " + auth;
  }
  /** 
 * @return true if this authentication supports preemptive authorization
 */
  @Override public boolean supportsPreemptiveAuthorization(){
    return true;
  }
  /** 
 * Set header(s) on the given connection. This will only be called for
 * definitive (i.e. non-preemptive) authorization.
 * @param conn The connection to apply the header(s) to
 * @param p A source of header values for this connection, if needed.
 * @param raw The raw header values for this connection, if needed.
 * @return true if all goes well, false if no headers were set.
 */
  @Override public boolean setHeaders(  HttpURLConnection conn,  HeaderParser p,  String raw){
    conn.setAuthenticationProperty(getHeaderName(),getHeaderValue(null,null));
    return true;
  }
  /** 
 * @return the value of the HTTP header this authentication wants set
 */
  @Override public String getHeaderValue(  URL url,  String method){
    return auth;
  }
  /** 
 * For Basic Authentication, the security parameters can never be stale.
 * In other words there is no possibility to reuse the credentials.
 * They are always either valid or invalid.
 */
  @Override public boolean isAuthorizationStale(  String header){
    return false;
  }
  /** 
 * @return the common root path between npath and path.
 * This is used to detect when we have an authentication for two
 * paths and the root of th authentication space is the common root.
 */
  static String getRootPath(  String npath,  String opath){
    int index=0;
    int toindex;
    try {
      npath=new URI(npath).normalize().getPath();
      opath=new URI(opath).normalize().getPath();
    }
 catch (    URISyntaxException e) {
    }
    while (index < opath.length()) {
      toindex=opath.indexOf('/',index + 1);
      if (toindex != -1 && opath.regionMatches(0,npath,0,toindex + 1))       index=toindex;
 else       return opath.substring(0,index + 1);
    }
    return npath;
  }
private class BasicBASE64Encoder extends BASE64Encoder {
    @Override protected int bytesPerLine(){
      return (10000);
    }
  }
}
