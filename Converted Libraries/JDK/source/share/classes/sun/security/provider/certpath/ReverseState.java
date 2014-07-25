package sun.security.provider.certpath;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X509CertImpl;
/** 
 * A specification of a reverse PKIX validation state
 * which is initialized by each build and updated each time a
 * certificate is added to the current path.
 * @since       1.4
 * @author      Sean Mullan
 * @author      Yassir Elley
 */
class ReverseState implements State {
  private static final Debug debug=Debug.getInstance("certpath");
  X500Principal subjectDN;
  PublicKey pubKey;
  SubjectKeyIdentifierExtension subjKeyId;
  NameConstraintsExtension nc;
  int explicitPolicy;
  int policyMapping;
  int inhibitAnyPolicy;
  int certIndex;
  PolicyNodeImpl rootNode;
  int remainingCACerts;
  ArrayList<PKIXCertPathChecker> userCheckers;
  private boolean init=true;
  public CrlRevocationChecker crlChecker;
  AlgorithmChecker algorithmChecker;
  TrustAnchor trustAnchor;
  public boolean crlSign=true;
  /** 
 * Returns a boolean flag indicating if the state is initial
 * (just starting)
 * @return boolean flag indicating if the state is initial (just starting)
 */
  public boolean isInitial(){
    return init;
  }
  /** 
 * Display state for debugging purposes
 */
  public String toString(){
    StringBuffer sb=new StringBuffer();
    try {
      sb.append("State [");
      sb.append("\n  subjectDN of last cert: " + subjectDN);
      sb.append("\n  subjectKeyIdentifier: " + String.valueOf(subjKeyId));
      sb.append("\n  nameConstraints: " + String.valueOf(nc));
      sb.append("\n  certIndex: " + certIndex);
      sb.append("\n  explicitPolicy: " + explicitPolicy);
      sb.append("\n  policyMapping:  " + policyMapping);
      sb.append("\n  inhibitAnyPolicy:  " + inhibitAnyPolicy);
      sb.append("\n  rootNode: " + rootNode);
      sb.append("\n  remainingCACerts: " + remainingCACerts);
      sb.append("\n  crlSign: " + crlSign);
      sb.append("\n  init: " + init);
      sb.append("\n]\n");
    }
 catch (    Exception e) {
      if (debug != null) {
        debug.println("ReverseState.toString() unexpected exception");
        e.printStackTrace();
      }
    }
    return sb.toString();
  }
  /** 
 * Initialize the state.
 * @param maxPathLen The maximum number of CA certs in a path, where -1
 * means unlimited and 0 means only a single EE cert is allowed.
 * @param explicitPolicyRequired True, if explicit policy is required.
 * @param policyMappingInhibited True, if policy mapping is inhibited.
 * @param anyPolicyInhibited True, if any policy is inhibited.
 * @param certPathCheckers the list of user-defined PKIXCertPathCheckers
 */
  public void initState(  int maxPathLen,  boolean explicitPolicyRequired,  boolean policyMappingInhibited,  boolean anyPolicyInhibited,  List<PKIXCertPathChecker> certPathCheckers) throws CertPathValidatorException {
    remainingCACerts=(maxPathLen == -1 ? Integer.MAX_VALUE : maxPathLen);
    if (explicitPolicyRequired) {
      explicitPolicy=0;
    }
 else {
      explicitPolicy=(maxPathLen == -1) ? maxPathLen : maxPathLen + 2;
    }
    if (policyMappingInhibited) {
      policyMapping=0;
    }
 else {
      policyMapping=(maxPathLen == -1) ? maxPathLen : maxPathLen + 2;
    }
    if (anyPolicyInhibited) {
      inhibitAnyPolicy=0;
    }
 else {
      inhibitAnyPolicy=(maxPathLen == -1) ? maxPathLen : maxPathLen + 2;
    }
    certIndex=1;
    Set<String> initExpPolSet=new HashSet<String>(1);
    initExpPolSet.add(PolicyChecker.ANY_POLICY);
    rootNode=new PolicyNodeImpl(null,PolicyChecker.ANY_POLICY,null,false,initExpPolSet,false);
    if (certPathCheckers != null) {
      userCheckers=new ArrayList<PKIXCertPathChecker>(certPathCheckers);
      for (      PKIXCertPathChecker checker : certPathCheckers) {
        checker.init(false);
      }
    }
 else {
      userCheckers=new ArrayList<PKIXCertPathChecker>();
    }
    crlSign=true;
    init=true;
  }
  /** 
 * Update the state with the specified trust anchor.
 * @param anchor the most-trusted CA
 */
  public void updateState(  TrustAnchor anchor) throws CertificateException, IOException, CertPathValidatorException {
    trustAnchor=anchor;
    X509Certificate trustedCert=anchor.getTrustedCert();
    if (trustedCert != null) {
      updateState(trustedCert);
    }
 else {
      X500Principal caName=anchor.getCA();
      updateState(anchor.getCAPublicKey(),caName);
    }
    for (    PKIXCertPathChecker checker : userCheckers) {
      if (checker instanceof AlgorithmChecker) {
        ((AlgorithmChecker)checker).trySetTrustAnchor(anchor);
      }
    }
    init=false;
  }
  /** 
 * Update the state. This method is used when the most-trusted CA is
 * a trusted public-key and caName, instead of a trusted cert.
 * @param pubKey the public key of the trusted CA
 * @param subjectDN the subject distinguished name of the trusted CA
 */
  private void updateState(  PublicKey pubKey,  X500Principal subjectDN){
    this.subjectDN=subjectDN;
    this.pubKey=pubKey;
  }
  /** 
 * Update the state with the next certificate added to the path.
 * @param cert the certificate which is used to update the state
 */
  public void updateState(  X509Certificate cert) throws CertificateException, IOException, CertPathValidatorException {
    if (cert == null) {
      return;
    }
    subjectDN=cert.getSubjectX500Principal();
    X509CertImpl icert=X509CertImpl.toImpl(cert);
    PublicKey newKey=cert.getPublicKey();
    if (newKey instanceof DSAPublicKey && (((DSAPublicKey)newKey).getParams() == null)) {
      newKey=BasicChecker.makeInheritedParamsKey(newKey,pubKey);
    }
    pubKey=newKey;
    if (init) {
      init=false;
      return;
    }
    subjKeyId=icert.getSubjectKeyIdentifierExtension();
    crlSign=CrlRevocationChecker.certCanSignCrl(cert);
    if (nc != null) {
      nc.merge(icert.getNameConstraintsExtension());
    }
 else {
      nc=icert.getNameConstraintsExtension();
      if (nc != null) {
        nc=(NameConstraintsExtension)nc.clone();
      }
    }
    explicitPolicy=PolicyChecker.mergeExplicitPolicy(explicitPolicy,icert,false);
    policyMapping=PolicyChecker.mergePolicyMapping(policyMapping,icert);
    inhibitAnyPolicy=PolicyChecker.mergeInhibitAnyPolicy(inhibitAnyPolicy,icert);
    certIndex++;
    remainingCACerts=ConstraintsChecker.mergeBasicConstraints(cert,remainingCACerts);
    init=false;
  }
  /** 
 * Returns a boolean flag indicating if a key lacking necessary key
 * algorithm parameters has been encountered.
 * @return boolean flag indicating if key lacking parameters encountered.
 */
  public boolean keyParamsNeeded(){
    return false;
  }
  public Object clone(){
    try {
      ReverseState clonedState=(ReverseState)super.clone();
      clonedState.userCheckers=(ArrayList<PKIXCertPathChecker>)userCheckers.clone();
      ListIterator<PKIXCertPathChecker> li=clonedState.userCheckers.listIterator();
      while (li.hasNext()) {
        PKIXCertPathChecker checker=li.next();
        if (checker instanceof Cloneable) {
          li.set((PKIXCertPathChecker)checker.clone());
        }
      }
      if (nc != null) {
        clonedState.nc=(NameConstraintsExtension)nc.clone();
      }
      if (rootNode != null) {
        clonedState.rootNode=rootNode.copyTree();
      }
      return clonedState;
    }
 catch (    CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }
}
