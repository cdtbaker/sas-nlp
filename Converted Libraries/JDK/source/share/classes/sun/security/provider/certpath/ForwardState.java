package sun.security.provider.certpath;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
/** 
 * A specification of a forward PKIX validation state
 * which is initialized by each build and updated each time a
 * certificate is added to the current path.
 * @since       1.4
 * @author      Yassir Elley
 */
class ForwardState implements State {
  private static final Debug debug=Debug.getInstance("certpath");
  X500Principal issuerDN;
  X509CertImpl cert;
  HashSet<GeneralNameInterface> subjectNamesTraversed;
  int traversedCACerts;
  private boolean init=true;
  public CrlRevocationChecker crlChecker;
  ArrayList<PKIXCertPathChecker> forwardCheckers;
  boolean keyParamsNeededFlag=false;
  /** 
 * Returns a boolean flag indicating if the state is initial
 * (just starting)
 * @return boolean flag indicating if the state is initial (just starting)
 */
  public boolean isInitial(){
    return init;
  }
  /** 
 * Return boolean flag indicating whether a public key that needs to inherit
 * key parameters has been encountered.
 * @return boolean true if key needing to inherit parameters has been
 * encountered; false otherwise.
 */
  public boolean keyParamsNeeded(){
    return keyParamsNeededFlag;
  }
  /** 
 * Display state for debugging purposes
 */
  public String toString(){
    StringBuffer sb=new StringBuffer();
    try {
      sb.append("State [");
      sb.append("\n  issuerDN of last cert: " + issuerDN);
      sb.append("\n  traversedCACerts: " + traversedCACerts);
      sb.append("\n  init: " + String.valueOf(init));
      sb.append("\n  keyParamsNeeded: " + String.valueOf(keyParamsNeededFlag));
      sb.append("\n  subjectNamesTraversed: \n" + subjectNamesTraversed);
      sb.append("]\n");
    }
 catch (    Exception e) {
      if (debug != null) {
        debug.println("ForwardState.toString() unexpected exception");
        e.printStackTrace();
      }
    }
    return sb.toString();
  }
  /** 
 * Initialize the state.
 * @param certPathCheckers the list of user-defined PKIXCertPathCheckers
 */
  public void initState(  List<PKIXCertPathChecker> certPathCheckers) throws CertPathValidatorException {
    subjectNamesTraversed=new HashSet<GeneralNameInterface>();
    traversedCACerts=0;
    forwardCheckers=new ArrayList<PKIXCertPathChecker>();
    if (certPathCheckers != null) {
      for (      PKIXCertPathChecker checker : certPathCheckers) {
        if (checker.isForwardCheckingSupported()) {
          checker.init(true);
          forwardCheckers.add(checker);
        }
      }
    }
    init=true;
  }
  /** 
 * Update the state with the next certificate added to the path.
 * @param cert the certificate which is used to update the state
 */
  public void updateState(  X509Certificate cert) throws CertificateException, IOException, CertPathValidatorException {
    if (cert == null)     return;
    X509CertImpl icert=X509CertImpl.toImpl(cert);
    PublicKey newKey=icert.getPublicKey();
    if (newKey instanceof DSAPublicKey && ((DSAPublicKey)newKey).getParams() == null) {
      keyParamsNeededFlag=true;
    }
    this.cert=icert;
    issuerDN=cert.getIssuerX500Principal();
    if (!X509CertImpl.isSelfIssued(cert)) {
      if (!init && cert.getBasicConstraints() != -1) {
        traversedCACerts++;
      }
    }
    if (init || !X509CertImpl.isSelfIssued(cert)) {
      X500Principal subjName=cert.getSubjectX500Principal();
      subjectNamesTraversed.add(X500Name.asX500Name(subjName));
      try {
        SubjectAlternativeNameExtension subjAltNameExt=icert.getSubjectAlternativeNameExtension();
        if (subjAltNameExt != null) {
          GeneralNames gNames=(GeneralNames)subjAltNameExt.get(SubjectAlternativeNameExtension.SUBJECT_NAME);
          for (Iterator<GeneralName> t=gNames.iterator(); t.hasNext(); ) {
            GeneralNameInterface gName=t.next().getName();
            subjectNamesTraversed.add(gName);
          }
        }
      }
 catch (      Exception e) {
        if (debug != null) {
          debug.println("ForwardState.updateState() unexpected " + "exception");
          e.printStackTrace();
        }
        throw new CertPathValidatorException(e);
      }
    }
    init=false;
  }
  public Object clone(){
    try {
      ForwardState clonedState=(ForwardState)super.clone();
      clonedState.forwardCheckers=(ArrayList<PKIXCertPathChecker>)forwardCheckers.clone();
      ListIterator<PKIXCertPathChecker> li=clonedState.forwardCheckers.listIterator();
      while (li.hasNext()) {
        PKIXCertPathChecker checker=li.next();
        if (checker instanceof Cloneable) {
          li.set((PKIXCertPathChecker)checker.clone());
        }
      }
      clonedState.subjectNamesTraversed=(HashSet<GeneralNameInterface>)subjectNamesTraversed.clone();
      return clonedState;
    }
 catch (    CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }
}
