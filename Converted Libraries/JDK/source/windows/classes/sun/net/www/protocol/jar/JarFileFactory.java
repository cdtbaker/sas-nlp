package sun.net.www.protocol.jar;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.security.Permission;
import sun.net.util.URLUtil;
class JarFileFactory implements URLJarFile.URLJarFileCloseController {
  private static HashMap<String,JarFile> fileCache=new HashMap<String,JarFile>();
  private static HashMap<JarFile,URL> urlCache=new HashMap<JarFile,URL>();
  URLConnection getConnection(  JarFile jarFile) throws IOException {
    URL u=urlCache.get(jarFile);
    if (u != null)     return u.openConnection();
    return null;
  }
  public JarFile get(  URL url) throws IOException {
    return get(url,true);
  }
  JarFile get(  URL url,  boolean useCaches) throws IOException {
    if (url.getProtocol().equalsIgnoreCase("file")) {
      String host=url.getHost();
      if (host != null && !host.equals("") && !host.equalsIgnoreCase("localhost")) {
        url=new URL("file","","//" + host + url.getPath());
      }
    }
    JarFile result=null;
    JarFile local_result=null;
    if (useCaches) {
synchronized (this) {
        result=getCachedJarFile(url);
      }
      if (result == null) {
        local_result=URLJarFile.getJarFile(url,this);
synchronized (this) {
          result=getCachedJarFile(url);
          if (result == null) {
            fileCache.put(URLUtil.urlNoFragString(url),local_result);
            urlCache.put(local_result,url);
            result=local_result;
          }
 else {
            if (local_result != null) {
              local_result.close();
            }
          }
        }
      }
    }
 else {
      result=URLJarFile.getJarFile(url,this);
    }
    if (result == null)     throw new FileNotFoundException(url.toString());
    return result;
  }
  /** 
 * Callback method of the URLJarFileCloseController to
 * indicate that the JarFile is close. This way we can
 * remove the JarFile from the cache
 */
  public void close(  JarFile jarFile){
    URL urlRemoved=urlCache.remove(jarFile);
    if (urlRemoved != null) {
      fileCache.remove(URLUtil.urlNoFragString(urlRemoved));
    }
  }
  private JarFile getCachedJarFile(  URL url){
    JarFile result=fileCache.get(URLUtil.urlNoFragString(url));
    if (result != null) {
      Permission perm=getPermission(result);
      if (perm != null) {
        SecurityManager sm=System.getSecurityManager();
        if (sm != null) {
          try {
            sm.checkPermission(perm);
          }
 catch (          SecurityException se) {
            if ((perm instanceof java.io.FilePermission) && perm.getActions().indexOf("read") != -1) {
              sm.checkRead(perm.getName());
            }
 else             if ((perm instanceof java.net.SocketPermission) && perm.getActions().indexOf("connect") != -1) {
              sm.checkConnect(url.getHost(),url.getPort());
            }
 else {
              throw se;
            }
          }
        }
      }
    }
    return result;
  }
  private Permission getPermission(  JarFile jarFile){
    try {
      URLConnection uc=(URLConnection)getConnection(jarFile);
      if (uc != null)       return uc.getPermission();
    }
 catch (    IOException ioe) {
    }
    return null;
  }
}
