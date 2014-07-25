package sun.applet;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.*;
import java.util.*;
import java.util.Collections;
import java.util.Locale;
import java.util.WeakHashMap;
import sun.awt.AppContext;
import sun.awt.EmbeddedFrame;
import sun.awt.SunToolkit;
import sun.misc.MessageUtils;
import sun.misc.PerformanceLogger;
import sun.misc.Queue;
import sun.security.util.SecurityConstants;
/** 
 * Applet panel class. The panel manages and manipulates the
 * applet as it is being loaded. It forks a separate thread in a new
 * thread group to call the applet's init(), start(), stop(), and
 * destroy() methods.
 * @author      Arthur van Hoff
 */
public abstract class AppletPanel extends Panel implements AppletStub, Runnable {
  /** 
 * The applet (if loaded).
 */
  Applet applet;
  /** 
 * Applet will allow initialization.  Should be
 * set to false if loading a serialized applet
 * that was pickled in the init=true state.
 */
  protected boolean doInit=true;
  /** 
 * The classloader for the applet.
 */
  protected AppletClassLoader loader;
  public final static int APPLET_DISPOSE=0;
  public final static int APPLET_LOAD=1;
  public final static int APPLET_INIT=2;
  public final static int APPLET_START=3;
  public final static int APPLET_STOP=4;
  public final static int APPLET_DESTROY=5;
  public final static int APPLET_QUIT=6;
  public final static int APPLET_ERROR=7;
  public final static int APPLET_RESIZE=51234;
  public final static int APPLET_LOADING=51235;
  public final static int APPLET_LOADING_COMPLETED=51236;
  /** 
 * The current status. One of:
 * APPLET_DISPOSE,
 * APPLET_LOAD,
 * APPLET_INIT,
 * APPLET_START,
 * APPLET_STOP,
 * APPLET_DESTROY,
 * APPLET_ERROR.
 */
  protected int status;
  /** 
 * The thread for the applet.
 */
  protected Thread handler;
  /** 
 * The initial applet size.
 */
  Dimension defaultAppletSize=new Dimension(10,10);
  /** 
 * The current applet size.
 */
  Dimension currentAppletSize=new Dimension(10,10);
  MessageUtils mu=new MessageUtils();
  /** 
 * The thread to use during applet loading
 */
  Thread loaderThread=null;
  /** 
 * Flag to indicate that a loading has been cancelled
 */
  boolean loadAbortRequest=false;
  abstract protected String getCode();
  abstract protected String getJarFiles();
  abstract protected String getSerializedObject();
  abstract public int getWidth();
  abstract public int getHeight();
  abstract public boolean hasInitialFocus();
  private static int threadGroupNumber=0;
  protected void setupAppletAppContext(){
  }
  synchronized void createAppletThread(){
    String nm="applet-" + getCode();
    loader=getClassLoader(getCodeBase(),getClassLoaderCacheKey());
    loader.grab();
    String param=getParameter("codebase_lookup");
    if (param != null && param.equals("false"))     loader.setCodebaseLookup(false);
 else     loader.setCodebaseLookup(true);
    ThreadGroup appletGroup=loader.getThreadGroup();
    handler=new Thread(appletGroup,this,"thread " + nm);
    AccessController.doPrivileged(new PrivilegedAction(){
      public Object run(){
        handler.setContextClassLoader(loader);
        return null;
      }
    }
);
    handler.start();
  }
  void joinAppletThread() throws InterruptedException {
    if (handler != null) {
      handler.join();
      handler=null;
    }
  }
  void release(){
    if (loader != null) {
      loader.release();
      loader=null;
    }
  }
  /** 
 * Construct an applet viewer and start the applet.
 */
  public void init(){
    try {
      defaultAppletSize.width=getWidth();
      currentAppletSize.width=defaultAppletSize.width;
      defaultAppletSize.height=getHeight();
      currentAppletSize.height=defaultAppletSize.height;
    }
 catch (    NumberFormatException e) {
      status=APPLET_ERROR;
      showAppletStatus("badattribute.exception");
      showAppletLog("badattribute.exception");
      showAppletException(e);
    }
    setLayout(new BorderLayout());
    createAppletThread();
  }
  /** 
 * Minimum size
 */
  public Dimension minimumSize(){
    return new Dimension(defaultAppletSize.width,defaultAppletSize.height);
  }
  /** 
 * Preferred size
 */
  public Dimension preferredSize(){
    return new Dimension(currentAppletSize.width,currentAppletSize.height);
  }
  private AppletListener listeners;
  /** 
 * AppletEvent Queue
 */
  private Queue queue=null;
  synchronized public void addAppletListener(  AppletListener l){
    listeners=AppletEventMulticaster.add(listeners,l);
  }
  synchronized public void removeAppletListener(  AppletListener l){
    listeners=AppletEventMulticaster.remove(listeners,l);
  }
  /** 
 * Dispatch event to the listeners..
 */
  public void dispatchAppletEvent(  int id,  Object argument){
    if (listeners != null) {
      AppletEvent evt=new AppletEvent(this,id,argument);
      listeners.appletStateChanged(evt);
    }
  }
  /** 
 * Send an event. Queue it for execution by the handler thread.
 */
  public void sendEvent(  int id){
synchronized (this) {
      if (queue == null) {
        queue=new Queue();
      }
      Integer eventId=Integer.valueOf(id);
      queue.enqueue(eventId);
      notifyAll();
    }
    if (id == APPLET_QUIT) {
      try {
        joinAppletThread();
      }
 catch (      InterruptedException e) {
      }
      if (loader == null)       loader=getClassLoader(getCodeBase(),getClassLoaderCacheKey());
      release();
    }
  }
  /** 
 * Get an event from the queue.
 */
  synchronized AppletEvent getNextEvent() throws InterruptedException {
    while (queue == null || queue.isEmpty()) {
      wait();
    }
    Integer eventId=(Integer)queue.dequeue();
    return new AppletEvent(this,eventId.intValue(),null);
  }
  boolean emptyEventQueue(){
    if ((queue == null) || (queue.isEmpty()))     return true;
 else     return false;
  }
  /** 
 * This kludge is specific to get over AccessControlException thrown during
 * Applet.stop() or destroy() when static thread is suspended.  Set a flag
 * in AppletClassLoader to indicate that an
 * AccessControlException for RuntimePermission "modifyThread" or
 * "modifyThreadGroup" had occurred.
 */
  private void setExceptionStatus(  AccessControlException e){
    Permission p=e.getPermission();
    if (p instanceof RuntimePermission) {
      if (p.getName().startsWith("modifyThread")) {
        if (loader == null)         loader=getClassLoader(getCodeBase(),getClassLoaderCacheKey());
        loader.setExceptionStatus();
      }
    }
  }
  /** 
 * Execute applet events.
 * Here is the state transition diagram
 * Note: (XXX) is the action
 * APPLET_XXX is the state
 * (applet code loaded) --> APPLET_LOAD -- (applet init called)--> APPLET_INIT -- (
 * applet start called) --> APPLET_START -- (applet stop called) -->APPLET_STOP --(applet
 * destroyed called) --> APPLET_DESTROY -->(applet gets disposed) -->
 * APPLET_DISPOSE -->....
 * In the legacy lifecycle model. The applet gets loaded, inited and started. So it stays
 * in the APPLET_START state unless the applet goes away(refresh page or leave the page).
 * So the applet stop method called and the applet enters APPLET_STOP state. Then if the applet
 * is revisited, it will call applet start method and enter the APPLET_START state and stay there.
 * In the modern lifecycle model. When the applet first time visited, it is same as legacy lifecycle
 * model. However, when the applet page goes away. It calls applet stop method and enters APPLET_STOP
 * state and then applet destroyed method gets called and enters APPLET_DESTROY state.
 * This code is also called by AppletViewer. In AppletViewer "Restart" menu, the applet is jump from
 * APPLET_STOP to APPLET_DESTROY and to APPLET_INIT .
 * Also, the applet can jump from APPLET_INIT state to APPLET_DESTROY (in Netscape/Mozilla case).
 * Same as APPLET_LOAD to
 * APPLET_DISPOSE since all of this are triggered by browser.
 */
  public void run(){
    Thread curThread=Thread.currentThread();
    if (curThread == loaderThread) {
      runLoader();
      return;
    }
    boolean disposed=false;
    while (!disposed && !curThread.isInterrupted()) {
      AppletEvent evt;
      try {
        evt=getNextEvent();
      }
 catch (      InterruptedException e) {
        showAppletStatus("bail");
        return;
      }
      try {
switch (evt.getID()) {
case APPLET_LOAD:
          if (!okToLoad()) {
            break;
          }
        if (loaderThread == null) {
          setLoaderThread(new Thread(this));
          loaderThread.start();
          loaderThread.join();
          setLoaderThread(null);
        }
 else {
        }
      break;
case APPLET_INIT:
    if (status != APPLET_LOAD && status != APPLET_DESTROY) {
      showAppletStatus("notloaded");
      break;
    }
  applet.resize(defaultAppletSize);
if (doInit) {
  if (PerformanceLogger.loggingEnabled()) {
    PerformanceLogger.setTime("Applet Init");
    PerformanceLogger.outputLog();
  }
  applet.init();
}
Font f=getFont();
if (f == null || "dialog".equals(f.getFamily().toLowerCase(Locale.ENGLISH)) && f.getSize() == 12 && f.getStyle() == Font.PLAIN) {
setFont(new Font(Font.DIALOG,Font.PLAIN,12));
}
doInit=true;
try {
final AppletPanel p=this;
EventQueue.invokeAndWait(new Runnable(){
public void run(){
p.validate();
}
}
);
}
 catch (InterruptedException ie) {
}
catch (InvocationTargetException ite) {
}
status=APPLET_INIT;
showAppletStatus("inited");
break;
case APPLET_START:
{
if (status != APPLET_INIT && status != APPLET_STOP) {
showAppletStatus("notinited");
break;
}
applet.resize(currentAppletSize);
applet.start();
try {
final AppletPanel p=this;
final Applet a=applet;
EventQueue.invokeAndWait(new Runnable(){
public void run(){
p.validate();
a.setVisible(true);
if (hasInitialFocus()) setDefaultFocus();
}
}
);
}
 catch (InterruptedException ie) {
}
catch (InvocationTargetException ite) {
}
status=APPLET_START;
showAppletStatus("started");
break;
}
case APPLET_STOP:
if (status != APPLET_START) {
showAppletStatus("notstarted");
break;
}
status=APPLET_STOP;
try {
final Applet a=applet;
EventQueue.invokeAndWait(new Runnable(){
public void run(){
a.setVisible(false);
}
}
);
}
 catch (InterruptedException ie) {
}
catch (InvocationTargetException ite) {
}
try {
applet.stop();
}
 catch (java.security.AccessControlException e) {
setExceptionStatus(e);
throw e;
}
showAppletStatus("stopped");
break;
case APPLET_DESTROY:
if (status != APPLET_STOP && status != APPLET_INIT) {
showAppletStatus("notstopped");
break;
}
status=APPLET_DESTROY;
try {
applet.destroy();
}
 catch (java.security.AccessControlException e) {
setExceptionStatus(e);
throw e;
}
showAppletStatus("destroyed");
break;
case APPLET_DISPOSE:
if (status != APPLET_DESTROY && status != APPLET_LOAD) {
showAppletStatus("notdestroyed");
break;
}
status=APPLET_DISPOSE;
try {
final Applet a=applet;
EventQueue.invokeAndWait(new Runnable(){
public void run(){
remove(a);
}
}
);
}
 catch (InterruptedException ie) {
}
catch (InvocationTargetException ite) {
}
applet=null;
showAppletStatus("disposed");
disposed=true;
break;
case APPLET_QUIT:
return;
}
}
 catch (Exception e) {
status=APPLET_ERROR;
if (e.getMessage() != null) {
showAppletStatus("exception2",e.getClass().getName(),e.getMessage());
}
 else {
showAppletStatus("exception",e.getClass().getName());
}
showAppletException(e);
}
catch (ThreadDeath e) {
showAppletStatus("death");
return;
}
catch (Error e) {
status=APPLET_ERROR;
if (e.getMessage() != null) {
showAppletStatus("error2",e.getClass().getName(),e.getMessage());
}
 else {
showAppletStatus("error",e.getClass().getName());
}
showAppletException(e);
}
clearLoadAbortRequest();
}
}
/** 
 * Gets most recent focus owner component associated with the given window.
 * It does that without calling Window.getMostRecentFocusOwner since it
 * provides its own logic contradicting with setDefautlFocus. Instead, it
 * calls KeyboardFocusManager directly.
 */
private Component getMostRecentFocusOwnerForWindow(Window w){
Method meth=(Method)AccessController.doPrivileged(new PrivilegedAction(){
public Object run(){
Method meth=null;
try {
meth=KeyboardFocusManager.class.getDeclaredMethod("getMostRecentFocusOwner",new Class[]{Window.class});
meth.setAccessible(true);
}
 catch (Exception e) {
e.printStackTrace();
}
return meth;
}
}
);
if (meth != null) {
try {
return (Component)meth.invoke(null,new Object[]{w});
}
 catch (Exception e) {
e.printStackTrace();
}
}
return w.getMostRecentFocusOwner();
}
private void setDefaultFocus(){
Component toFocus=null;
Container parent=getParent();
if (parent != null) {
if (parent instanceof Window) {
toFocus=getMostRecentFocusOwnerForWindow((Window)parent);
if (toFocus == parent || toFocus == null) {
toFocus=parent.getFocusTraversalPolicy().getInitialComponent((Window)parent);
}
}
 else if (parent.isFocusCycleRoot()) {
toFocus=parent.getFocusTraversalPolicy().getDefaultComponent(parent);
}
}
if (toFocus != null) {
if (parent instanceof EmbeddedFrame) {
((EmbeddedFrame)parent).synthesizeWindowActivation(true);
}
toFocus.requestFocusInWindow();
}
}
/** 
 * Load the applet into memory.
 * Runs in a seperate (and interruptible) thread from the rest of the
 * applet event processing so that it can be gracefully interrupted from
 * things like HotJava.
 */
private void runLoader(){
if (status != APPLET_DISPOSE) {
showAppletStatus("notdisposed");
return;
}
dispatchAppletEvent(APPLET_LOADING,null);
status=APPLET_LOAD;
loader=getClassLoader(getCodeBase(),getClassLoaderCacheKey());
String code=getCode();
setupAppletAppContext();
try {
loadJarFiles(loader);
applet=createApplet(loader);
}
 catch (ClassNotFoundException e) {
status=APPLET_ERROR;
showAppletStatus("notfound",code);
showAppletLog("notfound",code);
showAppletException(e);
return;
}
catch (InstantiationException e) {
status=APPLET_ERROR;
showAppletStatus("nocreate",code);
showAppletLog("nocreate",code);
showAppletException(e);
return;
}
catch (IllegalAccessException e) {
status=APPLET_ERROR;
showAppletStatus("noconstruct",code);
showAppletLog("noconstruct",code);
showAppletException(e);
return;
}
catch (Exception e) {
status=APPLET_ERROR;
showAppletStatus("exception",e.getMessage());
showAppletException(e);
return;
}
catch (ThreadDeath e) {
status=APPLET_ERROR;
showAppletStatus("death");
return;
}
catch (Error e) {
status=APPLET_ERROR;
showAppletStatus("error",e.getMessage());
showAppletException(e);
return;
}
 finally {
dispatchAppletEvent(APPLET_LOADING_COMPLETED,null);
}
if (applet != null) {
applet.setStub(this);
applet.hide();
add("Center",applet);
showAppletStatus("loaded");
validate();
}
}
protected Applet createApplet(final AppletClassLoader loader) throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException {
final String serName=getSerializedObject();
String code=getCode();
if (code != null && serName != null) {
System.err.println(amh.getMessage("runloader.err"));
throw new InstantiationException("Either \"code\" or \"object\" should be specified, but not both.");
}
if (code == null && serName == null) {
String msg="nocode";
status=APPLET_ERROR;
showAppletStatus(msg);
showAppletLog(msg);
repaint();
}
if (code != null) {
applet=(Applet)loader.loadCode(code).newInstance();
doInit=true;
}
 else {
InputStream is=(InputStream)java.security.AccessController.doPrivileged(new java.security.PrivilegedAction(){
public Object run(){
return loader.getResourceAsStream(serName);
}
}
);
ObjectInputStream ois=new AppletObjectInputStream(is,loader);
Object serObject=ois.readObject();
applet=(Applet)serObject;
doInit=false;
}
findAppletJDKLevel(applet);
if (Thread.interrupted()) {
try {
status=APPLET_DISPOSE;
applet=null;
showAppletStatus("death");
}
  finally {
Thread.currentThread().interrupt();
}
return null;
}
return applet;
}
protected void loadJarFiles(AppletClassLoader loader) throws IOException, InterruptedException {
String jarFiles=getJarFiles();
if (jarFiles != null) {
StringTokenizer st=new StringTokenizer(jarFiles,",",false);
while (st.hasMoreTokens()) {
String tok=st.nextToken().trim();
try {
loader.addJar(tok);
}
 catch (IllegalArgumentException e) {
continue;
}
}
}
}
/** 
 * Request that the loading of the applet be stopped.
 */
protected synchronized void stopLoading(){
if (loaderThread != null) {
loaderThread.interrupt();
}
 else {
setLoadAbortRequest();
}
}
protected synchronized boolean okToLoad(){
return !loadAbortRequest;
}
protected synchronized void clearLoadAbortRequest(){
loadAbortRequest=false;
}
protected synchronized void setLoadAbortRequest(){
loadAbortRequest=true;
}
private synchronized void setLoaderThread(Thread loaderThread){
this.loaderThread=loaderThread;
}
/** 
 * Return true when the applet has been started.
 */
public boolean isActive(){
return status == APPLET_START;
}
private EventQueue appEvtQ=null;
/** 
 * Is called when the applet wants to be resized.
 */
public void appletResize(int width,int height){
currentAppletSize.width=width;
currentAppletSize.height=height;
final Dimension currentSize=new Dimension(currentAppletSize.width,currentAppletSize.height);
if (loader != null) {
AppContext appCtxt=loader.getAppContext();
if (appCtxt != null) appEvtQ=(java.awt.EventQueue)appCtxt.get(AppContext.EVENT_QUEUE_KEY);
}
final AppletPanel ap=this;
if (appEvtQ != null) {
appEvtQ.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(),new Runnable(){
public void run(){
if (ap != null) {
ap.dispatchAppletEvent(APPLET_RESIZE,currentSize);
}
}
}
));
}
}
public void setBounds(int x,int y,int width,int height){
super.setBounds(x,y,width,height);
currentAppletSize.width=width;
currentAppletSize.height=height;
}
public Applet getApplet(){
return applet;
}
/** 
 * Status line. Called by the AppletPanel to provide
 * feedback on the Applet's state.
 */
protected void showAppletStatus(String status){
getAppletContext().showStatus(amh.getMessage(status));
}
protected void showAppletStatus(String status,Object arg){
getAppletContext().showStatus(amh.getMessage(status,arg));
}
protected void showAppletStatus(String status,Object arg1,Object arg2){
getAppletContext().showStatus(amh.getMessage(status,arg1,arg2));
}
/** 
 * Called by the AppletPanel to print to the log.
 */
protected void showAppletLog(String msg){
System.out.println(amh.getMessage(msg));
}
protected void showAppletLog(String msg,Object arg){
System.out.println(amh.getMessage(msg,arg));
}
/** 
 * Called by the AppletPanel to provide
 * feedback when an exception has happened.
 */
protected void showAppletException(Throwable t){
t.printStackTrace();
repaint();
}
/** 
 * Get caching key for classloader cache
 */
public String getClassLoaderCacheKey(){
return getCodeBase().toString();
}
/** 
 * The class loaders
 */
private static HashMap classloaders=new HashMap();
/** 
 * Flush a class loader.
 */
public static synchronized void flushClassLoader(String key){
classloaders.remove(key);
}
/** 
 * Flush all class loaders.
 */
public static synchronized void flushClassLoaders(){
classloaders=new HashMap();
}
/** 
 * This method actually creates an AppletClassLoader.
 * It can be override by subclasses (such as the Plug-in)
 * to provide different classloaders.
 */
protected AppletClassLoader createClassLoader(final URL codebase){
return new AppletClassLoader(codebase);
}
/** 
 * Get a class loader. Create in a restricted context
 */
synchronized AppletClassLoader getClassLoader(final URL codebase,final String key){
AppletClassLoader c=(AppletClassLoader)classloaders.get(key);
if (c == null) {
AccessControlContext acc=getAccessControlContext(codebase);
c=(AppletClassLoader)AccessController.doPrivileged(new PrivilegedAction(){
public Object run(){
AppletClassLoader ac=createClassLoader(codebase);
synchronized (getClass()) {
AppletClassLoader res=(AppletClassLoader)classloaders.get(key);
if (res == null) {
classloaders.put(key,ac);
return ac;
}
 else {
return res;
}
}
}
}
,acc);
}
return c;
}
/** 
 * get the context for the AppletClassLoader we are creating.
 * the context is granted permission to create the class loader,
 * connnect to the codebase, and whatever else the policy grants
 * to all codebases.
 */
private AccessControlContext getAccessControlContext(final URL codebase){
PermissionCollection perms=(PermissionCollection)AccessController.doPrivileged(new PrivilegedAction(){
public Object run(){
Policy p=java.security.Policy.getPolicy();
if (p != null) {
return p.getPermissions(new CodeSource(null,(java.security.cert.Certificate[])null));
}
 else {
return null;
}
}
}
);
if (perms == null) perms=new Permissions();
perms.add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
Permission p;
java.net.URLConnection urlConnection=null;
try {
urlConnection=codebase.openConnection();
p=urlConnection.getPermission();
}
 catch (java.io.IOException ioe) {
p=null;
}
if (p != null) perms.add(p);
if (p instanceof FilePermission) {
String path=p.getName();
int endIndex=path.lastIndexOf(File.separatorChar);
if (endIndex != -1) {
path=path.substring(0,endIndex + 1);
if (path.endsWith(File.separator)) {
path+="-";
}
perms.add(new FilePermission(path,SecurityConstants.FILE_READ_ACTION));
}
}
 else {
URL locUrl=codebase;
if (urlConnection instanceof JarURLConnection) {
locUrl=((JarURLConnection)urlConnection).getJarFileURL();
}
String host=locUrl.getHost();
if (host != null && (host.length() > 0)) perms.add(new SocketPermission(host,SecurityConstants.SOCKET_CONNECT_ACCEPT_ACTION));
}
ProtectionDomain domain=new ProtectionDomain(new CodeSource(codebase,(java.security.cert.Certificate[])null),perms);
AccessControlContext acc=new AccessControlContext(new ProtectionDomain[]{domain});
return acc;
}
public Thread getAppletHandlerThread(){
return handler;
}
public int getAppletWidth(){
return currentAppletSize.width;
}
public int getAppletHeight(){
return currentAppletSize.height;
}
public static void changeFrameAppContext(Frame frame,AppContext newAppContext){
AppContext oldAppContext=SunToolkit.targetToAppContext(frame);
if (oldAppContext == newAppContext) return;
synchronized (Window.class) {
WeakReference weakRef=null;
{
Vector<WeakReference<Window>> windowList=(Vector<WeakReference<Window>>)oldAppContext.get(Window.class);
if (windowList != null) {
for (WeakReference ref : windowList) {
if (ref.get() == frame) {
weakRef=ref;
break;
}
}
if (weakRef != null) windowList.remove(weakRef);
}
}
SunToolkit.insertTargetMapping(frame,newAppContext);
{
Vector<WeakReference<Window>> windowList=(Vector)newAppContext.get(Window.class);
if (windowList == null) {
windowList=new Vector<WeakReference<Window>>();
newAppContext.put(Window.class,windowList);
}
windowList.add(weakRef);
}
}
}
private boolean jdk11Applet=false;
private boolean jdk12Applet=false;
/** 
 * Determine JDK level of an applet.
 */
private void findAppletJDKLevel(Applet applet){
Class appletClass=applet.getClass();
synchronized (appletClass) {
Boolean jdk11Target=(Boolean)loader.isJDK11Target(appletClass);
Boolean jdk12Target=(Boolean)loader.isJDK12Target(appletClass);
if (jdk11Target != null || jdk12Target != null) {
jdk11Applet=(jdk11Target == null) ? false : jdk11Target.booleanValue();
jdk12Applet=(jdk12Target == null) ? false : jdk12Target.booleanValue();
return;
}
String name=appletClass.getName();
name=name.replace('.','/');
final String resourceName=name + ".class";
InputStream is=null;
byte[] classHeader=new byte[8];
try {
is=(InputStream)java.security.AccessController.doPrivileged(new java.security.PrivilegedAction(){
public Object run(){
return loader.getResourceAsStream(resourceName);
}
}
);
int byteRead=is.read(classHeader,0,8);
is.close();
if (byteRead != 8) return;
}
 catch (IOException e) {
return;
}
int major_version=readShort(classHeader,6);
if (major_version < 46) jdk11Applet=true;
 else if (major_version == 46) jdk12Applet=true;
loader.setJDK11Target(appletClass,jdk11Applet);
loader.setJDK12Target(appletClass,jdk12Applet);
}
}
/** 
 * Return true if applet is targeted to JDK 1.1.
 */
protected boolean isJDK11Applet(){
return jdk11Applet;
}
/** 
 * Return true if applet is targeted to JDK1.2.
 */
protected boolean isJDK12Applet(){
return jdk12Applet;
}
/** 
 * Read short from byte array.
 */
private int readShort(byte[] b,int off){
int hi=readByte(b[off]);
int lo=readByte(b[off + 1]);
return (hi << 8) | lo;
}
private int readByte(byte b){
return ((int)b) & 0xFF;
}
private static AppletMessageHandler amh=new AppletMessageHandler("appletpanel");
}
