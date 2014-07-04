package sun.net.www.protocol.file;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.URL;
import java.net.Proxy;
import java.net.MalformedURLException;
import java.net.URLStreamHandler;
import java.io.InputStream;
import java.io.IOException;
import sun.net.www.ParseUtil;
import java.io.File;
/** 
 * Open an file input stream given a URL.
 * @author      James Gosling
 */
public class Handler extends URLStreamHandler {
  private String getHost(  URL url){
    String host=url.getHost();
    if (host == null)     host="";
    return host;
  }
  protected void parseURL(  URL u,  String spec,  int start,  int limit){
    super.parseURL(u,spec.replace(File.separatorChar,'/'),start,limit);
  }
  public synchronized URLConnection openConnection(  URL u) throws IOException {
    return openConnection(u,null);
  }
  public synchronized URLConnection openConnection(  URL u,  Proxy p) throws IOException {
    String host=u.getHost();
    if (host == null || host.equals("") || host.equals("~") || host.equalsIgnoreCase("localhost")) {
      File file=new File(ParseUtil.decode(u.getPath()));
      return createFileURLConnection(u,file);
    }
    URLConnection uc;
    URL ru;
    try {
      ru=new URL("ftp",host,u.getFile() + (u.getRef() == null ? "" : "#" + u.getRef()));
      if (p != null) {
        uc=ru.openConnection(p);
      }
 else {
        uc=ru.openConnection();
      }
    }
 catch (    IOException e) {
      uc=null;
    }
    if (uc == null) {
      throw new IOException("Unable to connect to: " + u.toExternalForm());
    }
    return uc;
  }
  protected URLConnection createFileURLConnection(  URL u,  File file){
    return new FileURLConnection(u,file);
  }
  /** 
 * Compares the host components of two URLs.
 * @param u1 the URL of the first host to compare
 * @param u2 the URL of the second host to compare
 * @return  <tt>true</tt> if and only if they
 * are equal, <tt>false</tt> otherwise.
 */
  protected boolean hostsEqual(  URL u1,  URL u2){
    String s1=u1.getHost();
    String s2=u2.getHost();
    if ("localhost".equalsIgnoreCase(s1) && (s2 == null || "".equals(s2)))     return true;
    if ("localhost".equalsIgnoreCase(s2) && (s1 == null || "".equals(s1)))     return true;
    return super.hostsEqual(u1,u2);
  }
}
