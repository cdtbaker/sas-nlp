package sun.security.provider.certpath;
import java.util.*;
import java.security.cert.*;
import java.security.cert.PKIXReason;
import sun.security.util.Debug;
import sun.security.x509.PKIXExtensions;
/** 
 * KeyChecker is a <code>PKIXCertPathChecker</code> that checks that the
 * keyCertSign bit is set in the keyUsage extension in an intermediate CA
 * certificate. It also checks whether the final certificate in a
 * certification path meets the specified target constraints specified as
 * a CertSelector in the PKIXParameters passed to the CertPathValidator.
 * @since       1.4
 * @author      Yassir Elley
 */
class KeyChecker extends PKIXCertPathChecker {
  private static final Debug debug=Debug.getInstance("certpath");
  private static final int keyCertSign=5;
  private final int certPathLen;
  private CertSelector targetConstraints;
  private int remainingCerts;
  private Set<String> supportedExts;
  /** 
 * Default Constructor
 * @param certPathLen allowable cert path length
 * @param targetCertSel a CertSelector object specifying the constraints
 * on the target certificate
 */
  KeyChecker(  int certPathLen,  CertSelector targetCertSel) throws CertPathValidatorException {
    this.certPathLen=certPathLen;
    this.targetConstraints=targetCertSel;
    init(false);
  }
  /** 
 * Initializes the internal state of the checker from parameters
 * specified in the constructor
 */
  public void init(  boolean forward) throws CertPathValidatorException {
    if (!forward) {
      remainingCerts=certPathLen;
    }
 else {
      throw new CertPathValidatorException("forward checking not supported");
    }
  }
  public final boolean isForwardCheckingSupported(){
    return false;
  }
  public Set<String> getSupportedExtensions(){
    if (supportedExts == null) {
      supportedExts=new HashSet<String>();
      supportedExts.add(PKIXExtensions.KeyUsage_Id.toString());
      supportedExts.add(PKIXExtensions.ExtendedKeyUsage_Id.toString());
      supportedExts.add(PKIXExtensions.SubjectAlternativeName_Id.toString());
      supportedExts=Collections.unmodifiableSet(supportedExts);
    }
    return supportedExts;
  }
  /** 
 * Checks that keyUsage and target constraints are satisfied by
 * the specified certificate.
 * @param cert the Certificate
 * @param unresolvedCritExts the unresolved critical extensions
 * @exception CertPathValidatorException Exception thrown if certificate
 * does not verify
 */
  public void check(  Certificate cert,  Collection<String> unresCritExts) throws CertPathValidatorException {
    X509Certificate currCert=(X509Certificate)cert;
    remainingCerts--;
    if (remainingCerts == 0) {
      if ((targetConstraints != null) && (targetConstraints.match(currCert) == false)) {
        throw new CertPathValidatorException("target certificate " + "constraints check failed");
      }
    }
 else {
      verifyCAKeyUsage(currCert);
    }
    if (unresCritExts != null && !unresCritExts.isEmpty()) {
      unresCritExts.remove(PKIXExtensions.KeyUsage_Id.toString());
      unresCritExts.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
      unresCritExts.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
    }
  }
  /** 
 * Static method to verify that the key usage and extended key usage
 * extension in a CA cert. The key usage extension, if present, must
 * assert the keyCertSign bit. The extended key usage extension, if
 * present, must include anyExtendedKeyUsage.
 */
  static void verifyCAKeyUsage(  X509Certificate cert) throws CertPathValidatorException {
    String msg="CA key usage";
    if (debug != null) {
      debug.println("KeyChecker.verifyCAKeyUsage() ---checking " + msg + "...");
    }
    boolean[] keyUsageBits=cert.getKeyUsage();
    if (keyUsageBits == null) {
      return;
    }
    if (!keyUsageBits[keyCertSign]) {
      throw new CertPathValidatorException(msg + " check failed: keyCertSign bit is not set",null,null,-1,PKIXReason.INVALID_KEY_USAGE);
    }
    if (debug != null) {
      debug.println("KeyChecker.verifyCAKeyUsage() " + msg + " verified.");
    }
  }
}
