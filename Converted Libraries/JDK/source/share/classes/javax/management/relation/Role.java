package javax.management.relation;
import static com.sun.jmx.mbeanserver.Util.cast;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;
/** 
 * Represents a role: includes a role name and referenced MBeans (via their
 * ObjectNames). The role value is always represented as an ArrayList
 * collection (of ObjectNames) to homogenize the access.
 * <p>The <b>serialVersionUID</b> of this class is <code>-279985518429862552L</code>.
 * @since 1.5
 */
@SuppressWarnings("serial") public class Role implements Serializable {
  private static final long oldSerialVersionUID=-1959486389343113026L;
  private static final long newSerialVersionUID=-279985518429862552L;
  private static final ObjectStreamField[] oldSerialPersistentFields={new ObjectStreamField("myName",String.class),new ObjectStreamField("myObjNameList",ArrayList.class)};
  private static final ObjectStreamField[] newSerialPersistentFields={new ObjectStreamField("name",String.class),new ObjectStreamField("objectNameList",List.class)};
  private static final long serialVersionUID;
  /** 
 * @serialField name String Role name
 * @serialField objectNameList List {@link List} of {@link ObjectName}s of referenced MBeans
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
 * @serial Role name
 */
  private String name=null;
  /** 
 * @serial {@link List} of {@link ObjectName}s of referenced MBeans
 */
  private List<ObjectName> objectNameList=new ArrayList<ObjectName>();
  /** 
 * <p>Make a new Role object.
 * No check is made that the ObjectNames in the role value exist in
 * an MBean server.  That check will be made when the role is set
 * in a relation.
 * @param roleName  role name
 * @param roleValue  role value (List of ObjectName objects)
 * @exception IllegalArgumentException  if null parameter
 */
  public Role(  String roleName,  List<ObjectName> roleValue) throws IllegalArgumentException {
    if (roleName == null || roleValue == null) {
      String excMsg="Invalid parameter";
      throw new IllegalArgumentException(excMsg);
    }
    setRoleName(roleName);
    setRoleValue(roleValue);
    return;
  }
  /** 
 * Retrieves role name.
 * @return the role name.
 * @see #setRoleName
 */
  public String getRoleName(){
    return name;
  }
  /** 
 * Retrieves role value.
 * @return ArrayList of ObjectName objects for referenced MBeans.
 * @see #setRoleValue
 */
  public List<ObjectName> getRoleValue(){
    return objectNameList;
  }
  /** 
 * Sets role name.
 * @param roleName  role name
 * @exception IllegalArgumentException  if null parameter
 * @see #getRoleName
 */
  public void setRoleName(  String roleName) throws IllegalArgumentException {
    if (roleName == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    name=roleName;
    return;
  }
  /** 
 * Sets role value.
 * @param roleValue  List of ObjectName objects for referenced
 * MBeans.
 * @exception IllegalArgumentException  if null parameter
 * @see #getRoleValue
 */
  public void setRoleValue(  List<ObjectName> roleValue) throws IllegalArgumentException {
    if (roleValue == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    objectNameList=new ArrayList<ObjectName>(roleValue);
    return;
  }
  /** 
 * Returns a string describing the role.
 * @return the description of the role.
 */
  public String toString(){
    StringBuilder result=new StringBuilder();
    result.append("role name: " + name + "; role value: ");
    for (Iterator<ObjectName> objNameIter=objectNameList.iterator(); objNameIter.hasNext(); ) {
      ObjectName currObjName=objNameIter.next();
      result.append(currObjName.toString());
      if (objNameIter.hasNext()) {
        result.append(", ");
      }
    }
    return result.toString();
  }
  /** 
 * Clone the role object.
 * @return a Role that is an independent copy of the current Role object.
 */
  public Object clone(){
    try {
      return new Role(name,objectNameList);
    }
 catch (    IllegalArgumentException exc) {
      return null;
    }
  }
  /** 
 * Returns a string for the given role value.
 * @param roleValue  List of ObjectName objects
 * @return A String consisting of the ObjectNames separated by
 * newlines (\n).
 * @exception IllegalArgumentException  if null parameter
 */
  public static String roleValueToString(  List<ObjectName> roleValue) throws IllegalArgumentException {
    if (roleValue == null) {
      String excMsg="Invalid parameter";
      throw new IllegalArgumentException(excMsg);
    }
    StringBuilder result=new StringBuilder();
    for (    ObjectName currObjName : roleValue) {
      if (result.length() > 0)       result.append("\n");
      result.append(currObjName.toString());
    }
    return result.toString();
  }
  /** 
 * Deserializes a {@link Role} from an {@link ObjectInputStream}.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField fields=in.readFields();
      name=(String)fields.get("myName",null);
      if (fields.defaulted("myName")) {
        throw new NullPointerException("myName");
      }
      objectNameList=cast(fields.get("myObjNameList",null));
      if (fields.defaulted("myObjNameList")) {
        throw new NullPointerException("myObjNameList");
      }
    }
 else {
      in.defaultReadObject();
    }
  }
  /** 
 * Serializes a {@link Role} to an {@link ObjectOutputStream}.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField fields=out.putFields();
      fields.put("myName",name);
      fields.put("myObjNameList",objectNameList);
      out.writeFields();
    }
 else {
      out.defaultWriteObject();
    }
  }
}
