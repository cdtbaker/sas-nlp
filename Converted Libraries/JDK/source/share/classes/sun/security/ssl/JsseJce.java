package sun.security.ssl;
import java.util.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.*;
import javax.crypto.*;
import java.security.Provider;
import sun.security.jca.Providers;
import sun.security.jca.ProviderList;
import sun.security.ec.ECParameters;
import sun.security.ec.NamedCurve;
import static sun.security.ssl.SunJSSE.cryptoProvider;
/** 
 * This class contains a few static methods for interaction with the JCA/JCE
 * to obtain implementations, etc.
 * @author  Andreas Sterbenz
 */
final class JsseJce {
  private final static Debug debug=Debug.getInstance("ssl");
  private final static ProviderList fipsProviderList;
  private static Boolean ecAvailable;
  private final static boolean kerberosAvailable;
static {
    boolean temp;
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>(){
        public Void run() throws Exception {
          Class.forName("sun.security.krb5.PrincipalName",true,null);
          return null;
        }
      }
);
      temp=true;
    }
 catch (    Exception e) {
      temp=false;
    }
    kerberosAvailable=temp;
  }
static {
    if (SunJSSE.isFIPS() == false) {
      fipsProviderList=null;
    }
 else {
      Provider sun=Security.getProvider("SUN");
      if (sun == null) {
        throw new RuntimeException("FIPS mode: SUN provider must be installed");
      }
      Provider sunCerts=new SunCertificates(sun);
      fipsProviderList=ProviderList.newList(cryptoProvider,sunCerts);
    }
  }
private static final class SunCertificates extends Provider {
    SunCertificates(    final Provider p){
      super("SunCertificates",1.0d,"SunJSSE internal");
      AccessController.doPrivileged(new PrivilegedAction<Object>(){
        public Object run(){
          for (          Map.Entry<Object,Object> entry : p.entrySet()) {
            String key=(String)entry.getKey();
            if (key.startsWith("CertPathValidator.") || key.startsWith("CertPathBuilder.") || key.startsWith("CertStore.")|| key.startsWith("CertificateFactory.")) {
              put(key,entry.getValue());
            }
          }
          return null;
        }
      }
);
    }
  }
  /** 
 * JCE transformation string for RSA with PKCS#1 v1.5 padding.
 * Can be used for encryption, decryption, signing, verifying.
 */
  final static String CIPHER_RSA_PKCS1="RSA/ECB/PKCS1Padding";
  /** 
 * JCE transformation string for the stream cipher RC4.
 */
  final static String CIPHER_RC4="RC4";
  /** 
 * JCE transformation string for DES in CBC mode without padding.
 */
  final static String CIPHER_DES="DES/CBC/NoPadding";
  /** 
 * JCE transformation string for (3-key) Triple DES in CBC mode
 * without padding.
 */
  final static String CIPHER_3DES="DESede/CBC/NoPadding";
  /** 
 * JCE transformation string for AES in CBC mode
 * without padding.
 */
  final static String CIPHER_AES="AES/CBC/NoPadding";
  /** 
 * JCA identifier string for DSA, i.e. a DSA with SHA-1.
 */
  final static String SIGNATURE_DSA="DSA";
  /** 
 * JCA identifier string for ECDSA, i.e. a ECDSA with SHA-1.
 */
  final static String SIGNATURE_ECDSA="SHA1withECDSA";
  /** 
 * JCA identifier string for Raw DSA, i.e. a DSA signature without
 * hashing where the application provides the SHA-1 hash of the data.
 * Note that the standard name is "NONEwithDSA" but we use "RawDSA"
 * for compatibility.
 */
  final static String SIGNATURE_RAWDSA="RawDSA";
  /** 
 * JCA identifier string for Raw ECDSA, i.e. a DSA signature without
 * hashing where the application provides the SHA-1 hash of the data.
 */
  final static String SIGNATURE_RAWECDSA="NONEwithECDSA";
  /** 
 * JCA identifier string for Raw RSA, i.e. a RSA PKCS#1 v1.5 signature
 * without hashing where the application provides the hash of the data.
 * Used for RSA client authentication with a 36 byte hash.
 */
  final static String SIGNATURE_RAWRSA="NONEwithRSA";
  /** 
 * JCA identifier string for the SSL/TLS style RSA Signature. I.e.
 * an signature using RSA with PKCS#1 v1.5 padding signing a
 * concatenation of an MD5 and SHA-1 digest.
 */
  final static String SIGNATURE_SSLRSA="MD5andSHA1withRSA";
  private JsseJce(){
  }
  synchronized static boolean isEcAvailable(){
    if (ecAvailable == null) {
      try {
        JsseJce.getSignature(SIGNATURE_ECDSA);
        JsseJce.getSignature(SIGNATURE_RAWECDSA);
        JsseJce.getKeyAgreement("ECDH");
        JsseJce.getKeyFactory("EC");
        JsseJce.getKeyPairGenerator("EC");
        ecAvailable=true;
      }
 catch (      Exception e) {
        ecAvailable=false;
      }
    }
    return ecAvailable;
  }
  synchronized static void clearEcAvailable(){
    ecAvailable=null;
  }
  static boolean isKerberosAvailable(){
    return kerberosAvailable;
  }
  /** 
 * Return an JCE cipher implementation for the specified algorithm.
 */
  static Cipher getCipher(  String transformation) throws NoSuchAlgorithmException {
    try {
      if (cryptoProvider == null) {
        return Cipher.getInstance(transformation);
      }
 else {
        return Cipher.getInstance(transformation,cryptoProvider);
      }
    }
 catch (    NoSuchPaddingException e) {
      throw new NoSuchAlgorithmException(e);
    }
  }
  /** 
 * Return an JCA signature implementation for the specified algorithm.
 * The algorithm string should be one of the constants defined
 * in this class.
 */
  static Signature getSignature(  String algorithm) throws NoSuchAlgorithmException {
    if (cryptoProvider == null) {
      return Signature.getInstance(algorithm);
    }
 else {
      if (algorithm == SIGNATURE_SSLRSA) {
        if (cryptoProvider.getService("Signature",algorithm) == null) {
          try {
            return Signature.getInstance(algorithm,"SunJSSE");
          }
 catch (          NoSuchProviderException e) {
            throw new NoSuchAlgorithmException(e);
          }
        }
      }
      return Signature.getInstance(algorithm,cryptoProvider);
    }
  }
  static KeyGenerator getKeyGenerator(  String algorithm) throws NoSuchAlgorithmException {
    if (cryptoProvider == null) {
      return KeyGenerator.getInstance(algorithm);
    }
 else {
      return KeyGenerator.getInstance(algorithm,cryptoProvider);
    }
  }
  static KeyPairGenerator getKeyPairGenerator(  String algorithm) throws NoSuchAlgorithmException {
    if (cryptoProvider == null) {
      return KeyPairGenerator.getInstance(algorithm);
    }
 else {
      return KeyPairGenerator.getInstance(algorithm,cryptoProvider);
    }
  }
  static KeyAgreement getKeyAgreement(  String algorithm) throws NoSuchAlgorithmException {
    if (cryptoProvider == null) {
      return KeyAgreement.getInstance(algorithm);
    }
 else {
      return KeyAgreement.getInstance(algorithm,cryptoProvider);
    }
  }
  static Mac getMac(  String algorithm) throws NoSuchAlgorithmException {
    if (cryptoProvider == null) {
      return Mac.getInstance(algorithm);
    }
 else {
      return Mac.getInstance(algorithm,cryptoProvider);
    }
  }
  static KeyFactory getKeyFactory(  String algorithm) throws NoSuchAlgorithmException {
    if (cryptoProvider == null) {
      return KeyFactory.getInstance(algorithm);
    }
 else {
      return KeyFactory.getInstance(algorithm,cryptoProvider);
    }
  }
  static SecureRandom getSecureRandom() throws KeyManagementException {
    if (cryptoProvider == null) {
      return new SecureRandom();
    }
    try {
      return SecureRandom.getInstance("PKCS11",cryptoProvider);
    }
 catch (    NoSuchAlgorithmException e) {
    }
    for (    Provider.Service s : cryptoProvider.getServices()) {
      if (s.getType().equals("SecureRandom")) {
        try {
          return SecureRandom.getInstance(s.getAlgorithm(),cryptoProvider);
        }
 catch (        NoSuchAlgorithmException ee) {
        }
      }
    }
    throw new KeyManagementException("FIPS mode: no SecureRandom " + " implementation found in provider " + cryptoProvider.getName());
  }
  static MessageDigest getMD5(){
    return getMessageDigest("MD5");
  }
  static MessageDigest getSHA(){
    return getMessageDigest("SHA");
  }
  static MessageDigest getMessageDigest(  String algorithm){
    try {
      if (cryptoProvider == null) {
        return MessageDigest.getInstance(algorithm);
      }
 else {
        return MessageDigest.getInstance(algorithm,cryptoProvider);
      }
    }
 catch (    NoSuchAlgorithmException e) {
      throw new RuntimeException("Algorithm " + algorithm + " not available",e);
    }
  }
  static int getRSAKeyLength(  PublicKey key){
    BigInteger modulus;
    if (key instanceof RSAPublicKey) {
      modulus=((RSAPublicKey)key).getModulus();
    }
 else {
      RSAPublicKeySpec spec=getRSAPublicKeySpec(key);
      modulus=spec.getModulus();
    }
    return modulus.bitLength();
  }
  static RSAPublicKeySpec getRSAPublicKeySpec(  PublicKey key){
    if (key instanceof RSAPublicKey) {
      RSAPublicKey rsaKey=(RSAPublicKey)key;
      return new RSAPublicKeySpec(rsaKey.getModulus(),rsaKey.getPublicExponent());
    }
    try {
      KeyFactory factory=JsseJce.getKeyFactory("RSA");
      return factory.getKeySpec(key,RSAPublicKeySpec.class);
    }
 catch (    Exception e) {
      throw (RuntimeException)new RuntimeException().initCause(e);
    }
  }
  static ECParameterSpec getECParameterSpec(  String namedCurveOid){
    return NamedCurve.getECParameterSpec(namedCurveOid);
  }
  static String getNamedCurveOid(  ECParameterSpec params){
    return ECParameters.getCurveName(params);
  }
  static ECPoint decodePoint(  byte[] encoded,  EllipticCurve curve) throws java.io.IOException {
    return ECParameters.decodePoint(encoded,curve);
  }
  static byte[] encodePoint(  ECPoint point,  EllipticCurve curve){
    return ECParameters.encodePoint(point,curve);
  }
  static Object beginFipsProvider(){
    if (fipsProviderList == null) {
      return null;
    }
 else {
      return Providers.beginThreadProviderList(fipsProviderList);
    }
  }
  static void endFipsProvider(  Object o){
    if (fipsProviderList != null) {
      Providers.endThreadProviderList((ProviderList)o);
    }
  }
}
