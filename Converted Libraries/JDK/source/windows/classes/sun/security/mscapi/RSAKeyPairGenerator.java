package sun.security.mscapi;
import java.util.UUID;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.rsa.RSAKeyFactory;
/** 
 * RSA keypair generator.
 * Standard algorithm, minimum key length is 512 bit, maximum is 16,384.
 * Generates a private key that is exportable.
 * @since 1.6
 */
public final class RSAKeyPairGenerator extends KeyPairGeneratorSpi {
  static final int KEY_SIZE_MIN=512;
  static final int KEY_SIZE_MAX=16384;
  private static final int KEY_SIZE_DEFAULT=1024;
  private int keySize;
  public RSAKeyPairGenerator(){
    initialize(KEY_SIZE_DEFAULT,null);
  }
  public void initialize(  int keySize,  SecureRandom random){
    try {
      RSAKeyFactory.checkKeyLengths(keySize,null,KEY_SIZE_MIN,KEY_SIZE_MAX);
    }
 catch (    InvalidKeyException e) {
      throw new InvalidParameterException(e.getMessage());
    }
    this.keySize=keySize;
  }
  public void initialize(  AlgorithmParameterSpec params,  SecureRandom random) throws InvalidAlgorithmParameterException {
    int tmpSize;
    if (params == null) {
      tmpSize=KEY_SIZE_DEFAULT;
    }
 else     if (params instanceof RSAKeyGenParameterSpec) {
      if (((RSAKeyGenParameterSpec)params).getPublicExponent() != null) {
        throw new InvalidAlgorithmParameterException("Exponent parameter is not supported");
      }
      tmpSize=((RSAKeyGenParameterSpec)params).getKeysize();
    }
 else {
      throw new InvalidAlgorithmParameterException("Params must be an instance of RSAKeyGenParameterSpec");
    }
    try {
      RSAKeyFactory.checkKeyLengths(tmpSize,null,KEY_SIZE_MIN,KEY_SIZE_MAX);
    }
 catch (    InvalidKeyException e) {
      throw new InvalidAlgorithmParameterException("Invalid Key sizes",e);
    }
    this.keySize=tmpSize;
  }
  public KeyPair generateKeyPair(){
    try {
      RSAKeyPair keys=generateRSAKeyPair(keySize,"{" + UUID.randomUUID().toString() + "}");
      return new KeyPair(keys.getPublic(),keys.getPrivate());
    }
 catch (    KeyException e) {
      throw new ProviderException(e);
    }
  }
  private static native RSAKeyPair generateRSAKeyPair(  int keySize,  String keyContainerName) throws KeyException ;
}
