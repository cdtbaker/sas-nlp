package java.beans;
import com.sun.beans.finder.ClassFinder;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.beans.beancontext.BeanContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import sun.awt.AppContext;
/** 
 * This class provides some general purpose beans control methods.
 */
public class Beans {
  private static final Object DESIGN_TIME=new Object();
  private static final Object GUI_AVAILABLE=new Object();
  /** 
 * <p>
 * Instantiate a JavaBean.
 * </p>
 * @param cls         the class-loader from which we should create
 * the bean.  If this is null, then the system
 * class-loader is used.
 * @param beanName    the name of the bean within the class-loader.
 * For example "sun.beanbox.foobah"
 * @exception ClassNotFoundException if the class of a serialized
 * object could not be found.
 * @exception IOException if an I/O error occurs.
 */
  public static Object instantiate(  ClassLoader cls,  String beanName) throws IOException, ClassNotFoundException {
    return Beans.instantiate(cls,beanName,null,null);
  }
  /** 
 * <p>
 * Instantiate a JavaBean.
 * </p>
 * @param cls         the class-loader from which we should create
 * the bean.  If this is null, then the system
 * class-loader is used.
 * @param beanName    the name of the bean within the class-loader.
 * For example "sun.beanbox.foobah"
 * @param beanContext The BeanContext in which to nest the new bean
 * @exception ClassNotFoundException if the class of a serialized
 * object could not be found.
 * @exception IOException if an I/O error occurs.
 */
  public static Object instantiate(  ClassLoader cls,  String beanName,  BeanContext beanContext) throws IOException, ClassNotFoundException {
    return Beans.instantiate(cls,beanName,beanContext,null);
  }
  /** 
 * Instantiate a bean.
 * <p>
 * The bean is created based on a name relative to a class-loader.
 * This name should be a dot-separated name such as "a.b.c".
 * <p>
 * In Beans 1.0 the given name can indicate either a serialized object
 * or a class.  Other mechanisms may be added in the future.  In
 * beans 1.0 we first try to treat the beanName as a serialized object
 * name then as a class name.
 * <p>
 * When using the beanName as a serialized object name we convert the
 * given beanName to a resource pathname and add a trailing ".ser" suffix.
 * We then try to load a serialized object from that resource.
 * <p>
 * For example, given a beanName of "x.y", Beans.instantiate would first
 * try to read a serialized object from the resource "x/y.ser" and if
 * that failed it would try to load the class "x.y" and create an
 * instance of that class.
 * <p>
 * If the bean is a subtype of java.applet.Applet, then it is given
 * some special initialization.  First, it is supplied with a default
 * AppletStub and AppletContext.  Second, if it was instantiated from
 * a classname the applet's "init" method is called.  (If the bean was
 * deserialized this step is skipped.)
 * <p>
 * Note that for beans which are applets, it is the caller's responsiblity
 * to call "start" on the applet.  For correct behaviour, this should be done
 * after the applet has been added into a visible AWT container.
 * <p>
 * Note that applets created via beans.instantiate run in a slightly
 * different environment than applets running inside browsers.  In
 * particular, bean applets have no access to "parameters", so they may
 * wish to provide property get/set methods to set parameter values.  We
 * advise bean-applet developers to test their bean-applets against both
 * the JDK appletviewer (for a reference browser environment) and the
 * BDK BeanBox (for a reference bean container).
 * @param cls         the class-loader from which we should create
 * the bean.  If this is null, then the system
 * class-loader is used.
 * @param beanName    the name of the bean within the class-loader.
 * For example "sun.beanbox.foobah"
 * @param beanContext The BeanContext in which to nest the new bean
 * @param initializer The AppletInitializer for the new bean
 * @exception ClassNotFoundException if the class of a serialized
 * object could not be found.
 * @exception IOException if an I/O error occurs.
 */
  public static Object instantiate(  ClassLoader cls,  String beanName,  BeanContext beanContext,  AppletInitializer initializer) throws IOException, ClassNotFoundException {
    InputStream ins;
    ObjectInputStream oins=null;
    Object result=null;
    boolean serialized=false;
    IOException serex=null;
    if (cls == null) {
      try {
        cls=ClassLoader.getSystemClassLoader();
      }
 catch (      SecurityException ex) {
      }
    }
    final String serName=beanName.replace('.','/').concat(".ser");
    final ClassLoader loader=cls;
    ins=(InputStream)AccessController.doPrivileged(new PrivilegedAction(){
      public Object run(){
        if (loader == null)         return ClassLoader.getSystemResourceAsStream(serName);
 else         return loader.getResourceAsStream(serName);
      }
    }
);
    if (ins != null) {
      try {
        if (cls == null) {
          oins=new ObjectInputStream(ins);
        }
 else {
          oins=new ObjectInputStreamWithLoader(ins,cls);
        }
        result=oins.readObject();
        serialized=true;
        oins.close();
      }
 catch (      IOException ex) {
        ins.close();
        serex=ex;
      }
catch (      ClassNotFoundException ex) {
        ins.close();
        throw ex;
      }
    }
    if (result == null) {
      Class cl;
      try {
        cl=ClassFinder.findClass(beanName,cls);
      }
 catch (      ClassNotFoundException ex) {
        if (serex != null) {
          throw serex;
        }
        throw ex;
      }
      try {
        result=cl.newInstance();
      }
 catch (      Exception ex) {
        throw new ClassNotFoundException("" + cl + " : "+ ex,ex);
      }
    }
    if (result != null) {
      AppletStub stub=null;
      if (result instanceof Applet) {
        Applet applet=(Applet)result;
        boolean needDummies=initializer == null;
        if (needDummies) {
          final String resourceName;
          if (serialized) {
            resourceName=beanName.replace('.','/').concat(".ser");
          }
 else {
            resourceName=beanName.replace('.','/').concat(".class");
          }
          URL objectUrl=null;
          URL codeBase=null;
          URL docBase=null;
          final ClassLoader cloader=cls;
          objectUrl=(URL)AccessController.doPrivileged(new PrivilegedAction(){
            public Object run(){
              if (cloader == null)               return ClassLoader.getSystemResource(resourceName);
 else               return cloader.getResource(resourceName);
            }
          }
);
          if (objectUrl != null) {
            String s=objectUrl.toExternalForm();
            if (s.endsWith(resourceName)) {
              int ix=s.length() - resourceName.length();
              codeBase=new URL(s.substring(0,ix));
              docBase=codeBase;
              ix=s.lastIndexOf('/');
              if (ix >= 0) {
                docBase=new URL(s.substring(0,ix + 1));
              }
            }
          }
          BeansAppletContext context=new BeansAppletContext(applet);
          stub=(AppletStub)new BeansAppletStub(applet,context,codeBase,docBase);
          applet.setStub(stub);
        }
 else {
          initializer.initialize(applet,beanContext);
        }
        if (beanContext != null) {
          beanContext.add(result);
        }
        if (!serialized) {
          applet.setSize(100,100);
          applet.init();
        }
        if (needDummies) {
          ((BeansAppletStub)stub).active=true;
        }
 else         initializer.activate(applet);
      }
 else       if (beanContext != null)       beanContext.add(result);
    }
    return result;
  }
  /** 
 * From a given bean, obtain an object representing a specified
 * type view of that source object.
 * <p>
 * The result may be the same object or a different object.  If
 * the requested target view isn't available then the given
 * bean is returned.
 * <p>
 * This method is provided in Beans 1.0 as a hook to allow the
 * addition of more flexible bean behaviour in the future.
 * @param bean        Object from which we want to obtain a view.
 * @param targetType  The type of view we'd like to get.
 */
  public static Object getInstanceOf(  Object bean,  Class<?> targetType){
    return bean;
  }
  /** 
 * Check if a bean can be viewed as a given target type.
 * The result will be true if the Beans.getInstanceof method
 * can be used on the given bean to obtain an object that
 * represents the specified targetType type view.
 * @param bean  Bean from which we want to obtain a view.
 * @param targetType  The type of view we'd like to get.
 * @return "true" if the given bean supports the given targetType.
 */
  public static boolean isInstanceOf(  Object bean,  Class<?> targetType){
    return Introspector.isSubclass(bean.getClass(),targetType);
  }
  /** 
 * Test if we are in design-mode.
 * @return  True if we are running in an application construction
 * environment.
 * @see DesignMode
 */
  public static boolean isDesignTime(){
    Object value=AppContext.getAppContext().get(DESIGN_TIME);
    return (value instanceof Boolean) && (Boolean)value;
  }
  /** 
 * Determines whether beans can assume a GUI is available.
 * @return  True if we are running in an environment where beans
 * can assume that an interactive GUI is available, so they
 * can pop up dialog boxes, etc.  This will normally return
 * true in a windowing environment, and will normally return
 * false in a server environment or if an application is
 * running as part of a batch job.
 * @see Visibility
 */
  public static boolean isGuiAvailable(){
    Object value=AppContext.getAppContext().get(GUI_AVAILABLE);
    return (value instanceof Boolean) ? (Boolean)value : !GraphicsEnvironment.isHeadless();
  }
  /** 
 * Used to indicate whether of not we are running in an application
 * builder environment.
 * <p>Note that this method is security checked
 * and is not available to (for example) untrusted applets.
 * More specifically, if there is a security manager,
 * its <code>checkPropertiesAccess</code>
 * method is called. This could result in a SecurityException.
 * @param isDesignTime  True if we're in an application builder tool.
 * @exception SecurityException  if a security manager exists and its
 * <code>checkPropertiesAccess</code> method doesn't allow setting
 * of system properties.
 * @see SecurityManager#checkPropertiesAccess
 */
  public static void setDesignTime(  boolean isDesignTime) throws SecurityException {
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      sm.checkPropertiesAccess();
    }
    AppContext.getAppContext().put(DESIGN_TIME,Boolean.valueOf(isDesignTime));
  }
  /** 
 * Used to indicate whether of not we are running in an environment
 * where GUI interaction is available.
 * <p>Note that this method is security checked
 * and is not available to (for example) untrusted applets.
 * More specifically, if there is a security manager,
 * its <code>checkPropertiesAccess</code>
 * method is called. This could result in a SecurityException.
 * @param isGuiAvailable  True if GUI interaction is available.
 * @exception SecurityException  if a security manager exists and its
 * <code>checkPropertiesAccess</code> method doesn't allow setting
 * of system properties.
 * @see SecurityManager#checkPropertiesAccess
 */
  public static void setGuiAvailable(  boolean isGuiAvailable) throws SecurityException {
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      sm.checkPropertiesAccess();
    }
    AppContext.getAppContext().put(GUI_AVAILABLE,Boolean.valueOf(isGuiAvailable));
  }
}
/** 
 * This subclass of ObjectInputStream delegates loading of classes to
 * an existing ClassLoader.
 */
class ObjectInputStreamWithLoader extends ObjectInputStream {
  private ClassLoader loader;
  /** 
 * Loader must be non-null;
 */
  public ObjectInputStreamWithLoader(  InputStream in,  ClassLoader loader) throws IOException, StreamCorruptedException {
    super(in);
    if (loader == null) {
      throw new IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader");
    }
    this.loader=loader;
  }
  /** 
 * Use the given ClassLoader rather than using the system class
 */
  protected Class resolveClass(  ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
    String cname=classDesc.getName();
    return ClassFinder.resolveClass(cname,this.loader);
  }
}
/** 
 * Package private support class.  This provides a default AppletContext
 * for beans which are applets.
 */
class BeansAppletContext implements AppletContext {
  Applet target;
  Hashtable imageCache=new Hashtable();
  BeansAppletContext(  Applet target){
    this.target=target;
  }
  public AudioClip getAudioClip(  URL url){
    try {
      return (AudioClip)url.getContent();
    }
 catch (    Exception ex) {
      return null;
    }
  }
  public synchronized Image getImage(  URL url){
    Object o=imageCache.get(url);
    if (o != null) {
      return (Image)o;
    }
    try {
      o=url.getContent();
      if (o == null) {
        return null;
      }
      if (o instanceof Image) {
        imageCache.put(url,o);
        return (Image)o;
      }
      Image img=target.createImage((java.awt.image.ImageProducer)o);
      imageCache.put(url,img);
      return img;
    }
 catch (    Exception ex) {
      return null;
    }
  }
  public Applet getApplet(  String name){
    return null;
  }
  public Enumeration getApplets(){
    Vector applets=new Vector();
    applets.addElement(target);
    return applets.elements();
  }
  public void showDocument(  URL url){
  }
  public void showDocument(  URL url,  String target){
  }
  public void showStatus(  String status){
  }
  public void setStream(  String key,  InputStream stream) throws IOException {
  }
  public InputStream getStream(  String key){
    return null;
  }
  public Iterator getStreamKeys(){
    return null;
  }
}
/** 
 * Package private support class.  This provides an AppletStub
 * for beans which are applets.
 */
class BeansAppletStub implements AppletStub {
  transient boolean active;
  transient Applet target;
  transient AppletContext context;
  transient URL codeBase;
  transient URL docBase;
  BeansAppletStub(  Applet target,  AppletContext context,  URL codeBase,  URL docBase){
    this.target=target;
    this.context=context;
    this.codeBase=codeBase;
    this.docBase=docBase;
  }
  public boolean isActive(){
    return active;
  }
  public URL getDocumentBase(){
    return docBase;
  }
  public URL getCodeBase(){
    return codeBase;
  }
  public String getParameter(  String name){
    return null;
  }
  public AppletContext getAppletContext(){
    return context;
  }
  public void appletResize(  int width,  int height){
  }
}
