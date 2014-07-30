package java.security;
import java.util.*;
import java.security.Provider.Service;
import sun.security.jca.*;
import sun.security.jca.GetInstance.Instance;
/** 
 * This class provides a cryptographically strong random number
 * generator (RNG).
 * <p>A cryptographically strong random number
 * minimally complies with the statistical random number generator tests
 * specified in <a href="http://csrc.nist.gov/cryptval/140-2.htm">
 * <i>FIPS 140-2, Security Requirements for Cryptographic Modules</i></a>,
 * section 4.9.1.
 * Additionally, SecureRandom must produce non-deterministic output.
 * Therefore any seed material passed to a SecureRandom object must be
 * unpredictable, and all SecureRandom output sequences must be
 * cryptographically strong, as described in
 * <a href="http://www.ietf.org/rfc/rfc1750.txt">
 * <i>RFC 1750: Randomness Recommendations for Security</i></a>.
 * <p>A caller obtains a SecureRandom instance via the
 * no-argument constructor or one of the <code>getInstance</code> methods:
 * <pre>
 * SecureRandom random = new SecureRandom();
 * </pre>
 * <p> Many SecureRandom implementations are in the form of a pseudo-random
 * number generator (PRNG), which means they use a deterministic algorithm
 * to produce a pseudo-random sequence from a true random seed.
 * Other implementations may produce true random numbers,
 * and yet others may use a combination of both techniques.
 * <p> Typical callers of SecureRandom invoke the following methods
 * to retrieve random bytes:
 * <pre>
 * SecureRandom random = new SecureRandom();
 * byte bytes[] = new byte[20];
 * random.nextBytes(bytes);
 * </pre>
 * <p> Callers may also invoke the <code>generateSeed</code> method
 * to generate a given number of seed bytes (to seed other random number
 * generators, for example):
 * <pre>
 * byte seed[] = random.generateSeed(20);
 * </pre>
 * Note: Depending on the implementation, the <code>generateSeed</code> and
 * <code>nextBytes</code> methods may block as entropy is being gathered,
 * for example, if they need to read from /dev/random on various unix-like
 * operating systems.
 * @see java.security.SecureRandomSpi
 * @see java.util.Random
 * @author Benjamin Renaud
 * @author Josh Bloch
 */
public class SecureRandom extends java.util.Random {
  /** 
 * The provider.
 * @serial
 * @since 1.2
 */
  private Provider provider=null;
  /** 
 * The provider implementation.
 * @serial
 * @since 1.2
 */
  private SecureRandomSpi secureRandomSpi=null;
  private String algorithm;
  private static volatile SecureRandom seedGenerator=null;
  /** 
 * Constructs a secure random number generator (RNG) implementing the
 * default random number algorithm.
 * <p> This constructor traverses the list of registered security Providers,
 * starting with the most preferred Provider.
 * A new SecureRandom object encapsulating the
 * SecureRandomSpi implementation from the first
 * Provider that supports a SecureRandom (RNG) algorithm is returned.
 * If none of the Providers support a RNG algorithm,
 * then an implementation-specific default is returned.
 * <p> Note that the list of registered providers may be retrieved via
 * the {@link Security#getProviders() Security.getProviders()} method.
 * <p> See the SecureRandom section in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#SecureRandom">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard RNG algorithm names.
 * <p> The returned SecureRandom object has not been seeded.  To seed the
 * returned object, call the <code>setSeed</code> method.
 * If <code>setSeed</code> is not called, the first call to
 * <code>nextBytes</code> will force the SecureRandom object to seed itself.
 * This self-seeding will not occur if <code>setSeed</code> was
 * previously called.
 */
  public SecureRandom(){
    super(0);
    getDefaultPRNG(false,null);
  }
  /** 
 * Constructs a secure random number generator (RNG) implementing the
 * default random number algorithm.
 * The SecureRandom instance is seeded with the specified seed bytes.
 * <p> This constructor traverses the list of registered security Providers,
 * starting with the most preferred Provider.
 * A new SecureRandom object encapsulating the
 * SecureRandomSpi implementation from the first
 * Provider that supports a SecureRandom (RNG) algorithm is returned.
 * If none of the Providers support a RNG algorithm,
 * then an implementation-specific default is returned.
 * <p> Note that the list of registered providers may be retrieved via
 * the {@link Security#getProviders() Security.getProviders()} method.
 * <p> See the SecureRandom section in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#SecureRandom">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard RNG algorithm names.
 * @param seed the seed.
 */
  public SecureRandom(  byte seed[]){
    super(0);
    getDefaultPRNG(true,seed);
  }
  private void getDefaultPRNG(  boolean setSeed,  byte[] seed){
    String prng=getPrngAlgorithm();
    if (prng == null) {
      prng="SHA1PRNG";
      this.secureRandomSpi=new sun.security.provider.SecureRandom();
      this.provider=Providers.getSunProvider();
      if (setSeed) {
        this.secureRandomSpi.engineSetSeed(seed);
      }
    }
 else {
      try {
        SecureRandom random=SecureRandom.getInstance(prng);
        this.secureRandomSpi=random.getSecureRandomSpi();
        this.provider=random.getProvider();
        if (setSeed) {
          this.secureRandomSpi.engineSetSeed(seed);
        }
      }
 catch (      NoSuchAlgorithmException nsae) {
        throw new RuntimeException(nsae);
      }
    }
    if (getClass() == SecureRandom.class) {
      this.algorithm=prng;
    }
  }
  /** 
 * Creates a SecureRandom object.
 * @param secureRandomSpi the SecureRandom implementation.
 * @param provider the provider.
 */
  protected SecureRandom(  SecureRandomSpi secureRandomSpi,  Provider provider){
    this(secureRandomSpi,provider,null);
  }
  private SecureRandom(  SecureRandomSpi secureRandomSpi,  Provider provider,  String algorithm){
    super(0);
    this.secureRandomSpi=secureRandomSpi;
    this.provider=provider;
    this.algorithm=algorithm;
  }
  /** 
 * Returns a SecureRandom object that implements the specified
 * Random Number Generator (RNG) algorithm.
 * <p> This method traverses the list of registered security Providers,
 * starting with the most preferred Provider.
 * A new SecureRandom object encapsulating the
 * SecureRandomSpi implementation from the first
 * Provider that supports the specified algorithm is returned.
 * <p> Note that the list of registered providers may be retrieved via
 * the {@link Security#getProviders() Security.getProviders()} method.
 * <p> The returned SecureRandom object has not been seeded.  To seed the
 * returned object, call the <code>setSeed</code> method.
 * If <code>setSeed</code> is not called, the first call to
 * <code>nextBytes</code> will force the SecureRandom object to seed itself.
 * This self-seeding will not occur if <code>setSeed</code> was
 * previously called.
 * @param algorithm the name of the RNG algorithm.
 * See the SecureRandom section in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#SecureRandom">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard RNG algorithm names.
 * @return the new SecureRandom object.
 * @exception NoSuchAlgorithmException if no Provider supports a
 * SecureRandomSpi implementation for the
 * specified algorithm.
 * @see Provider
 * @since 1.2
 */
  public static SecureRandom getInstance(  String algorithm) throws NoSuchAlgorithmException {
    Instance instance=GetInstance.getInstance("SecureRandom",SecureRandomSpi.class,algorithm);
    return new SecureRandom((SecureRandomSpi)instance.impl,instance.provider,algorithm);
  }
  /** 
 * Returns a SecureRandom object that implements the specified
 * Random Number Generator (RNG) algorithm.
 * <p> A new SecureRandom object encapsulating the
 * SecureRandomSpi implementation from the specified provider
 * is returned.  The specified provider must be registered
 * in the security provider list.
 * <p> Note that the list of registered providers may be retrieved via
 * the {@link Security#getProviders() Security.getProviders()} method.
 * <p> The returned SecureRandom object has not been seeded.  To seed the
 * returned object, call the <code>setSeed</code> method.
 * If <code>setSeed</code> is not called, the first call to
 * <code>nextBytes</code> will force the SecureRandom object to seed itself.
 * This self-seeding will not occur if <code>setSeed</code> was
 * previously called.
 * @param algorithm the name of the RNG algorithm.
 * See the SecureRandom section in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#SecureRandom">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard RNG algorithm names.
 * @param provider the name of the provider.
 * @return the new SecureRandom object.
 * @exception NoSuchAlgorithmException if a SecureRandomSpi
 * implementation for the specified algorithm is not
 * available from the specified provider.
 * @exception NoSuchProviderException if the specified provider is not
 * registered in the security provider list.
 * @exception IllegalArgumentException if the provider name is null
 * or empty.
 * @see Provider
 * @since 1.2
 */
  public static SecureRandom getInstance(  String algorithm,  String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
    Instance instance=GetInstance.getInstance("SecureRandom",SecureRandomSpi.class,algorithm,provider);
    return new SecureRandom((SecureRandomSpi)instance.impl,instance.provider,algorithm);
  }
  /** 
 * Returns a SecureRandom object that implements the specified
 * Random Number Generator (RNG) algorithm.
 * <p> A new SecureRandom object encapsulating the
 * SecureRandomSpi implementation from the specified Provider
 * object is returned.  Note that the specified Provider object
 * does not have to be registered in the provider list.
 * <p> The returned SecureRandom object has not been seeded.  To seed the
 * returned object, call the <code>setSeed</code> method.
 * If <code>setSeed</code> is not called, the first call to
 * <code>nextBytes</code> will force the SecureRandom object to seed itself.
 * This self-seeding will not occur if <code>setSeed</code> was
 * previously called.
 * @param algorithm the name of the RNG algorithm.
 * See the SecureRandom section in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#SecureRandom">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard RNG algorithm names.
 * @param provider the provider.
 * @return the new SecureRandom object.
 * @exception NoSuchAlgorithmException if a SecureRandomSpi
 * implementation for the specified algorithm is not available
 * from the specified Provider object.
 * @exception IllegalArgumentException if the specified provider is null.
 * @see Provider
 * @since 1.4
 */
  public static SecureRandom getInstance(  String algorithm,  Provider provider) throws NoSuchAlgorithmException {
    Instance instance=GetInstance.getInstance("SecureRandom",SecureRandomSpi.class,algorithm,provider);
    return new SecureRandom((SecureRandomSpi)instance.impl,instance.provider,algorithm);
  }
  /** 
 * Returns the SecureRandomSpi of this SecureRandom object.
 */
  SecureRandomSpi getSecureRandomSpi(){
    return secureRandomSpi;
  }
  /** 
 * Returns the provider of this SecureRandom object.
 * @return the provider of this SecureRandom object.
 */
  public final Provider getProvider(){
    return provider;
  }
  /** 
 * Returns the name of the algorithm implemented by this SecureRandom
 * object.
 * @return the name of the algorithm or <code>unknown</code>
 * if the algorithm name cannot be determined.
 * @since 1.5
 */
  public String getAlgorithm(){
    return (algorithm != null) ? algorithm : "unknown";
  }
  /** 
 * Reseeds this random object. The given seed supplements, rather than
 * replaces, the existing seed. Thus, repeated calls are guaranteed
 * never to reduce randomness.
 * @param seed the seed.
 * @see #getSeed
 */
  synchronized public void setSeed(  byte[] seed){
    secureRandomSpi.engineSetSeed(seed);
  }
  /** 
 * Reseeds this random object, using the eight bytes contained
 * in the given <code>long seed</code>. The given seed supplements,
 * rather than replaces, the existing seed. Thus, repeated calls
 * are guaranteed never to reduce randomness.
 * <p>This method is defined for compatibility with
 * <code>java.util.Random</code>.
 * @param seed the seed.
 * @see #getSeed
 */
  public void setSeed(  long seed){
    if (seed != 0) {
      secureRandomSpi.engineSetSeed(longToByteArray(seed));
    }
  }
  /** 
 * Generates a user-specified number of random bytes.
 * <p> If a call to <code>setSeed</code> had not occurred previously,
 * the first call to this method forces this SecureRandom object
 * to seed itself.  This self-seeding will not occur if
 * <code>setSeed</code> was previously called.
 * @param bytes the array to be filled in with random bytes.
 */
  synchronized public void nextBytes(  byte[] bytes){
    secureRandomSpi.engineNextBytes(bytes);
  }
  /** 
 * Generates an integer containing the user-specified number of
 * pseudo-random bits (right justified, with leading zeros).  This
 * method overrides a <code>java.util.Random</code> method, and serves
 * to provide a source of random bits to all of the methods inherited
 * from that class (for example, <code>nextInt</code>,
 * <code>nextLong</code>, and <code>nextFloat</code>).
 * @param numBits number of pseudo-random bits to be generated, where
 * 0 <= <code>numBits</code> <= 32.
 * @return an <code>int</code> containing the user-specified number
 * of pseudo-random bits (right justified, with leading zeros).
 */
  final protected int next(  int numBits){
    int numBytes=(numBits + 7) / 8;
    byte b[]=new byte[numBytes];
    int next=0;
    nextBytes(b);
    for (int i=0; i < numBytes; i++)     next=(next << 8) + (b[i] & 0xFF);
    return next >>> (numBytes * 8 - numBits);
  }
  /** 
 * Returns the given number of seed bytes, computed using the seed
 * generation algorithm that this class uses to seed itself.  This
 * call may be used to seed other random number generators.
 * <p>This method is only included for backwards compatibility.
 * The caller is encouraged to use one of the alternative
 * <code>getInstance</code> methods to obtain a SecureRandom object, and
 * then call the <code>generateSeed</code> method to obtain seed bytes
 * from that object.
 * @param numBytes the number of seed bytes to generate.
 * @return the seed bytes.
 * @see #setSeed
 */
  public static byte[] getSeed(  int numBytes){
    if (seedGenerator == null)     seedGenerator=new SecureRandom();
    return seedGenerator.generateSeed(numBytes);
  }
  /** 
 * Returns the given number of seed bytes, computed using the seed
 * generation algorithm that this class uses to seed itself.  This
 * call may be used to seed other random number generators.
 * @param numBytes the number of seed bytes to generate.
 * @return the seed bytes.
 */
  public byte[] generateSeed(  int numBytes){
    return secureRandomSpi.engineGenerateSeed(numBytes);
  }
  /** 
 * Helper function to convert a long into a byte array (least significant
 * byte first).
 */
  private static byte[] longToByteArray(  long l){
    byte[] retVal=new byte[8];
    for (int i=0; i < 8; i++) {
      retVal[i]=(byte)l;
      l>>=8;
    }
    return retVal;
  }
  /** 
 * Gets a default PRNG algorithm by looking through all registered
 * providers. Returns the first PRNG algorithm of the first provider that
 * has registered a SecureRandom implementation, or null if none of the
 * registered providers supplies a SecureRandom implementation.
 */
  private static String getPrngAlgorithm(){
    for (    Provider p : Providers.getProviderList().providers()) {
      for (      Service s : p.getServices()) {
        if (s.getType().equals("SecureRandom")) {
          return s.getAlgorithm();
        }
      }
    }
    return null;
  }
  static final long serialVersionUID=4940670005562187L;
  /** 
 * @serial
 */
  private byte[] state;
  /** 
 * @serial
 */
  private MessageDigest digest=null;
  /** 
 * @serialWe know that the MessageDigest class does not implement
 * java.io.Serializable.  However, since this field is no longer
 * used, it will always be NULL and won't affect the serialization
 * of the SecureRandom class itself.
 */
  private byte[] randomBytes;
  /** 
 * @serial
 */
  private int randomBytesUsed;
  /** 
 * @serial
 */
  private long counter;
}