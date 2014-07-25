package java.security;
/** 
 * <p>A private key. This interface contains no methods or constants.
 * It merely serves to group (and provide type safety for) all private key
 * interfaces.
 * Note: The specialized private key interfaces extend this interface.
 * See, for example, the DSAPrivateKey interface in
 * <code>java.security.interfaces</code>.
 * @see Key
 * @see PublicKey
 * @see Certificate
 * @see Signature#initVerify
 * @see java.security.interfaces.DSAPrivateKey
 * @see java.security.interfaces.RSAPrivateKey
 * @see java.security.interfaces.RSAPrivateCrtKey
 * @author Benjamin Renaud
 * @author Josh Bloch
 */
public interface PrivateKey extends Key {
  /** 
 * The class fingerprint that is set to indicate serialization
 * compatibility with a previous version of the class.
 */
  static final long serialVersionUID=6034044314589513430L;
}
