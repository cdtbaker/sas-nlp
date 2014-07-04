package com.sun.java.browser.net;
import java.net.URL;
/** 
 * @author  Zhengyu Gu
 */
public interface ProxyServiceProvider {
  public ProxyInfo[] getProxyInfo(  URL url);
}
