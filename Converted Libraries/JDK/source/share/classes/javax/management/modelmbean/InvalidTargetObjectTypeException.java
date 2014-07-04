package javax.management.modelmbean;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
/** 
 * Exception thrown when an invalid target object type is specified.
 * <p>The <b>serialVersionUID</b> of this class is <code>1190536278266811217L</code>.
 * @since 1.5
 */
@SuppressWarnings("serial") public class InvalidTargetObjectTypeException extends Exception {
  private static final long oldSerialVersionUID=3711724570458346634L;
  private static final long newSerialVersionUID=1190536278266811217L;
  private static final ObjectStreamField[] oldSerialPersistentFields={new ObjectStreamField("msgStr",String.class),new ObjectStreamField("relatedExcept",Exception.class)};
  private static final ObjectStreamField[] newSerialPersistentFields={new ObjectStreamField("exception",Exception.class)};
  private static final long serialVersionUID;
  /** 
 * @serialField exception Exception Encapsulated {@link Exception}
 */
  private static final ObjectStreamField[] serialPersistentFields;
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
 * @serial Encapsulated {@link Exception}
 */
  Exception exception;
  /** 
 * Default constructor.
 */
  public InvalidTargetObjectTypeException(){
    super("InvalidTargetObjectTypeException: ");
    exception=null;
  }
  /** 
 * Constructor from a string.
 * @param s String value that will be incorporated in the message for
 * this exception.
 */
  public InvalidTargetObjectTypeException(  String s){
    super("InvalidTargetObjectTypeException: " + s);
    exception=null;
  }
  /** 
 * Constructor taking an exception and a string.
 * @param e Exception that we may have caught to reissue as an
 * InvalidTargetObjectTypeException.  The message will be used, and we may want to
 * consider overriding the printStackTrace() methods to get data
 * pointing back to original throw stack.
 * @param s String value that will be incorporated in message for
 * this exception.
 */
  public InvalidTargetObjectTypeException(  Exception e,  String s){
    super("InvalidTargetObjectTypeException: " + s + ((e != null) ? ("\n\t triggered by:" + e.toString()) : ""));
    exception=e;
  }
  /** 
 * Deserializes an {@link InvalidTargetObjectTypeException} from an {@link ObjectInputStream}.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField fields=in.readFields();
      exception=(Exception)fields.get("relatedExcept",null);
      if (fields.defaulted("relatedExcept")) {
        throw new NullPointerException("relatedExcept");
      }
    }
 else {
      in.defaultReadObject();
    }
  }
  /** 
 * Serializes an {@link InvalidTargetObjectTypeException} to an {@link ObjectOutputStream}.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField fields=out.putFields();
      fields.put("relatedExcept",exception);
      fields.put("msgStr",((exception != null) ? exception.getMessage() : ""));
      out.writeFields();
    }
 else {
      out.defaultWriteObject();
    }
  }
}
