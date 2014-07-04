package sun.security.ssl;
import java.io.*;
import java.security.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.net.ssl.*;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
/** 
 * This is the client key exchange message (CLIENT --> SERVER) used with
 * all RSA key exchanges; it holds the RSA-encrypted pre-master secret.
 * The message is encrypted using PKCS #1 block type 02 encryption with the
 * server's public key.  The padding and resulting message size is a function
 * of this server's public key modulus size, but the pre-master secret is
 * always exactly 48 bytes.
 */
final class RSAClientKeyExchange extends HandshakeMessage {
  /** 
 * The TLS spec says that the version in the RSA premaster secret must
 * be the maximum version supported by the client (i.e. the version it
 * requested in its client hello version). However, we (and other
 * implementations) used to send the active negotiated version. The
 * system property below allows to toggle the behavior.
 */
  private final static String PROP_NAME="com.sun.net.ssl.rsaPreMasterSecretFix";
  private final static boolean rsaPreMasterSecretFix=Debug.getBooleanProperty(PROP_NAME,false);
  private ProtocolVersion protocolVersion;
  SecretKey preMaster;
  private byte[] encrypted;
  RSAClientKeyExchange(  ProtocolVersion protocolVersion,  ProtocolVersion maxVersion,  SecureRandom generator,  PublicKey publicKey) throws IOException {
    if (publicKey.getAlgorithm().equals("RSA") == false) {
      throw new SSLKeyException("Public key not of type RSA");
    }
    this.protocolVersion=protocolVersion;
    int major, minor;
    if (rsaPreMasterSecretFix || maxVersion.v >= ProtocolVersion.TLS11.v) {
      major=maxVersion.major;
      minor=maxVersion.minor;
    }
 else {
      major=protocolVersion.major;
      minor=protocolVersion.minor;
    }
    try {
      String s=((protocolVersion.v >= ProtocolVersion.TLS12.v) ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret");
      KeyGenerator kg=JsseJce.getKeyGenerator(s);
      kg.init(new TlsRsaPremasterSecretParameterSpec(major,minor),generator);
      preMaster=kg.generateKey();
      Cipher cipher=JsseJce.getCipher(JsseJce.CIPHER_RSA_PKCS1);
      cipher.init(Cipher.WRAP_MODE,publicKey,generator);
      encrypted=cipher.wrap(preMaster);
    }
 catch (    GeneralSecurityException e) {
      throw (SSLKeyException)new SSLKeyException("RSA premaster secret error").initCause(e);
    }
  }
  RSAClientKeyExchange(  ProtocolVersion currentVersion,  ProtocolVersion maxVersion,  SecureRandom generator,  HandshakeInStream input,  int messageSize,  PrivateKey privateKey) throws IOException {
    if (privateKey.getAlgorithm().equals("RSA") == false) {
      throw new SSLKeyException("Private key not of type RSA");
    }
    if (currentVersion.v >= ProtocolVersion.TLS10.v) {
      encrypted=input.getBytes16();
    }
 else {
      encrypted=new byte[messageSize];
      if (input.read(encrypted) != messageSize) {
        throw new SSLProtocolException("SSL: read PreMasterSecret: short read");
      }
    }
    try {
      Cipher cipher=JsseJce.getCipher(JsseJce.CIPHER_RSA_PKCS1);
      cipher.init(Cipher.UNWRAP_MODE,privateKey);
      preMaster=(SecretKey)cipher.unwrap(encrypted,"TlsRsaPremasterSecret",Cipher.SECRET_KEY);
      preMaster=polishPreMasterSecretKey(currentVersion,maxVersion,generator,preMaster,null);
    }
 catch (    Exception e) {
      preMaster=polishPreMasterSecretKey(currentVersion,maxVersion,generator,null,e);
    }
  }
  /** 
 * To avoid vulnerabilities described by section 7.4.7.1, RFC 5246,
 * treating incorrectly formatted message blocks and/or mismatched
 * version numbers in a manner indistinguishable from correctly
 * formatted RSA blocks.
 * RFC 5246 describes the approach as :
 * 1. Generate a string R of 46 random bytes
 * 2. Decrypt the message to recover the plaintext M
 * 3. If the PKCS#1 padding is not correct, or the length of message
 * M is not exactly 48 bytes:
 * pre_master_secret = ClientHello.client_version || R
 * else If ClientHello.client_version <= TLS 1.0, and version
 * number check is explicitly disabled:
 * pre_master_secret = M
 * else:
 * pre_master_secret = ClientHello.client_version || M[2..47]
 */
  private SecretKey polishPreMasterSecretKey(  ProtocolVersion currentVersion,  ProtocolVersion clientHelloVersion,  SecureRandom generator,  SecretKey secretKey,  Exception failoverException){
    this.protocolVersion=clientHelloVersion;
    if (failoverException == null && secretKey != null) {
      byte[] encoded=secretKey.getEncoded();
      if (encoded == null) {
        if (debug != null && Debug.isOn("handshake")) {
          System.out.println("unable to get the plaintext of the premaster secret");
        }
        return secretKey;
      }
 else       if (encoded.length == 48) {
        if (clientHelloVersion.major == encoded[0] && clientHelloVersion.minor == encoded[1]) {
          return secretKey;
        }
 else         if (clientHelloVersion.v <= ProtocolVersion.TLS10.v) {
          if (currentVersion.major == encoded[0] && currentVersion.minor == encoded[1]) {
            this.protocolVersion=currentVersion;
            return secretKey;
          }
        }
        if (debug != null && Debug.isOn("handshake")) {
          System.out.println("Mismatching Protocol Versions, " + "ClientHello.client_version is " + clientHelloVersion + ", while PreMasterSecret.client_version is "+ ProtocolVersion.valueOf(encoded[0],encoded[1]));
        }
      }
 else {
        if (debug != null && Debug.isOn("handshake")) {
          System.out.println("incorrect length of premaster secret: " + encoded.length);
        }
      }
    }
    if (debug != null && Debug.isOn("handshake")) {
      if (failoverException != null) {
        System.out.println("Error decrypting premaster secret:");
        failoverException.printStackTrace(System.out);
      }
      System.out.println("Generating random secret");
    }
    return generateDummySecret(clientHelloVersion);
  }
  static SecretKey generateDummySecret(  ProtocolVersion version){
    try {
      String s=((version.v >= ProtocolVersion.TLS12.v) ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret");
      KeyGenerator kg=JsseJce.getKeyGenerator(s);
      kg.init(new TlsRsaPremasterSecretParameterSpec(version.major,version.minor));
      return kg.generateKey();
    }
 catch (    GeneralSecurityException e) {
      throw new RuntimeException("Could not generate dummy secret",e);
    }
  }
  @Override int messageType(){
    return ht_client_key_exchange;
  }
  @Override int messageLength(){
    if (protocolVersion.v >= ProtocolVersion.TLS10.v) {
      return encrypted.length + 2;
    }
 else {
      return encrypted.length;
    }
  }
  @Override void send(  HandshakeOutStream s) throws IOException {
    if (protocolVersion.v >= ProtocolVersion.TLS10.v) {
      s.putBytes16(encrypted);
    }
 else {
      s.write(encrypted);
    }
  }
  @Override void print(  PrintStream s) throws IOException {
    s.println("*** ClientKeyExchange, RSA PreMasterSecret, " + protocolVersion);
  }
}
