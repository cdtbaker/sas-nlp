package sun.net.spi;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.io.IOException;
import sun.misc.RegexpPool;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.NetProperties;
import sun.net.SocksProxy;
/** 
 * Supports proxy settings using system properties This proxy selector
 * provides backward compatibility with the old http protocol handler
 * as far as how proxy is set
 * Most of the implementation copied from the old http protocol handler
 * Supports http/https/ftp.proxyHost, http/https/ftp.proxyPort,
 * proxyHost, proxyPort, and http/https/ftp.nonProxyHost, and socks.
 * NOTE: need to do gopher as well
 */
public class DefaultProxySelector extends ProxySelector {
  /** 
 * This is where we define all the valid System Properties we have to
 * support for each given protocol.
 * The format of this 2 dimensional array is :
 * - 1 row per protocol (http, ftp, ...)
 * - 1st element of each row is the protocol name
 * - subsequent elements are prefixes for Host & Port properties
 * listed in order of priority.
 * Example:
 * {"ftp", "ftp.proxy", "ftpProxy", "proxy", "socksProxy"},
 * means for FTP we try in that oder:
 * + ftp.proxyHost & ftp.proxyPort
 * + ftpProxyHost & ftpProxyPort
 * + proxyHost & proxyPort
 * + socksProxyHost & socksProxyPort
 * Note that the socksProxy should *always* be the last on the list
 */
  final static String[][] props={{"http","http.proxy","proxy","socksProxy"},{"https","https.proxy","proxy","socksProxy"},{"ftp","ftp.proxy","ftpProxy","proxy","socksProxy"},{"gopher","gopherProxy","socksProxy"},{"socket","socksProxy"}};
  private static final String SOCKS_PROXY_VERSION="socksProxyVersion";
  private static boolean hasSystemProxies=false;
static {
    final String key="java.net.useSystemProxies";
    Boolean b=AccessController.doPrivileged(new PrivilegedAction<Boolean>(){
      public Boolean run(){
        return NetProperties.getBoolean(key);
      }
    }
);
    if (b != null && b.booleanValue()) {
      java.security.AccessController.doPrivileged(new sun.security.action.LoadLibraryAction("net"));
      hasSystemProxies=init();
    }
  }
  /** 
 * How to deal with "non proxy hosts":
 * since we do have to generate a RegexpPool we don't want to do that if
 * it's not necessary. Therefore we do cache the result, on a per-protocol
 * basis, and change it only when the "source", i.e. the system property,
 * did change.
 */
static class NonProxyInfo {
    static final String defStringVal="localhost|127.*|[::1]";
    String hostsSource;
    RegexpPool hostsPool;
    final String property;
    final String defaultVal;
    static NonProxyInfo ftpNonProxyInfo=new NonProxyInfo("ftp.nonProxyHosts",null,null,defStringVal);
    static NonProxyInfo httpNonProxyInfo=new NonProxyInfo("http.nonProxyHosts",null,null,defStringVal);
    NonProxyInfo(    String p,    String s,    RegexpPool pool,    String d){
      property=p;
      hostsSource=s;
      hostsPool=pool;
      defaultVal=d;
    }
  }
  /** 
 * select() method. Where all the hard work is done.
 * Build a list of proxies depending on URI.
 * Since we're only providing compatibility with the system properties
 * from previous releases (see list above), that list will always
 * contain 1 single proxy, default being NO_PROXY.
 */
  public java.util.List<Proxy> select(  URI uri){
    if (uri == null) {
      throw new IllegalArgumentException("URI can't be null.");
    }
    String protocol=uri.getScheme();
    String host=uri.getHost();
    if (host == null) {
      String auth=uri.getAuthority();
      if (auth != null) {
        int i;
        i=auth.indexOf('@');
        if (i >= 0) {
          auth=auth.substring(i + 1);
        }
        i=auth.lastIndexOf(':');
        if (i >= 0) {
          auth=auth.substring(0,i);
        }
        host=auth;
      }
    }
    if (protocol == null || host == null) {
      throw new IllegalArgumentException("protocol = " + protocol + " host = "+ host);
    }
    List<Proxy> proxyl=new ArrayList<Proxy>(1);
    NonProxyInfo pinfo=null;
    if ("http".equalsIgnoreCase(protocol)) {
      pinfo=NonProxyInfo.httpNonProxyInfo;
    }
 else     if ("https".equalsIgnoreCase(protocol)) {
      pinfo=NonProxyInfo.httpNonProxyInfo;
    }
 else     if ("ftp".equalsIgnoreCase(protocol)) {
      pinfo=NonProxyInfo.ftpNonProxyInfo;
    }
    final String proto=protocol;
    final NonProxyInfo nprop=pinfo;
    final String urlhost=host.toLowerCase();
    Proxy p=AccessController.doPrivileged(new PrivilegedAction<Proxy>(){
      public Proxy run(){
        int i, j;
        String phost=null;
        int pport=0;
        String nphosts=null;
        InetSocketAddress saddr=null;
        for (i=0; i < props.length; i++) {
          if (props[i][0].equalsIgnoreCase(proto)) {
            for (j=1; j < props[i].length; j++) {
              phost=NetProperties.get(props[i][j] + "Host");
              if (phost != null && phost.length() != 0)               break;
            }
            if (phost == null || phost.length() == 0) {
              if (hasSystemProxies) {
                String sproto;
                if (proto.equalsIgnoreCase("socket"))                 sproto="socks";
 else                 sproto=proto;
                Proxy sproxy=getSystemProxy(sproto,urlhost);
                if (sproxy != null) {
                  return sproxy;
                }
              }
              return Proxy.NO_PROXY;
            }
            if (nprop != null) {
              nphosts=NetProperties.get(nprop.property);
synchronized (nprop) {
                if (nphosts == null) {
                  if (nprop.defaultVal != null) {
                    nphosts=nprop.defaultVal;
                  }
 else {
                    nprop.hostsSource=null;
                    nprop.hostsPool=null;
                  }
                }
                if (nphosts != null) {
                  if (!nphosts.equals(nprop.hostsSource)) {
                    RegexpPool pool=new RegexpPool();
                    StringTokenizer st=new StringTokenizer(nphosts,"|",false);
                    try {
                      while (st.hasMoreTokens()) {
                        pool.add(st.nextToken().toLowerCase(),Boolean.TRUE);
                      }
                    }
 catch (                    sun.misc.REException ex) {
                    }
                    nprop.hostsPool=pool;
                    nprop.hostsSource=nphosts;
                  }
                }
                if (nprop.hostsPool != null && nprop.hostsPool.match(urlhost) != null) {
                  return Proxy.NO_PROXY;
                }
              }
            }
            pport=NetProperties.getInteger(props[i][j] + "Port",0).intValue();
            if (pport == 0 && j < (props[i].length - 1)) {
              for (int k=1; k < (props[i].length - 1); k++) {
                if ((k != j) && (pport == 0))                 pport=NetProperties.getInteger(props[i][k] + "Port",0).intValue();
              }
            }
            if (pport == 0) {
              if (j == (props[i].length - 1))               pport=defaultPort("socket");
 else               pport=defaultPort(proto);
            }
            saddr=InetSocketAddress.createUnresolved(phost,pport);
            if (j == (props[i].length - 1)) {
              int version=NetProperties.getInteger(SOCKS_PROXY_VERSION,5).intValue();
              return SocksProxy.create(saddr,version);
            }
 else {
              return new Proxy(Proxy.Type.HTTP,saddr);
            }
          }
        }
        return Proxy.NO_PROXY;
      }
    }
);
    proxyl.add(p);
    return proxyl;
  }
  public void connectFailed(  URI uri,  SocketAddress sa,  IOException ioe){
    if (uri == null || sa == null || ioe == null) {
      throw new IllegalArgumentException("Arguments can't be null.");
    }
  }
  private int defaultPort(  String protocol){
    if ("http".equalsIgnoreCase(protocol)) {
      return 80;
    }
 else     if ("https".equalsIgnoreCase(protocol)) {
      return 443;
    }
 else     if ("ftp".equalsIgnoreCase(protocol)) {
      return 80;
    }
 else     if ("socket".equalsIgnoreCase(protocol)) {
      return 1080;
    }
 else     if ("gopher".equalsIgnoreCase(protocol)) {
      return 80;
    }
 else {
      return -1;
    }
  }
  private native static boolean init();
  private native Proxy getSystemProxy(  String protocol,  String host);
}
