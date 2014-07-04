package com.sun.jmx.mbeanserver;
import static com.sun.jmx.defaults.JmxProperties.MBEANSERVER_LOGGER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.management.ObjectName;
import javax.management.loading.PrivateClassLoader;
/** 
 * This class keeps the list of Class Loaders registered in the MBean Server.
 * It provides the necessary methods to load classes using the
 * registered Class Loaders.
 * @since 1.5
 */
final class ClassLoaderRepositorySupport implements ModifiableClassLoaderRepository {
private static class LoaderEntry {
    ObjectName name;
    ClassLoader loader;
    LoaderEntry(    ObjectName name,    ClassLoader loader){
      this.name=name;
      this.loader=loader;
    }
  }
  private static final LoaderEntry[] EMPTY_LOADER_ARRAY=new LoaderEntry[0];
  /** 
 * List of class loaders
 * Only read-only actions should be performed on this object.
 * We do O(n) operations on this array, e.g. when removing
 * a ClassLoader.  The assumption is that the number of elements
 * is small, probably less than ten, and that the vast majority
 * of operations are searches (loadClass) which are by definition
 * linear.
 */
  private LoaderEntry[] loaders=EMPTY_LOADER_ARRAY;
  /** 
 * Same behavior as add(Object o) in {@link java.util.List}.
 * Replace the loader list with a new one in which the new
 * loader has been added.
 */
  private synchronized boolean add(  ObjectName name,  ClassLoader cl){
    List<LoaderEntry> l=new ArrayList<LoaderEntry>(Arrays.asList(loaders));
    l.add(new LoaderEntry(name,cl));
    loaders=l.toArray(EMPTY_LOADER_ARRAY);
    return true;
  }
  /** 
 * Same behavior as remove(Object o) in {@link java.util.List}.
 * Replace the loader list with a new one in which the old loader
 * has been removed.
 * The ObjectName may be null, in which case the entry to
 * be removed must also have a null ObjectName and the ClassLoader
 * values must match.  If the ObjectName is not null, then
 * the first entry with a matching ObjectName is removed,
 * regardless of whether ClassLoader values match.  (In fact,
 * the ClassLoader parameter will usually be null in this case.)
 */
  private synchronized boolean remove(  ObjectName name,  ClassLoader cl){
    final int size=loaders.length;
    for (int i=0; i < size; i++) {
      LoaderEntry entry=loaders[i];
      boolean match=(name == null) ? cl == entry.loader : name.equals(entry.name);
      if (match) {
        LoaderEntry[] newloaders=new LoaderEntry[size - 1];
        System.arraycopy(loaders,0,newloaders,0,i);
        System.arraycopy(loaders,i + 1,newloaders,i,size - 1 - i);
        loaders=newloaders;
        return true;
      }
    }
    return false;
  }
  /** 
 * List of valid search
 */
  private final Map<String,List<ClassLoader>> search=new Hashtable<String,List<ClassLoader>>(10);
  /** 
 * List of named class loaders.
 */
  private final Map<ObjectName,ClassLoader> loadersWithNames=new Hashtable<ObjectName,ClassLoader>(10);
  public final Class<?> loadClass(  String className) throws ClassNotFoundException {
    return loadClass(loaders,className,null,null);
  }
  public final Class<?> loadClassWithout(  ClassLoader without,  String className) throws ClassNotFoundException {
    if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      MBEANSERVER_LOGGER.logp(Level.FINER,ClassLoaderRepositorySupport.class.getName(),"loadClassWithout",className + " without " + without);
    }
    if (without == null)     return loadClass(loaders,className,null,null);
    startValidSearch(without,className);
    try {
      return loadClass(loaders,className,without,null);
    }
  finally {
      stopValidSearch(without,className);
    }
  }
  public final Class<?> loadClassBefore(  ClassLoader stop,  String className) throws ClassNotFoundException {
    if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      MBEANSERVER_LOGGER.logp(Level.FINER,ClassLoaderRepositorySupport.class.getName(),"loadClassBefore",className + " before " + stop);
    }
    if (stop == null)     return loadClass(loaders,className,null,null);
    startValidSearch(stop,className);
    try {
      return loadClass(loaders,className,null,stop);
    }
  finally {
      stopValidSearch(stop,className);
    }
  }
  private Class<?> loadClass(  final LoaderEntry list[],  final String className,  final ClassLoader without,  final ClassLoader stop) throws ClassNotFoundException {
    final int size=list.length;
    for (int i=0; i < size; i++) {
      try {
        final ClassLoader cl=list[i].loader;
        if (cl == null)         return Class.forName(className,false,null);
        if (cl == without)         continue;
        if (cl == stop)         break;
        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
          MBEANSERVER_LOGGER.logp(Level.FINER,ClassLoaderRepositorySupport.class.getName(),"loadClass","Trying loader = " + cl);
        }
        return Class.forName(className,false,cl);
      }
 catch (      ClassNotFoundException e) {
      }
    }
    throw new ClassNotFoundException(className);
  }
  private synchronized void startValidSearch(  ClassLoader aloader,  String className) throws ClassNotFoundException {
    List<ClassLoader> excluded=search.get(className);
    if ((excluded != null) && (excluded.contains(aloader))) {
      if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        MBEANSERVER_LOGGER.logp(Level.FINER,ClassLoaderRepositorySupport.class.getName(),"startValidSearch","Already requested loader = " + aloader + " class = "+ className);
      }
      throw new ClassNotFoundException(className);
    }
    if (excluded == null) {
      excluded=new ArrayList<ClassLoader>(1);
      search.put(className,excluded);
    }
    excluded.add(aloader);
    if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      MBEANSERVER_LOGGER.logp(Level.FINER,ClassLoaderRepositorySupport.class.getName(),"startValidSearch","loader = " + aloader + " class = "+ className);
    }
  }
  private synchronized void stopValidSearch(  ClassLoader aloader,  String className){
    List<ClassLoader> excluded=search.get(className);
    if (excluded != null) {
      excluded.remove(aloader);
      if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        MBEANSERVER_LOGGER.logp(Level.FINER,ClassLoaderRepositorySupport.class.getName(),"stopValidSearch","loader = " + aloader + " class = "+ className);
      }
    }
  }
  public final void addClassLoader(  ClassLoader loader){
    add(null,loader);
  }
  public final void removeClassLoader(  ClassLoader loader){
    remove(null,loader);
  }
  public final synchronized void addClassLoader(  ObjectName name,  ClassLoader loader){
    loadersWithNames.put(name,loader);
    if (!(loader instanceof PrivateClassLoader))     add(name,loader);
  }
  public final synchronized void removeClassLoader(  ObjectName name){
    ClassLoader loader=loadersWithNames.remove(name);
    if (!(loader instanceof PrivateClassLoader))     remove(name,loader);
  }
  public final ClassLoader getClassLoader(  ObjectName name){
    return loadersWithNames.get(name);
  }
}
