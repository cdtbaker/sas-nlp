package sun.security.krb5;
/** 
 * KrbCryptoExceptoin is a wrapper exception for exceptions thrown by JCE.
 * @author Yanni Zhang
 */
public class KrbCryptoException extends KrbException {
  private static final long serialVersionUID=-1657367919979982250L;
  public KrbCryptoException(  String s){
    super(s);
  }
}
