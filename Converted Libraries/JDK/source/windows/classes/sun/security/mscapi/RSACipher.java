package sun.security.mscapi;
import java.math.BigInteger;
import java.security.*;
import java.security.Key;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import sun.security.rsa.RSAKeyFactory;
/** 
 * RSA cipher implementation using the Microsoft Crypto API.
 * Supports RSA en/decryption and signing/verifying using PKCS#1 v1.5 padding.
 * Objects should be instantiated by calling Cipher.getInstance() using the
 * following algorithm name:
 * . "RSA/ECB/PKCS1Padding" (or "RSA") for PKCS#1 padding. The mode (blocktype)
 * is selected based on the en/decryption mode and public/private key used.
 * We only do one RSA operation per doFinal() call. If the application passes
 * more data via calls to update() or doFinal(), we throw an
 * IllegalBlockSizeException when doFinal() is called (see JCE API spec).
 * Bulk encryption using RSA does not make sense and is not standardized.
 * Note: RSA keys should be at least 512 bits long
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Vincent Ryan
 */
public final class RSACipher extends CipherSpi {
  private final static byte[] B0=new byte[0];
  private final static int MODE_ENCRYPT=1;
  private final static int MODE_DECRYPT=2;
  private final static int MODE_SIGN=3;
  private final static int MODE_VERIFY=4;
  private final static String PAD_PKCS1="PKCS1Padding";
  private final static int PAD_PKCS1_LENGTH=11;
  private int mode;
  private String paddingType;
  private int paddingLength=0;
  private byte[] buffer;
  private int bufOfs;
  private int outputSize;
  private sun.security.mscapi.Key publicKey;
  private sun.security.mscapi.Key privateKey;
  public RSACipher(){
    paddingType=PAD_PKCS1;
  }
  protected void engineSetMode(  String mode) throws NoSuchAlgorithmException {
    if (mode.equalsIgnoreCase("ECB") == false) {
      throw new NoSuchAlgorithmException("Unsupported mode " + mode);
    }
  }
  protected void engineSetPadding(  String paddingName) throws NoSuchPaddingException {
    if (paddingName.equalsIgnoreCase(PAD_PKCS1)) {
      paddingType=PAD_PKCS1;
    }
 else {
      throw new NoSuchPaddingException("Padding " + paddingName + " not supported");
    }
  }
  protected int engineGetBlockSize(){
    return 0;
  }
  protected int engineGetOutputSize(  int inputLen){
    return outputSize;
  }
  protected byte[] engineGetIV(){
    return null;
  }
  protected AlgorithmParameters engineGetParameters(){
    return null;
  }
  protected void engineInit(  int opmode,  Key key,  SecureRandom random) throws InvalidKeyException {
    init(opmode,key);
  }
  protected void engineInit(  int opmode,  Key key,  AlgorithmParameterSpec params,  SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (params != null) {
      throw new InvalidAlgorithmParameterException("Parameters not supported");
    }
    init(opmode,key);
  }
  protected void engineInit(  int opmode,  Key key,  AlgorithmParameters params,  SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (params != null) {
      throw new InvalidAlgorithmParameterException("Parameters not supported");
    }
    init(opmode,key);
  }
  private void init(  int opmode,  Key key) throws InvalidKeyException {
    boolean encrypt;
switch (opmode) {
case Cipher.ENCRYPT_MODE:
case Cipher.WRAP_MODE:
      paddingLength=PAD_PKCS1_LENGTH;
    encrypt=true;
  break;
case Cipher.DECRYPT_MODE:
case Cipher.UNWRAP_MODE:
paddingLength=0;
encrypt=false;
break;
default :
throw new InvalidKeyException("Unknown mode: " + opmode);
}
if (!(key instanceof sun.security.mscapi.Key)) {
if (key instanceof java.security.interfaces.RSAPublicKey) {
java.security.interfaces.RSAPublicKey rsaKey=(java.security.interfaces.RSAPublicKey)key;
BigInteger modulus=rsaKey.getModulus();
BigInteger exponent=rsaKey.getPublicExponent();
RSAKeyFactory.checkKeyLengths(((modulus.bitLength() + 7) & ~7),exponent,-1,RSAKeyPairGenerator.KEY_SIZE_MAX);
byte[] modulusBytes=modulus.toByteArray();
byte[] exponentBytes=exponent.toByteArray();
int keyBitLength=(modulusBytes[0] == 0) ? (modulusBytes.length - 1) * 8 : modulusBytes.length * 8;
byte[] keyBlob=RSASignature.generatePublicKeyBlob(keyBitLength,modulusBytes,exponentBytes);
try {
key=RSASignature.importPublicKey(keyBlob,keyBitLength);
}
 catch (KeyStoreException e) {
throw new InvalidKeyException(e);
}
}
 else {
throw new InvalidKeyException("Unsupported key type: " + key);
}
}
if (key instanceof PublicKey) {
mode=encrypt ? MODE_ENCRYPT : MODE_VERIFY;
publicKey=(sun.security.mscapi.Key)key;
privateKey=null;
outputSize=publicKey.bitLength() / 8;
}
 else if (key instanceof PrivateKey) {
mode=encrypt ? MODE_SIGN : MODE_DECRYPT;
privateKey=(sun.security.mscapi.Key)key;
publicKey=null;
outputSize=privateKey.bitLength() / 8;
}
 else {
throw new InvalidKeyException("Unknown key type: " + key);
}
bufOfs=0;
buffer=new byte[outputSize];
}
private void update(byte[] in,int inOfs,int inLen){
if ((inLen == 0) || (in == null)) {
return;
}
if (bufOfs + inLen > (buffer.length - paddingLength)) {
bufOfs=buffer.length + 1;
return;
}
System.arraycopy(in,inOfs,buffer,bufOfs,inLen);
bufOfs+=inLen;
}
private byte[] doFinal() throws BadPaddingException, IllegalBlockSizeException {
if (bufOfs > buffer.length) {
throw new IllegalBlockSizeException("Data must not be longer " + "than " + (buffer.length - paddingLength) + " bytes");
}
try {
byte[] data=buffer;
switch (mode) {
case MODE_SIGN:
return encryptDecrypt(data,bufOfs,privateKey.getHCryptKey(),true);
case MODE_VERIFY:
return encryptDecrypt(data,bufOfs,publicKey.getHCryptKey(),false);
case MODE_ENCRYPT:
return encryptDecrypt(data,bufOfs,publicKey.getHCryptKey(),true);
case MODE_DECRYPT:
return encryptDecrypt(data,bufOfs,privateKey.getHCryptKey(),false);
default :
throw new AssertionError("Internal error");
}
}
 catch (KeyException e) {
throw new ProviderException(e);
}
 finally {
bufOfs=0;
}
}
protected byte[] engineUpdate(byte[] in,int inOfs,int inLen){
update(in,inOfs,inLen);
return B0;
}
protected int engineUpdate(byte[] in,int inOfs,int inLen,byte[] out,int outOfs){
update(in,inOfs,inLen);
return 0;
}
protected byte[] engineDoFinal(byte[] in,int inOfs,int inLen) throws BadPaddingException, IllegalBlockSizeException {
update(in,inOfs,inLen);
return doFinal();
}
protected int engineDoFinal(byte[] in,int inOfs,int inLen,byte[] out,int outOfs) throws ShortBufferException, BadPaddingException, IllegalBlockSizeException {
if (outputSize > out.length - outOfs) {
throw new ShortBufferException("Need " + outputSize + " bytes for output");
}
update(in,inOfs,inLen);
byte[] result=doFinal();
int n=result.length;
System.arraycopy(result,0,out,outOfs,n);
return n;
}
protected byte[] engineWrap(Key key) throws InvalidKeyException, IllegalBlockSizeException {
byte[] encoded=key.getEncoded();
if ((encoded == null) || (encoded.length == 0)) {
throw new InvalidKeyException("Could not obtain encoded key");
}
if (encoded.length > buffer.length) {
throw new InvalidKeyException("Key is too long for wrapping");
}
update(encoded,0,encoded.length);
try {
return doFinal();
}
 catch (BadPaddingException e) {
throw new InvalidKeyException("Wrapping failed",e);
}
}
protected java.security.Key engineUnwrap(byte[] wrappedKey,String algorithm,int type) throws InvalidKeyException, NoSuchAlgorithmException {
if (wrappedKey.length > buffer.length) {
throw new InvalidKeyException("Key is too long for unwrapping");
}
update(wrappedKey,0,wrappedKey.length);
try {
byte[] encoding=doFinal();
switch (type) {
case Cipher.PUBLIC_KEY:
return constructPublicKey(encoding,algorithm);
case Cipher.PRIVATE_KEY:
return constructPrivateKey(encoding,algorithm);
case Cipher.SECRET_KEY:
return constructSecretKey(encoding,algorithm);
default :
throw new InvalidKeyException("Unknown key type " + type);
}
}
 catch (BadPaddingException e) {
throw new InvalidKeyException("Unwrapping failed",e);
}
catch (IllegalBlockSizeException e) {
throw new InvalidKeyException("Unwrapping failed",e);
}
}
protected int engineGetKeySize(Key key) throws InvalidKeyException {
if (key instanceof sun.security.mscapi.Key) {
return ((sun.security.mscapi.Key)key).bitLength();
}
 else if (key instanceof RSAKey) {
return ((RSAKey)key).getModulus().bitLength();
}
 else {
throw new InvalidKeyException("Unsupported key type: " + key);
}
}
private static PublicKey constructPublicKey(byte[] encodedKey,String encodedKeyAlgorithm) throws InvalidKeyException, NoSuchAlgorithmException {
try {
KeyFactory keyFactory=KeyFactory.getInstance(encodedKeyAlgorithm);
X509EncodedKeySpec keySpec=new X509EncodedKeySpec(encodedKey);
return keyFactory.generatePublic(keySpec);
}
 catch (NoSuchAlgorithmException nsae) {
throw new NoSuchAlgorithmException("No installed provider " + "supports the " + encodedKeyAlgorithm + " algorithm",nsae);
}
catch (InvalidKeySpecException ike) {
throw new InvalidKeyException("Cannot construct public key",ike);
}
}
private static PrivateKey constructPrivateKey(byte[] encodedKey,String encodedKeyAlgorithm) throws InvalidKeyException, NoSuchAlgorithmException {
try {
KeyFactory keyFactory=KeyFactory.getInstance(encodedKeyAlgorithm);
PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(encodedKey);
return keyFactory.generatePrivate(keySpec);
}
 catch (NoSuchAlgorithmException nsae) {
throw new NoSuchAlgorithmException("No installed provider " + "supports the " + encodedKeyAlgorithm + " algorithm",nsae);
}
catch (InvalidKeySpecException ike) {
throw new InvalidKeyException("Cannot construct private key",ike);
}
}
private static SecretKey constructSecretKey(byte[] encodedKey,String encodedKeyAlgorithm){
return new SecretKeySpec(encodedKey,encodedKeyAlgorithm);
}
private native static byte[] encryptDecrypt(byte[] data,int dataSize,long hCryptKey,boolean doEncrypt) throws KeyException ;
}
