package sun.io;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.util.Properties;
/** 
 * Package-private utility class that caches the default converter classes and
 * provides other logic common to both the ByteToCharConverter and
 * CharToByteConverter classes.
 * @author   Mark Reinhold
 * @since    1.2
 * @deprecated Replaced by {@link java.nio.charset}.  THIS API WILL BE
 * REMOVED IN J2SE 1.6.
 */
@Deprecated public class Converters {
  private Converters(){
  }
  private static Object lock=Converters.class;
  private static String converterPackageName=null;
  private static String defaultEncoding=null;
  public static final int BYTE_TO_CHAR=0;
  public static final int CHAR_TO_BYTE=1;
  private static final String[] converterPrefix={"ByteToChar","CharToByte"};
  private static final int CACHE_SIZE=3;
  private static final Object DEFAULT_NAME=new Object();
  @SuppressWarnings("unchecked") private static SoftReference<Object[]>[][] classCache=(SoftReference<Object[]>[][])new SoftReference<?>[][]{new SoftReference<?>[CACHE_SIZE],new SoftReference<?>[CACHE_SIZE]};
  private static void moveToFront(  Object[] oa,  int i){
    Object ob=oa[i];
    for (int j=i; j > 0; j--)     oa[j]=oa[j - 1];
    oa[0]=ob;
  }
  private static Class<?> cache(  int type,  Object encoding){
    SoftReference<Object[]>[] srs=classCache[type];
    for (int i=0; i < CACHE_SIZE; i++) {
      SoftReference<Object[]> sr=srs[i];
      if (sr == null)       continue;
      Object[] oa=sr.get();
      if (oa == null) {
        srs[i]=null;
        continue;
      }
      if (oa[1].equals(encoding)) {
        moveToFront(srs,i);
        return (Class<?>)oa[0];
      }
    }
    return null;
  }
  private static Class<?> cache(  int type,  Object encoding,  Class<?> c){
    SoftReference<Object[]>[] srs=classCache[type];
    srs[CACHE_SIZE - 1]=new SoftReference<>(new Object[]{c,encoding});
    moveToFront(srs,CACHE_SIZE - 1);
    return c;
  }
  public static boolean isCached(  int type,  String encoding){
synchronized (lock) {
      SoftReference<Object[]>[] srs=classCache[type];
      for (int i=0; i < CACHE_SIZE; i++) {
        SoftReference<Object[]> sr=srs[i];
        if (sr == null)         continue;
        Object[] oa=sr.get();
        if (oa == null) {
          srs[i]=null;
          continue;
        }
        if (oa[1].equals(encoding))         return true;
      }
      return false;
    }
  }
  /** 
 * Get the name of the converter package 
 */
  private static String getConverterPackageName(){
    String cp=converterPackageName;
    if (cp != null)     return cp;
    java.security.PrivilegedAction<String> pa=new sun.security.action.GetPropertyAction("file.encoding.pkg");
    cp=java.security.AccessController.doPrivileged(pa);
    if (cp != null) {
      converterPackageName=cp;
    }
 else {
      cp="sun.io";
    }
    return cp;
  }
  public static String getDefaultEncodingName(){
synchronized (lock) {
      if (defaultEncoding == null) {
        java.security.PrivilegedAction<String> pa=new sun.security.action.GetPropertyAction("file.encoding");
        defaultEncoding=java.security.AccessController.doPrivileged(pa);
      }
    }
    return defaultEncoding;
  }
  public static void resetDefaultEncodingName(){
    if (sun.misc.VM.isBooted())     return;
synchronized (lock) {
      defaultEncoding="ISO-8859-1";
      Properties p=System.getProperties();
      p.setProperty("file.encoding",defaultEncoding);
      System.setProperties(p);
    }
  }
  /** 
 * Get the class that implements the given type of converter for the named
 * encoding, or throw an UnsupportedEncodingException if no such class can
 * be found
 */
  private static Class<?> getConverterClass(  int type,  String encoding) throws UnsupportedEncodingException {
    String enc=null;
    if (!encoding.equals("ISO8859_1")) {
      if (encoding.equals("8859_1")) {
        enc="ISO8859_1";
      }
 else       if (encoding.equals("ISO8859-1")) {
        enc="ISO8859_1";
      }
 else       if (encoding.equals("646")) {
        enc="ASCII";
      }
 else {
        enc=CharacterEncoding.aliasName(encoding);
      }
    }
    if (enc == null) {
      enc=encoding;
    }
    try {
      return Class.forName(getConverterPackageName() + "." + converterPrefix[type]+ enc);
    }
 catch (    ClassNotFoundException e) {
      throw new UnsupportedEncodingException(enc);
    }
  }
  /** 
 * Instantiate the given converter class, or throw an
 * UnsupportedEncodingException if it cannot be instantiated
 */
  private static Object newConverter(  String enc,  Class<?> c) throws UnsupportedEncodingException {
    try {
      return c.newInstance();
    }
 catch (    InstantiationException e) {
      throw new UnsupportedEncodingException(enc);
    }
catch (    IllegalAccessException e) {
      throw new UnsupportedEncodingException(enc);
    }
  }
  /** 
 * Create a converter object that implements the given type of converter
 * for the given encoding, or throw an UnsupportedEncodingException if no
 * appropriate converter class can be found and instantiated
 */
  static Object newConverter(  int type,  String enc) throws UnsupportedEncodingException {
    Class<?> c;
synchronized (lock) {
      c=cache(type,enc);
      if (c == null) {
        c=getConverterClass(type,enc);
        if (!c.getName().equals("sun.io.CharToByteUTF8"))         cache(type,enc,c);
      }
    }
    return newConverter(enc,c);
  }
  /** 
 * Find the class that implements the given type of converter for the
 * default encoding.  If the default encoding cannot be determined or is
 * not yet defined, return a class that implements the fallback default
 * encoding, which is just ISO 8859-1.
 */
  private static Class<?> getDefaultConverterClass(  int type){
    boolean fillCache=false;
    Class<?> c;
    c=cache(type,DEFAULT_NAME);
    if (c != null)     return c;
    String enc=getDefaultEncodingName();
    if (enc != null) {
      fillCache=true;
    }
 else {
      enc="ISO8859_1";
    }
    try {
      c=getConverterClass(type,enc);
      if (fillCache) {
        cache(type,DEFAULT_NAME,c);
      }
    }
 catch (    UnsupportedEncodingException x) {
      try {
        c=getConverterClass(type,"ISO8859_1");
      }
 catch (      UnsupportedEncodingException y) {
        throw new InternalError("Cannot find default " + converterPrefix[type] + " converter class");
      }
    }
    return c;
  }
  /** 
 * Create a converter object that implements the given type of converter
 * for the default encoding, falling back to ISO 8859-1 if the default
 * encoding cannot be determined.
 */
  static Object newDefaultConverter(  int type){
    Class<?> c;
synchronized (lock) {
      c=getDefaultConverterClass(type);
    }
    try {
      return newConverter("",c);
    }
 catch (    UnsupportedEncodingException x) {
      throw new InternalError("Cannot instantiate default converter" + " class " + c.getName());
    }
  }
}
