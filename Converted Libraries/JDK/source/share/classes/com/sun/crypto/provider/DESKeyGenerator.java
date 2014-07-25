package com.sun.crypto.provider;
import java.security.SecureRandom;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
/** 
 * This class generates a DES key.
 * @author Jan Luehe
 */
public final class DESKeyGenerator extends KeyGeneratorSpi {
  private SecureRandom random=null;
  /** 
 * Empty constructor
 */
  public DESKeyGenerator(){
  }
  /** 
 * Initializes this key generator.
 * @param random the source of randomness for this generator
 */
  protected void engineInit(  SecureRandom random){
    this.random=random;
  }
  /** 
 * Initializes this key generator with the specified parameter
 * set and a user-provided source of randomness.
 * @param params the key generation parameters
 * @param random the source of randomness for this key generator
 * @exception InvalidAlgorithmParameterException if <code>params</code> is
 * inappropriate for this key generator
 */
  protected void engineInit(  AlgorithmParameterSpec params,  SecureRandom random) throws InvalidAlgorithmParameterException {
    throw new InvalidAlgorithmParameterException("DES key generation does not take any parameters");
  }
  /** 
 * Initializes this key generator for a certain keysize, using the given
 * source of randomness.
 * @param keysize the keysize. This is an algorithm-specific
 * metric specified in number of bits.
 * @param random the source of randomness for this key generator
 */
  protected void engineInit(  int keysize,  SecureRandom random){
    if (keysize != 56) {
      throw new InvalidParameterException("Wrong keysize: must " + "be equal to 56");
    }
    this.engineInit(random);
  }
  /** 
 * Generates the DES key.
 * @return the new DES key
 */
  protected SecretKey engineGenerateKey(){
    DESKey desKey=null;
    if (this.random == null) {
      this.random=SunJCE.RANDOM;
    }
    try {
      byte[] key=new byte[DESKeySpec.DES_KEY_LEN];
      do {
        this.random.nextBytes(key);
        setParityBit(key,0);
      }
 while (DESKeySpec.isWeak(key,0));
      desKey=new DESKey(key);
    }
 catch (    InvalidKeyException e) {
    }
    return desKey;
  }
  static void setParityBit(  byte[] key,  int offset){
    if (key == null)     return;
    for (int i=0; i < DESKeySpec.DES_KEY_LEN; i++) {
      int b=key[offset] & 0xfe;
      b|=(Integer.bitCount(b) & 1) ^ 1;
      key[offset++]=(byte)b;
    }
  }
}
