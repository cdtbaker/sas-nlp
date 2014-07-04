package sun.security.provider.certpath;
import java.io.IOException;
import java.util.*;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXReason;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.cert.X509CertSelector;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.PolicyMappingsExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.SerialNumber;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import java.math.BigInteger;
/** 
 * This class represents a forward builder, which is able to retrieve
 * matching certificates from CertStores and verify a particular certificate
 * against a ForwardState.
 * @since       1.4
 * @author      Yassir Elley
 * @author      Sean Mullan
 */
class ForwardBuilder extends Builder {
  private static final Debug debug=Debug.getInstance("certpath");
  private final Set<X509Certificate> trustedCerts;
  private final Set<X500Principal> trustedSubjectDNs;
  private final Set<TrustAnchor> trustAnchors;
  private X509CertSelector eeSelector;
  private AdaptableX509CertSelector caSelector;
  private X509CertSelector caTargetSelector;
  TrustAnchor trustAnchor;
  private Comparator<X509Certificate> comparator;
  private boolean searchAllCertStores=true;
  private boolean onlyEECert=false;
  /** 
 * Initialize the builder with the input parameters.
 * @param params the parameter set used to build a certification path
 */
  ForwardBuilder(  PKIXBuilderParameters buildParams,  X500Principal targetSubjectDN,  boolean searchAllCertStores,  boolean onlyEECert){
    super(buildParams,targetSubjectDN);
    trustAnchors=buildParams.getTrustAnchors();
    trustedCerts=new HashSet<X509Certificate>(trustAnchors.size());
    trustedSubjectDNs=new HashSet<X500Principal>(trustAnchors.size());
    for (    TrustAnchor anchor : trustAnchors) {
      X509Certificate trustedCert=anchor.getTrustedCert();
      if (trustedCert != null) {
        trustedCerts.add(trustedCert);
        trustedSubjectDNs.add(trustedCert.getSubjectX500Principal());
      }
 else {
        trustedSubjectDNs.add(anchor.getCA());
      }
    }
    comparator=new PKIXCertComparator(trustedSubjectDNs);
    this.searchAllCertStores=searchAllCertStores;
    this.onlyEECert=onlyEECert;
  }
  /** 
 * Retrieves all certs from the specified CertStores that satisfy the
 * requirements specified in the parameters and the current
 * PKIX state (name constraints, policy constraints, etc).
 * @param currentState the current state.
 * Must be an instance of <code>ForwardState</code>
 * @param certStores list of CertStores
 */
  Collection<X509Certificate> getMatchingCerts(  State currentState,  List<CertStore> certStores) throws CertStoreException, CertificateException, IOException {
    if (debug != null) {
      debug.println("ForwardBuilder.getMatchingCerts()...");
    }
    ForwardState currState=(ForwardState)currentState;
    Set<X509Certificate> certs=new TreeSet<X509Certificate>(comparator);
    if (currState.isInitial()) {
      getMatchingEECerts(currState,certStores,certs);
    }
    getMatchingCACerts(currState,certStores,certs);
    return certs;
  }
  private void getMatchingEECerts(  ForwardState currentState,  List<CertStore> certStores,  Collection<X509Certificate> eeCerts) throws IOException {
    if (debug != null) {
      debug.println("ForwardBuilder.getMatchingEECerts()...");
    }
    if (eeSelector == null) {
      eeSelector=(X509CertSelector)targetCertConstraints.clone();
      eeSelector.setCertificateValid(date);
      if (buildParams.isExplicitPolicyRequired()) {
        eeSelector.setPolicy(getMatchingPolicies());
      }
      eeSelector.setBasicConstraints(-2);
    }
    addMatchingCerts(eeSelector,certStores,eeCerts,searchAllCertStores);
  }
  /** 
 * Retrieves all CA certificates which satisfy constraints
 * and requirements specified in the parameters and PKIX state.
 */
  private void getMatchingCACerts(  ForwardState currentState,  List<CertStore> certStores,  Collection<X509Certificate> caCerts) throws IOException {
    if (debug != null) {
      debug.println("ForwardBuilder.getMatchingCACerts()...");
    }
    int initialSize=caCerts.size();
    X509CertSelector sel=null;
    if (currentState.isInitial()) {
      if (targetCertConstraints.getBasicConstraints() == -2) {
        return;
      }
      if (debug != null) {
        debug.println("ForwardBuilder.getMatchingCACerts(): ca is target");
      }
      if (caTargetSelector == null) {
        caTargetSelector=(X509CertSelector)targetCertConstraints.clone();
        if (buildParams.isExplicitPolicyRequired())         caTargetSelector.setPolicy(getMatchingPolicies());
      }
      sel=caTargetSelector;
    }
 else {
      if (caSelector == null) {
        caSelector=new AdaptableX509CertSelector();
        if (buildParams.isExplicitPolicyRequired())         caSelector.setPolicy(getMatchingPolicies());
      }
      caSelector.setSubject(currentState.issuerDN);
      CertPathHelper.setPathToNames(caSelector,currentState.subjectNamesTraversed);
      AuthorityKeyIdentifierExtension akidext=currentState.cert.getAuthorityKeyIdentifierExtension();
      caSelector.parseAuthorityKeyIdentifierExtension(akidext);
      caSelector.setValidityPeriod(currentState.cert.getNotBefore(),currentState.cert.getNotAfter());
      sel=caSelector;
    }
    sel.setBasicConstraints(-1);
    for (    X509Certificate trustedCert : trustedCerts) {
      if (sel.match(trustedCert)) {
        if (debug != null) {
          debug.println("ForwardBuilder.getMatchingCACerts: " + "found matching trust anchor");
        }
        if (caCerts.add(trustedCert) && !searchAllCertStores) {
          return;
        }
      }
    }
    sel.setCertificateValid(date);
    sel.setBasicConstraints(currentState.traversedCACerts);
    if (currentState.isInitial() || (buildParams.getMaxPathLength() == -1) || (buildParams.getMaxPathLength() > currentState.traversedCACerts)) {
      if (addMatchingCerts(sel,certStores,caCerts,searchAllCertStores) && !searchAllCertStores) {
        return;
      }
    }
    if (!currentState.isInitial() && Builder.USE_AIA) {
      AuthorityInfoAccessExtension aiaExt=currentState.cert.getAuthorityInfoAccessExtension();
      if (aiaExt != null) {
        getCerts(aiaExt,caCerts);
      }
    }
    if (debug != null) {
      int numCerts=caCerts.size() - initialSize;
      debug.println("ForwardBuilder.getMatchingCACerts: found " + numCerts + " CA certs");
    }
  }
  /** 
 * Download Certificates from the given AIA and add them to the
 * specified Collection.
 */
  private boolean getCerts(  AuthorityInfoAccessExtension aiaExt,  Collection<X509Certificate> certs){
    if (Builder.USE_AIA == false) {
      return false;
    }
    List<AccessDescription> adList=aiaExt.getAccessDescriptions();
    if (adList == null || adList.isEmpty()) {
      return false;
    }
    boolean add=false;
    for (    AccessDescription ad : adList) {
      CertStore cs=URICertStore.getInstance(ad);
      try {
        if (certs.addAll((Collection<X509Certificate>)cs.getCertificates(caSelector))) {
          add=true;
          if (!searchAllCertStores) {
            return true;
          }
        }
      }
 catch (      CertStoreException cse) {
        if (debug != null) {
          debug.println("exception getting certs from CertStore:");
          cse.printStackTrace();
        }
        continue;
      }
    }
    return add;
  }
  /** 
 * This inner class compares 2 PKIX certificates according to which
 * should be tried first when building a path from the target.
 * The preference order is as follows:
 * Given trusted certificate(s):
 * Subject:ou=D,ou=C,o=B,c=A
 * Preference order for current cert:
 * 1) Issuer matches a trusted subject
 * Issuer: ou=D,ou=C,o=B,c=A
 * 2) Issuer is a descendant of a trusted subject (in order of
 * number of links to the trusted subject)
 * a) Issuer: ou=E,ou=D,ou=C,o=B,c=A        [links=1]
 * b) Issuer: ou=F,ou=E,ou=D,ou=C,ou=B,c=A  [links=2]
 * 3) Issuer is an ancestor of a trusted subject (in order of number of
 * links to the trusted subject)
 * a) Issuer: ou=C,o=B,c=A [links=1]
 * b) Issuer: o=B,c=A      [links=2]
 * 4) Issuer is in the same namespace as a trusted subject (in order of
 * number of links to the trusted subject)
 * a) Issuer: ou=G,ou=C,o=B,c=A  [links=2]
 * b) Issuer: ou=H,o=B,c=A       [links=3]
 * 5) Issuer is an ancestor of certificate subject (in order of number
 * of links to the certificate subject)
 * a) Issuer:  ou=K,o=J,c=A
 * Subject: ou=L,ou=K,o=J,c=A
 * b) Issuer:  o=J,c=A
 * Subject: ou=L,ou=K,0=J,c=A
 * 6) Any other certificates
 */
static class PKIXCertComparator implements Comparator<X509Certificate> {
    final static String METHOD_NME="PKIXCertComparator.compare()";
    private final Set<X500Principal> trustedSubjectDNs;
    PKIXCertComparator(    Set<X500Principal> trustedSubjectDNs){
      this.trustedSubjectDNs=trustedSubjectDNs;
    }
    /** 
 * @param oCert1 First X509Certificate to be compared
 * @param oCert2 Second X509Certificate to be compared
 * @return -1 if oCert1 is preferable to oCert2, or
 * if oCert1 and oCert2 are equally preferable (in this
 * case it doesn't matter which is preferable, but we don't
 * return 0 because the comparator would behave strangely
 * when used in a SortedSet).
 * 1 if oCert2 is preferable to oCert1
 * 0 if oCert1.equals(oCert2). We only return 0 if the
 * certs are equal so that this comparator behaves
 * correctly when used in a SortedSet.
 * @throws ClassCastException if either argument is not of type
 * X509Certificate
 */
    public int compare(    X509Certificate oCert1,    X509Certificate oCert2){
      if (oCert1.equals(oCert2))       return 0;
      X500Principal cIssuer1=oCert1.getIssuerX500Principal();
      X500Principal cIssuer2=oCert2.getIssuerX500Principal();
      X500Name cIssuer1Name=X500Name.asX500Name(cIssuer1);
      X500Name cIssuer2Name=X500Name.asX500Name(cIssuer2);
      if (debug != null) {
        debug.println(METHOD_NME + " o1 Issuer:  " + cIssuer1);
        debug.println(METHOD_NME + " o2 Issuer:  " + cIssuer2);
      }
      if (debug != null) {
        debug.println(METHOD_NME + " MATCH TRUSTED SUBJECT TEST...");
      }
      boolean m1=trustedSubjectDNs.contains(cIssuer1);
      boolean m2=trustedSubjectDNs.contains(cIssuer2);
      if (debug != null) {
        debug.println(METHOD_NME + " m1: " + m1);
        debug.println(METHOD_NME + " m2: " + m2);
      }
      if (m1 && m2) {
        return -1;
      }
 else       if (m1) {
        return -1;
      }
 else       if (m2) {
        return 1;
      }
      if (debug != null) {
        debug.println(METHOD_NME + " NAMING DESCENDANT TEST...");
      }
      for (      X500Principal tSubject : trustedSubjectDNs) {
        X500Name tSubjectName=X500Name.asX500Name(tSubject);
        int distanceTto1=Builder.distance(tSubjectName,cIssuer1Name,-1);
        int distanceTto2=Builder.distance(tSubjectName,cIssuer2Name,-1);
        if (debug != null) {
          debug.println(METHOD_NME + " distanceTto1: " + distanceTto1);
          debug.println(METHOD_NME + " distanceTto2: " + distanceTto2);
        }
        if (distanceTto1 > 0 || distanceTto2 > 0) {
          if (distanceTto1 == distanceTto2) {
            return -1;
          }
 else           if (distanceTto1 > 0 && distanceTto2 <= 0) {
            return -1;
          }
 else           if (distanceTto1 <= 0 && distanceTto2 > 0) {
            return 1;
          }
 else           if (distanceTto1 < distanceTto2) {
            return -1;
          }
 else {
            return 1;
          }
        }
      }
      if (debug != null) {
        debug.println(METHOD_NME + " NAMING ANCESTOR TEST...");
      }
      for (      X500Principal tSubject : trustedSubjectDNs) {
        X500Name tSubjectName=X500Name.asX500Name(tSubject);
        int distanceTto1=Builder.distance(tSubjectName,cIssuer1Name,Integer.MAX_VALUE);
        int distanceTto2=Builder.distance(tSubjectName,cIssuer2Name,Integer.MAX_VALUE);
        if (debug != null) {
          debug.println(METHOD_NME + " distanceTto1: " + distanceTto1);
          debug.println(METHOD_NME + " distanceTto2: " + distanceTto2);
        }
        if (distanceTto1 < 0 || distanceTto2 < 0) {
          if (distanceTto1 == distanceTto2) {
            return -1;
          }
 else           if (distanceTto1 < 0 && distanceTto2 >= 0) {
            return -1;
          }
 else           if (distanceTto1 >= 0 && distanceTto2 < 0) {
            return 1;
          }
 else           if (distanceTto1 > distanceTto2) {
            return -1;
          }
 else {
            return 1;
          }
        }
      }
      if (debug != null) {
        debug.println(METHOD_NME + " SAME NAMESPACE AS TRUSTED TEST...");
      }
      for (      X500Principal tSubject : trustedSubjectDNs) {
        X500Name tSubjectName=X500Name.asX500Name(tSubject);
        X500Name tAo1=tSubjectName.commonAncestor(cIssuer1Name);
        X500Name tAo2=tSubjectName.commonAncestor(cIssuer2Name);
        if (debug != null) {
          debug.println(METHOD_NME + " tAo1: " + String.valueOf(tAo1));
          debug.println(METHOD_NME + " tAo2: " + String.valueOf(tAo2));
        }
        if (tAo1 != null || tAo2 != null) {
          if (tAo1 != null && tAo2 != null) {
            int hopsTto1=Builder.hops(tSubjectName,cIssuer1Name,Integer.MAX_VALUE);
            int hopsTto2=Builder.hops(tSubjectName,cIssuer2Name,Integer.MAX_VALUE);
            if (debug != null) {
              debug.println(METHOD_NME + " hopsTto1: " + hopsTto1);
              debug.println(METHOD_NME + " hopsTto2: " + hopsTto2);
            }
            if (hopsTto1 == hopsTto2) {
            }
 else             if (hopsTto1 > hopsTto2) {
              return 1;
            }
 else {
              return -1;
            }
          }
 else           if (tAo1 == null) {
            return 1;
          }
 else {
            return -1;
          }
        }
      }
      if (debug != null) {
        debug.println(METHOD_NME + " CERT ISSUER/SUBJECT COMPARISON TEST...");
      }
      X500Principal cSubject1=oCert1.getSubjectX500Principal();
      X500Principal cSubject2=oCert2.getSubjectX500Principal();
      X500Name cSubject1Name=X500Name.asX500Name(cSubject1);
      X500Name cSubject2Name=X500Name.asX500Name(cSubject2);
      if (debug != null) {
        debug.println(METHOD_NME + " o1 Subject: " + cSubject1);
        debug.println(METHOD_NME + " o2 Subject: " + cSubject2);
      }
      int distanceStoI1=Builder.distance(cSubject1Name,cIssuer1Name,Integer.MAX_VALUE);
      int distanceStoI2=Builder.distance(cSubject2Name,cIssuer2Name,Integer.MAX_VALUE);
      if (debug != null) {
        debug.println(METHOD_NME + " distanceStoI1: " + distanceStoI1);
        debug.println(METHOD_NME + " distanceStoI2: " + distanceStoI2);
      }
      if (distanceStoI2 > distanceStoI1) {
        return -1;
      }
 else       if (distanceStoI2 < distanceStoI1) {
        return 1;
      }
      if (debug != null) {
        debug.println(METHOD_NME + " no tests matched; RETURN 0");
      }
      return -1;
    }
  }
  /** 
 * Verifies a matching certificate.
 * This method executes the validation steps in the PKIX path
 * validation algorithm <draft-ietf-pkix-new-part1-08.txt> which were
 * not satisfied by the selection criteria used by getCertificates()
 * to find the certs and only the steps that can be executed in a
 * forward direction (target to trust anchor). Those steps that can
 * only be executed in a reverse direction are deferred until the
 * complete path has been built.
 * Trust anchor certs are not validated, but are used to verify the
 * signature and revocation status of the previous cert.
 * If the last certificate is being verified (the one whose subject
 * matches the target subject, then steps in 6.1.4 of the PKIX
 * Certification Path Validation algorithm are NOT executed,
 * regardless of whether or not the last cert is an end-entity
 * cert or not. This allows callers to certify CA certs as
 * well as EE certs.
 * @param cert the certificate to be verified
 * @param currentState the current state against which the cert is verified
 * @param certPathList the certPathList generated thus far
 */
  void verifyCert(  X509Certificate cert,  State currentState,  List<X509Certificate> certPathList) throws GeneralSecurityException {
    if (debug != null) {
      debug.println("ForwardBuilder.verifyCert(SN: " + Debug.toHexString(cert.getSerialNumber()) + "\n  Issuer: "+ cert.getIssuerX500Principal()+ ")"+ "\n  Subject: "+ cert.getSubjectX500Principal()+ ")");
    }
    ForwardState currState=(ForwardState)currentState;
    if (certPathList != null) {
      boolean policyMappingFound=false;
      for (      X509Certificate cpListCert : certPathList) {
        X509CertImpl cpListCertImpl=X509CertImpl.toImpl(cpListCert);
        PolicyMappingsExtension policyMappingsExt=cpListCertImpl.getPolicyMappingsExtension();
        if (policyMappingsExt != null) {
          policyMappingFound=true;
        }
        if (debug != null) {
          debug.println("policyMappingFound = " + policyMappingFound);
        }
        if (cert.equals(cpListCert)) {
          if ((buildParams.isPolicyMappingInhibited()) || (!policyMappingFound)) {
            if (debug != null) {
              debug.println("loop detected!!");
            }
            throw new CertPathValidatorException("loop detected");
          }
        }
      }
    }
    boolean isTrustedCert=trustedCerts.contains(cert);
    if (!isTrustedCert) {
      Set<String> unresCritExts=cert.getCriticalExtensionOIDs();
      if (unresCritExts == null) {
        unresCritExts=Collections.<String>emptySet();
      }
      for (      PKIXCertPathChecker checker : currState.forwardCheckers) {
        checker.check(cert,unresCritExts);
      }
      for (      PKIXCertPathChecker checker : buildParams.getCertPathCheckers()) {
        if (!checker.isForwardCheckingSupported()) {
          Set<String> supportedExts=checker.getSupportedExtensions();
          if (supportedExts != null) {
            unresCritExts.removeAll(supportedExts);
          }
        }
      }
      if (!unresCritExts.isEmpty()) {
        unresCritExts.remove(PKIXExtensions.BasicConstraints_Id.toString());
        unresCritExts.remove(PKIXExtensions.NameConstraints_Id.toString());
        unresCritExts.remove(PKIXExtensions.CertificatePolicies_Id.toString());
        unresCritExts.remove(PKIXExtensions.PolicyMappings_Id.toString());
        unresCritExts.remove(PKIXExtensions.PolicyConstraints_Id.toString());
        unresCritExts.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
        unresCritExts.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
        unresCritExts.remove(PKIXExtensions.KeyUsage_Id.toString());
        unresCritExts.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
        if (!unresCritExts.isEmpty())         throw new CertPathValidatorException("Unrecognized critical extension(s)",null,null,-1,PKIXReason.UNRECOGNIZED_CRIT_EXT);
      }
    }
    if (currState.isInitial()) {
      return;
    }
    if (!isTrustedCert) {
      if (cert.getBasicConstraints() == -1) {
        throw new CertificateException("cert is NOT a CA cert");
      }
      KeyChecker.verifyCAKeyUsage(cert);
    }
    if (buildParams.isRevocationEnabled()) {
      if (CrlRevocationChecker.certCanSignCrl(cert)) {
        if (!currState.keyParamsNeeded())         currState.crlChecker.check(currState.cert,cert.getPublicKey(),true);
      }
    }
    if (!currState.keyParamsNeeded()) {
      (currState.cert).verify(cert.getPublicKey(),buildParams.getSigProvider());
    }
  }
  /** 
 * Verifies whether the input certificate completes the path.
 * Checks the cert against each trust anchor that was specified, in order,
 * and returns true as soon as it finds a valid anchor.
 * Returns true if the cert matches a trust anchor specified as a
 * certificate or if the cert verifies with a trust anchor that
 * was specified as a trusted {pubkey, caname} pair. Returns false if none
 * of the trust anchors are valid for this cert.
 * @param cert the certificate to test
 * @return a boolean value indicating whether the cert completes the path.
 */
  boolean isPathCompleted(  X509Certificate cert){
    for (    TrustAnchor anchor : trustAnchors) {
      if (anchor.getTrustedCert() != null) {
        if (cert.equals(anchor.getTrustedCert())) {
          this.trustAnchor=anchor;
          return true;
        }
 else {
          continue;
        }
      }
 else {
        X500Principal principal=anchor.getCA();
        java.security.PublicKey publicKey=anchor.getCAPublicKey();
        if (principal != null && publicKey != null && principal.equals(cert.getSubjectX500Principal())) {
          if (publicKey.equals(cert.getPublicKey())) {
            this.trustAnchor=anchor;
            return true;
          }
        }
        if (principal == null || !principal.equals(cert.getIssuerX500Principal())) {
          continue;
        }
      }
      if (buildParams.isRevocationEnabled()) {
        try {
          CrlRevocationChecker crlChecker=new CrlRevocationChecker(anchor,buildParams,null,onlyEECert);
          crlChecker.check(cert,anchor.getCAPublicKey(),true);
        }
 catch (        CertPathValidatorException cpve) {
          if (debug != null) {
            debug.println("ForwardBuilder.isPathCompleted() cpve");
            cpve.printStackTrace();
          }
          continue;
        }
      }
      try {
        cert.verify(anchor.getCAPublicKey(),buildParams.getSigProvider());
      }
 catch (      InvalidKeyException ike) {
        if (debug != null) {
          debug.println("ForwardBuilder.isPathCompleted() invalid " + "DSA key found");
        }
        continue;
      }
catch (      Exception e) {
        if (debug != null) {
          debug.println("ForwardBuilder.isPathCompleted() " + "unexpected exception");
          e.printStackTrace();
        }
        continue;
      }
      this.trustAnchor=anchor;
      return true;
    }
    return false;
  }
  /** 
 * Adds the certificate to the certPathList
 * @param cert the certificate to be added
 * @param certPathList the certification path list
 */
  void addCertToPath(  X509Certificate cert,  LinkedList<X509Certificate> certPathList){
    certPathList.addFirst(cert);
  }
  /** 
 * Removes final certificate from the certPathList
 * @param certPathList the certification path list
 */
  void removeFinalCertFromPath(  LinkedList<X509Certificate> certPathList){
    certPathList.removeFirst();
  }
}
