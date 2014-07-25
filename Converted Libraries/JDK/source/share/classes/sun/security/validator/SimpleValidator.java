package sun.security.validator;
import java.io.IOException;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X509CertImpl;
import sun.security.x509.NetscapeCertTypeExtension;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.security.provider.certpath.AlgorithmChecker;
/** 
 * A simple validator implementation. It is based on code from the JSSE
 * X509TrustManagerImpl. This implementation is designed for compatibility with
 * deployed certificates and previous J2SE versions. It will never support
 * more advanced features and will be deemphasized in favor of the PKIX
 * validator going forward.
 * <p>{@code SimpleValidator} objects are immutable once they have been created.
 * Please DO NOT add methods that can change the state of an instance once
 * it has been created.
 * @author Andreas Sterbenz
 */
public final class SimpleValidator extends Validator {
  final static String OID_BASIC_CONSTRAINTS="2.5.29.19";
  final static String OID_NETSCAPE_CERT_TYPE="2.16.840.1.113730.1.1";
  final static String OID_KEY_USAGE="2.5.29.15";
  final static String OID_EXTENDED_KEY_USAGE="2.5.29.37";
  final static String OID_EKU_ANY_USAGE="2.5.29.37.0";
  final static ObjectIdentifier OBJID_NETSCAPE_CERT_TYPE=NetscapeCertTypeExtension.NetscapeCertType_Id;
  private final static String NSCT_SSL_CA=NetscapeCertTypeExtension.SSL_CA;
  private final static String NSCT_CODE_SIGNING_CA=NetscapeCertTypeExtension.OBJECT_SIGNING_CA;
  /** 
 * The trusted certificates as:
 * Map (X500Principal)subject of trusted cert -> List of X509Certificate
 * The list is used because there may be multiple certificates
 * with an identical subject DN.
 */
  private final Map<X500Principal,List<X509Certificate>> trustedX500Principals;
  /** 
 * Set of the trusted certificates. Present only for
 * getTrustedCertificates().
 */
  private final Collection<X509Certificate> trustedCerts;
  SimpleValidator(  String variant,  Collection<X509Certificate> trustedCerts){
    super(TYPE_SIMPLE,variant);
    this.trustedCerts=trustedCerts;
    trustedX500Principals=new HashMap<X500Principal,List<X509Certificate>>();
    for (    X509Certificate cert : trustedCerts) {
      X500Principal principal=cert.getSubjectX500Principal();
      List<X509Certificate> list=trustedX500Principals.get(principal);
      if (list == null) {
        list=new ArrayList<X509Certificate>(2);
        trustedX500Principals.put(principal,list);
      }
      list.add(cert);
    }
  }
  public Collection<X509Certificate> getTrustedCertificates(){
    return trustedCerts;
  }
  /** 
 * Perform simple validation of chain. The arguments otherCerts and
 * parameter are ignored.
 */
  @Override X509Certificate[] engineValidate(  X509Certificate[] chain,  Collection<X509Certificate> otherCerts,  AlgorithmConstraints constraints,  Object parameter) throws CertificateException {
    if ((chain == null) || (chain.length == 0)) {
      throw new CertificateException("null or zero-length certificate chain");
    }
    chain=buildTrustedChain(chain);
    Date date=validationDate;
    if (date == null) {
      date=new Date();
    }
    TrustAnchor anchor=new TrustAnchor(chain[chain.length - 1],null);
    AlgorithmChecker defaultAlgChecker=new AlgorithmChecker(anchor);
    AlgorithmChecker appAlgChecker=null;
    if (constraints != null) {
      appAlgChecker=new AlgorithmChecker(anchor,constraints);
    }
    int maxPathLength=chain.length - 1;
    for (int i=chain.length - 2; i >= 0; i--) {
      X509Certificate issuerCert=chain[i + 1];
      X509Certificate cert=chain[i];
      try {
        defaultAlgChecker.check(cert,Collections.<String>emptySet());
        if (appAlgChecker != null) {
          appAlgChecker.check(cert,Collections.<String>emptySet());
        }
      }
 catch (      CertPathValidatorException cpve) {
        throw new ValidatorException(ValidatorException.T_ALGORITHM_DISABLED,cert,cpve);
      }
      if ((variant.equals(VAR_CODE_SIGNING) == false) && (variant.equals(VAR_JCE_SIGNING) == false)) {
        cert.checkValidity(date);
      }
      if (cert.getIssuerX500Principal().equals(issuerCert.getSubjectX500Principal()) == false) {
        throw new ValidatorException(ValidatorException.T_NAME_CHAINING,cert);
      }
      try {
        cert.verify(issuerCert.getPublicKey());
      }
 catch (      GeneralSecurityException e) {
        throw new ValidatorException(ValidatorException.T_SIGNATURE_ERROR,cert,e);
      }
      if (i != 0) {
        maxPathLength=checkExtensions(cert,maxPathLength);
      }
    }
    return chain;
  }
  private int checkExtensions(  X509Certificate cert,  int maxPathLen) throws CertificateException {
    Set<String> critSet=cert.getCriticalExtensionOIDs();
    if (critSet == null) {
      critSet=Collections.<String>emptySet();
    }
    int pathLenConstraint=checkBasicConstraints(cert,critSet,maxPathLen);
    checkKeyUsage(cert,critSet);
    checkNetscapeCertType(cert,critSet);
    if (!critSet.isEmpty()) {
      throw new ValidatorException("Certificate contains unknown critical extensions: " + critSet,ValidatorException.T_CA_EXTENSIONS,cert);
    }
    return pathLenConstraint;
  }
  private void checkNetscapeCertType(  X509Certificate cert,  Set<String> critSet) throws CertificateException {
    if (variant.equals(VAR_GENERIC)) {
    }
 else     if (variant.equals(VAR_TLS_CLIENT) || variant.equals(VAR_TLS_SERVER)) {
      if (getNetscapeCertTypeBit(cert,NSCT_SSL_CA) == false) {
        throw new ValidatorException("Invalid Netscape CertType extension for SSL CA " + "certificate",ValidatorException.T_CA_EXTENSIONS,cert);
      }
      critSet.remove(OID_NETSCAPE_CERT_TYPE);
    }
 else     if (variant.equals(VAR_CODE_SIGNING) || variant.equals(VAR_JCE_SIGNING)) {
      if (getNetscapeCertTypeBit(cert,NSCT_CODE_SIGNING_CA) == false) {
        throw new ValidatorException("Invalid Netscape CertType extension for code " + "signing CA certificate",ValidatorException.T_CA_EXTENSIONS,cert);
      }
      critSet.remove(OID_NETSCAPE_CERT_TYPE);
    }
 else {
      throw new CertificateException("Unknown variant " + variant);
    }
  }
  /** 
 * Get the value of the specified bit in the Netscape certificate type
 * extension. If the extension is not present at all, we return true.
 */
  static boolean getNetscapeCertTypeBit(  X509Certificate cert,  String type){
    try {
      NetscapeCertTypeExtension ext;
      if (cert instanceof X509CertImpl) {
        X509CertImpl certImpl=(X509CertImpl)cert;
        ObjectIdentifier oid=OBJID_NETSCAPE_CERT_TYPE;
        ext=(NetscapeCertTypeExtension)certImpl.getExtension(oid);
        if (ext == null) {
          return true;
        }
      }
 else {
        byte[] extVal=cert.getExtensionValue(OID_NETSCAPE_CERT_TYPE);
        if (extVal == null) {
          return true;
        }
        DerInputStream in=new DerInputStream(extVal);
        byte[] encoded=in.getOctetString();
        encoded=new DerValue(encoded).getUnalignedBitString().toByteArray();
        ext=new NetscapeCertTypeExtension(encoded);
      }
      Boolean val=(Boolean)ext.get(type);
      return val.booleanValue();
    }
 catch (    IOException e) {
      return false;
    }
  }
  private int checkBasicConstraints(  X509Certificate cert,  Set<String> critSet,  int maxPathLen) throws CertificateException {
    critSet.remove(OID_BASIC_CONSTRAINTS);
    int constraints=cert.getBasicConstraints();
    if (constraints < 0) {
      throw new ValidatorException("End user tried to act as a CA",ValidatorException.T_CA_EXTENSIONS,cert);
    }
    if (!X509CertImpl.isSelfIssued(cert)) {
      if (maxPathLen <= 1) {
        throw new ValidatorException("Violated path length constraints",ValidatorException.T_CA_EXTENSIONS,cert);
      }
      maxPathLen--;
    }
    if (maxPathLen > constraints) {
      maxPathLen=constraints;
    }
    return maxPathLen;
  }
  private void checkKeyUsage(  X509Certificate cert,  Set<String> critSet) throws CertificateException {
    critSet.remove(OID_KEY_USAGE);
    critSet.remove(OID_EXTENDED_KEY_USAGE);
    boolean[] keyUsageInfo=cert.getKeyUsage();
    if (keyUsageInfo != null) {
      if ((keyUsageInfo.length < 6) || (keyUsageInfo[5] == false)) {
        throw new ValidatorException("Wrong key usage: expected keyCertSign",ValidatorException.T_CA_EXTENSIONS,cert);
      }
    }
  }
  /** 
 * Build a trusted certificate chain. This method always returns a chain
 * with a trust anchor as the final cert in the chain. If no trust anchor
 * could be found, a CertificateException is thrown.
 */
  private X509Certificate[] buildTrustedChain(  X509Certificate[] chain) throws CertificateException {
    List<X509Certificate> c=new ArrayList<X509Certificate>(chain.length);
    for (int i=0; i < chain.length; i++) {
      X509Certificate cert=chain[i];
      X509Certificate trustedCert=getTrustedCertificate(cert);
      if (trustedCert != null) {
        c.add(trustedCert);
        return c.toArray(CHAIN0);
      }
      c.add(cert);
    }
    X509Certificate cert=chain[chain.length - 1];
    X500Principal subject=cert.getSubjectX500Principal();
    X500Principal issuer=cert.getIssuerX500Principal();
    List<X509Certificate> list=trustedX500Principals.get(issuer);
    if (list != null) {
      X509Certificate trustedCert=list.iterator().next();
      c.add(trustedCert);
      return c.toArray(CHAIN0);
    }
    throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
  }
  /** 
 * Return a trusted certificate that matches the input certificate,
 * or null if no such certificate can be found. This method also handles
 * cases where a CA re-issues a trust anchor with the same public key and
 * same subject and issuer names but a new validity period, etc.
 */
  private X509Certificate getTrustedCertificate(  X509Certificate cert){
    Principal certSubjectName=cert.getSubjectX500Principal();
    List<X509Certificate> list=trustedX500Principals.get(certSubjectName);
    if (list == null) {
      return null;
    }
    Principal certIssuerName=cert.getIssuerX500Principal();
    PublicKey certPublicKey=cert.getPublicKey();
    for (    X509Certificate mycert : list) {
      if (mycert.equals(cert)) {
        return cert;
      }
      if (!mycert.getIssuerX500Principal().equals(certIssuerName)) {
        continue;
      }
      if (!mycert.getPublicKey().equals(certPublicKey)) {
        continue;
      }
      return mycert;
    }
    return null;
  }
}
