package sun.applet;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.FileDescriptor;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketPermission;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.security.*;
import java.lang.reflect.*;
import sun.awt.AWTSecurityManager;
import sun.awt.AppContext;
import sun.security.provider.*;
import sun.security.util.SecurityConstants;
/** 
 * This class defines an applet security policy
 */
public class AppletSecurity extends AWTSecurityManager {
  private AppContext mainAppContext;
  private static Field facc=null;
  private static Field fcontext=null;
static {
    try {
      facc=URLClassLoader.class.getDeclaredField("acc");
      facc.setAccessible(true);
      fcontext=AccessControlContext.class.getDeclaredField("context");
      fcontext.setAccessible(true);
    }
 catch (    NoSuchFieldException e) {
      throw new UnsupportedOperationException(e);
    }
  }
  /** 
 * Construct and initialize.
 */
  public AppletSecurity(){
    reset();
    mainAppContext=AppContext.getAppContext();
  }
  private HashSet restrictedPackages=new HashSet();
  /** 
 * Reset from Properties
 */
  public void reset(){
    restrictedPackages.clear();
    AccessController.doPrivileged(new PrivilegedAction(){
      public Object run(){
        Enumeration e=System.getProperties().propertyNames();
        while (e.hasMoreElements()) {
          String name=(String)e.nextElement();
          if (name != null && name.startsWith("package.restrict.access.")) {
            String value=System.getProperty(name);
            if (value != null && value.equalsIgnoreCase("true")) {
              String pkg=name.substring(24);
              restrictedPackages.add(pkg);
            }
          }
        }
        return null;
      }
    }
);
  }
  /** 
 * get the current (first) instance of an AppletClassLoader on the stack.
 */
  private AppletClassLoader currentAppletClassLoader(){
    ClassLoader loader=currentClassLoader();
    if ((loader == null) || (loader instanceof AppletClassLoader))     return (AppletClassLoader)loader;
    Class[] context=getClassContext();
    for (int i=0; i < context.length; i++) {
      loader=context[i].getClassLoader();
      if (loader instanceof AppletClassLoader)       return (AppletClassLoader)loader;
    }
    for (int i=0; i < context.length; i++) {
      final ClassLoader currentLoader=context[i].getClassLoader();
      if (currentLoader instanceof URLClassLoader) {
        loader=(ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){
          public Object run(){
            AccessControlContext acc=null;
            ProtectionDomain[] pds=null;
            try {
              acc=(AccessControlContext)facc.get(currentLoader);
              if (acc == null) {
                return null;
              }
              pds=(ProtectionDomain[])fcontext.get(acc);
              if (pds == null) {
                return null;
              }
            }
 catch (            Exception e) {
              throw new UnsupportedOperationException(e);
            }
            for (int i=0; i < pds.length; i++) {
              ClassLoader cl=pds[i].getClassLoader();
              if (cl instanceof AppletClassLoader) {
                return cl;
              }
            }
            return null;
          }
        }
);
        if (loader != null) {
          return (AppletClassLoader)loader;
        }
      }
    }
    loader=Thread.currentThread().getContextClassLoader();
    if (loader instanceof AppletClassLoader)     return (AppletClassLoader)loader;
    return (AppletClassLoader)null;
  }
  /** 
 * Returns true if this threadgroup is in the applet's own thread
 * group. This will return false if there is no current class
 * loader.
 */
  protected boolean inThreadGroup(  ThreadGroup g){
    if (currentAppletClassLoader() == null)     return false;
 else     return getThreadGroup().parentOf(g);
  }
  /** 
 * Returns true of the threadgroup of thread is in the applet's
 * own threadgroup.
 */
  protected boolean inThreadGroup(  Thread thread){
    return inThreadGroup(thread.getThreadGroup());
  }
  /** 
 * Applets are not allowed to manipulate threads outside
 * applet thread groups. However a terminated thread no longer belongs
 * to any group.
 */
  public void checkAccess(  Thread t){
    if ((t.getState() != Thread.State.TERMINATED) && !inThreadGroup(t)) {
      checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
    }
  }
  private boolean inThreadGroupCheck=false;
  /** 
 * Applets are not allowed to manipulate thread groups outside
 * applet thread groups.
 */
  public synchronized void checkAccess(  ThreadGroup g){
    if (inThreadGroupCheck) {
      checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
    }
 else {
      try {
        inThreadGroupCheck=true;
        if (!inThreadGroup(g)) {
          checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
        }
      }
  finally {
        inThreadGroupCheck=false;
      }
    }
  }
  /** 
 * Throws a <code>SecurityException</code> if the
 * calling thread is not allowed to access the package specified by
 * the argument.
 * <p>
 * This method is used by the <code>loadClass</code> method of class
 * loaders.
 * <p>
 * The <code>checkPackageAccess</code> method for class
 * <code>SecurityManager</code>  calls
 * <code>checkPermission</code> with the
 * <code>RuntimePermission("accessClassInPackage."+pkg)</code>
 * permission.
 * @param pkg   the package name.
 * @exception SecurityException  if the caller does not have
 * permission to access the specified package.
 * @see java.lang.ClassLoader#loadClass(java.lang.String,boolean)
 */
  public void checkPackageAccess(  final String pkgname){
    super.checkPackageAccess(pkgname);
    for (Iterator iter=restrictedPackages.iterator(); iter.hasNext(); ) {
      String pkg=(String)iter.next();
      if (pkgname.equals(pkg) || pkgname.startsWith(pkg + ".")) {
        checkPermission(new java.lang.RuntimePermission("accessClassInPackage." + pkgname));
      }
    }
  }
  /** 
 * Tests if a client can get access to the AWT event queue.
 * <p>
 * This method calls <code>checkPermission</code> with the
 * <code>AWTPermission("accessEventQueue")</code> permission.
 * @since   JDK1.1
 * @exception SecurityException  if the caller does not have
 * permission to accesss the AWT event queue.
 */
  public void checkAwtEventQueueAccess(){
    AppContext appContext=AppContext.getAppContext();
    AppletClassLoader appletClassLoader=currentAppletClassLoader();
    if ((appContext == mainAppContext) && (appletClassLoader != null)) {
      super.checkAwtEventQueueAccess();
    }
  }
  /** 
 * Returns the thread group of the applet. We consult the classloader
 * if there is one.
 */
  public ThreadGroup getThreadGroup(){
    AppletClassLoader appletLoader=currentAppletClassLoader();
    ThreadGroup loaderGroup=(appletLoader == null) ? null : appletLoader.getThreadGroup();
    if (loaderGroup != null) {
      return loaderGroup;
    }
 else {
      return super.getThreadGroup();
    }
  }
  /** 
 * Get the AppContext corresponding to the current context.
 * The default implementation returns null, but this method
 * may be overridden by various SecurityManagers
 * (e.g. AppletSecurity) to index AppContext objects by the
 * calling context.
 * @return  the AppContext corresponding to the current context.
 * @see sun.awt.AppContext
 * @see java.lang.SecurityManager
 * @since   JDK1.2.1
 */
  public AppContext getAppContext(){
    AppletClassLoader appletLoader=currentAppletClassLoader();
    if (appletLoader == null) {
      return null;
    }
 else {
      AppContext context=appletLoader.getAppContext();
      if (context == null) {
        throw new SecurityException("Applet classloader has invalid AppContext");
      }
      return context;
    }
  }
}
