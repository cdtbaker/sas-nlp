package javax.management.modelmbean;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
/** 
 * This exception is thrown when an XML formatted string is being parsed into ModelMBean objects
 * or when XML formatted strings are being created from ModelMBean objects.
 * It is also used to wrapper exceptions from XML parsers that may be used.
 * <p>The <b>serialVersionUID</b> of this class is <code>3176664577895105181L</code>.
 * @since 1.5
 */
@SuppressWarnings("serial") public class XMLParseException extends Exception {
  private static final long oldSerialVersionUID=-7780049316655891976L;
  private static final long newSerialVersionUID=3176664577895105181L;
  private static final ObjectStreamField[] oldSerialPersistentFields={new ObjectStreamField("msgStr",String.class)};
  private static final ObjectStreamField[] newSerialPersistentFields={};
  private static final long serialVersionUID;
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
 * Default constructor .
 */
  public XMLParseException(){
    super("XML Parse Exception.");
  }
  /** 
 * Constructor taking a string.
 * @param s the detail message.
 */
  public XMLParseException(  String s){
    super("XML Parse Exception: " + s);
  }
  /** 
 * Constructor taking a string and an exception.
 * @param e the nested exception.
 * @param s the detail message.
 */
  public XMLParseException(  Exception e,  String s){
    super("XML Parse Exception: " + s + ":"+ e.toString());
  }
  /** 
 * Deserializes an {@link XMLParseException} from an {@link ObjectInputStream}.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
  }
  /** 
 * Serializes an {@link XMLParseException} to an {@link ObjectOutputStream}.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField fields=out.putFields();
      fields.put("msgStr",getMessage());
      out.writeFields();
    }
 else {
      out.defaultWriteObject();
    }
  }
}
