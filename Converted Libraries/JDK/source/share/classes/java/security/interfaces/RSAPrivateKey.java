package java.security.interfaces;
import java.math.BigInteger;
/** 
 * The interface to an RSA private key.
 * @author Jan Luehe
 * @see RSAPrivateCrtKey
 */
public interface RSAPrivateKey extends java.security.PrivateKey, RSAKey {
  static final long serialVersionUID=5187144804936595022L;
  /** 
 * Returns the private exponent.
 * @return the private exponent
 */
  public BigInteger getPrivateExponent();
}