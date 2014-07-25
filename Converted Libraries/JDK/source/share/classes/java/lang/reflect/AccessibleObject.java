package java.lang.reflect;
import java.security.AccessController;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import java.lang.annotation.Annotation;
/** 
 * The AccessibleObject class is the base class for Field, Method and
 * Constructor objects.  It provides the ability to flag a reflected
 * object as suppressing default Java language access control checks
 * when it is used.  The access checks--for public, default (package)
 * access, protected, and private members--are performed when Fields,
 * Methods or Constructors are used to set or get fields, to invoke
 * methods, or to create and initialize new instances of classes,
 * respectively.
 * <p>Setting the {@code accessible} flag in a reflected object
 * permits sophisticated applications with sufficient privilege, such
 * as Java Object Serialization or other persistence mechanisms, to
 * manipulate objects in a manner that would normally be prohibited.
 * <p>By default, a reflected object is <em>not</em> accessible.
 * @see Field
 * @see Method
 * @see Constructor
 * @see ReflectPermission
 * @since 1.2
 */
public class AccessibleObject implements AnnotatedElement {
  /** 
 * The Permission object that is used to check whether a client
 * has sufficient privilege to defeat Java language access
 * control checks.
 */
  static final private java.security.Permission ACCESS_PERMISSION=new ReflectPermission("suppressAccessChecks");
  /** 
 * Convenience method to set the {@code accessible} flag for an
 * array of objects with a single security check (for efficiency).
 * <p>First, if there is a security manager, its{@code checkPermission} method is called with a{@code ReflectPermission("suppressAccessChecks")} permission.
 * <p>A {@code SecurityException} is raised if {@code flag} is{@code true} but accessibility of any of the elements of the input{@code array} may not be changed (for example, if the element
 * object is a {@link Constructor} object for the class {@link java.lang.Class}).  In the event of such a SecurityException, the
 * accessibility of objects is set to {@code flag} for array elements
 * upto (and excluding) the element for which the exception occurred; the
 * accessibility of elements beyond (and including) the element for which
 * the exception occurred is unchanged.
 * @param array the array of AccessibleObjects
 * @param flag  the new value for the {@code accessible} flag
 * in each object
 * @throws SecurityException if the request is denied.
 * @see SecurityManager#checkPermission
 * @see java.lang.RuntimePermission
 */
  public static void setAccessible(  AccessibleObject[] array,  boolean flag) throws SecurityException {
    SecurityManager sm=System.getSecurityManager();
    if (sm != null)     sm.checkPermission(ACCESS_PERMISSION);
    for (int i=0; i < array.length; i++) {
      setAccessible0(array[i],flag);
    }
  }
  /** 
 * Set the {@code accessible} flag for this object to
 * the indicated boolean value.  A value of {@code true} indicates that
 * the reflected object should suppress Java language access
 * checking when it is used.  A value of {@code false} indicates
 * that the reflected object should enforce Java language access checks.
 * <p>First, if there is a security manager, its{@code checkPermission} method is called with a{@code ReflectPermission("suppressAccessChecks")} permission.
 * <p>A {@code SecurityException} is raised if {@code flag} is{@code true} but accessibility of this object may not be changed
 * (for example, if this element object is a {@link Constructor} object for
 * the class {@link java.lang.Class}).
 * <p>A {@code SecurityException} is raised if this object is a {@link java.lang.reflect.Constructor} object for the class{@code java.lang.Class}, and {@code flag} is true.
 * @param flag the new value for the {@code accessible} flag
 * @throws SecurityException if the request is denied.
 * @see SecurityManager#checkPermission
 * @see java.lang.RuntimePermission
 */
  public void setAccessible(  boolean flag) throws SecurityException {
    SecurityManager sm=System.getSecurityManager();
    if (sm != null)     sm.checkPermission(ACCESS_PERMISSION);
    setAccessible0(this,flag);
  }
  private static void setAccessible0(  AccessibleObject obj,  boolean flag) throws SecurityException {
    if (obj instanceof Constructor && flag == true) {
      Constructor<?> c=(Constructor<?>)obj;
      if (c.getDeclaringClass() == Class.class) {
        throw new SecurityException("Can not make a java.lang.Class" + " constructor accessible");
      }
    }
    obj.override=flag;
  }
  /** 
 * Get the value of the {@code accessible} flag for this object.
 * @return the value of the object's {@code accessible} flag
 */
  public boolean isAccessible(){
    return override;
  }
  /** 
 * Constructor: only used by the Java Virtual Machine.
 */
  protected AccessibleObject(){
  }
  boolean override;
  static final ReflectionFactory reflectionFactory=AccessController.doPrivileged(new sun.reflect.ReflectionFactory.GetReflectionFactoryAction());
  /** 
 * @throws NullPointerException {@inheritDoc}
 * @since 1.5
 */
  public <T extends Annotation>T getAnnotation(  Class<T> annotationClass){
    throw new AssertionError("All subclasses should override this method");
  }
  /** 
 * @throws NullPointerException {@inheritDoc}
 * @since 1.5
 */
  public boolean isAnnotationPresent(  Class<? extends Annotation> annotationClass){
    return getAnnotation(annotationClass) != null;
  }
  /** 
 * @since 1.5
 */
  public Annotation[] getAnnotations(){
    return getDeclaredAnnotations();
  }
  /** 
 * @since 1.5
 */
  public Annotation[] getDeclaredAnnotations(){
    throw new AssertionError("All subclasses should override this method");
  }
  volatile Object securityCheckCache;
  void checkAccess(  Class<?> caller,  Class<?> clazz,  Object obj,  int modifiers) throws IllegalAccessException {
    if (caller == clazz) {
      return;
    }
    Object cache=securityCheckCache;
    Class<?> targetClass=clazz;
    if (obj != null && Modifier.isProtected(modifiers) && ((targetClass=obj.getClass()) != clazz)) {
      if (cache instanceof Class[]) {
        Class<?>[] cache2=(Class<?>[])cache;
        if (cache2[1] == targetClass && cache2[0] == caller) {
          return;
        }
      }
    }
 else     if (cache == caller) {
      return;
    }
    slowCheckMemberAccess(caller,clazz,obj,modifiers,targetClass);
  }
  void slowCheckMemberAccess(  Class<?> caller,  Class<?> clazz,  Object obj,  int modifiers,  Class<?> targetClass) throws IllegalAccessException {
    Reflection.ensureMemberAccess(caller,clazz,obj,modifiers);
    Object cache=((targetClass == clazz) ? caller : new Class<?>[]{caller,targetClass});
    securityCheckCache=cache;
  }
}
