package sun.security.validator;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import javax.security.auth.x500.X500Principal;
import sun.security.action.GetBooleanAction;
import sun.security.provider.certpath.AlgorithmChecker;
/** 
 * Validator implementation built on the PKIX CertPath API. This
 * implementation will be emphasized going forward.<p>
 * <p>
 * Note that the validate() implementation tries to use a PKIX validator
 * if that appears possible and a PKIX builder otherwise. This increases
 * performance and currently also leads to better exception messages
 * in case of failures.
 * <p>{@code PKIXValidator} objects are immutable once they have been created.
 * Please DO NOT add methods that can change the state of an instance once
 * it has been created.
 * @author Andreas Sterbenz
 */
public final class PKIXValidator extends Validator {
  /** 
 * Flag indicating whether to enable revocation check for the PKIX trust
 * manager. Typically, this will only work if the PKIX implementation
 * supports CRL distribution points as we do not manually setup CertStores.
 */
  private final static boolean checkTLSRevocation=AccessController.doPrivileged(new GetBooleanAction("com.sun.net.ssl.checkRevocation"));
  private final static boolean TRY_VALIDATOR=true;
  private final Set<X509Certificate> trustedCerts;
  private final PKIXBuilderParameters parameterTemplate;
  private int certPathLength=-1;
  private final Map<X500Principal,List<PublicKey>> trustedSubjects;
  private final CertificateFactory factory;
  private final boolean plugin;
  PKIXValidator(  String variant,  Collection<X509Certificate> trustedCerts){
    super(TYPE_PKIX,variant);
    if (trustedCerts instanceof Set) {
      this.trustedCerts=(Set<X509Certificate>)trustedCerts;
    }
 else {
      this.trustedCerts=new HashSet<X509Certificate>(trustedCerts);
    }
    Set<TrustAnchor> trustAnchors=new HashSet<TrustAnchor>();
    for (    X509Certificate cert : trustedCerts) {
      trustAnchors.add(new TrustAnchor(cert,null));
    }
    try {
      parameterTemplate=new PKIXBuilderParameters(trustAnchors,null);
    }
 catch (    InvalidAlgorithmParameterException e) {
      throw new RuntimeException("Unexpected error: " + e.toString(),e);
    }
    setDefaultParameters(variant);
    if (TRY_VALIDATOR) {
      if (TRY_VALIDATOR == false) {
        return;
      }
      trustedSubjects=new HashMap<X500Principal,List<PublicKey>>();
      for (      X509Certificate cert : trustedCerts) {
        X500Principal dn=cert.getSubjectX500Principal();
        List<PublicKey> keys;
        if (trustedSubjects.containsKey(dn)) {
          keys=trustedSubjects.get(dn);
        }
 else {
          keys=new ArrayList<PublicKey>();
          trustedSubjects.put(dn,keys);
        }
        keys.add(cert.getPublicKey());
      }
      try {
        factory=CertificateFactory.getInstance("X.509");
      }
 catch (      CertificateException e) {
        throw new RuntimeException("Internal error",e);
      }
      plugin=variant.equals(VAR_PLUGIN_CODE_SIGNING);
    }
 else {
      plugin=false;
    }
  }
  PKIXValidator(  String variant,  PKIXBuilderParameters params){
    super(TYPE_PKIX,variant);
    trustedCerts=new HashSet<X509Certificate>();
    for (    TrustAnchor anchor : params.getTrustAnchors()) {
      X509Certificate cert=anchor.getTrustedCert();
      if (cert != null) {
        trustedCerts.add(cert);
      }
    }
    parameterTemplate=params;
    if (TRY_VALIDATOR) {
      if (TRY_VALIDATOR == false) {
        return;
      }
      trustedSubjects=new HashMap<X500Principal,List<PublicKey>>();
      for (      X509Certificate cert : trustedCerts) {
        X500Principal dn=cert.getSubjectX500Principal();
        List<PublicKey> keys;
        if (trustedSubjects.containsKey(dn)) {
          keys=trustedSubjects.get(dn);
        }
 else {
          keys=new ArrayList<PublicKey>();
          trustedSubjects.put(dn,keys);
        }
        keys.add(cert.getPublicKey());
      }
      try {
        factory=CertificateFactory.getInstance("X.509");
      }
 catch (      CertificateException e) {
        throw new RuntimeException("Internal error",e);
      }
      plugin=variant.equals(VAR_PLUGIN_CODE_SIGNING);
    }
 else {
      plugin=false;
    }
  }
  public Collection<X509Certificate> getTrustedCertificates(){
    return trustedCerts;
  }
  /** 
 * Returns the length of the last certification path that is validated by
 * CertPathValidator. This is intended primarily as a callback mechanism
 * for PKIXCertPathCheckers to determine the length of the certification
 * path that is being validated. It is necessary since engineValidate()
 * may modify the length of the path.
 * @return the length of the last certification path passed to
 * CertPathValidator.validate, or -1 if it has not been invoked yet
 */
  public int getCertPathLength(){
    return certPathLength;
  }
  /** 
 * Set J2SE global default PKIX parameters. Currently, hardcoded to disable
 * revocation checking. In the future, this should be configurable.
 */
  private void setDefaultParameters(  String variant){
    if ((variant == Validator.VAR_TLS_SERVER) || (variant == Validator.VAR_TLS_CLIENT)) {
      parameterTemplate.setRevocationEnabled(checkTLSRevocation);
    }
 else {
      parameterTemplate.setRevocationEnabled(false);
    }
  }
  /** 
 * Return the PKIX parameters used by this instance. An application may
 * modify the parameters but must make sure not to perform any concurrent
 * validations.
 */
  public PKIXBuilderParameters getParameters(){
    return parameterTemplate;
  }
  @Override X509Certificate[] engineValidate(  X509Certificate[] chain,  Collection<X509Certificate> otherCerts,  AlgorithmConstraints constraints,  Object parameter) throws CertificateException {
    if ((chain == null) || (chain.length == 0)) {
      throw new CertificateException("null or zero-length certificate chain");
    }
    PKIXBuilderParameters pkixParameters=(PKIXBuilderParameters)parameterTemplate.clone();
    AlgorithmChecker algorithmChecker=null;
    if (constraints != null) {
      algorithmChecker=new AlgorithmChecker(constraints);
      pkixParameters.addCertPathChecker(algorithmChecker);
    }
    if (TRY_VALIDATOR) {
      X500Principal prevIssuer=null;
      for (int i=0; i < chain.length; i++) {
        X509Certificate cert=chain[i];
        X500Principal dn=cert.getSubjectX500Principal();
        if (i != 0 && !dn.equals(prevIssuer)) {
          return doBuild(chain,otherCerts,pkixParameters);
        }
        if (trustedCerts.contains(cert) || (trustedSubjects.containsKey(dn) && trustedSubjects.get(dn).contains(cert.getPublicKey()))) {
          if (i == 0) {
            return new X509Certificate[]{chain[0]};
          }
          X509Certificate[] newChain=new X509Certificate[i];
          System.arraycopy(chain,0,newChain,0,i);
          return doValidate(newChain,pkixParameters);
        }
        prevIssuer=cert.getIssuerX500Principal();
      }
      X509Certificate last=chain[chain.length - 1];
      X500Principal issuer=last.getIssuerX500Principal();
      X500Principal subject=last.getSubjectX500Principal();
      if (trustedSubjects.containsKey(issuer) && isSignatureValid(trustedSubjects.get(issuer),last)) {
        return doValidate(chain,pkixParameters);
      }
      if (plugin) {
        if (chain.length > 1) {
          X509Certificate[] newChain=new X509Certificate[chain.length - 1];
          System.arraycopy(chain,0,newChain,0,newChain.length);
          try {
            pkixParameters.setTrustAnchors(Collections.singleton(new TrustAnchor(chain[chain.length - 1],null)));
          }
 catch (          InvalidAlgorithmParameterException iape) {
            throw new CertificateException(iape);
          }
          doValidate(newChain,pkixParameters);
        }
        throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
      }
    }
    return doBuild(chain,otherCerts,pkixParameters);
  }
  private boolean isSignatureValid(  List<PublicKey> keys,  X509Certificate sub){
    if (plugin) {
      for (      PublicKey key : keys) {
        try {
          sub.verify(key);
          return true;
        }
 catch (        Exception ex) {
          continue;
        }
      }
      return false;
    }
    return true;
  }
  private static X509Certificate[] toArray(  CertPath path,  TrustAnchor anchor) throws CertificateException {
    List<? extends java.security.cert.Certificate> list=path.getCertificates();
    X509Certificate[] chain=new X509Certificate[list.size() + 1];
    list.toArray(chain);
    X509Certificate trustedCert=anchor.getTrustedCert();
    if (trustedCert == null) {
      throw new ValidatorException("TrustAnchor must be specified as certificate");
    }
    chain[chain.length - 1]=trustedCert;
    return chain;
  }
  /** 
 * Set the check date (for debugging).
 */
  private void setDate(  PKIXBuilderParameters params){
    Date date=validationDate;
    if (date != null) {
      params.setDate(date);
    }
  }
  private X509Certificate[] doValidate(  X509Certificate[] chain,  PKIXBuilderParameters params) throws CertificateException {
    try {
      setDate(params);
      CertPathValidator validator=CertPathValidator.getInstance("PKIX");
      CertPath path=factory.generateCertPath(Arrays.asList(chain));
      certPathLength=chain.length;
      PKIXCertPathValidatorResult result=(PKIXCertPathValidatorResult)validator.validate(path,params);
      return toArray(path,result.getTrustAnchor());
    }
 catch (    GeneralSecurityException e) {
      throw new ValidatorException("PKIX path validation failed: " + e.toString(),e);
    }
  }
  private X509Certificate[] doBuild(  X509Certificate[] chain,  Collection<X509Certificate> otherCerts,  PKIXBuilderParameters params) throws CertificateException {
    try {
      setDate(params);
      X509CertSelector selector=new X509CertSelector();
      selector.setCertificate(chain[0]);
      params.setTargetCertConstraints(selector);
      Collection<X509Certificate> certs=new ArrayList<X509Certificate>();
      certs.addAll(Arrays.asList(chain));
      if (otherCerts != null) {
        certs.addAll(otherCerts);
      }
      CertStore store=CertStore.getInstance("Collection",new CollectionCertStoreParameters(certs));
      params.addCertStore(store);
      CertPathBuilder builder=CertPathBuilder.getInstance("PKIX");
      PKIXCertPathBuilderResult result=(PKIXCertPathBuilderResult)builder.build(params);
      return toArray(result.getCertPath(),result.getTrustAnchor());
    }
 catch (    GeneralSecurityException e) {
      throw new ValidatorException("PKIX path building failed: " + e.toString(),e);
    }
  }
}
