package com.sun.jmx.mbeanserver;
import static com.sun.jmx.mbeanserver.Util.*;
import java.util.Iterator;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
/** 
 * Base class for MXBeans.
 * @since 1.6
 */
public class MXBeanSupport extends MBeanSupport<ConvertingMethod> {
  /** 
 * <p>Construct an MXBean that wraps the given resource using the
 * given MXBean interface.</p>
 * @param resource the underlying resource for the new MXBean.
 * @param mxbeanInterface the interface to be used to determine
 * the MXBean's management interface.
 * @param<T>
 *  a type parameter that allows the compiler to check
 * that {@code resource} implements {@code mxbeanInterface},
 * provided that {@code mxbeanInterface} is a class constant like{@code SomeMXBean.class}.
 * @throws IllegalArgumentException if {@code resource} is null or
 * if it does not implement the class {@code mxbeanInterface} or if
 * that class is not a valid MXBean interface.
 */
  public <T>MXBeanSupport(  T resource,  Class<T> mxbeanInterface) throws NotCompliantMBeanException {
    super(resource,mxbeanInterface);
  }
  @Override MBeanIntrospector<ConvertingMethod> getMBeanIntrospector(){
    return MXBeanIntrospector.getInstance();
  }
  @Override Object getCookie(){
    return mxbeanLookup;
  }
  static <T>Class<? super T> findMXBeanInterface(  Class<T> resourceClass){
    if (resourceClass == null)     throw new IllegalArgumentException("Null resource class");
    final Set<Class<?>> intfs=transitiveInterfaces(resourceClass);
    final Set<Class<?>> candidates=newSet();
    for (    Class<?> intf : intfs) {
      if (JMX.isMXBeanInterface(intf))       candidates.add(intf);
    }
    reduce:     while (candidates.size() > 1) {
      for (      Class<?> intf : candidates) {
        for (Iterator<Class<?>> it=candidates.iterator(); it.hasNext(); ) {
          final Class<?> intf2=it.next();
          if (intf != intf2 && intf2.isAssignableFrom(intf)) {
            it.remove();
            continue reduce;
          }
        }
      }
      final String msg="Class " + resourceClass.getName() + " implements more than "+ "one MXBean interface: "+ candidates;
      throw new IllegalArgumentException(msg);
    }
    if (candidates.iterator().hasNext()) {
      return Util.cast(candidates.iterator().next());
    }
 else {
      final String msg="Class " + resourceClass.getName() + " is not a JMX compliant MXBean";
      throw new IllegalArgumentException(msg);
    }
  }
  private static Set<Class<?>> transitiveInterfaces(  Class<?> c){
    Set<Class<?>> set=newSet();
    transitiveInterfaces(c,set);
    return set;
  }
  private static void transitiveInterfaces(  Class<?> c,  Set<Class<?>> intfs){
    if (c == null)     return;
    if (c.isInterface())     intfs.add(c);
    transitiveInterfaces(c.getSuperclass(),intfs);
    for (    Class<?> sup : c.getInterfaces())     transitiveInterfaces(sup,intfs);
  }
  @Override public void register(  MBeanServer server,  ObjectName name) throws InstanceAlreadyExistsException {
    if (name == null)     throw new IllegalArgumentException("Null object name");
synchronized (lock) {
      this.mxbeanLookup=MXBeanLookup.lookupFor(server);
      this.mxbeanLookup.addReference(name,getResource());
      this.objectName=name;
    }
  }
  @Override public void unregister(){
synchronized (lock) {
      if (mxbeanLookup != null) {
        if (mxbeanLookup.removeReference(objectName,getResource()))         objectName=null;
      }
    }
  }
  private final Object lock=new Object();
  private MXBeanLookup mxbeanLookup;
  private ObjectName objectName;
}
