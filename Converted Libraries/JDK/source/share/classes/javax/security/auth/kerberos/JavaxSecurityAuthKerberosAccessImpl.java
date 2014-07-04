package javax.security.auth.kerberos;
import sun.misc.JavaxSecurityAuthKerberosAccess;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
class JavaxSecurityAuthKerberosAccessImpl implements JavaxSecurityAuthKerberosAccess {
  public EncryptionKey[] keyTabGetEncryptionKeys(  KeyTab ktab,  PrincipalName principal){
    return ktab.getEncryptionKeys(principal);
  }
}
