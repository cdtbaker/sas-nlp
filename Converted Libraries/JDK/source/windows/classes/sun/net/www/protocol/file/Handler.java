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
  public synchronized URLConnection openConnection(  URL url) throws IOException {
    return openConnection(url,null);
  }
  public synchronized URLConnection openConnection(  URL url,  Proxy p) throws IOException {
    String path;
    String file=url.getFile();
    String host=url.getHost();
    path=ParseUtil.decode(file);
    path=path.replace('/','\\');
    path=path.replace('|',':');
    if ((host == null) || host.equals("") || host.equalsIgnoreCase("localhost")|| host.equals("~")) {
      return createFileURLConnection(url,new File(path));
    }
    path="\\\\" + host + path;
    File f=new File(path);
    if (f.exists()) {
      return createFileURLConnection(url,f);
    }
    URLConnection uc;
    URL newurl;
    try {
      newurl=new URL("ftp",host,file + (url.getRef() == null ? "" : "#" + url.getRef()));
      if (p != null) {
        uc=newurl.openConnection(p);
      }
 else {
        uc=newurl.openConnection();
      }
    }
 catch (    IOException e) {
      uc=null;
    }
    if (uc == null) {
      throw new IOException("Unable to connect to: " + url.toExternalForm());
    }
    return uc;
  }
  /** 
 * Template method to be overriden by Java Plug-in. [stanleyh]
 */
  protected URLConnection createFileURLConnection(  URL url,  File file){
    return new FileURLConnection(url,file);
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
