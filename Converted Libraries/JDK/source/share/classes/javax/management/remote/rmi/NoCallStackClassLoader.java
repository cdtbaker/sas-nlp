package javax.management.remote.rmi;
import java.security.ProtectionDomain;
/** 
 * <p>A class loader that only knows how to define a limited number
 * of classes, and load a limited number of other classes through
 * delegation to another loader.  It is used to get around a problem
 * with Serialization, in particular as used by RMI (including
 * RMI/IIOP).  The JMX Remote API defines exactly what class loader
 * must be used to deserialize arguments on the server, and return
 * values on the client.  We communicate this class loader to RMI by
 * setting it as the context class loader.  RMI uses the context
 * class loader to load classes as it deserializes, which is what we
 * want.  However, before consulting the context class loader, it
 * looks up the call stack for a class with a non-null class loader,
 * and uses that if it finds one.  So, in the standalone version of
 * javax.management.remote, if the class you're looking for is known
 * to the loader of jmxremote.jar (typically the system class loader)
 * then that loader will load it.  This contradicts the class-loading
 * semantics required.
 * <p>We get around the problem by ensuring that the search up the
 * call stack will find a non-null class loader that doesn't load any
 * classes of interest, namely this one.  So even though this loader
 * is indeed consulted during deserialization, it never finds the
 * class being deserialized.  RMI then proceeds to use the context
 * class loader, as we require.
 * <p>This loader is constructed with the name and byte-code of one
 * or more classes that it defines, and a class-loader to which it
 * will delegate certain other classes required by that byte-code.
 * We construct the byte-code somewhat painstakingly, by compiling
 * the Java code directly, converting into a string, copying that
 * string into the class that needs this loader, and using the
 * stringToBytes method to convert it into the byte array.  We
 * compile with -g:none because there's not much point in having
 * line-number information and the like in these directly-encoded
 * classes.
 * <p>The referencedClassNames should contain the names of all
 * classes that are referenced by the classes defined by this loader.
 * It is not necessary to include standard J2SE classes, however.
 * Here, a class is referenced if it is the superclass or a
 * superinterface of a defined class, or if it is the type of a
 * field, parameter, or return value.  A class is not referenced if
 * it only appears in the throws clause of a method or constructor.
 * Of course, referencedClassNames should not contain any classes
 * that the user might want to deserialize, because the whole point
 * of this loader is that it does not find such classes.
 */
class NoCallStackClassLoader extends ClassLoader {
  /** 
 * Simplified constructor when this loader only defines one class.  
 */
  public NoCallStackClassLoader(  String className,  byte[] byteCode,  String[] referencedClassNames,  ClassLoader referencedClassLoader,  ProtectionDomain protectionDomain){
    this(new String[]{className},new byte[][]{byteCode},referencedClassNames,referencedClassLoader,protectionDomain);
  }
  public NoCallStackClassLoader(  String[] classNames,  byte[][] byteCodes,  String[] referencedClassNames,  ClassLoader referencedClassLoader,  ProtectionDomain protectionDomain){
    super(null);
    if (classNames == null || classNames.length == 0 || byteCodes == null || classNames.length != byteCodes.length || referencedClassNames == null || protectionDomain == null)     throw new IllegalArgumentException();
    for (int i=0; i < classNames.length; i++) {
      if (classNames[i] == null || byteCodes[i] == null)       throw new IllegalArgumentException();
    }
    for (int i=0; i < referencedClassNames.length; i++) {
      if (referencedClassNames[i] == null)       throw new IllegalArgumentException();
    }
    this.classNames=classNames;
    this.byteCodes=byteCodes;
    this.referencedClassNames=referencedClassNames;
    this.referencedClassLoader=referencedClassLoader;
    this.protectionDomain=protectionDomain;
  }
  @Override protected Class<?> findClass(  String name) throws ClassNotFoundException {
    for (int i=0; i < classNames.length; i++) {
      if (name.equals(classNames[i])) {
        return defineClass(classNames[i],byteCodes[i],0,byteCodes[i].length,protectionDomain);
      }
    }
    if (referencedClassLoader != null) {
      for (int i=0; i < referencedClassNames.length; i++) {
        if (name.equals(referencedClassNames[i]))         return referencedClassLoader.loadClass(name);
      }
    }
    throw new ClassNotFoundException(name);
  }
  private final String[] classNames;
  private final byte[][] byteCodes;
  private final String[] referencedClassNames;
  private final ClassLoader referencedClassLoader;
  private final ProtectionDomain protectionDomain;
  /** 
 * <p>Construct a <code>byte[]</code> using the characters of the
 * given <code>String</code>.  Only the low-order byte of each
 * character is used.  This method is useful to reduce the
 * footprint of classes that include big byte arrays (e.g. the
 * byte code of other classes), because a string takes up much
 * less space in a class file than the byte code to initialize a
 * <code>byte[]</code> with the same number of bytes.</p>
 * <p>We use just one byte per character even though characters
 * contain two bytes.  The resultant output length is much the
 * same: using one byte per character is shorter because it has
 * more characters in the optimal 1-127 range but longer because
 * it has more zero bytes (which are frequent, and are encoded as
 * two bytes in classfile UTF-8).  But one byte per character has
 * two key advantages: (1) you can see the string constants, which
 * is reassuring, (2) you don't need to know whether the class
 * file length is odd.</p>
 * <p>This method differs from {@link String#getBytes()} in that
 * it does not use any encoding.  So it is guaranteed that each
 * byte of the result is numerically identical (mod 256) to the
 * corresponding character of the input.
 */
  public static byte[] stringToBytes(  String s){
    final int slen=s.length();
    byte[] bytes=new byte[slen];
    for (int i=0; i < slen; i++)     bytes[i]=(byte)s.charAt(i);
    return bytes;
  }
}
