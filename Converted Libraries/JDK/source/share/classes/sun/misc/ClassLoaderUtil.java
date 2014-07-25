package sun.misc;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
public class ClassLoaderUtil {
  /** 
 * Releases resources held by a URLClassLoader. A new classloader must
 * be created before the underlying resources can be accessed again.
 * @param classLoader the instance of URLClassLoader (or a subclass)
 */
  public static void releaseLoader(  URLClassLoader classLoader){
    releaseLoader(classLoader,null);
  }
  /** 
 * Releases resources held by a URLClassLoader.  Notably, close the jars
 * opened by the loader. Initializes and updates the List of
 * jars that have been successfully closed.
 * <p>
 * @param classLoader the instance of URLClassLoader (or a subclass)
 * @param jarsClosed a List of Strings that will contain the names of jars
 * successfully closed; can be null if the caller does not need the information returned
 * @return a List of IOExceptions reporting jars that failed to close; null
 * indicates that an error other than an IOException occurred attempting to
 * release the loader; empty indicates a successful release; non-empty
 * indicates at least one error attempting to close an open jar.
 */
  public static List<IOException> releaseLoader(  URLClassLoader classLoader,  List<String> jarsClosed){
    List<IOException> ioExceptions=new LinkedList<IOException>();
    try {
      if (jarsClosed != null) {
        jarsClosed.clear();
      }
      URLClassPath ucp=SharedSecrets.getJavaNetAccess().getURLClassPath(classLoader);
      ArrayList loaders=ucp.loaders;
      Stack urls=ucp.urls;
      HashMap lmap=ucp.lmap;
synchronized (urls) {
        urls.clear();
      }
synchronized (lmap) {
        lmap.clear();
      }
synchronized (ucp) {
        for (        Object o : loaders) {
          if (o != null) {
            if (o instanceof URLClassPath.JarLoader) {
              URLClassPath.JarLoader jl=(URLClassPath.JarLoader)o;
              JarFile jarFile=jl.getJarFile();
              try {
                if (jarFile != null) {
                  jarFile.close();
                  if (jarsClosed != null) {
                    jarsClosed.add(jarFile.getName());
                  }
                }
              }
 catch (              IOException ioe) {
                String jarFileName=(jarFile == null) ? "filename not available" : jarFile.getName();
                String msg="Error closing JAR file: " + jarFileName;
                IOException newIOE=new IOException(msg);
                newIOE.initCause(ioe);
                ioExceptions.add(newIOE);
              }
            }
          }
        }
        loaders.clear();
      }
    }
 catch (    Throwable t) {
      throw new RuntimeException(t);
    }
    return ioExceptions;
  }
}
