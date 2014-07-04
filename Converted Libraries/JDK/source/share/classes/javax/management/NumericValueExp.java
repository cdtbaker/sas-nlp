package javax.management;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
/** 
 * This class represents numbers that are arguments to relational constraints.
 * A NumericValueExp may be used anywhere a ValueExp is required.
 * <p>The <b>serialVersionUID</b> of this class is <code>-4679739485102359104L</code>.
 * @serial include
 * @since 1.5
 */
@SuppressWarnings("serial") class NumericValueExp extends QueryEval implements ValueExp {
  private static final long oldSerialVersionUID=-6227876276058904000L;
  private static final long newSerialVersionUID=-4679739485102359104L;
  private static final ObjectStreamField[] oldSerialPersistentFields={new ObjectStreamField("longVal",Long.TYPE),new ObjectStreamField("doubleVal",Double.TYPE),new ObjectStreamField("valIsLong",Boolean.TYPE)};
  private static final ObjectStreamField[] newSerialPersistentFields={new ObjectStreamField("val",Number.class)};
  private static final long serialVersionUID;
  /** 
 * @serialField val Number The numeric value
 * <p>The <b>serialVersionUID</b> of this class is <code>-4679739485102359104L</code>.
 */
  private static final ObjectStreamField[] serialPersistentFields;
  private Number val=0.0;
  private static boolean compat=false;
static {
    try {
      GetPropertyAction act=new GetPropertyAction("jmx.serial.form");
      String form=AccessController.doPrivileged(act);
      compat=(form != null && form.equals("1.0"));
    }
 catch (    Exception e) {
    }
    if (compat) {
      serialPersistentFields=oldSerialPersistentFields;
      serialVersionUID=oldSerialVersionUID;
    }
 else {
      serialPersistentFields=newSerialPersistentFields;
      serialVersionUID=newSerialVersionUID;
    }
  }
  /** 
 * Basic constructor.
 */
  public NumericValueExp(){
  }
  /** 
 * Creates a new NumericValue representing the numeric literal <val>.
 */
  NumericValueExp(  Number val){
    this.val=val;
  }
  /** 
 * Returns a double numeric value
 */
  public double doubleValue(){
    if (val instanceof Long || val instanceof Integer) {
      return (double)(val.longValue());
    }
    return val.doubleValue();
  }
  /** 
 * Returns a long numeric value
 */
  public long longValue(){
    if (val instanceof Long || val instanceof Integer) {
      return val.longValue();
    }
    return (long)(val.doubleValue());
  }
  /** 
 * Returns true is if the numeric value is a long, false otherwise.
 */
  public boolean isLong(){
    return (val instanceof Long || val instanceof Integer);
  }
  /** 
 * Returns the string representing the object
 */
  public String toString(){
    if (val == null)     return "null";
    if (val instanceof Long || val instanceof Integer) {
      return Long.toString(val.longValue());
    }
    double d=val.doubleValue();
    if (Double.isInfinite(d))     return (d > 0) ? "(1.0 / 0.0)" : "(-1.0 / 0.0)";
    if (Double.isNaN(d))     return "(0.0 / 0.0)";
    return Double.toString(d);
  }
  /** 
 * Applies the ValueExp on a MBean.
 * @param name The name of the MBean on which the ValueExp will be applied.
 * @return  The <CODE>ValueExp</CODE>.
 * @exception BadStringOperationException
 * @exception BadBinaryOpValueExpException
 * @exception BadAttributeValueExpException
 * @exception InvalidApplicationException
 */
  public ValueExp apply(  ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    return this;
  }
  /** 
 * Deserializes a {@link NumericValueExp} from an {@link ObjectInputStream}.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    if (compat) {
      double doubleVal;
      long longVal;
      boolean isLong;
      ObjectInputStream.GetField fields=in.readFields();
      doubleVal=fields.get("doubleVal",(double)0);
      if (fields.defaulted("doubleVal")) {
        throw new NullPointerException("doubleVal");
      }
      longVal=fields.get("longVal",(long)0);
      if (fields.defaulted("longVal")) {
        throw new NullPointerException("longVal");
      }
      isLong=fields.get("valIsLong",false);
      if (fields.defaulted("valIsLong")) {
        throw new NullPointerException("valIsLong");
      }
      if (isLong) {
        this.val=longVal;
      }
 else {
        this.val=doubleVal;
      }
    }
 else {
      in.defaultReadObject();
    }
  }
  /** 
 * Serializes a {@link NumericValueExp} to an {@link ObjectOutputStream}.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField fields=out.putFields();
      fields.put("doubleVal",doubleValue());
      fields.put("longVal",longValue());
      fields.put("valIsLong",isLong());
      out.writeFields();
    }
 else {
      out.defaultWriteObject();
    }
  }
  @Deprecated public void setMBeanServer(  MBeanServer s){
    super.setMBeanServer(s);
  }
}
