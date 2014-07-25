package com.sun.script.javascript;
import java.util.*;
import sun.org.mozilla.javascript.internal.*;
/** 
 * This class prevents script access to certain sensitive classes.
 * Note that this class checks over and above SecurityManager. i.e., although
 * a SecurityManager would pass, class shutter may still prevent access.
 * @author A. Sundararajan
 * @since 1.6
 */
final class RhinoClassShutter implements ClassShutter {
  private static Map<String,Boolean> protectedClasses;
  private static RhinoClassShutter theInstance;
  private RhinoClassShutter(){
  }
  static synchronized ClassShutter getInstance(){
    if (theInstance == null) {
      theInstance=new RhinoClassShutter();
      protectedClasses=new HashMap<String,Boolean>();
      protectedClasses.put("java.security.AccessController",Boolean.TRUE);
    }
    return theInstance;
  }
  public boolean visibleToScripts(  String fullClassName){
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      int i=fullClassName.lastIndexOf(".");
      if (i != -1) {
        try {
          sm.checkPackageAccess(fullClassName.substring(0,i));
        }
 catch (        SecurityException se) {
          return false;
        }
      }
    }
    return protectedClasses.get(fullClassName) == null;
  }
}
