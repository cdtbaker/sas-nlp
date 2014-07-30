package sun.security.ssl;
import java.io.*;
import java.util.*;
import javax.net.ssl.SSLException;
/** 
 * A list of CipherSuites. Also maintains the lists of supported and
 * default ciphersuites and supports I/O from handshake streams.
 * Instances of this class are immutable.
 */
final class CipherSuiteList {
  private final Collection<CipherSuite> cipherSuites;
  private String[] suiteNames;
  private volatile Boolean containsEC;
  CipherSuiteList(  Collection<CipherSuite> cipherSuites){
    this.cipherSuites=cipherSuites;
  }
  /** 
 * Create a CipherSuiteList with a single element.
 */
  CipherSuiteList(  CipherSuite suite){
    cipherSuites=new ArrayList<CipherSuite>(1);
    cipherSuites.add(suite);
  }
  /** 
 * Construct a CipherSuiteList from a array of names. We don't bother
 * to eliminate duplicates.
 * @exception IllegalArgumentException if the array or any of its elements
 * is null or if the ciphersuite name is unrecognized or unsupported
 * using currently installed providers.
 */
  CipherSuiteList(  String[] names){
    if (names == null) {
      throw new IllegalArgumentException("CipherSuites may not be null");
    }
    cipherSuites=new ArrayList<CipherSuite>(names.length);
    boolean refreshed=false;
    for (int i=0; i < names.length; i++) {
      String suiteName=names[i];
      CipherSuite suite=CipherSuite.valueOf(suiteName);
      if (suite.isAvailable() == false) {
        if (refreshed == false) {
          clearAvailableCache();
          refreshed=true;
        }
        if (suite.isAvailable() == false) {
          throw new IllegalArgumentException("Cannot support " + suiteName + " with currently installed providers");
        }
      }
      cipherSuites.add(suite);
    }
  }
  /** 
 * Read a CipherSuiteList from a HandshakeInStream in V3 ClientHello
 * format. Does not check if the listed ciphersuites are known or
 * supported.
 */
  CipherSuiteList(  HandshakeInStream in) throws IOException {
    byte[] bytes=in.getBytes16();
    if ((bytes.length & 1) != 0) {
      throw new SSLException("Invalid ClientHello message");
    }
    cipherSuites=new ArrayList<CipherSuite>(bytes.length >> 1);
    for (int i=0; i < bytes.length; i+=2) {
      cipherSuites.add(CipherSuite.valueOf(bytes[i],bytes[i + 1]));
    }
  }
  /** 
 * Return whether this list contains the given CipherSuite.
 */
  boolean contains(  CipherSuite suite){
    return cipherSuites.contains(suite);
  }
  boolean containsEC(){
    if (containsEC == null) {
      for (      CipherSuite c : cipherSuites) {
switch (c.keyExchange) {
case K_ECDH_ECDSA:
case K_ECDH_RSA:
case K_ECDHE_ECDSA:
case K_ECDHE_RSA:
case K_ECDH_ANON:
          containsEC=true;
        return true;
default :
      break;
  }
}
containsEC=false;
}
return containsEC;
}
/** 
 * Return an Iterator for the CipherSuites in this list.
 */
Iterator<CipherSuite> iterator(){
return cipherSuites.iterator();
}
/** 
 * Return a reference to the internal Collection of CipherSuites.
 * The Collection MUST NOT be modified.
 */
Collection<CipherSuite> collection(){
return cipherSuites;
}
/** 
 * Return the number of CipherSuites in this list.
 */
int size(){
return cipherSuites.size();
}
/** 
 * Return an array with the names of the CipherSuites in this list.
 */
synchronized String[] toStringArray(){
if (suiteNames == null) {
suiteNames=new String[cipherSuites.size()];
int i=0;
for (CipherSuite c : cipherSuites) {
  suiteNames[i++]=c.name;
}
}
return suiteNames.clone();
}
public String toString(){
return cipherSuites.toString();
}
/** 
 * Write this list to an HandshakeOutStream in V3 ClientHello format.
 */
void send(HandshakeOutStream s) throws IOException {
byte[] suiteBytes=new byte[cipherSuites.size() * 2];
int i=0;
for (CipherSuite c : cipherSuites) {
suiteBytes[i]=(byte)(c.id >> 8);
suiteBytes[i + 1]=(byte)c.id;
i+=2;
}
s.putBytes16(suiteBytes);
}
/** 
 * Clear cache of available ciphersuites. If we support all ciphers
 * internally, there is no need to clear the cache and calling this
 * method has no effect.
 */
static synchronized void clearAvailableCache(){
if (CipherSuite.DYNAMIC_AVAILABILITY) {
CipherSuite.BulkCipher.clearAvailableCache();
JsseJce.clearEcAvailable();
}
}
}