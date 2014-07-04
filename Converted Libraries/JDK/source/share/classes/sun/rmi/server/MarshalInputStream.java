package sun.rmi.server;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.*;
import java.security.AccessControlException;
import java.security.Permission;
import java.rmi.server.RMIClassLoader;
/** 
 * MarshalInputStream is an extension of ObjectInputStream.  When resolving
 * a class, it reads an object from the stream written by a corresponding
 * MarshalOutputStream.  If the class to be resolved is not available
 * locally, from the first class loader on the execution stack, or from the
 * context class loader of the current thread, it will attempt to load the
 * class from the location annotated by the sending MarshalOutputStream.
 * This location object must be a string representing a path of URLs.
 * A new MarshalInputStream should be created to deserialize remote objects or
 * graphs containing remote objects.  Objects are created from the stream
 * using the ObjectInputStream.readObject method.
 * @author      Peter Jones
 */
public class MarshalInputStream extends ObjectInputStream {
  /** 
 * value of "java.rmi.server.useCodebaseOnly" property,
 * as cached at class initialization time.
 */
  private static final boolean useCodebaseOnlyProperty=java.security.AccessController.doPrivileged(new sun.security.action.GetBooleanAction("java.rmi.server.useCodebaseOnly")).booleanValue();
  /** 
 * table to hold sun classes to which access is explicitly permitted 
 */
  protected static Map<String,Class<?>> permittedSunClasses=new HashMap<String,Class<?>>(3);
  /** 
 * if true, don't try superclass first in resolveClass() 
 */
  private boolean skipDefaultResolveClass=false;
  /** 
 * callbacks to make when done() called: maps Object to Runnable 
 */
  private final Map<Object,Runnable> doneCallbacks=new HashMap<Object,Runnable>(3);
  /** 
 * if true, load classes (if not available locally) only from the
 * URL specified by the "java.rmi.server.codebase" property.
 */
  private boolean useCodebaseOnly=useCodebaseOnlyProperty;
static {
    try {
      String system="sun.rmi.server.Activation$ActivationSystemImpl_Stub";
      String registry="sun.rmi.registry.RegistryImpl_Stub";
      permittedSunClasses.put(system,Class.forName(system));
      permittedSunClasses.put(registry,Class.forName(registry));
    }
 catch (    ClassNotFoundException e) {
      throw new NoClassDefFoundError("Missing system class: " + e.getMessage());
    }
  }
  /** 
 * Load the "rmi" native library.
 */
static {
    java.security.AccessController.doPrivileged(new sun.security.action.LoadLibraryAction("rmi"));
  }
  /** 
 * Create a new MarshalInputStream object.
 */
  public MarshalInputStream(  InputStream in) throws IOException, StreamCorruptedException {
    super(in);
  }
  /** 
 * Returns a callback previously registered via the setDoneCallback
 * method with given key, or null if no callback has yet been registered
 * with that key.
 */
  public Runnable getDoneCallback(  Object key){
    return doneCallbacks.get(key);
  }
  /** 
 * Registers a callback to make when this stream's done() method is
 * invoked, along with a key for retrieving the same callback instance
 * subsequently from the getDoneCallback method.
 */
  public void setDoneCallback(  Object key,  Runnable callback){
    doneCallbacks.put(key,callback);
  }
  /** 
 * Indicates that the user of this MarshalInputStream is done reading
 * objects from it, so all callbacks registered with the setDoneCallback
 * method should now be (synchronously) executed.  When this method
 * returns, there are no more callbacks registered.
 * This method is implicitly invoked by close() before it delegates to
 * the superclass's close method.
 */
  public void done(){
    Iterator<Runnable> iter=doneCallbacks.values().iterator();
    while (iter.hasNext()) {
      Runnable callback=iter.next();
      callback.run();
    }
    doneCallbacks.clear();
  }
  /** 
 * Closes this stream, implicitly invoking done() first.
 */
  public void close() throws IOException {
    done();
    super.close();
  }
  /** 
 * resolveClass is extended to acquire (if present) the location
 * from which to load the specified class.
 * It will find, load, and return the class.
 */
  protected Class resolveClass(  ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
    Object annotation=readLocation();
    String className=classDesc.getName();
    ClassLoader defaultLoader=skipDefaultResolveClass ? null : latestUserDefinedLoader();
    String codebase=null;
    if (!useCodebaseOnly && annotation instanceof String) {
      codebase=(String)annotation;
    }
    try {
      return RMIClassLoader.loadClass(codebase,className,defaultLoader);
    }
 catch (    AccessControlException e) {
      return checkSunClass(className,e);
    }
catch (    ClassNotFoundException e) {
      try {
        if (Character.isLowerCase(className.charAt(0)) && className.indexOf('.') == -1) {
          return super.resolveClass(classDesc);
        }
      }
 catch (      ClassNotFoundException e2) {
      }
      throw e;
    }
  }
  /** 
 * resolveProxyClass is extended to acquire (if present) the location
 * to determine the class loader to define the proxy class in.
 */
  protected Class resolveProxyClass(  String[] interfaces) throws IOException, ClassNotFoundException {
    Object annotation=readLocation();
    ClassLoader defaultLoader=skipDefaultResolveClass ? null : latestUserDefinedLoader();
    String codebase=null;
    if (!useCodebaseOnly && annotation instanceof String) {
      codebase=(String)annotation;
    }
    return RMIClassLoader.loadProxyClass(codebase,interfaces,defaultLoader);
  }
  private static native ClassLoader latestUserDefinedLoader();
  /** 
 * Fix for 4179055: Need to assist resolving sun stubs; resolve
 * class locally if it is a "permitted" sun class
 */
  private Class checkSunClass(  String className,  AccessControlException e) throws AccessControlException {
    Permission perm=e.getPermission();
    String name=null;
    if (perm != null) {
      name=perm.getName();
    }
    Class<?> resolvedClass=permittedSunClasses.get(className);
    if ((name == null) || (resolvedClass == null) || ((!name.equals("accessClassInPackage.sun.rmi.server")) && (!name.equals("accessClassInPackage.sun.rmi.registry")))) {
      throw e;
    }
    return resolvedClass;
  }
  /** 
 * Return the location for the class in the stream.  This method can
 * be overridden by subclasses that store this annotation somewhere
 * else than as the next object in the stream, as is done by this class.
 */
  protected Object readLocation() throws IOException, ClassNotFoundException {
    return readObject();
  }
  /** 
 * Set a flag to indicate that the superclass's default resolveClass()
 * implementation should not be invoked by our resolveClass().
 */
  void skipDefaultResolveClass(){
    skipDefaultResolveClass=true;
  }
  /** 
 * Disable code downloading except from the URL specified by the
 * "java.rmi.server.codebase" property.
 */
  void useCodebaseOnly(){
    useCodebaseOnly=true;
  }
}
