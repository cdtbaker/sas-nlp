package sun.security.ssl;
import java.security.*;
/** 
 * The JSSE provider.
 * The RSA implementation has been removed from JSSE, but we still need to
 * register the same algorithms for compatibility. We just point to the RSA
 * implementation in the SunRsaSign provider. This works because all classes
 * are in the bootclasspath and therefore loaded by the same classloader.
 * SunJSSE now supports an experimental FIPS compliant mode when used with an
 * appropriate FIPS certified crypto provider. In FIPS mode, we:
 * . allow only TLS 1.0 or later
 * . allow only FIPS approved ciphersuites
 * . perform all crypto in the FIPS crypto provider
 * It is currently not possible to use both FIPS compliant SunJSSE and
 * standard JSSE at the same time because of the various static data structures
 * we use.
 * However, we do want to allow FIPS mode to be enabled at runtime and without
 * editing the java.security file. That means we need to allow
 * Security.removeProvider("SunJSSE") to work, which creates an instance of
 * this class in non-FIPS mode. That is why we delay the selection of the mode
 * as long as possible. This is until we open an SSL/TLS connection and the
 * data structures need to be initialized or until SunJSSE is initialized in
 * FIPS mode.
 */
public abstract class SunJSSE extends java.security.Provider {
  private static final long serialVersionUID=3231825739635378733L;
  private static String info="Sun JSSE provider" + "(PKCS12, SunX509 key/trust factories, SSLv3, TLSv1)";
  private static String fipsInfo="Sun JSSE provider (FIPS mode, crypto provider ";
  private static Boolean fips;
  static java.security.Provider cryptoProvider;
  protected static synchronized boolean isFIPS(){
    if (fips == null) {
      fips=false;
    }
    return fips;
  }
  private static synchronized void ensureFIPS(  java.security.Provider p){
    if (fips == null) {
      fips=true;
      cryptoProvider=p;
    }
 else {
      if (fips == false) {
        throw new ProviderException("SunJSSE already initialized in non-FIPS mode");
      }
      if (cryptoProvider != p) {
        throw new ProviderException("SunJSSE already initialized with FIPS crypto provider " + cryptoProvider);
      }
    }
  }
  protected SunJSSE(){
    super("SunJSSE",1.7d,info);
    subclassCheck();
    if (Boolean.TRUE.equals(fips)) {
      throw new ProviderException("SunJSSE is already initialized in FIPS mode");
    }
    registerAlgorithms(false);
  }
  protected SunJSSE(  java.security.Provider cryptoProvider){
    this(checkNull(cryptoProvider),cryptoProvider.getName());
  }
  protected SunJSSE(  String cryptoProvider){
    this(null,checkNull(cryptoProvider));
  }
  private static <T>T checkNull(  T t){
    if (t == null) {
      throw new ProviderException("cryptoProvider must not be null");
    }
    return t;
  }
  private SunJSSE(  java.security.Provider cryptoProvider,  String providerName){
    super("SunJSSE",1.6d,fipsInfo + providerName + ")");
    subclassCheck();
    if (cryptoProvider == null) {
      cryptoProvider=Security.getProvider(providerName);
      if (cryptoProvider == null) {
        throw new ProviderException("Crypto provider not installed: " + providerName);
      }
    }
    ensureFIPS(cryptoProvider);
    registerAlgorithms(true);
  }
  private void registerAlgorithms(  final boolean isfips){
    AccessController.doPrivileged(new PrivilegedAction<Object>(){
      public Object run(){
        doRegister(isfips);
        return null;
      }
    }
);
  }
  private void doRegister(  boolean isfips){
    if (isfips == false) {
      put("KeyFactory.RSA","sun.security.rsa.RSAKeyFactory");
      put("Alg.Alias.KeyFactory.1.2.840.113549.1.1","RSA");
      put("Alg.Alias.KeyFactory.OID.1.2.840.113549.1.1","RSA");
      put("KeyPairGenerator.RSA","sun.security.rsa.RSAKeyPairGenerator");
      put("Alg.Alias.KeyPairGenerator.1.2.840.113549.1.1","RSA");
      put("Alg.Alias.KeyPairGenerator.OID.1.2.840.113549.1.1","RSA");
      put("Signature.MD2withRSA","sun.security.rsa.RSASignature$MD2withRSA");
      put("Alg.Alias.Signature.1.2.840.113549.1.1.2","MD2withRSA");
      put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.2","MD2withRSA");
      put("Signature.MD5withRSA","sun.security.rsa.RSASignature$MD5withRSA");
      put("Alg.Alias.Signature.1.2.840.113549.1.1.4","MD5withRSA");
      put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.4","MD5withRSA");
      put("Signature.SHA1withRSA","sun.security.rsa.RSASignature$SHA1withRSA");
      put("Alg.Alias.Signature.1.2.840.113549.1.1.5","SHA1withRSA");
      put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.5","SHA1withRSA");
      put("Alg.Alias.Signature.1.3.14.3.2.29","SHA1withRSA");
      put("Alg.Alias.Signature.OID.1.3.14.3.2.29","SHA1withRSA");
    }
    put("Signature.MD5andSHA1withRSA","sun.security.ssl.RSASignature");
    put("KeyManagerFactory.SunX509","sun.security.ssl.KeyManagerFactoryImpl$SunX509");
    put("KeyManagerFactory.NewSunX509","sun.security.ssl.KeyManagerFactoryImpl$X509");
    put("Alg.Alias.KeyManagerFactory.PKIX","NewSunX509");
    put("TrustManagerFactory.SunX509","sun.security.ssl.TrustManagerFactoryImpl$SimpleFactory");
    put("TrustManagerFactory.PKIX","sun.security.ssl.TrustManagerFactoryImpl$PKIXFactory");
    put("Alg.Alias.TrustManagerFactory.SunPKIX","PKIX");
    put("Alg.Alias.TrustManagerFactory.X509","PKIX");
    put("Alg.Alias.TrustManagerFactory.X.509","PKIX");
    put("SSLContext.TLSv1","sun.security.ssl.SSLContextImpl$TLS10Context");
    put("Alg.Alias.SSLContext.TLS","TLSv1");
    if (isfips == false) {
      put("Alg.Alias.SSLContext.SSL","TLSv1");
      put("Alg.Alias.SSLContext.SSLv3","TLSv1");
    }
    put("SSLContext.TLSv1.1","sun.security.ssl.SSLContextImpl$TLS11Context");
    put("SSLContext.TLSv1.2","sun.security.ssl.SSLContextImpl$TLS12Context");
    put("SSLContext.Default","sun.security.ssl.SSLContextImpl$DefaultSSLContext");
    put("KeyStore.PKCS12","sun.security.pkcs12.PKCS12KeyStore");
  }
  private void subclassCheck(){
    if (getClass() != com.sun.net.ssl.internal.ssl.Provider.class) {
      throw new AssertionError("Illegal subclass: " + getClass());
    }
  }
  @Override protected final void finalize() throws Throwable {
    super.finalize();
  }
}
