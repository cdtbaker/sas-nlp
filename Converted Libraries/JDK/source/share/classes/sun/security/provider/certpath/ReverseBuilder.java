package sun.security.provider.certpath;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.Extension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.PolicyMappingsExtension;
/** 
 * This class represents a reverse builder, which is able to retrieve
 * matching certificates from CertStores and verify a particular certificate
 * against a ReverseState.
 * @since       1.4
 * @author      Sean Mullan
 * @author      Yassir Elley
 */
class ReverseBuilder extends Builder {
  private Debug debug=Debug.getInstance("certpath");
  Set<String> initPolicies;
  /** 
 * Initialize the builder with the input parameters.
 * @param params the parameter set used to build a certification path
 */
  ReverseBuilder(  PKIXBuilderParameters buildParams,  X500Principal targetSubjectDN){
    super(buildParams,targetSubjectDN);
    Set<String> initialPolicies=buildParams.getInitialPolicies();
    initPolicies=new HashSet<String>();
    if (initialPolicies.isEmpty()) {
      initPolicies.add(PolicyChecker.ANY_POLICY);
    }
 else {
      for (      String policy : initialPolicies) {
        initPolicies.add(policy);
      }
    }
  }
  /** 
 * Retrieves all certs from the specified CertStores that satisfy the
 * requirements specified in the parameters and the current
 * PKIX state (name constraints, policy constraints, etc).
 * @param currentState the current state.
 * Must be an instance of <code>ReverseState</code>
 * @param certStores list of CertStores
 */
  Collection<X509Certificate> getMatchingCerts(  State currState,  List<CertStore> certStores) throws CertStoreException, CertificateException, IOException {
    ReverseState currentState=(ReverseState)currState;
    if (debug != null)     debug.println("In ReverseBuilder.getMatchingCerts.");
    Collection<X509Certificate> certs=getMatchingEECerts(currentState,certStores);
    certs.addAll(getMatchingCACerts(currentState,certStores));
    return certs;
  }
  private Collection<X509Certificate> getMatchingEECerts(  ReverseState currentState,  List<CertStore> certStores) throws CertStoreException, CertificateException, IOException {
    X509CertSelector sel=(X509CertSelector)targetCertConstraints.clone();
    sel.setIssuer(currentState.subjectDN);
    sel.setCertificateValid(date);
    if (currentState.explicitPolicy == 0)     sel.setPolicy(getMatchingPolicies());
    sel.setBasicConstraints(-2);
    HashSet<X509Certificate> eeCerts=new HashSet<X509Certificate>();
    addMatchingCerts(sel,certStores,eeCerts,true);
    if (debug != null) {
      debug.println("ReverseBuilder.getMatchingEECerts got " + eeCerts.size() + " certs.");
    }
    return eeCerts;
  }
  private Collection<X509Certificate> getMatchingCACerts(  ReverseState currentState,  List<CertStore> certStores) throws CertificateException, CertStoreException, IOException {
    X509CertSelector sel=new X509CertSelector();
    sel.setIssuer(currentState.subjectDN);
    sel.setCertificateValid(date);
    sel.addPathToName(4,targetCertConstraints.getSubjectAsBytes());
    if (currentState.explicitPolicy == 0)     sel.setPolicy(getMatchingPolicies());
    sel.setBasicConstraints(0);
    ArrayList<X509Certificate> reverseCerts=new ArrayList<X509Certificate>();
    addMatchingCerts(sel,certStores,reverseCerts,true);
    Collections.sort(reverseCerts,new PKIXCertComparator());
    if (debug != null)     debug.println("ReverseBuilder.getMatchingCACerts got " + reverseCerts.size() + " certs.");
    return reverseCerts;
  }
class PKIXCertComparator implements Comparator<X509Certificate> {
    private Debug debug=Debug.getInstance("certpath");
    public int compare(    X509Certificate cert1,    X509Certificate cert2){
      if (cert1.getSubjectX500Principal().equals(targetSubjectDN)) {
        return -1;
      }
      if (cert2.getSubjectX500Principal().equals(targetSubjectDN)) {
        return 1;
      }
      int targetDist1;
      int targetDist2;
      try {
        X500Name targetSubjectName=X500Name.asX500Name(targetSubjectDN);
        targetDist1=Builder.targetDistance(null,cert1,targetSubjectName);
        targetDist2=Builder.targetDistance(null,cert2,targetSubjectName);
      }
 catch (      IOException e) {
        if (debug != null) {
          debug.println("IOException in call to Builder.targetDistance");
          e.printStackTrace();
        }
        throw new ClassCastException("Invalid target subject distinguished name");
      }
      if (targetDist1 == targetDist2)       return 0;
      if (targetDist1 == -1)       return 1;
      if (targetDist1 < targetDist2)       return -1;
      return 1;
    }
  }
  /** 
 * Verifies a matching certificate.
 * This method executes any of the validation steps in the PKIX path validation
 * algorithm which were not satisfied via filtering out non-compliant
 * certificates with certificate matching rules.
 * If the last certificate is being verified (the one whose subject
 * matches the target subject, then the steps in Section 6.1.4 of the
 * Certification Path Validation algorithm are NOT executed,
 * regardless of whether or not the last cert is an end-entity
 * cert or not. This allows callers to certify CA certs as
 * well as EE certs.
 * @param cert the certificate to be verified
 * @param currentState the current state against which the cert is verified
 * @param certPathList the certPathList generated thus far
 */
  void verifyCert(  X509Certificate cert,  State currState,  List<X509Certificate> certPathList) throws GeneralSecurityException {
    if (debug != null) {
      debug.println("ReverseBuilder.verifyCert(SN: " + Debug.toHexString(cert.getSerialNumber()) + "\n  Subject: "+ cert.getSubjectX500Principal()+ ")");
    }
    ReverseState currentState=(ReverseState)currState;
    if (currentState.isInitial()) {
      return;
    }
    if ((certPathList != null) && (!certPathList.isEmpty())) {
      List<X509Certificate> reverseCertList=new ArrayList<X509Certificate>();
      for (      X509Certificate c : certPathList) {
        reverseCertList.add(0,c);
      }
      boolean policyMappingFound=false;
      for (      X509Certificate cpListCert : reverseCertList) {
        X509CertImpl cpListCertImpl=X509CertImpl.toImpl(cpListCert);
        PolicyMappingsExtension policyMappingsExt=cpListCertImpl.getPolicyMappingsExtension();
        if (policyMappingsExt != null) {
          policyMappingFound=true;
        }
        if (debug != null)         debug.println("policyMappingFound = " + policyMappingFound);
        if (cert.equals(cpListCert)) {
          if ((buildParams.isPolicyMappingInhibited()) || (!policyMappingFound)) {
            if (debug != null)             debug.println("loop detected!!");
            throw new CertPathValidatorException("loop detected");
          }
        }
      }
    }
    boolean finalCert=cert.getSubjectX500Principal().equals(targetSubjectDN);
    boolean caCert=(cert.getBasicConstraints() != -1 ? true : false);
    if (!finalCert) {
      if (!caCert)       throw new CertPathValidatorException("cert is NOT a CA cert");
      if ((currentState.remainingCACerts <= 0) && !X509CertImpl.isSelfIssued(cert)) {
        throw new CertPathValidatorException("pathLenConstraint violated, path too long",null,null,-1,PKIXReason.PATH_TOO_LONG);
      }
      KeyChecker.verifyCAKeyUsage(cert);
    }
 else {
      if (targetCertConstraints.match(cert) == false) {
        throw new CertPathValidatorException("target certificate " + "constraints check failed");
      }
    }
    if (buildParams.isRevocationEnabled()) {
      currentState.crlChecker.check(cert,currentState.pubKey,currentState.crlSign);
    }
    if (finalCert || !X509CertImpl.isSelfIssued(cert)) {
      if (currentState.nc != null) {
        try {
          if (!currentState.nc.verify(cert)) {
            throw new CertPathValidatorException("name constraints check failed",null,null,-1,PKIXReason.INVALID_NAME);
          }
        }
 catch (        IOException ioe) {
          throw new CertPathValidatorException(ioe);
        }
      }
    }
    X509CertImpl certImpl=X509CertImpl.toImpl(cert);
    currentState.rootNode=PolicyChecker.processPolicies(currentState.certIndex,initPolicies,currentState.explicitPolicy,currentState.policyMapping,currentState.inhibitAnyPolicy,buildParams.getPolicyQualifiersRejected(),currentState.rootNode,certImpl,finalCert);
    Set<String> unresolvedCritExts=cert.getCriticalExtensionOIDs();
    if (unresolvedCritExts == null) {
      unresolvedCritExts=Collections.<String>emptySet();
    }
    currentState.algorithmChecker.check(cert,unresolvedCritExts);
    for (    PKIXCertPathChecker checker : currentState.userCheckers) {
      checker.check(cert,unresolvedCritExts);
    }
    if (!unresolvedCritExts.isEmpty()) {
      unresolvedCritExts.remove(PKIXExtensions.BasicConstraints_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.NameConstraints_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.CertificatePolicies_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.PolicyMappings_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.PolicyConstraints_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.KeyUsage_Id.toString());
      unresolvedCritExts.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
      if (!unresolvedCritExts.isEmpty())       throw new CertPathValidatorException("Unrecognized critical extension(s)",null,null,-1,PKIXReason.UNRECOGNIZED_CRIT_EXT);
    }
    if (buildParams.getSigProvider() != null) {
      cert.verify(currentState.pubKey,buildParams.getSigProvider());
    }
 else {
      cert.verify(currentState.pubKey);
    }
  }
  /** 
 * Verifies whether the input certificate completes the path.
 * This checks whether the cert is the target certificate.
 * @param cert the certificate to test
 * @return a boolean value indicating whether the cert completes the path.
 */
  boolean isPathCompleted(  X509Certificate cert){
    return cert.getSubjectX500Principal().equals(targetSubjectDN);
  }
  /** 
 * Adds the certificate to the certPathList
 * @param cert the certificate to be added
 * @param certPathList the certification path list
 */
  void addCertToPath(  X509Certificate cert,  LinkedList<X509Certificate> certPathList){
    certPathList.addLast(cert);
  }
  /** 
 * Removes final certificate from the certPathList
 * @param certPathList the certification path list
 */
  void removeFinalCertFromPath(  LinkedList<X509Certificate> certPathList){
    certPathList.removeLast();
  }
}
