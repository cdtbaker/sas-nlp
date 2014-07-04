package com.sun.crypto.provider;
import java.security.InvalidKeyException;
/** 
 * This class represents ciphers in cipher-feedback (CFB) mode.
 * <p>This mode is implemented independently of a particular cipher.
 * Ciphers to which this mode should apply (e.g., DES) must be
 * <i>plugged-in</i> using the constructor.
 * <p>NOTE: This class does not deal with buffering or padding.
 * @author Gigi Ankeny
 */
final class CipherFeedback extends FeedbackCipher {
  private final byte[] k;
  private final byte[] register;
  private int numBytes;
  private byte[] registerSave=null;
  CipherFeedback(  SymmetricCipher embeddedCipher,  int numBytes){
    super(embeddedCipher);
    if (numBytes > blockSize) {
      numBytes=blockSize;
    }
    this.numBytes=numBytes;
    k=new byte[blockSize];
    register=new byte[blockSize];
  }
  /** 
 * Gets the name of this feedback mode.
 * @return the string <code>CFB</code>
 */
  String getFeedback(){
    return "CFB";
  }
  /** 
 * Initializes the cipher in the specified mode with the given key
 * and iv.
 * @param decrypting flag indicating encryption or decryption
 * @param algorithm the algorithm name
 * @param key the key
 * @param iv the iv
 * @exception InvalidKeyException if the given key is inappropriate for
 * initializing this cipher
 */
  void init(  boolean decrypting,  String algorithm,  byte[] key,  byte[] iv) throws InvalidKeyException {
    if ((key == null) || (iv == null) || (iv.length != blockSize)) {
      throw new InvalidKeyException("Internal error");
    }
    this.iv=iv;
    reset();
    embeddedCipher.init(false,algorithm,key);
  }
  /** 
 * Resets the iv to its original value.
 * This is used when doFinal is called in the Cipher class, so that the
 * cipher can be reused (with its original iv).
 */
  void reset(){
    System.arraycopy(iv,0,register,0,blockSize);
  }
  /** 
 * Save the current content of this cipher.
 */
  void save(){
    if (registerSave == null) {
      registerSave=new byte[blockSize];
    }
    System.arraycopy(register,0,registerSave,0,blockSize);
  }
  /** 
 * Restores the content of this cipher to the previous saved one.
 */
  void restore(){
    System.arraycopy(registerSave,0,register,0,blockSize);
  }
  /** 
 * Performs encryption operation.
 * <p>The input plain text <code>plain</code>, starting at
 * <code>plainOffset</code> and ending at
 * <code>(plainOffset + len - 1)</code>, is encrypted.
 * The result is stored in <code>cipher</code>, starting at
 * <code>cipherOffset</code>.
 * <p>It is the application's responsibility to make sure that
 * <code>plainLen</code> is a multiple of the stream unit size
 * <code>numBytes</code>, as any excess bytes are ignored.
 * <p>It is also the application's responsibility to make sure that
 * <code>init</code> has been called before this method is called.
 * (This check is omitted here, to avoid double checking.)
 * @param plain the buffer with the input data to be encrypted
 * @param plainOffset the offset in <code>plain</code>
 * @param plainLen the length of the input data
 * @param cipher the buffer for the result
 * @param cipherOffset the offset in <code>cipher</code>
 */
  void encrypt(  byte[] plain,  int plainOffset,  int plainLen,  byte[] cipher,  int cipherOffset){
    int i, len;
    len=blockSize - numBytes;
    int loopCount=plainLen / numBytes;
    int oddBytes=plainLen % numBytes;
    if (len == 0) {
      for (; loopCount > 0; plainOffset+=numBytes, cipherOffset+=numBytes, loopCount--) {
        embeddedCipher.encryptBlock(register,0,k,0);
        for (i=0; i < blockSize; i++)         register[i]=cipher[i + cipherOffset]=(byte)(k[i] ^ plain[i + plainOffset]);
      }
      if (oddBytes > 0) {
        embeddedCipher.encryptBlock(register,0,k,0);
        for (i=0; i < oddBytes; i++)         register[i]=cipher[i + cipherOffset]=(byte)(k[i] ^ plain[i + plainOffset]);
      }
    }
 else {
      for (; loopCount > 0; plainOffset+=numBytes, cipherOffset+=numBytes, loopCount--) {
        embeddedCipher.encryptBlock(register,0,k,0);
        System.arraycopy(register,numBytes,register,0,len);
        for (i=0; i < numBytes; i++)         register[i + len]=cipher[i + cipherOffset]=(byte)(k[i] ^ plain[i + plainOffset]);
      }
      if (oddBytes != 0) {
        embeddedCipher.encryptBlock(register,0,k,0);
        System.arraycopy(register,numBytes,register,0,len);
        for (i=0; i < oddBytes; i++) {
          register[i + len]=cipher[i + cipherOffset]=(byte)(k[i] ^ plain[i + plainOffset]);
        }
      }
    }
  }
  /** 
 * Performs decryption operation.
 * <p>The input cipher text <code>cipher</code>, starting at
 * <code>cipherOffset</code> and ending at
 * <code>(cipherOffset + len - 1)</code>, is decrypted.
 * The result is stored in <code>plain</code>, starting at
 * <code>plainOffset</code>.
 * <p>It is the application's responsibility to make sure that
 * <code>cipherLen</code> is a multiple of the stream unit size
 * <code>numBytes</code>, as any excess bytes are ignored.
 * <p>It is also the application's responsibility to make sure that
 * <code>init</code> has been called before this method is called.
 * (This check is omitted here, to avoid double checking.)
 * @param cipher the buffer with the input data to be decrypted
 * @param cipherOffset the offset in <code>cipherOffset</code>
 * @param cipherLen the length of the input data
 * @param plain the buffer for the result
 * @param plainOffset the offset in <code>plain</code>
 */
  void decrypt(  byte[] cipher,  int cipherOffset,  int cipherLen,  byte[] plain,  int plainOffset){
    int i, len;
    len=blockSize - numBytes;
    int loopCount=cipherLen / numBytes;
    int oddBytes=cipherLen % numBytes;
    if (len == 0) {
      for (; loopCount > 0; plainOffset+=numBytes, cipherOffset+=numBytes, loopCount--) {
        embeddedCipher.encryptBlock(register,0,k,0);
        for (i=0; i < blockSize; i++) {
          register[i]=cipher[i + cipherOffset];
          plain[i + plainOffset]=(byte)(cipher[i + cipherOffset] ^ k[i]);
        }
      }
      if (oddBytes > 0) {
        embeddedCipher.encryptBlock(register,0,k,0);
        for (i=0; i < oddBytes; i++) {
          register[i]=cipher[i + cipherOffset];
          plain[i + plainOffset]=(byte)(cipher[i + cipherOffset] ^ k[i]);
        }
      }
    }
 else {
      for (; loopCount > 0; plainOffset+=numBytes, cipherOffset+=numBytes, loopCount--) {
        embeddedCipher.encryptBlock(register,0,k,0);
        System.arraycopy(register,numBytes,register,0,len);
        for (i=0; i < numBytes; i++) {
          register[i + len]=cipher[i + cipherOffset];
          plain[i + plainOffset]=(byte)(cipher[i + cipherOffset] ^ k[i]);
        }
      }
      if (oddBytes != 0) {
        embeddedCipher.encryptBlock(register,0,k,0);
        System.arraycopy(register,numBytes,register,0,len);
        for (i=0; i < oddBytes; i++) {
          register[i + len]=cipher[i + cipherOffset];
          plain[i + plainOffset]=(byte)(cipher[i + cipherOffset] ^ k[i]);
        }
      }
    }
  }
}
