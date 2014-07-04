package sun.text.normalizer;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;
/** 
 * Provides access to ICU data files as InputStreams.  Implements security checking.
 */
public final class ICUData {
  private static InputStream getStream(  final Class root,  final String resourceName,  boolean required){
    InputStream i=null;
    if (System.getSecurityManager() != null) {
      i=(InputStream)AccessController.doPrivileged(new PrivilegedAction(){
        public Object run(){
          return root.getResourceAsStream(resourceName);
        }
      }
);
    }
 else {
      i=root.getResourceAsStream(resourceName);
    }
    if (i == null && required) {
      throw new MissingResourceException("could not locate data",root.getPackage().getName(),resourceName);
    }
    return i;
  }
  public static InputStream getStream(  String resourceName){
    return getStream(ICUData.class,resourceName,false);
  }
  public static InputStream getRequiredStream(  String resourceName){
    return getStream(ICUData.class,resourceName,true);
  }
}
