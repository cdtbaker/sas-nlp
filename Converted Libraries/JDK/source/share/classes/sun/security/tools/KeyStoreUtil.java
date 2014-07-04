package sun.security.tools;
/** 
 * <p> This class provides several utilities to <code>KeyStore</code>.
 * @since 1.6.0
 */
public class KeyStoreUtil {
  private KeyStoreUtil(){
  }
  /** 
 * Returns true if KeyStore has a password. This is true except for
 * MSCAPI KeyStores
 */
  public static boolean isWindowsKeyStore(  String storetype){
    return storetype.equalsIgnoreCase("Windows-MY") || storetype.equalsIgnoreCase("Windows-ROOT");
  }
  /** 
 * Returns standard-looking names for storetype
 */
  public static String niceStoreTypeName(  String storetype){
    if (storetype.equalsIgnoreCase("Windows-MY")) {
      return "Windows-MY";
    }
 else     if (storetype.equalsIgnoreCase("Windows-ROOT")) {
      return "Windows-ROOT";
    }
 else {
      return storetype.toUpperCase();
    }
  }
}
