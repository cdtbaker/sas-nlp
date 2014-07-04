package sun.security.rsa;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.*;
import sun.security.util.*;
import sun.security.pkcs.PKCS8Key;
/** 
 * Key implementation for RSA private keys, non-CRT form (modulus, private
 * exponent only). For CRT private keys, see RSAPrivateCrtKeyImpl. We need
 * separate classes to ensure correct behavior in instanceof checks, etc.
 * Note: RSA keys must be at least 512 bits long
 * @see RSAPrivateCrtKeyImpl
 * @see RSAKeyFactory
 * @since   1.5
 * @author  Andreas Sterbenz
 */
public final class RSAPrivateKeyImpl extends PKCS8Key implements RSAPrivateKey {
  private static final long serialVersionUID=-33106691987952810L;
  private final BigInteger n;
  private final BigInteger d;
  /** 
 * Construct a key from its components. Used by the
 * RSAKeyFactory and the RSAKeyPairGenerator.
 */
  RSAPrivateKeyImpl(  BigInteger n,  BigInteger d) throws InvalidKeyException {
    this.n=n;
    this.d=d;
    RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(),null);
    algid=RSAPrivateCrtKeyImpl.rsaId;
    try {
      DerOutputStream out=new DerOutputStream();
      out.putInteger(0);
      out.putInteger(n);
      out.putInteger(0);
      out.putInteger(d);
      out.putInteger(0);
      out.putInteger(0);
      out.putInteger(0);
      out.putInteger(0);
      out.putInteger(0);
      DerValue val=new DerValue(DerValue.tag_Sequence,out.toByteArray());
      key=val.toByteArray();
    }
 catch (    IOException exc) {
      throw new InvalidKeyException(exc);
    }
  }
  public String getAlgorithm(){
    return "RSA";
  }
  public BigInteger getModulus(){
    return n;
  }
  public BigInteger getPrivateExponent(){
    return d;
  }
  public String toString(){
    return "Sun RSA private key, " + n.bitLength() + " bits\n  modulus: "+ n+ "\n  private exponent: "+ d;
  }
}
