package sun.security.mscapi;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyRep;
import java.security.ProviderException;
import java.security.PublicKey;
import sun.security.rsa.RSAPublicKeyImpl;
/** 
 * The handle for an RSA public key using the Microsoft Crypto API.
 * @since 1.6
 */
class RSAPublicKey extends Key implements java.security.interfaces.RSAPublicKey {
  private byte[] publicKeyBlob=null;
  private byte[] encoding=null;
  private BigInteger modulus=null;
  private BigInteger exponent=null;
  /** 
 * Construct an RSAPublicKey object.
 */
  RSAPublicKey(  long hCryptProv,  long hCryptKey,  int keyLength){
    super(hCryptProv,hCryptKey,keyLength);
  }
  /** 
 * Returns the standard algorithm name for this key. For
 * example, "RSA" would indicate that this key is a RSA key.
 * See Appendix A in the <a href=
 * "../../../guide/security/CryptoSpec.html#AppA">
 * Java Cryptography Architecture API Specification &amp; Reference </a>
 * for information about standard algorithm names.
 * @return the name of the algorithm associated with this key.
 */
  public String getAlgorithm(){
    return "RSA";
  }
  /** 
 * Returns a printable description of the key.
 */
  public String toString(){
    StringBuffer sb=new StringBuffer();
    sb.append("RSAPublicKey [size=").append(keyLength).append(" bits, type=").append(getKeyType(hCryptKey)).append(", container=").append(getContainerName(hCryptProv)).append("]\n  modulus: ").append(getModulus()).append("\n  public exponent: ").append(getPublicExponent());
    return sb.toString();
  }
  /** 
 * Returns the public exponent.
 */
  public BigInteger getPublicExponent(){
    if (exponent == null) {
      try {
        publicKeyBlob=getPublicKeyBlob(hCryptKey);
        exponent=new BigInteger(1,getExponent(publicKeyBlob));
      }
 catch (      KeyException e) {
        throw new ProviderException(e);
      }
    }
    return exponent;
  }
  /** 
 * Returns the modulus.
 */
  public BigInteger getModulus(){
    if (modulus == null) {
      try {
        publicKeyBlob=getPublicKeyBlob(hCryptKey);
        modulus=new BigInteger(1,getModulus(publicKeyBlob));
      }
 catch (      KeyException e) {
        throw new ProviderException(e);
      }
    }
    return modulus;
  }
  /** 
 * Returns the name of the primary encoding format of this key,
 * or null if this key does not support encoding.
 * The primary encoding format is
 * named in terms of the appropriate ASN.1 data format, if an
 * ASN.1 specification for this key exists.
 * For example, the name of the ASN.1 data format for public
 * keys is <I>SubjectPublicKeyInfo</I>, as
 * defined by the X.509 standard; in this case, the returned format is
 * <code>"X.509"</code>. Similarly,
 * the name of the ASN.1 data format for private keys is
 * <I>PrivateKeyInfo</I>,
 * as defined by the PKCS #8 standard; in this case, the returned format is
 * <code>"PKCS#8"</code>.
 * @return the primary encoding format of the key.
 */
  public String getFormat(){
    return "X.509";
  }
  /** 
 * Returns the key in its primary encoding format, or null
 * if this key does not support encoding.
 * @return the encoded key, or null if the key does not support
 * encoding.
 */
  public byte[] getEncoded(){
    if (encoding == null) {
      try {
        encoding=new RSAPublicKeyImpl(getModulus(),getPublicExponent()).getEncoded();
      }
 catch (      KeyException e) {
      }
    }
    return encoding;
  }
  protected Object writeReplace() throws java.io.ObjectStreamException {
    return new KeyRep(KeyRep.Type.PUBLIC,getAlgorithm(),getFormat(),getEncoded());
  }
  private native byte[] getPublicKeyBlob(  long hCryptKey) throws KeyException ;
  private native byte[] getExponent(  byte[] keyBlob) throws KeyException ;
  private native byte[] getModulus(  byte[] keyBlob) throws KeyException ;
}
