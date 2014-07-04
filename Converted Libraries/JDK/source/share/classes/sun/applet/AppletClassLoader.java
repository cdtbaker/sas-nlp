package sun.applet;
import java.lang.NullPointerException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.SocketPermission;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.misc.IOUtils;
import sun.net.www.ParseUtil;
import sun.security.util.SecurityConstants;
/** 
 * This class defines the class loader for loading applet classes and
 * resources. It extends URLClassLoader to search the applet code base
 * for the class or resource after checking any loaded JAR files.
 */
public class AppletClassLoader extends URLClassLoader {
  private URL base;
  private CodeSource codesource;
  private AccessControlContext acc;
  private boolean exceptionStatus=false;
  private final Object threadGroupSynchronizer=new Object();
  private final Object grabReleaseSynchronizer=new Object();
  private boolean codebaseLookup=true;
  private volatile boolean allowRecursiveDirectoryRead=true;
  protected AppletClassLoader(  URL base){
    super(new URL[0]);
    this.base=base;
    this.codesource=new CodeSource(base,(java.security.cert.Certificate[])null);
    acc=AccessController.getContext();
  }
  public void disableRecursiveDirectoryRead(){
    allowRecursiveDirectoryRead=false;
  }
  /** 
 * Set the codebase lookup flag.
 */
  void setCodebaseLookup(  boolean codebaseLookup){
    this.codebaseLookup=codebaseLookup;
  }
  URL getBaseURL(){
    return base;
  }
  public URL[] getURLs(){
    URL[] jars=super.getURLs();
    URL[] urls=new URL[jars.length + 1];
    System.arraycopy(jars,0,urls,0,jars.length);
    urls[urls.length - 1]=base;
    return urls;
  }
  protected void addJar(  String name) throws IOException {
    URL url;
    try {
      url=new URL(base,name);
    }
 catch (    MalformedURLException e) {
      throw new IllegalArgumentException("name");
    }
    addURL(url);
  }
  public synchronized Class loadClass(  String name,  boolean resolve) throws ClassNotFoundException {
    int i=name.lastIndexOf('.');
    if (i != -1) {
      SecurityManager sm=System.getSecurityManager();
      if (sm != null)       sm.checkPackageAccess(name.substring(0,i));
    }
    try {
      return super.loadClass(name,resolve);
    }
 catch (    ClassNotFoundException e) {
      throw e;
    }
catch (    RuntimeException e) {
      throw e;
    }
catch (    Error e) {
      throw e;
    }
  }
  protected Class findClass(  String name) throws ClassNotFoundException {
    int index=name.indexOf(";");
    String cookie="";
    if (index != -1) {
      cookie=name.substring(index,name.length());
      name=name.substring(0,index);
    }
    try {
      return super.findClass(name);
    }
 catch (    ClassNotFoundException e) {
    }
    if (codebaseLookup == false)     throw new ClassNotFoundException(name);
    String encodedName=ParseUtil.encodePath(name.replace('.','/'),false);
    final String path=(new StringBuffer(encodedName)).append(".class").append(cookie).toString();
    try {
      byte[] b=(byte[])AccessController.doPrivileged(new PrivilegedExceptionAction(){
        public Object run() throws IOException {
          try {
            URL finalURL=new URL(base,path);
            if (base.getProtocol().equals(finalURL.getProtocol()) && base.getHost().equals(finalURL.getHost()) && base.getPort() == finalURL.getPort()) {
              return getBytes(finalURL);
            }
 else {
              return null;
            }
          }
 catch (          Exception e) {
            return null;
          }
        }
      }
,acc);
      if (b != null) {
        return defineClass(name,b,0,b.length,codesource);
      }
 else {
        throw new ClassNotFoundException(name);
      }
    }
 catch (    PrivilegedActionException e) {
      throw new ClassNotFoundException(name,e.getException());
    }
  }
  /** 
 * Returns the permissions for the given codesource object.
 * The implementation of this method first calls super.getPermissions,
 * to get the permissions
 * granted by the super class, and then adds additional permissions
 * based on the URL of the codesource.
 * <p>
 * If the protocol is "file"
 * and the path specifies a file, permission is granted to read all files
 * and (recursively) all files and subdirectories contained in
 * that directory. This is so applets with a codebase of
 * file:/blah/some.jar can read in file:/blah/, which is needed to
 * be backward compatible. We also add permission to connect back to
 * the "localhost".
 * @param codesource the codesource
 * @return the permissions granted to the codesource
 */
  protected PermissionCollection getPermissions(  CodeSource codesource){
    final PermissionCollection perms=super.getPermissions(codesource);
    URL url=codesource.getLocation();
    String path=null;
    Permission p;
    try {
      p=url.openConnection().getPermission();
    }
 catch (    java.io.IOException ioe) {
      p=null;
    }
    if (p instanceof FilePermission) {
      path=p.getName();
    }
 else     if ((p == null) && (url.getProtocol().equals("file"))) {
      path=url.getFile().replace('/',File.separatorChar);
      path=ParseUtil.decode(path);
    }
    if (path != null) {
      final String rawPath=path;
      if (!path.endsWith(File.separator)) {
        int endIndex=path.lastIndexOf(File.separatorChar);
        if (endIndex != -1) {
          path=path.substring(0,endIndex + 1) + "-";
          perms.add(new FilePermission(path,SecurityConstants.FILE_READ_ACTION));
        }
      }
      final File f=new File(rawPath);
      final boolean isDirectory=f.isDirectory();
      if (allowRecursiveDirectoryRead && (isDirectory || rawPath.toLowerCase().endsWith(".jar") || rawPath.toLowerCase().endsWith(".zip"))) {
        Permission bperm;
        try {
          bperm=base.openConnection().getPermission();
        }
 catch (        java.io.IOException ioe) {
          bperm=null;
        }
        if (bperm instanceof FilePermission) {
          String bpath=bperm.getName();
          if (bpath.endsWith(File.separator)) {
            bpath+="-";
          }
          perms.add(new FilePermission(bpath,SecurityConstants.FILE_READ_ACTION));
        }
 else         if ((bperm == null) && (base.getProtocol().equals("file"))) {
          String bpath=base.getFile().replace('/',File.separatorChar);
          bpath=ParseUtil.decode(bpath);
          if (bpath.endsWith(File.separator)) {
            bpath+="-";
          }
          perms.add(new FilePermission(bpath,SecurityConstants.FILE_READ_ACTION));
        }
      }
    }
    return perms;
  }
  private static byte[] getBytes(  URL url) throws IOException {
    URLConnection uc=url.openConnection();
    if (uc instanceof java.net.HttpURLConnection) {
      java.net.HttpURLConnection huc=(java.net.HttpURLConnection)uc;
      int code=huc.getResponseCode();
      if (code >= java.net.HttpURLConnection.HTTP_BAD_REQUEST) {
        throw new IOException("open HTTP connection failed.");
      }
    }
    int len=uc.getContentLength();
    InputStream in=new BufferedInputStream(uc.getInputStream());
    byte[] b;
    try {
      b=IOUtils.readFully(in,len,true);
    }
  finally {
      in.close();
    }
    return b;
  }
  private Object syncResourceAsStream=new Object();
  private Object syncResourceAsStreamFromJar=new Object();
  private boolean resourceAsStreamInCall=false;
  private boolean resourceAsStreamFromJarInCall=false;
  /** 
 * Returns an input stream for reading the specified resource.
 * The search order is described in the documentation for {@link #getResource(String)}.<p>
 * @param name the resource name
 * @return an input stream for reading the resource, or <code>null</code>
 * if the resource could not be found
 * @since  JDK1.1
 */
  public InputStream getResourceAsStream(  String name){
    if (name == null) {
      throw new NullPointerException("name");
    }
    try {
      InputStream is=null;
synchronized (syncResourceAsStream) {
        resourceAsStreamInCall=true;
        is=super.getResourceAsStream(name);
        resourceAsStreamInCall=false;
      }
      if (codebaseLookup == true && is == null) {
        URL url=new URL(base,ParseUtil.encodePath(name,false));
        is=url.openStream();
      }
      return is;
    }
 catch (    Exception e) {
      return null;
    }
  }
  /** 
 * Returns an input stream for reading the specified resource from the
 * the loaded jar files.
 * The search order is described in the documentation for {@link #getResource(String)}.<p>
 * @param name the resource name
 * @return an input stream for reading the resource, or <code>null</code>
 * if the resource could not be found
 * @since  JDK1.1
 */
  public InputStream getResourceAsStreamFromJar(  String name){
    if (name == null) {
      throw new NullPointerException("name");
    }
    try {
      InputStream is=null;
synchronized (syncResourceAsStreamFromJar) {
        resourceAsStreamFromJarInCall=true;
        is=super.getResourceAsStream(name);
        resourceAsStreamFromJarInCall=false;
      }
      return is;
    }
 catch (    Exception e) {
      return null;
    }
  }
  public URL findResource(  String name){
    URL url=super.findResource(name);
    if (name.startsWith("META-INF/"))     return url;
    if (codebaseLookup == false)     return url;
    if (url == null) {
      boolean insideGetResourceAsStreamFromJar=false;
synchronized (syncResourceAsStreamFromJar) {
        insideGetResourceAsStreamFromJar=resourceAsStreamFromJarInCall;
      }
      if (insideGetResourceAsStreamFromJar) {
        return null;
      }
      boolean insideGetResourceAsStream=false;
synchronized (syncResourceAsStream) {
        insideGetResourceAsStream=resourceAsStreamInCall;
      }
      if (insideGetResourceAsStream == false) {
        try {
          url=new URL(base,ParseUtil.encodePath(name,false));
          if (!resourceExists(url))           url=null;
        }
 catch (        Exception e) {
          url=null;
        }
      }
    }
    return url;
  }
  private boolean resourceExists(  URL url){
    boolean ok=true;
    try {
      URLConnection conn=url.openConnection();
      if (conn instanceof java.net.HttpURLConnection) {
        java.net.HttpURLConnection hconn=(java.net.HttpURLConnection)conn;
        hconn.setRequestMethod("HEAD");
        int code=hconn.getResponseCode();
        if (code == java.net.HttpURLConnection.HTTP_OK) {
          return true;
        }
        if (code >= java.net.HttpURLConnection.HTTP_BAD_REQUEST) {
          return false;
        }
      }
 else {
        InputStream is=conn.getInputStream();
        is.close();
      }
    }
 catch (    Exception ex) {
      ok=false;
    }
    return ok;
  }
  public Enumeration findResources(  String name) throws IOException {
    final Enumeration e=super.findResources(name);
    if (name.startsWith("META-INF/"))     return e;
    if (codebaseLookup == false)     return e;
    URL u=new URL(base,ParseUtil.encodePath(name,false));
    if (!resourceExists(u)) {
      u=null;
    }
    final URL url=u;
    return new Enumeration(){
      private boolean done;
      public Object nextElement(){
        if (!done) {
          if (e.hasMoreElements()) {
            return e.nextElement();
          }
          done=true;
          if (url != null) {
            return url;
          }
        }
        throw new NoSuchElementException();
      }
      public boolean hasMoreElements(){
        return !done && (e.hasMoreElements() || url != null);
      }
    }
;
  }
  Class loadCode(  String name) throws ClassNotFoundException {
    name=name.replace('/','.');
    name=name.replace(File.separatorChar,'.');
    String cookie=null;
    int index=name.indexOf(";");
    if (index != -1) {
      cookie=name.substring(index,name.length());
      name=name.substring(0,index);
    }
    String fullName=name;
    if (name.endsWith(".class") || name.endsWith(".java")) {
      name=name.substring(0,name.lastIndexOf('.'));
    }
    try {
      if (cookie != null)       name=(new StringBuffer(name)).append(cookie).toString();
      return loadClass(name);
    }
 catch (    ClassNotFoundException e) {
    }
    if (cookie != null)     fullName=(new StringBuffer(fullName)).append(cookie).toString();
    return loadClass(fullName);
  }
  private AppletThreadGroup threadGroup;
  private AppContext appContext;
  public ThreadGroup getThreadGroup(){
synchronized (threadGroupSynchronizer) {
      if (threadGroup == null || threadGroup.isDestroyed()) {
        AccessController.doPrivileged(new PrivilegedAction(){
          public Object run(){
            threadGroup=new AppletThreadGroup(base + "-threadGroup");
            AppContextCreator creatorThread=new AppContextCreator(threadGroup);
            creatorThread.setContextClassLoader(AppletClassLoader.this);
            creatorThread.start();
            try {
synchronized (creatorThread.syncObject) {
                while (!creatorThread.created) {
                  creatorThread.syncObject.wait();
                }
              }
            }
 catch (            InterruptedException e) {
            }
            appContext=creatorThread.appContext;
            return null;
          }
        }
);
      }
      return threadGroup;
    }
  }
  public AppContext getAppContext(){
    return appContext;
  }
  int usageCount=0;
  /** 
 * Grab this AppletClassLoader and its ThreadGroup/AppContext, so they
 * won't be destroyed.
 */
  public void grab(){
synchronized (grabReleaseSynchronizer) {
      usageCount++;
    }
    getThreadGroup();
  }
  protected void setExceptionStatus(){
    exceptionStatus=true;
  }
  public boolean getExceptionStatus(){
    return exceptionStatus;
  }
  /** 
 * Release this AppletClassLoader and its ThreadGroup/AppContext.
 * If nothing else has grabbed this AppletClassLoader, its ThreadGroup
 * and AppContext will be destroyed.
 * Because this method may destroy the AppletClassLoader's ThreadGroup,
 * this method should NOT be called from within the AppletClassLoader's
 * ThreadGroup.
 * Changed modifier to protected in order to be able to overwrite this
 * function in PluginClassLoader.java
 */
  protected void release(){
    AppContext tempAppContext=null;
synchronized (grabReleaseSynchronizer) {
      if (usageCount > 1) {
        --usageCount;
      }
 else {
synchronized (threadGroupSynchronizer) {
          tempAppContext=resetAppContext();
        }
      }
    }
    if (tempAppContext != null) {
      try {
        tempAppContext.dispose();
      }
 catch (      IllegalThreadStateException e) {
      }
    }
  }
  protected AppContext resetAppContext(){
    AppContext tempAppContext=null;
synchronized (threadGroupSynchronizer) {
      tempAppContext=appContext;
      usageCount=0;
      appContext=null;
      threadGroup=null;
    }
    return tempAppContext;
  }
  private HashMap jdk11AppletInfo=new HashMap();
  private HashMap jdk12AppletInfo=new HashMap();
  /** 
 * Set applet target level as JDK 1.1.
 * @param clazz Applet class.
 * @param bool true if JDK is targeted for JDK 1.1;
 * false otherwise.
 */
  void setJDK11Target(  Class clazz,  boolean bool){
    jdk11AppletInfo.put(clazz.toString(),Boolean.valueOf(bool));
  }
  /** 
 * Set applet target level as JDK 1.2.
 * @param clazz Applet class.
 * @param bool true if JDK is targeted for JDK 1.2;
 * false otherwise.
 */
  void setJDK12Target(  Class clazz,  boolean bool){
    jdk12AppletInfo.put(clazz.toString(),Boolean.valueOf(bool));
  }
  /** 
 * Determine if applet is targeted for JDK 1.1.
 * @param applet Applet class.
 * @return TRUE if applet is targeted for JDK 1.1;
 * FALSE if applet is not;
 * null if applet is unknown.
 */
  Boolean isJDK11Target(  Class clazz){
    return (Boolean)jdk11AppletInfo.get(clazz.toString());
  }
  /** 
 * Determine if applet is targeted for JDK 1.2.
 * @param applet Applet class.
 * @return TRUE if applet is targeted for JDK 1.2;
 * FALSE if applet is not;
 * null if applet is unknown.
 */
  Boolean isJDK12Target(  Class clazz){
    return (Boolean)jdk12AppletInfo.get(clazz.toString());
  }
  private static AppletMessageHandler mh=new AppletMessageHandler("appletclassloader");
  private static void printError(  String name,  Throwable e){
    String s=null;
    if (e == null) {
      s=mh.getMessage("filenotfound",name);
    }
 else     if (e instanceof IOException) {
      s=mh.getMessage("fileioexception",name);
    }
 else     if (e instanceof ClassFormatError) {
      s=mh.getMessage("fileformat",name);
    }
 else     if (e instanceof ThreadDeath) {
      s=mh.getMessage("filedeath",name);
    }
 else     if (e instanceof Error) {
      s=mh.getMessage("fileerror",e.toString(),name);
    }
    if (s != null) {
      System.err.println(s);
    }
  }
}
class AppContextCreator extends Thread {
  Object syncObject=new Object();
  AppContext appContext=null;
  volatile boolean created=false;
  AppContextCreator(  ThreadGroup group){
    super(group,"AppContextCreator");
  }
  public void run(){
    appContext=SunToolkit.createNewAppContext();
    created=true;
synchronized (syncObject) {
      syncObject.notifyAll();
    }
  }
}
