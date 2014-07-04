package com.sun.net.ssl.internal.www.protocol.https;
import java.net.URL;
import java.net.Proxy;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.security.Principal;
import java.security.cert.*;
import javax.security.auth.x500.X500Principal;
import sun.security.util.HostnameChecker;
import sun.security.util.DerValue;
import sun.security.x509.X500Name;
import sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection;
/** 
 * This class was introduced to provide an additional level of
 * abstraction between javax.net.ssl.HttpURLConnection and
 * com.sun.net.ssl.HttpURLConnection objects. <p>
 * javax.net.ssl.HttpURLConnection is used in the new sun.net version
 * of protocol implementation (this one)
 * com.sun.net.ssl.HttpURLConnection is used in the com.sun version.
 */
public class DelegateHttpsURLConnection extends AbstractDelegateHttpsURLConnection {
  public com.sun.net.ssl.HttpsURLConnection httpsURLConnection;
  DelegateHttpsURLConnection(  URL url,  sun.net.www.protocol.http.Handler handler,  com.sun.net.ssl.HttpsURLConnection httpsURLConnection) throws IOException {
    this(url,null,handler,httpsURLConnection);
  }
  DelegateHttpsURLConnection(  URL url,  Proxy p,  sun.net.www.protocol.http.Handler handler,  com.sun.net.ssl.HttpsURLConnection httpsURLConnection) throws IOException {
    super(url,p,handler);
    this.httpsURLConnection=httpsURLConnection;
  }
  protected javax.net.ssl.SSLSocketFactory getSSLSocketFactory(){
    return httpsURLConnection.getSSLSocketFactory();
  }
  protected javax.net.ssl.HostnameVerifier getHostnameVerifier(){
    return new VerifierWrapper(httpsURLConnection.getHostnameVerifier());
  }
  protected void dispose() throws Throwable {
    super.finalize();
  }
}
class VerifierWrapper implements javax.net.ssl.HostnameVerifier {
  private com.sun.net.ssl.HostnameVerifier verifier;
  VerifierWrapper(  com.sun.net.ssl.HostnameVerifier verifier){
    this.verifier=verifier;
  }
  public boolean verify(  String hostname,  javax.net.ssl.SSLSession session){
    try {
      String serverName;
      if (session.getCipherSuite().startsWith("TLS_KRB5")) {
        serverName=HostnameChecker.getServerName(getPeerPrincipal(session));
      }
 else {
        Certificate[] serverChain=session.getPeerCertificates();
        if ((serverChain == null) || (serverChain.length == 0)) {
          return false;
        }
        if (serverChain[0] instanceof X509Certificate == false) {
          return false;
        }
        X509Certificate serverCert=(X509Certificate)serverChain[0];
        serverName=getServername(serverCert);
      }
      if (serverName == null) {
        return false;
      }
      return verifier.verify(hostname,serverName);
    }
 catch (    javax.net.ssl.SSLPeerUnverifiedException e) {
      return false;
    }
  }
  private Principal getPeerPrincipal(  javax.net.ssl.SSLSession session) throws javax.net.ssl.SSLPeerUnverifiedException {
    Principal principal;
    try {
      principal=session.getPeerPrincipal();
    }
 catch (    AbstractMethodError e) {
      principal=null;
    }
    return principal;
  }
  private static String getServername(  X509Certificate peerCert){
    try {
      Collection subjAltNames=peerCert.getSubjectAlternativeNames();
      if (subjAltNames != null) {
        for (Iterator itr=subjAltNames.iterator(); itr.hasNext(); ) {
          List next=(List)itr.next();
          if (((Integer)next.get(0)).intValue() == 2) {
            String dnsName=((String)next.get(1));
            return dnsName;
          }
        }
      }
      X500Name subject=HostnameChecker.getSubjectX500Name(peerCert);
      DerValue derValue=subject.findMostSpecificAttribute(X500Name.commonName_oid);
      if (derValue != null) {
        try {
          String name=derValue.getAsString();
          return name;
        }
 catch (        IOException e) {
        }
      }
    }
 catch (    java.security.cert.CertificateException e) {
    }
    return null;
  }
}
