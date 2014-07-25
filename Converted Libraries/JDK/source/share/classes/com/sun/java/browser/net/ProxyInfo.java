package com.sun.java.browser.net;
/** 
 * @author  Zhengyu Gu
 */
public interface ProxyInfo {
  public String getHost();
  public int getPort();
  public boolean isSocks();
}
