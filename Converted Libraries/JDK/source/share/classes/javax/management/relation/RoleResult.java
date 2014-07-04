package javax.management.relation;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.Iterator;
/** 
 * Represents the result of a multiple access to several roles of a relation
 * (either for reading or writing).
 * <p>The <b>serialVersionUID</b> of this class is <code>-6304063118040985512L</code>.
 * @since 1.5
 */
@SuppressWarnings("serial") public class RoleResult implements Serializable {
  private static final long oldSerialVersionUID=3786616013762091099L;
  private static final long newSerialVersionUID=-6304063118040985512L;
  private static final ObjectStreamField[] oldSerialPersistentFields={new ObjectStreamField("myRoleList",RoleList.class),new ObjectStreamField("myRoleUnresList",RoleUnresolvedList.class)};
  private static final ObjectStreamField[] newSerialPersistentFields={new ObjectStreamField("roleList",RoleList.class),new ObjectStreamField("unresolvedRoleList",RoleUnresolvedList.class)};
  private static final long serialVersionUID;
  /** 
 * @serialField roleList RoleList List of roles successfully accessed
 * @serialField unresolvedRoleList RoleUnresolvedList List of roles unsuccessfully accessed
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
 * @serial List of roles successfully accessed
 */
  private RoleList roleList=null;
  /** 
 * @serial List of roles unsuccessfully accessed
 */
  private RoleUnresolvedList unresolvedRoleList=null;
  /** 
 * Constructor.
 * @param list  list of roles successfully accessed.
 * @param unresolvedList  list of roles not accessed (with problem
 * descriptions).
 */
  public RoleResult(  RoleList list,  RoleUnresolvedList unresolvedList){
    setRoles(list);
    setRolesUnresolved(unresolvedList);
    return;
  }
  /** 
 * Retrieves list of roles successfully accessed.
 * @return a RoleList
 * @see #setRoles
 */
  public RoleList getRoles(){
    return roleList;
  }
  /** 
 * Retrieves list of roles unsuccessfully accessed.
 * @return a RoleUnresolvedList.
 * @see #setRolesUnresolved
 */
  public RoleUnresolvedList getRolesUnresolved(){
    return unresolvedRoleList;
  }
  /** 
 * Sets list of roles successfully accessed.
 * @param list  list of roles successfully accessed
 * @see #getRoles
 */
  public void setRoles(  RoleList list){
    if (list != null) {
      roleList=new RoleList();
      for (Iterator<?> roleIter=list.iterator(); roleIter.hasNext(); ) {
        Role currRole=(Role)(roleIter.next());
        roleList.add((Role)(currRole.clone()));
      }
    }
 else {
      roleList=null;
    }
    return;
  }
  /** 
 * Sets list of roles unsuccessfully accessed.
 * @param unresolvedList  list of roles unsuccessfully accessed
 * @see #getRolesUnresolved
 */
  public void setRolesUnresolved(  RoleUnresolvedList unresolvedList){
    if (unresolvedList != null) {
      unresolvedRoleList=new RoleUnresolvedList();
      for (Iterator<?> roleUnresIter=unresolvedList.iterator(); roleUnresIter.hasNext(); ) {
        RoleUnresolved currRoleUnres=(RoleUnresolved)(roleUnresIter.next());
        unresolvedRoleList.add((RoleUnresolved)(currRoleUnres.clone()));
      }
    }
 else {
      unresolvedRoleList=null;
    }
    return;
  }
  /** 
 * Deserializes a {@link RoleResult} from an {@link ObjectInputStream}.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField fields=in.readFields();
      roleList=(RoleList)fields.get("myRoleList",null);
      if (fields.defaulted("myRoleList")) {
        throw new NullPointerException("myRoleList");
      }
      unresolvedRoleList=(RoleUnresolvedList)fields.get("myRoleUnresList",null);
      if (fields.defaulted("myRoleUnresList")) {
        throw new NullPointerException("myRoleUnresList");
      }
    }
 else {
      in.defaultReadObject();
    }
  }
  /** 
 * Serializes a {@link RoleResult} to an {@link ObjectOutputStream}.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField fields=out.putFields();
      fields.put("myRoleList",roleList);
      fields.put("myRoleUnresList",unresolvedRoleList);
      out.writeFields();
    }
 else {
      out.defaultWriteObject();
    }
  }
}
