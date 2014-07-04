package sun.security.jgss.krb5;
import org.ietf.jgss.*;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.*;
import sun.security.krb5.*;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.AccessControlContext;
import javax.security.auth.DestroyFailedException;
/** 
 * Implements the krb5 acceptor credential element.
 * @author Mayank Upadhyay
 * @since 1.4
 */
public class Krb5AcceptCredential implements Krb5CredElement {
  private static final long serialVersionUID=7714332137352567952L;
  private Krb5NameElement name;
  private Krb5Util.ServiceCreds screds;
  private Krb5AcceptCredential(  Krb5NameElement name,  Krb5Util.ServiceCreds creds){
    this.name=name;
    this.screds=creds;
  }
  static Krb5AcceptCredential getInstance(  final GSSCaller caller,  Krb5NameElement name) throws GSSException {
    final String serverPrinc=(name == null ? null : name.getKrb5PrincipalName().getName());
    final AccessControlContext acc=AccessController.getContext();
    Krb5Util.ServiceCreds creds=null;
    try {
      creds=AccessController.doPrivileged(new PrivilegedExceptionAction<Krb5Util.ServiceCreds>(){
        public Krb5Util.ServiceCreds run() throws Exception {
          return Krb5Util.getServiceCreds(caller == GSSCaller.CALLER_UNKNOWN ? GSSCaller.CALLER_ACCEPT : caller,serverPrinc,acc);
        }
      }
);
    }
 catch (    PrivilegedActionException e) {
      GSSException ge=new GSSException(GSSException.NO_CRED,-1,"Attempt to obtain new ACCEPT credentials failed!");
      ge.initCause(e.getException());
      throw ge;
    }
    if (creds == null)     throw new GSSException(GSSException.NO_CRED,-1,"Failed to find any Kerberos credentails");
    if (name == null) {
      String fullName=creds.getName();
      name=Krb5NameElement.getInstance(fullName,Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
    }
    return new Krb5AcceptCredential(name,creds);
  }
  /** 
 * Returns the principal name for this credential. The name
 * is in mechanism specific format.
 * @return GSSNameSpi representing principal name of this credential
 * @exception GSSException may be thrown
 */
  public final GSSNameSpi getName() throws GSSException {
    return name;
  }
  /** 
 * Returns the init lifetime remaining.
 * @return the init lifetime remaining in seconds
 * @exception GSSException may be thrown
 */
  public int getInitLifetime() throws GSSException {
    return 0;
  }
  /** 
 * Returns the accept lifetime remaining.
 * @return the accept lifetime remaining in seconds
 * @exception GSSException may be thrown
 */
  public int getAcceptLifetime() throws GSSException {
    return GSSCredential.INDEFINITE_LIFETIME;
  }
  public boolean isInitiatorCredential() throws GSSException {
    return false;
  }
  public boolean isAcceptorCredential() throws GSSException {
    return true;
  }
  /** 
 * Returns the oid representing the underlying credential
 * mechanism oid.
 * @return the Oid for this credential mechanism
 * @exception GSSException may be thrown
 */
  public final Oid getMechanism(){
    return Krb5MechFactory.GSS_KRB5_MECH_OID;
  }
  public final java.security.Provider getProvider(){
    return Krb5MechFactory.PROVIDER;
  }
  EncryptionKey[] getKrb5EncryptionKeys(){
    return screds.getEKeys();
  }
  /** 
 * Called to invalidate this credential element.
 */
  public void dispose() throws GSSException {
    try {
      destroy();
    }
 catch (    DestroyFailedException e) {
      GSSException gssException=new GSSException(GSSException.FAILURE,-1,"Could not destroy credentials - " + e.getMessage());
      gssException.initCause(e);
    }
  }
  /** 
 * Destroys the locally cached EncryptionKey value and then calls
 * destroy in the base class.
 */
  public void destroy() throws DestroyFailedException {
    screds.destroy();
  }
}
