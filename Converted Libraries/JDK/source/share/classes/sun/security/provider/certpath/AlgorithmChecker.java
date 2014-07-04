package sun.security.provider.certpath;
import java.security.AlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.EnumSet;
import java.util.HashSet;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.PKIXReason;
import java.io.IOException;
import java.security.interfaces.*;
import java.security.spec.*;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.AlgorithmId;
/** 
 * A <code>PKIXCertPathChecker</code> implementation to check whether a
 * specified certificate contains the required algorithm constraints.
 * <p>
 * Certificate fields such as the subject public key, the signature
 * algorithm, key usage, extended key usage, etc. need to conform to
 * the specified algorithm constraints.
 * @see PKIXCertPathChecker
 * @see PKIXParameters
 */
final public class AlgorithmChecker extends PKIXCertPathChecker {
  private final AlgorithmConstraints constraints;
  private final PublicKey trustedPubKey;
  private PublicKey prevPubKey;
  private final static Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET=EnumSet.of(CryptoPrimitive.SIGNATURE);
  private final static DisabledAlgorithmConstraints certPathDefaultConstraints=new DisabledAlgorithmConstraints(DisabledAlgorithmConstraints.PROPERTY_CERTPATH_DISABLED_ALGS);
  /** 
 * Create a new <code>AlgorithmChecker</code> with the algorithm
 * constraints specified in security property
 * "jdk.certpath.disabledAlgorithms".
 * @param anchor the trust anchor selected to validate the target
 * certificate
 */
  public AlgorithmChecker(  TrustAnchor anchor){
    this(anchor,certPathDefaultConstraints);
  }
  /** 
 * Create a new <code>AlgorithmChecker</code> with the
 * given {@code AlgorithmConstraints}.
 * <p>
 * Note that this constructor will be used to check a certification
 * path where the trust anchor is unknown, or a certificate list which may
 * contain the trust anchor. This constructor is used by SunJSSE.
 * @param constraints the algorithm constraints (or null)
 */
  public AlgorithmChecker(  AlgorithmConstraints constraints){
    this.prevPubKey=null;
    this.trustedPubKey=null;
    this.constraints=constraints;
  }
  /** 
 * Create a new <code>AlgorithmChecker</code> with the
 * given <code>TrustAnchor</code> and <code>AlgorithmConstraints</code>.
 * @param anchor the trust anchor selected to validate the target
 * certificate
 * @param constraints the algorithm constraints (or null)
 * @throws IllegalArgumentException if the <code>anchor</code> is null
 */
  public AlgorithmChecker(  TrustAnchor anchor,  AlgorithmConstraints constraints){
    if (anchor == null) {
      throw new IllegalArgumentException("The trust anchor cannot be null");
    }
    if (anchor.getTrustedCert() != null) {
      this.trustedPubKey=anchor.getTrustedCert().getPublicKey();
    }
 else {
      this.trustedPubKey=anchor.getCAPublicKey();
    }
    this.prevPubKey=trustedPubKey;
    this.constraints=constraints;
  }
  @Override public void init(  boolean forward) throws CertPathValidatorException {
    if (!forward) {
      if (trustedPubKey != null) {
        prevPubKey=trustedPubKey;
      }
 else {
        prevPubKey=null;
      }
    }
 else {
      throw new CertPathValidatorException("forward checking not supported");
    }
  }
  @Override public boolean isForwardCheckingSupported(){
    return false;
  }
  @Override public Set<String> getSupportedExtensions(){
    return null;
  }
  @Override public void check(  Certificate cert,  Collection<String> unresolvedCritExts) throws CertPathValidatorException {
    if (!(cert instanceof X509Certificate) || constraints == null) {
      return;
    }
    X509CertImpl x509Cert=null;
    try {
      x509Cert=X509CertImpl.toImpl((X509Certificate)cert);
    }
 catch (    CertificateException ce) {
      throw new CertPathValidatorException(ce);
    }
    PublicKey currPubKey=x509Cert.getPublicKey();
    String currSigAlg=x509Cert.getSigAlgName();
    AlgorithmId algorithmId=null;
    try {
      algorithmId=(AlgorithmId)x509Cert.get(X509CertImpl.SIG_ALG);
    }
 catch (    CertificateException ce) {
      throw new CertPathValidatorException(ce);
    }
    AlgorithmParameters currSigAlgParams=algorithmId.getParameters();
    if (!constraints.permits(SIGNATURE_PRIMITIVE_SET,currSigAlg,currSigAlgParams)) {
      throw new CertPathValidatorException("Algorithm constraints check failed: " + currSigAlg,null,null,-1,BasicReason.ALGORITHM_CONSTRAINED);
    }
    boolean[] keyUsage=x509Cert.getKeyUsage();
    if (keyUsage != null && keyUsage.length < 9) {
      throw new CertPathValidatorException("incorrect KeyUsage extension",null,null,-1,PKIXReason.INVALID_KEY_USAGE);
    }
    if (keyUsage != null) {
      Set<CryptoPrimitive> primitives=EnumSet.noneOf(CryptoPrimitive.class);
      if (keyUsage[0] || keyUsage[1] || keyUsage[5]|| keyUsage[6]) {
        primitives.add(CryptoPrimitive.SIGNATURE);
      }
      if (keyUsage[2]) {
        primitives.add(CryptoPrimitive.KEY_ENCAPSULATION);
      }
      if (keyUsage[3]) {
        primitives.add(CryptoPrimitive.PUBLIC_KEY_ENCRYPTION);
      }
      if (keyUsage[4]) {
        primitives.add(CryptoPrimitive.KEY_AGREEMENT);
      }
      if (!primitives.isEmpty()) {
        if (!constraints.permits(primitives,currPubKey)) {
          throw new CertPathValidatorException("algorithm constraints check failed",null,null,-1,BasicReason.ALGORITHM_CONSTRAINED);
        }
      }
    }
    if (prevPubKey != null) {
      if (currSigAlg != null) {
        if (!constraints.permits(SIGNATURE_PRIMITIVE_SET,currSigAlg,prevPubKey,currSigAlgParams)) {
          throw new CertPathValidatorException("Algorithm constraints check failed: " + currSigAlg,null,null,-1,BasicReason.ALGORITHM_CONSTRAINED);
        }
      }
      if (currPubKey instanceof DSAPublicKey && ((DSAPublicKey)currPubKey).getParams() == null) {
        if (!(prevPubKey instanceof DSAPublicKey)) {
          throw new CertPathValidatorException("Input key is not " + "of a appropriate type for inheriting parameters");
        }
        DSAParams params=((DSAPublicKey)prevPubKey).getParams();
        if (params == null) {
          throw new CertPathValidatorException("Key parameters missing");
        }
        try {
          BigInteger y=((DSAPublicKey)currPubKey).getY();
          KeyFactory kf=KeyFactory.getInstance("DSA");
          DSAPublicKeySpec ks=new DSAPublicKeySpec(y,params.getP(),params.getQ(),params.getG());
          currPubKey=kf.generatePublic(ks);
        }
 catch (        GeneralSecurityException e) {
          throw new CertPathValidatorException("Unable to generate " + "key with inherited parameters: " + e.getMessage(),e);
        }
      }
    }
    prevPubKey=currPubKey;
  }
  /** 
 * Try to set the trust anchor of the checker.
 * <p>
 * If there is no trust anchor specified and the checker has not started,
 * set the trust anchor.
 * @param anchor the trust anchor selected to validate the target
 * certificate
 */
  void trySetTrustAnchor(  TrustAnchor anchor){
    if (prevPubKey == null) {
      if (anchor == null) {
        throw new IllegalArgumentException("The trust anchor cannot be null");
      }
      if (anchor.getTrustedCert() != null) {
        prevPubKey=anchor.getTrustedCert().getPublicKey();
      }
 else {
        prevPubKey=anchor.getCAPublicKey();
      }
    }
  }
  /** 
 * Check the signature algorithm with the specified public key.
 * @param key the public key to verify the CRL signature
 * @param crl the target CRL
 */
  static void check(  PublicKey key,  X509CRL crl) throws CertPathValidatorException {
    X509CRLImpl x509CRLImpl=null;
    try {
      x509CRLImpl=X509CRLImpl.toImpl(crl);
    }
 catch (    CRLException ce) {
      throw new CertPathValidatorException(ce);
    }
    AlgorithmId algorithmId=x509CRLImpl.getSigAlgId();
    check(key,algorithmId);
  }
  /** 
 * Check the signature algorithm with the specified public key.
 * @param key the public key to verify the CRL signature
 * @param crl the target CRL
 */
  static void check(  PublicKey key,  AlgorithmId algorithmId) throws CertPathValidatorException {
    String sigAlgName=algorithmId.getName();
    AlgorithmParameters sigAlgParams=algorithmId.getParameters();
    if (!certPathDefaultConstraints.permits(SIGNATURE_PRIMITIVE_SET,sigAlgName,key,sigAlgParams)) {
      throw new CertPathValidatorException("algorithm check failed: " + sigAlgName + " is disabled",null,null,-1,BasicReason.ALGORITHM_CONSTRAINED);
    }
  }
}
