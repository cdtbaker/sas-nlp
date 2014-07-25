package javax.management.openmbean;
import java.util.Arrays;
import java.util.HashSet;
import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
/** 
 * The {@code OpenMBeanInfoSupport} class describes the management
 * information of an <i>open MBean</i>: it is a subclass of {@link javax.management.MBeanInfo}, and it implements the {@link OpenMBeanInfo} interface.  Note that an <i>open MBean</i> is
 * recognized as such if its {@code getMBeanInfo()} method returns an
 * instance of a class which implements the OpenMBeanInfo interface,
 * typically {@code OpenMBeanInfoSupport}.
 * @since 1.5
 */
public class OpenMBeanInfoSupport extends MBeanInfo implements OpenMBeanInfo {
  static final long serialVersionUID=4349395935420511492L;
  private transient Integer myHashCode=null;
  private transient String myToString=null;
  /** 
 * <p>Constructs an {@code OpenMBeanInfoSupport} instance, which
 * describes a class of open MBeans with the specified {@codeclassName}, {@code description}, {@code openAttributes}, {@codeopenConstructors} , {@code openOperations} and {@codenotifications}.</p>
 * <p>The {@code openAttributes}, {@code openConstructors},{@code openOperations} and {@code notifications}array parameters are internally copied, so that subsequent changes
 * to the arrays referenced by these parameters have no effect on this
 * instance.</p>
 * @param className The fully qualified Java class name of the
 * open MBean described by this <CODE>OpenMBeanInfoSupport</CODE>
 * instance.
 * @param description A human readable description of the open
 * MBean described by this <CODE>OpenMBeanInfoSupport</CODE>
 * instance.
 * @param openAttributes The list of exposed attributes of the
 * described open MBean; Must be an array of instances of a
 * subclass of {@code MBeanAttributeInfo}, typically {@codeOpenMBeanAttributeInfoSupport}.
 * @param openConstructors The list of exposed public constructors
 * of the described open MBean; Must be an array of instances of a
 * subclass of {@code MBeanConstructorInfo}, typically {@codeOpenMBeanConstructorInfoSupport}.
 * @param openOperations The list of exposed operations of the
 * described open MBean.  Must be an array of instances of a
 * subclass of {@code MBeanOperationInfo}, typically {@codeOpenMBeanOperationInfoSupport}.
 * @param notifications The list of notifications emitted by the
 * described open MBean.
 * @throws ArrayStoreException If {@code openAttributes}, {@codeopenConstructors} or {@code openOperations} is not an array of
 * instances of a subclass of {@code MBeanAttributeInfo}, {@codeMBeanConstructorInfo} or {@code MBeanOperationInfo}respectively.
 */
  public OpenMBeanInfoSupport(  String className,  String description,  OpenMBeanAttributeInfo[] openAttributes,  OpenMBeanConstructorInfo[] openConstructors,  OpenMBeanOperationInfo[] openOperations,  MBeanNotificationInfo[] notifications){
    this(className,description,openAttributes,openConstructors,openOperations,notifications,(Descriptor)null);
  }
  /** 
 * <p>Constructs an {@code OpenMBeanInfoSupport} instance, which
 * describes a class of open MBeans with the specified {@codeclassName}, {@code description}, {@code openAttributes}, {@codeopenConstructors} , {@code openOperations}, {@codenotifications}, and {@code descriptor}.</p>
 * <p>The {@code openAttributes}, {@code openConstructors}, {@codeopenOperations} and {@code notifications} array parameters are
 * internally copied, so that subsequent changes to the arrays
 * referenced by these parameters have no effect on this
 * instance.</p>
 * @param className The fully qualified Java class name of the
 * open MBean described by this <CODE>OpenMBeanInfoSupport</CODE>
 * instance.
 * @param description A human readable description of the open
 * MBean described by this <CODE>OpenMBeanInfoSupport</CODE>
 * instance.
 * @param openAttributes The list of exposed attributes of the
 * described open MBean; Must be an array of instances of a
 * subclass of {@code MBeanAttributeInfo}, typically {@codeOpenMBeanAttributeInfoSupport}.
 * @param openConstructors The list of exposed public constructors
 * of the described open MBean; Must be an array of instances of a
 * subclass of {@code MBeanConstructorInfo}, typically {@codeOpenMBeanConstructorInfoSupport}.
 * @param openOperations The list of exposed operations of the
 * described open MBean.  Must be an array of instances of a
 * subclass of {@code MBeanOperationInfo}, typically {@codeOpenMBeanOperationInfoSupport}.
 * @param notifications The list of notifications emitted by the
 * described open MBean.
 * @param descriptor The descriptor for the MBean.  This may be null
 * which is equivalent to an empty descriptor.
 * @throws ArrayStoreException If {@code openAttributes}, {@codeopenConstructors} or {@code openOperations} is not an array of
 * instances of a subclass of {@code MBeanAttributeInfo}, {@codeMBeanConstructorInfo} or {@code MBeanOperationInfo}respectively.
 * @since 1.6
 */
  public OpenMBeanInfoSupport(  String className,  String description,  OpenMBeanAttributeInfo[] openAttributes,  OpenMBeanConstructorInfo[] openConstructors,  OpenMBeanOperationInfo[] openOperations,  MBeanNotificationInfo[] notifications,  Descriptor descriptor){
    super(className,description,attributeArray(openAttributes),constructorArray(openConstructors),operationArray(openOperations),(notifications == null) ? null : notifications.clone(),descriptor);
  }
  private static MBeanAttributeInfo[] attributeArray(  OpenMBeanAttributeInfo[] src){
    if (src == null)     return null;
    MBeanAttributeInfo[] dst=new MBeanAttributeInfo[src.length];
    System.arraycopy(src,0,dst,0,src.length);
    return dst;
  }
  private static MBeanConstructorInfo[] constructorArray(  OpenMBeanConstructorInfo[] src){
    if (src == null)     return null;
    MBeanConstructorInfo[] dst=new MBeanConstructorInfo[src.length];
    System.arraycopy(src,0,dst,0,src.length);
    return dst;
  }
  private static MBeanOperationInfo[] operationArray(  OpenMBeanOperationInfo[] src){
    if (src == null)     return null;
    MBeanOperationInfo[] dst=new MBeanOperationInfo[src.length];
    System.arraycopy(src,0,dst,0,src.length);
    return dst;
  }
  /** 
 * <p>Compares the specified {@code obj} parameter with this{@code OpenMBeanInfoSupport} instance for equality.</p>
 * <p>Returns {@code true} if and only if all of the following
 * statements are true:
 * <ul>
 * <li>{@code obj} is non null,</li>
 * <li>{@code obj} also implements the {@code OpenMBeanInfo}interface,</li>
 * <li>their class names are equal</li>
 * <li>their infos on attributes, constructors, operations and
 * notifications are equal</li>
 * </ul>
 * This ensures that this {@code equals} method works properly for{@code obj} parameters which are different implementations of
 * the {@code OpenMBeanInfo} interface.
 * @param obj the object to be compared for equality with this{@code OpenMBeanInfoSupport} instance;
 * @return {@code true} if the specified object is equal to this{@code OpenMBeanInfoSupport} instance.
 */
  public boolean equals(  Object obj){
    if (obj == null) {
      return false;
    }
    OpenMBeanInfo other;
    try {
      other=(OpenMBeanInfo)obj;
    }
 catch (    ClassCastException e) {
      return false;
    }
    if (!this.getClassName().equals(other.getClassName()))     return false;
    if (!sameArrayContents(this.getAttributes(),other.getAttributes()))     return false;
    if (!sameArrayContents(this.getConstructors(),other.getConstructors()))     return false;
    if (!sameArrayContents(this.getOperations(),other.getOperations()))     return false;
    if (!sameArrayContents(this.getNotifications(),other.getNotifications()))     return false;
    return true;
  }
  private static <T>boolean sameArrayContents(  T[] a1,  T[] a2){
    return (new HashSet<T>(Arrays.asList(a1)).equals(new HashSet<T>(Arrays.asList(a2))));
  }
  /** 
 * <p>Returns the hash code value for this {@codeOpenMBeanInfoSupport} instance.</p>
 * <p>The hash code of an {@code OpenMBeanInfoSupport} instance is
 * the sum of the hash codes of all elements of information used
 * in {@code equals} comparisons (ie: its class name, and its
 * infos on attributes, constructors, operations and
 * notifications, where the hashCode of each of these arrays is
 * calculated by a call to {@code new
 * java.util.HashSet(java.util.Arrays.asList(this.getSignature)).hashCode()}).</p>
 * <p>This ensures that {@code t1.equals(t2)} implies that {@codet1.hashCode()==t2.hashCode()} for any two {@codeOpenMBeanInfoSupport} instances {@code t1} and {@code t2}, as
 * required by the general contract of the method {@link Object#hashCode() Object.hashCode()}.</p>
 * <p>However, note that another instance of a class implementing
 * the {@code OpenMBeanInfo} interface may be equal to this {@codeOpenMBeanInfoSupport} instance as defined by {@link #equals(java.lang.Object)}, but may have a different hash code
 * if it is calculated differently.</p>
 * <p>As {@code OpenMBeanInfoSupport} instances are immutable, the
 * hash code for this instance is calculated once, on the first
 * call to {@code hashCode}, and then the same value is returned
 * for subsequent calls.</p>
 * @return the hash code value for this {@codeOpenMBeanInfoSupport} instance
 */
  public int hashCode(){
    if (myHashCode == null) {
      int value=0;
      value+=this.getClassName().hashCode();
      value+=arraySetHash(this.getAttributes());
      value+=arraySetHash(this.getConstructors());
      value+=arraySetHash(this.getOperations());
      value+=arraySetHash(this.getNotifications());
      myHashCode=Integer.valueOf(value);
    }
    return myHashCode.intValue();
  }
  private static <T>int arraySetHash(  T[] a){
    return new HashSet<T>(Arrays.asList(a)).hashCode();
  }
  /** 
 * <p>Returns a string representation of this {@codeOpenMBeanInfoSupport} instance.</p>
 * <p>The string representation consists of the name of this class
 * (ie {@code javax.management.openmbean.OpenMBeanInfoSupport}),
 * the MBean class name, the string representation of infos on
 * attributes, constructors, operations and notifications of the
 * described MBean and the string representation of the descriptor.</p>
 * <p>As {@code OpenMBeanInfoSupport} instances are immutable, the
 * string representation for this instance is calculated once, on
 * the first call to {@code toString}, and then the same value is
 * returned for subsequent calls.</p>
 * @return a string representation of this {@codeOpenMBeanInfoSupport} instance
 */
  public String toString(){
    if (myToString == null) {
      myToString=new StringBuilder().append(this.getClass().getName()).append("(mbean_class_name=").append(this.getClassName()).append(",attributes=").append(Arrays.asList(this.getAttributes()).toString()).append(",constructors=").append(Arrays.asList(this.getConstructors()).toString()).append(",operations=").append(Arrays.asList(this.getOperations()).toString()).append(",notifications=").append(Arrays.asList(this.getNotifications()).toString()).append(",descriptor=").append(this.getDescriptor()).append(")").toString();
    }
    return myToString;
  }
}
