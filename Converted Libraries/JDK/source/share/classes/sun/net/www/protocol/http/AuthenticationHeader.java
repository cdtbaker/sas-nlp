package sun.net.www.protocol.http;
import sun.net.www.*;
import java.util.Iterator;
import java.util.HashMap;
/** 
 * This class is used to parse the information in WWW-Authenticate: and Proxy-Authenticate:
 * headers. It searches among multiple header lines and within each header line
 * for the best currently supported scheme. It can also return a HeaderParser
 * containing the challenge data for that particular scheme.
 * Some examples:
 * WWW-Authenticate: Basic realm="foo" Digest realm="bar" NTLM
 * Note the realm parameter must be associated with the particular scheme.
 * or
 * WWW-Authenticate: Basic realm="foo"
 * WWW-Authenticate: Digest realm="foo",qop="auth",nonce="thisisanunlikelynonce"
 * WWW-Authenticate: NTLM
 * or
 * WWW-Authenticate: Basic realm="foo"
 * WWW-Authenticate: NTLM ASKAJK9893289889QWQIOIONMNMN
 * The last example shows how NTLM breaks the rules of rfc2617 for the structure of
 * the authentication header. This is the reason why the raw header field is used for ntlm.
 * At present, the class chooses schemes in following order :
 * 1. Negotiate (if supported)
 * 2. Kerberos (if supported)
 * 3. Digest
 * 4. NTLM (if supported)
 * 5. Basic
 * This choice can be modified by setting a system property:
 * -Dhttp.auth.preference="scheme"
 * which in this case, specifies that "scheme" should be used as the auth scheme when offered
 * disregarding the default prioritisation. If scheme is not offered then the default priority
 * is used.
 * Attention: when http.auth.preference is set as SPNEGO or Kerberos, it's actually "Negotiate
 * with SPNEGO" or "Negotiate with Kerberos", which means the user will prefer the Negotiate
 * scheme with GSS/SPNEGO or GSS/Kerberos mechanism.
 * This also means that the real "Kerberos" scheme can never be set as a preference.
 */
public class AuthenticationHeader {
  MessageHeader rsp;
  HeaderParser preferred;
  String preferred_r;
  private final HttpCallerInfo hci;
  boolean dontUseNegotiate=false;
  static String authPref=null;
  public String toString(){
    return "AuthenticationHeader: prefer " + preferred_r;
  }
static {
    authPref=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("http.auth.preference"));
    if (authPref != null) {
      authPref=authPref.toLowerCase();
      if (authPref.equals("spnego") || authPref.equals("kerberos")) {
        authPref="negotiate";
      }
    }
  }
  String hdrname;
  /** 
 * parse a set of authentication headers and choose the preferred scheme
 * that we support for a given host
 */
  public AuthenticationHeader(  String hdrname,  MessageHeader response,  HttpCallerInfo hci,  boolean dontUseNegotiate){
    this.hci=hci;
    this.dontUseNegotiate=dontUseNegotiate;
    rsp=response;
    this.hdrname=hdrname;
    schemes=new HashMap();
    parse();
  }
  public HttpCallerInfo getHttpCallerInfo(){
    return hci;
  }
static class SchemeMapValue {
    SchemeMapValue(    HeaderParser h,    String r){
      raw=r;
      parser=h;
    }
    String raw;
    HeaderParser parser;
  }
  HashMap schemes;
  private void parse(){
    Iterator iter=rsp.multiValueIterator(hdrname);
    while (iter.hasNext()) {
      String raw=(String)iter.next();
      HeaderParser hp=new HeaderParser(raw);
      Iterator keys=hp.keys();
      int i, lastSchemeIndex;
      for (i=0, lastSchemeIndex=-1; keys.hasNext(); i++) {
        keys.next();
        if (hp.findValue(i) == null) {
          if (lastSchemeIndex != -1) {
            HeaderParser hpn=hp.subsequence(lastSchemeIndex,i);
            String scheme=hpn.findKey(0);
            schemes.put(scheme,new SchemeMapValue(hpn,raw));
          }
          lastSchemeIndex=i;
        }
      }
      if (i > lastSchemeIndex) {
        HeaderParser hpn=hp.subsequence(lastSchemeIndex,i);
        String scheme=hpn.findKey(0);
        schemes.put(scheme,new SchemeMapValue(hpn,raw));
      }
    }
    SchemeMapValue v=null;
    if (authPref == null || (v=(SchemeMapValue)schemes.get(authPref)) == null) {
      if (v == null && !dontUseNegotiate) {
        SchemeMapValue tmp=(SchemeMapValue)schemes.get("negotiate");
        if (tmp != null) {
          if (hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(hci,"Negotiate"))) {
            tmp=null;
          }
          v=tmp;
        }
      }
      if (v == null && !dontUseNegotiate) {
        SchemeMapValue tmp=(SchemeMapValue)schemes.get("kerberos");
        if (tmp != null) {
          if (hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(hci,"Kerberos"))) {
            tmp=null;
          }
          v=tmp;
        }
      }
      if (v == null) {
        if ((v=(SchemeMapValue)schemes.get("digest")) == null) {
          if (((v=(SchemeMapValue)schemes.get("ntlm")) == null)) {
            v=(SchemeMapValue)schemes.get("basic");
          }
        }
      }
    }
 else {
      if (dontUseNegotiate && authPref.equals("negotiate")) {
        v=null;
      }
    }
    if (v != null) {
      preferred=v.parser;
      preferred_r=v.raw;
    }
  }
  /** 
 * return a header parser containing the preferred authentication scheme (only).
 * The preferred scheme is the strongest of the schemes proposed by the server.
 * The returned HeaderParser will contain the relevant parameters for that scheme
 */
  public HeaderParser headerParser(){
    return preferred;
  }
  /** 
 * return the name of the preferred scheme
 */
  public String scheme(){
    if (preferred != null) {
      return preferred.findKey(0);
    }
 else {
      return null;
    }
  }
  public String raw(){
    return preferred_r;
  }
  /** 
 * returns true is the header exists and contains a recognised scheme
 */
  public boolean isPresent(){
    return preferred != null;
  }
}