package sun.security.provider.certpath;
import sun.security.util.Debug;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.security.cert.CertificateRevokedException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
/** 
 * This class is initialized with a list of <code>PKIXCertPathChecker</code>s
 * and is used to verify the certificates in a <code>CertPath</code> by
 * feeding each certificate to each <code>PKIXCertPathChecker</code>.
 * @since       1.4
 * @author      Yassir Elley
 */
class PKIXMasterCertPathValidator {
  private static final Debug debug=Debug.getInstance("certpath");
  private List<PKIXCertPathChecker> certPathCheckers;
  /** 
 * Initializes the list of PKIXCertPathCheckers whose checks
 * will be performed on each certificate in the certpath.
 * @param certPathCheckers a List of checkers to use
 */
  PKIXMasterCertPathValidator(  List<PKIXCertPathChecker> certPathCheckers){
    this.certPathCheckers=certPathCheckers;
  }
  /** 
 * Validates a certification path consisting exclusively of
 * <code>X509Certificate</code>s using the
 * <code>PKIXCertPathChecker</code>s specified
 * in the constructor. It is assumed that the
 * <code>PKIXCertPathChecker</code>s
 * have been initialized with any input parameters they may need.
 * @param cpOriginal the original X509 CertPath passed in by the user
 * @param reversedCertList the reversed X509 CertPath (as a List)
 * @exception CertPathValidatorException Exception thrown if cert
 * path does not validate.
 */
  void validate(  CertPath cpOriginal,  List<X509Certificate> reversedCertList) throws CertPathValidatorException {
    int cpSize=reversedCertList.size();
    if (debug != null) {
      debug.println("--------------------------------------------------" + "------------");
      debug.println("Executing PKIX certification path validation " + "algorithm.");
    }
    for (int i=0; i < cpSize; i++) {
      if (debug != null)       debug.println("Checking cert" + (i + 1) + " ...");
      X509Certificate currCert=reversedCertList.get(i);
      Set<String> unresolvedCritExts=currCert.getCriticalExtensionOIDs();
      if (unresolvedCritExts == null) {
        unresolvedCritExts=Collections.<String>emptySet();
      }
      if (debug != null && !unresolvedCritExts.isEmpty()) {
        debug.println("Set of critical extensions:");
        for (        String oid : unresolvedCritExts) {
          debug.println(oid);
        }
      }
      CertPathValidatorException ocspCause=null;
      for (int j=0; j < certPathCheckers.size(); j++) {
        PKIXCertPathChecker currChecker=certPathCheckers.get(j);
        if (debug != null) {
          debug.println("-Using checker" + (j + 1) + " ... ["+ currChecker.getClass().getName()+ "]");
        }
        if (i == 0)         currChecker.init(false);
        try {
          currChecker.check(currCert,unresolvedCritExts);
          if (isRevocationCheck(currChecker,j,certPathCheckers)) {
            if (debug != null) {
              debug.println("-checker" + (j + 1) + " validation succeeded");
            }
            j++;
            continue;
          }
        }
 catch (        CertPathValidatorException cpve) {
          if (ocspCause != null && currChecker instanceof CrlRevocationChecker) {
            if (cpve.getReason() == BasicReason.REVOKED) {
              throw cpve;
            }
 else {
              throw ocspCause;
            }
          }
          CertPathValidatorException currentCause=new CertPathValidatorException(cpve.getMessage(),cpve.getCause(),cpOriginal,cpSize - (i + 1),cpve.getReason());
          if (cpve.getReason() == BasicReason.REVOKED) {
            throw currentCause;
          }
          if (!isRevocationCheck(currChecker,j,certPathCheckers)) {
            throw currentCause;
          }
          ocspCause=currentCause;
          if (debug != null) {
            debug.println(cpve.getMessage());
            debug.println("preparing to failover (from OCSP to CRLs)");
          }
        }
        if (debug != null)         debug.println("-checker" + (j + 1) + " validation succeeded");
      }
      if (debug != null)       debug.println("checking for unresolvedCritExts");
      if (!unresolvedCritExts.isEmpty()) {
        throw new CertPathValidatorException("unrecognized " + "critical extension(s)",null,cpOriginal,cpSize - (i + 1),PKIXReason.UNRECOGNIZED_CRIT_EXT);
      }
      if (debug != null)       debug.println("\ncert" + (i + 1) + " validation succeeded.\n");
    }
    if (debug != null) {
      debug.println("Cert path validation succeeded. (PKIX validation " + "algorithm)");
      debug.println("-------------------------------------------------" + "-------------");
    }
  }
  private static boolean isRevocationCheck(  PKIXCertPathChecker checker,  int index,  List<PKIXCertPathChecker> checkers){
    if (checker instanceof OCSPChecker && index + 1 < checkers.size()) {
      PKIXCertPathChecker nextChecker=checkers.get(index + 1);
      if (nextChecker instanceof CrlRevocationChecker) {
        return true;
      }
    }
    return false;
  }
}