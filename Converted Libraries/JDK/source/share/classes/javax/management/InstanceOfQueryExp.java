package javax.management;
/** 
 * This class is used by the query building mechanism for isInstanceOf expressions.
 * @serial include
 * @since 1.6
 */
class InstanceOfQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID=-1081892073854801359L;
  /** 
 * @serial The {@link StringValueExp} returning the name of the class
 * of which selected MBeans should be instances.
 */
  private StringValueExp classNameValue;
  /** 
 * Creates a new InstanceOfExp with a specific class name.
 * @param classNameValue The {@link StringValueExp} returning the name of
 * the class of which selected MBeans should be instances.
 */
  public InstanceOfQueryExp(  StringValueExp classNameValue){
    if (classNameValue == null) {
      throw new IllegalArgumentException("Null class name.");
    }
    this.classNameValue=classNameValue;
  }
  /** 
 * Returns the class name.
 * @returns The {@link StringValueExp} returning the name of
 * the class of which selected MBeans should be instances.
 */
  public StringValueExp getClassNameValue(){
    return classNameValue;
  }
  /** 
 * Applies the InstanceOf on a MBean.
 * @param name The name of the MBean on which the InstanceOf will be applied.
 * @return  True if the MBean specified by the name is instance of the class.
 * @exception BadAttributeValueExpException
 * @exception InvalidApplicationException
 * @exception BadStringOperationException
 * @exception BadBinaryOpValueExpException
 */
  public boolean apply(  ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    final StringValueExp val;
    try {
      val=(StringValueExp)classNameValue.apply(name);
    }
 catch (    ClassCastException x) {
      final BadStringOperationException y=new BadStringOperationException(x.toString());
      y.initCause(x);
      throw y;
    }
    try {
      return getMBeanServer().isInstanceOf(name,val.getValue());
    }
 catch (    InstanceNotFoundException infe) {
      return false;
    }
  }
  /** 
 * Returns a string representation of this InstanceOfQueryExp.
 * @return a string representation of this InstanceOfQueryExp.
 */
  public String toString(){
    return "InstanceOf " + classNameValue.toString();
  }
}
