package com.sun.jndi.ldap;
import java.net.MalformedURLException;
import java.net.URL;
abstract class VersionHelper {
  private static VersionHelper helper=null;
  VersionHelper(){
  }
static {
    try {
      Class.forName("java.net.URLClassLoader");
      Class.forName("java.security.PrivilegedAction");
      helper=(VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper12").newInstance();
    }
 catch (    Exception e) {
    }
    if (helper == null) {
      try {
        helper=(VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper11").newInstance();
      }
 catch (      Exception e) {
      }
    }
  }
  static VersionHelper getVersionHelper(){
    return helper;
  }
  abstract ClassLoader getURLClassLoader(  String[] url) throws MalformedURLException ;
  static protected URL[] getUrlArray(  String[] url) throws MalformedURLException {
    URL[] urlArray=new URL[url.length];
    for (int i=0; i < urlArray.length; i++) {
      urlArray[i]=new URL(url[i]);
    }
    return urlArray;
  }
  abstract Class loadClass(  String className) throws ClassNotFoundException ;
  abstract Thread createThread(  Runnable r);
}
