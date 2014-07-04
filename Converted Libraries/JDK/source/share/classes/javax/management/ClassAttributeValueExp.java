package javax.management;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
/** 
 * This class represents the name of the Java implementation class of
 * the MBean. It is used for performing queries based on the class of
 * the MBean.
 * @serial include
 * <p>The <b>serialVersionUID</b> of this class is <code>-1081892073854801359L</code>.
 * @since 1.5
 */
@SuppressWarnings("serial") class ClassAttributeValueExp extends AttributeValueExp {
  private static final long oldSerialVersionUID=-2212731951078526753L;
  private static final long newSerialVersionUID=-1081892073854801359L;
  private static final long serialVersionUID;
static {
    boolean compat=false;
    try {
      GetPropertyAction act=new GetPropertyAction("jmx.serial.form");
      String form=AccessController.doPrivileged(act);
      compat=(form != null && form.equals("1.0"));
    }
 catch (    Exception e) {
    }
    if (compat)     serialVersionUID=oldSerialVersionUID;
 else     serialVersionUID=newSerialVersionUID;
  }
  /** 
 * @serial The name of the attribute
 * <p>The <b>serialVersionUID</b> of this class is <code>-1081892073854801359L</code>.
 */
  private String attr;
  /** 
 * Basic Constructor.
 */
  public ClassAttributeValueExp(){
    super("Class");
    attr="Class";
  }
  /** 
 * Applies the ClassAttributeValueExp on an MBean. Returns the name of
 * the Java implementation class of the MBean.
 * @param name The name of the MBean on which the ClassAttributeValueExp will be applied.
 * @return  The ValueExp.
 * @exception BadAttributeValueExpException
 * @exception InvalidApplicationException
 */
  public ValueExp apply(  ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    Object result=getValue(name);
    if (result instanceof String) {
      return new StringValueExp((String)result);
    }
 else {
      throw new BadAttributeValueExpException(result);
    }
  }
  /** 
 * Returns the string "Class" representing its value
 */
  public String toString(){
    return attr;
  }
  protected Object getValue(  ObjectName name){
    try {
      MBeanServer server=QueryEval.getMBeanServer();
      return server.getObjectInstance(name).getClassName();
    }
 catch (    Exception re) {
      return null;
    }
  }
}
