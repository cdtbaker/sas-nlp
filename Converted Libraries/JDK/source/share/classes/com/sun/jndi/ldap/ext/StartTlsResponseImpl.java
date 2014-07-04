package com.sun.jndi.ldap.ext;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;
import sun.security.util.HostnameChecker;
import javax.naming.*;
import javax.naming.ldap.*;
import com.sun.jndi.ldap.Connection;
/** 
 * This class implements the LDAPv3 Extended Response for StartTLS as
 * defined in
 * <a href="http://www.ietf.org/rfc/rfc2830.txt">Lightweight Directory
 * Access Protocol (v3): Extension for Transport Layer Security</a>
 * The object identifier for StartTLS is 1.3.6.1.4.1.1466.20037
 * and no extended response value is defined.
 * <p>
 * The Start TLS extended request and response are used to establish
 * a TLS connection over the existing LDAP connection associated with
 * the JNDI context on which <tt>extendedOperation()</tt> is invoked.
 * @see StartTlsRequest
 * @author Vincent Ryan
 */
final public class StartTlsResponseImpl extends StartTlsResponse {
  private static final boolean debug=false;
  private static final int DNSNAME_TYPE=2;
  private transient String hostname=null;
  private transient Connection ldapConnection=null;
  private transient InputStream originalInputStream=null;
  private transient OutputStream originalOutputStream=null;
  private transient SSLSocket sslSocket=null;
  private transient SSLSocketFactory defaultFactory=null;
  private transient SSLSocketFactory currentFactory=null;
  private transient String[] suites=null;
  private transient HostnameVerifier verifier=null;
  private transient boolean isClosed=true;
  private static final long serialVersionUID=-1126624615143411328L;
  public StartTlsResponseImpl(){
  }
  /** 
 * Overrides the default list of cipher suites enabled for use on the
 * TLS connection. The cipher suites must have already been listed by
 * <tt>SSLSocketFactory.getSupportedCipherSuites()</tt> as being supported.
 * Even if a suite has been enabled, it still might not be used because
 * the peer does not support it, or because the requisite certificates
 * (and private keys) are not available.
 * @param suites The non-null list of names of all the cipher suites to
 * enable.
 * @see #negotiate
 */
  public void setEnabledCipherSuites(  String[] suites){
    this.suites=suites;
  }
  /** 
 * Overrides the default hostname verifier used by <tt>negotiate()</tt>
 * after the TLS handshake has completed. If
 * <tt>setHostnameVerifier()</tt> has not been called before
 * <tt>negotiate()</tt> is invoked, <tt>negotiate()</tt>
 * will perform a simple case ignore match. If called after
 * <tt>negotiate()</tt>, this method does not do anything.
 * @param verifier The non-null hostname verifier callback.
 * @see #negotiate
 */
  public void setHostnameVerifier(  HostnameVerifier verifier){
    this.verifier=verifier;
  }
  /** 
 * Negotiates a TLS session using the default SSL socket factory.
 * <p>
 * This method is equivalent to <tt>negotiate(null)</tt>.
 * @return The negotiated SSL session
 * @throw IOException If an IO error was encountered while establishing
 * the TLS session.
 * @see #setEnabledCipherSuites
 * @see #setHostnameVerifier
 */
  public SSLSession negotiate() throws IOException {
    return negotiate(null);
  }
  /** 
 * Negotiates a TLS session using an SSL socket factory.
 * <p>
 * Creates an SSL socket using the supplied SSL socket factory and
 * attaches it to the existing connection. Performs the TLS handshake
 * and returns the negotiated session information.
 * <p>
 * If cipher suites have been set via <tt>setEnabledCipherSuites</tt>
 * then they are enabled before the TLS handshake begins.
 * <p>
 * Hostname verification is performed after the TLS handshake completes.
 * The default check performs a case insensitive match of the server's
 * hostname against that in the server's certificate. The server's
 * hostname is extracted from the subjectAltName in the server's
 * certificate (if present). Otherwise the value of the common name
 * attribute of the subject name is used. If a callback has
 * been set via <tt>setHostnameVerifier</tt> then that verifier is used if
 * the default check fails.
 * <p>
 * If an error occurs then the SSL socket is closed and an IOException
 * is thrown. The underlying connection remains intact.
 * @param factory The possibly null SSL socket factory to use.
 * If null, the default SSL socket factory is used.
 * @return The negotiated SSL session
 * @throw IOException If an IO error was encountered while establishing
 * the TLS session.
 * @see #setEnabledCipherSuites
 * @see #setHostnameVerifier
 */
  public SSLSession negotiate(  SSLSocketFactory factory) throws IOException {
    if (isClosed && sslSocket != null) {
      throw new IOException("TLS connection is closed.");
    }
    if (factory == null) {
      factory=getDefaultFactory();
    }
    if (debug) {
      System.out.println("StartTLS: About to start handshake");
    }
    SSLSession sslSession=startHandshake(factory).getSession();
    if (debug) {
      System.out.println("StartTLS: Completed handshake");
    }
    SSLPeerUnverifiedException verifExcep=null;
    try {
      if (verify(hostname,sslSession)) {
        isClosed=false;
        return sslSession;
      }
    }
 catch (    SSLPeerUnverifiedException e) {
      verifExcep=e;
    }
    if ((verifier != null) && verifier.verify(hostname,sslSession)) {
      isClosed=false;
      return sslSession;
    }
    close();
    sslSession.invalidate();
    if (verifExcep == null) {
      verifExcep=new SSLPeerUnverifiedException("hostname of the server '" + hostname + "' does not match the hostname in the "+ "server's certificate.");
    }
    throw verifExcep;
  }
  /** 
 * Closes the TLS connection gracefully and reverts back to the underlying
 * connection.
 * @throw IOException If an IO error was encountered while closing the
 * TLS connection
 */
  public void close() throws IOException {
    if (isClosed) {
      return;
    }
    if (debug) {
      System.out.println("StartTLS: replacing SSL " + "streams with originals");
    }
    ldapConnection.replaceStreams(originalInputStream,originalOutputStream);
    if (debug) {
      System.out.println("StartTLS: closing SSL Socket");
    }
    sslSocket.close();
    isClosed=true;
  }
  /** 
 * Sets the connection for TLS to use. The TLS connection will be attached
 * to this connection.
 * @param ldapConnection The non-null connection to use.
 * @param hostname The server's hostname. If null, the hostname used to
 * open the connection will be used instead.
 */
  public void setConnection(  Connection ldapConnection,  String hostname){
    this.ldapConnection=ldapConnection;
    this.hostname=(hostname != null) ? hostname : ldapConnection.host;
    originalInputStream=ldapConnection.inStream;
    originalOutputStream=ldapConnection.outStream;
  }
  private SSLSocketFactory getDefaultFactory() throws IOException {
    if (defaultFactory != null) {
      return defaultFactory;
    }
    return (defaultFactory=(SSLSocketFactory)SSLSocketFactory.getDefault());
  }
  private SSLSocket startHandshake(  SSLSocketFactory factory) throws IOException {
    if (ldapConnection == null) {
      throw new IllegalStateException("LDAP connection has not been set." + " TLS requires an existing LDAP connection.");
    }
    if (factory != currentFactory) {
      sslSocket=(SSLSocket)factory.createSocket(ldapConnection.sock,ldapConnection.host,ldapConnection.port,false);
      currentFactory=factory;
      if (debug) {
        System.out.println("StartTLS: Created socket : " + sslSocket);
      }
    }
    if (suites != null) {
      sslSocket.setEnabledCipherSuites(suites);
      if (debug) {
        System.out.println("StartTLS: Enabled cipher suites");
      }
    }
    try {
      if (debug) {
        System.out.println("StartTLS: Calling sslSocket.startHandshake");
      }
      sslSocket.startHandshake();
      if (debug) {
        System.out.println("StartTLS: + Finished sslSocket.startHandshake");
      }
      ldapConnection.replaceStreams(sslSocket.getInputStream(),sslSocket.getOutputStream());
      if (debug) {
        System.out.println("StartTLS: Replaced IO Streams");
      }
    }
 catch (    IOException e) {
      if (debug) {
        System.out.println("StartTLS: Got IO error during handshake");
        e.printStackTrace();
      }
      sslSocket.close();
      isClosed=true;
      throw e;
    }
    return sslSocket;
  }
  private boolean verify(  String hostname,  SSLSession session) throws SSLPeerUnverifiedException {
    java.security.cert.Certificate[] certs=null;
    if (hostname != null && hostname.startsWith("[") && hostname.endsWith("]")) {
      hostname=hostname.substring(1,hostname.length() - 1);
    }
    try {
      HostnameChecker checker=HostnameChecker.getInstance(HostnameChecker.TYPE_LDAP);
      if (session.getCipherSuite().startsWith("TLS_KRB5")) {
        Principal principal=getPeerPrincipal(session);
        if (!checker.match(hostname,principal)) {
          throw new SSLPeerUnverifiedException("hostname of the kerberos principal:" + principal + " does not match the hostname:"+ hostname);
        }
      }
 else {
        certs=session.getPeerCertificates();
        X509Certificate peerCert;
        if (certs[0] instanceof java.security.cert.X509Certificate) {
          peerCert=(java.security.cert.X509Certificate)certs[0];
        }
 else {
          throw new SSLPeerUnverifiedException("Received a non X509Certificate from the server");
        }
        checker.match(hostname,peerCert);
      }
      return true;
    }
 catch (    SSLPeerUnverifiedException e) {
      String cipher=session.getCipherSuite();
      if (cipher != null && (cipher.indexOf("_anon_") != -1)) {
        return true;
      }
      throw e;
    }
catch (    CertificateException e) {
      throw (SSLPeerUnverifiedException)new SSLPeerUnverifiedException("hostname of the server '" + hostname + "' does not match the hostname in the "+ "server's certificate.").initCause(e);
    }
  }
  private static Principal getPeerPrincipal(  SSLSession session) throws SSLPeerUnverifiedException {
    Principal principal;
    try {
      principal=session.getPeerPrincipal();
    }
 catch (    AbstractMethodError e) {
      principal=null;
    }
    return principal;
  }
}
