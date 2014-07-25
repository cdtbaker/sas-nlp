package sun.security.validator;
import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import sun.security.action.*;
/** 
 * Collection of static utility methods related to KeyStores.
 * @author Andreas Sterbenz
 */
public class KeyStores {
  private KeyStores(){
  }
  /** 
 * Return a Set with all trusted X509Certificates contained in
 * this KeyStore.
 */
  public static Set<X509Certificate> getTrustedCerts(  KeyStore ks){
    Set<X509Certificate> set=new HashSet<X509Certificate>();
    try {
      for (Enumeration<String> e=ks.aliases(); e.hasMoreElements(); ) {
        String alias=e.nextElement();
        if (ks.isCertificateEntry(alias)) {
          Certificate cert=ks.getCertificate(alias);
          if (cert instanceof X509Certificate) {
            set.add((X509Certificate)cert);
          }
        }
 else         if (ks.isKeyEntry(alias)) {
          Certificate[] certs=ks.getCertificateChain(alias);
          if ((certs != null) && (certs.length > 0) && (certs[0] instanceof X509Certificate)) {
            set.add((X509Certificate)certs[0]);
          }
        }
      }
    }
 catch (    KeyStoreException e) {
    }
    return set;
  }
}
