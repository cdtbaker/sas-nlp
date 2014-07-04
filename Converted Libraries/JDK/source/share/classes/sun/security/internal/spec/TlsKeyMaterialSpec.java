package sun.security.internal.spec;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
/** 
 * KeySpec class for SSL/TLS key material.
 * <p>Instances of this class are returned by the <code>generateKey()</code>
 * method of KeyGenerators of the type "TlsKeyMaterial".
 * Instances of this class are immutable.
 * @since   1.6
 * @author  Andreas Sterbenz
 * @deprecated Sun JDK internal use only --- WILL BE REMOVED in a future
 * release.
 */
@Deprecated public class TlsKeyMaterialSpec implements KeySpec, SecretKey {
  static final long serialVersionUID=812912859129525028L;
  private final SecretKey clientMacKey, serverMacKey;
  private final SecretKey clientCipherKey, serverCipherKey;
  private final IvParameterSpec clientIv, serverIv;
  /** 
 * Constructs a new TlsKeymaterialSpec from the client and server MAC
 * keys.
 * This call is equivalent to
 * <code>new TlsKeymaterialSpec(clientMacKey, serverMacKey,
 * null, null, null, null)</code>.
 * @param clientMacKey the client MAC key
 * @param serverMacKey the server MAC key
 * @throws NullPointerException if clientMacKey or serverMacKey is null
 */
  public TlsKeyMaterialSpec(  SecretKey clientMacKey,  SecretKey serverMacKey){
    this(clientMacKey,serverMacKey,null,null,null,null);
  }
  /** 
 * Constructs a new TlsKeymaterialSpec from the client and server MAC
 * keys and client and server cipher keys.
 * This call is equivalent to
 * <code>new TlsKeymaterialSpec(clientMacKey, serverMacKey,
 * clientCipherKey, serverCipherKey, null, null)</code>.
 * @param clientMacKey the client MAC key
 * @param serverMacKey the server MAC key
 * @param clientCipherKey the client cipher key (or null)
 * @param serverCipherKey the server cipher key (or null)
 * @throws NullPointerException if clientMacKey or serverMacKey is null
 */
  public TlsKeyMaterialSpec(  SecretKey clientMacKey,  SecretKey serverMacKey,  SecretKey clientCipherKey,  SecretKey serverCipherKey){
    this(clientMacKey,serverMacKey,clientCipherKey,null,serverCipherKey,null);
  }
  /** 
 * Constructs a new TlsKeymaterialSpec from the client and server MAC
 * keys, client and server cipher keys, and client and server
 * initialization vectors.
 * @param clientMacKey the client MAC key
 * @param serverMacKey the server MAC key
 * @param clientCipherKey the client cipher key (or null)
 * @param clientIv the client initialization vector (or null)
 * @param serverCipherKey the server cipher key (or null)
 * @param serverIv the server initialization vector (or null)
 * @throws NullPointerException if clientMacKey or serverMacKey is null
 */
  public TlsKeyMaterialSpec(  SecretKey clientMacKey,  SecretKey serverMacKey,  SecretKey clientCipherKey,  IvParameterSpec clientIv,  SecretKey serverCipherKey,  IvParameterSpec serverIv){
    if ((clientMacKey == null) || (serverMacKey == null)) {
      throw new NullPointerException("MAC keys must not be null");
    }
    this.clientMacKey=clientMacKey;
    this.serverMacKey=serverMacKey;
    this.clientCipherKey=clientCipherKey;
    this.serverCipherKey=serverCipherKey;
    this.clientIv=clientIv;
    this.serverIv=serverIv;
  }
  /** 
 * Returns <code>TlsKeyMaterial</code>.
 * @return <code>TlsKeyMaterial</code>.
 */
  public String getAlgorithm(){
    return "TlsKeyMaterial";
  }
  /** 
 * Returns <code>null</code> because keys of this type have no encoding.
 * @return <code>null</code> because keys of this type have no encoding.
 */
  public String getFormat(){
    return null;
  }
  /** 
 * Returns <code>null</code> because keys of this type have no encoding.
 * @return <code>null</code> because keys of this type have no encoding.
 */
  public byte[] getEncoded(){
    return null;
  }
  /** 
 * Returns the client MAC key.
 * @return the client MAC key.
 */
  public SecretKey getClientMacKey(){
    return clientMacKey;
  }
  /** 
 * Return the server MAC key.
 * @return the server MAC key.
 */
  public SecretKey getServerMacKey(){
    return serverMacKey;
  }
  /** 
 * Return the client cipher key (or null).
 * @return the client cipher key (or null).
 */
  public SecretKey getClientCipherKey(){
    return clientCipherKey;
  }
  /** 
 * Return the client initialization vector (or null).
 * @return the client initialization vector (or null).
 */
  public IvParameterSpec getClientIv(){
    return clientIv;
  }
  /** 
 * Return the server cipher key (or null).
 * @return the server cipher key (or null).
 */
  public SecretKey getServerCipherKey(){
    return serverCipherKey;
  }
  /** 
 * Return the server initialization vector (or null).
 * @return the server initialization vector (or null).
 */
  public IvParameterSpec getServerIv(){
    return serverIv;
  }
}
