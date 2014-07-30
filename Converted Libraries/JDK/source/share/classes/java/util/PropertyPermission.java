package java.util;
import java.io.Serializable;
import java.io.IOException;
import java.security.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Collections;
import java.io.ObjectStreamField;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import sun.security.util.SecurityConstants;
/** 
 * This class is for property permissions.
 * <P>
 * The name is the name of the property ("java.home",
 * "os.name", etc). The naming
 * convention follows the  hierarchical property naming convention.
 * Also, an asterisk
 * may appear at the end of the name, following a ".", or by itself, to
 * signify a wildcard match. For example: "java.*" or "*" is valid,
 * "*java" or "a*b" is not valid.
 * <P>
 * <P>
 * The actions to be granted are passed to the constructor in a string containing
 * a list of one or more comma-separated keywords. The possible keywords are
 * "read" and "write". Their meaning is defined as follows:
 * <P>
 * <DL>
 * <DT> read
 * <DD> read permission. Allows <code>System.getProperty</code> to
 * be called.
 * <DT> write
 * <DD> write permission. Allows <code>System.setProperty</code> to
 * be called.
 * </DL>
 * <P>
 * The actions string is converted to lowercase before processing.
 * <P>
 * Care should be taken before granting code permission to access
 * certain system properties.  For example, granting permission to
 * access the "java.home" system property gives potentially malevolent
 * code sensitive information about the system environment (the Java
 * installation directory).  Also, granting permission to access
 * the "user.name" and "user.home" system properties gives potentially
 * malevolent code sensitive information about the user environment
 * (the user's account name and home directory).
 * @see java.security.BasicPermission
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 * @author Roland Schemers
 * @since 1.2
 * @serial exclude
 */
public final class PropertyPermission extends BasicPermission {
  /** 
 * Read action.
 */
  private final static int READ=0x1;
  /** 
 * Write action.
 */
  private final static int WRITE=0x2;
  /** 
 * All actions (read,write);
 */
  private final static int ALL=READ | WRITE;
  /** 
 * No actions.
 */
  private final static int NONE=0x0;
  /** 
 * The actions mask.
 */
  private transient int mask;
  /** 
 * The actions string.
 * @serial
 */
  private String actions;
  /** 
 * initialize a PropertyPermission object. Common to all constructors.
 * Also called during de-serialization.
 * @param mask the actions mask to use.
 */
  private void init(  int mask){
    if ((mask & ALL) != mask)     throw new IllegalArgumentException("invalid actions mask");
    if (mask == NONE)     throw new IllegalArgumentException("invalid actions mask");
    if (getName() == null)     throw new NullPointerException("name can't be null");
    this.mask=mask;
  }
  /** 
 * Creates a new PropertyPermission object with the specified name.
 * The name is the name of the system property, and
 * <i>actions</i> contains a comma-separated list of the
 * desired actions granted on the property. Possible actions are
 * "read" and "write".
 * @param name the name of the PropertyPermission.
 * @param actions the actions string.
 * @throws NullPointerException if <code>name</code> is <code>null</code>.
 * @throws IllegalArgumentException if <code>name</code> is empty or if
 * <code>actions</code> is invalid.
 */
  public PropertyPermission(  String name,  String actions){
    super(name,actions);
    init(getMask(actions));
  }
  /** 
 * Checks if this PropertyPermission object "implies" the specified
 * permission.
 * <P>
 * More specifically, this method returns true if:<p>
 * <ul>
 * <li> <i>p</i> is an instanceof PropertyPermission,<p>
 * <li> <i>p</i>'s actions are a subset of this
 * object's actions, and <p>
 * <li> <i>p</i>'s name is implied by this object's
 * name. For example, "java.*" implies "java.home".
 * </ul>
 * @param p the permission to check against.
 * @return true if the specified permission is implied by this object,
 * false if not.
 */
  public boolean implies(  Permission p){
    if (!(p instanceof PropertyPermission))     return false;
    PropertyPermission that=(PropertyPermission)p;
    return ((this.mask & that.mask) == that.mask) && super.implies(that);
  }
  /** 
 * Checks two PropertyPermission objects for equality. Checks that <i>obj</i> is
 * a PropertyPermission, and has the same name and actions as this object.
 * <P>
 * @param obj the object we are testing for equality with this object.
 * @return true if obj is a PropertyPermission, and has the same name and
 * actions as this PropertyPermission object.
 */
  public boolean equals(  Object obj){
    if (obj == this)     return true;
    if (!(obj instanceof PropertyPermission))     return false;
    PropertyPermission that=(PropertyPermission)obj;
    return (this.mask == that.mask) && (this.getName().equals(that.getName()));
  }
  /** 
 * Returns the hash code value for this object.
 * The hash code used is the hash code of this permissions name, that is,
 * <code>getName().hashCode()</code>, where <code>getName</code> is
 * from the Permission superclass.
 * @return a hash code value for this object.
 */
  public int hashCode(){
    return this.getName().hashCode();
  }
  /** 
 * Converts an actions String to an actions mask.
 * @param action the action string.
 * @return the actions mask.
 */
  private static int getMask(  String actions){
    int mask=NONE;
    if (actions == null) {
      return mask;
    }
    if (actions == SecurityConstants.PROPERTY_READ_ACTION) {
      return READ;
    }
    if (actions == SecurityConstants.PROPERTY_WRITE_ACTION) {
      return WRITE;
    }
 else     if (actions == SecurityConstants.PROPERTY_RW_ACTION) {
      return READ | WRITE;
    }
    char[] a=actions.toCharArray();
    int i=a.length - 1;
    if (i < 0)     return mask;
    while (i != -1) {
      char c;
      while ((i != -1) && ((c=a[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))       i--;
      int matchlen;
      if (i >= 3 && (a[i - 3] == 'r' || a[i - 3] == 'R') && (a[i - 2] == 'e' || a[i - 2] == 'E') && (a[i - 1] == 'a' || a[i - 1] == 'A') && (a[i] == 'd' || a[i] == 'D')) {
        matchlen=4;
        mask|=READ;
      }
 else       if (i >= 4 && (a[i - 4] == 'w' || a[i - 4] == 'W') && (a[i - 3] == 'r' || a[i - 3] == 'R') && (a[i - 2] == 'i' || a[i - 2] == 'I') && (a[i - 1] == 't' || a[i - 1] == 'T') && (a[i] == 'e' || a[i] == 'E')) {
        matchlen=5;
        mask|=WRITE;
      }
 else {
        throw new IllegalArgumentException("invalid permission: " + actions);
      }
      boolean seencomma=false;
      while (i >= matchlen && !seencomma) {
switch (a[i - matchlen]) {
case ',':
          seencomma=true;
case ' ':
case '\r':
case '\n':
case '\f':
case '\t':
        break;
default :
      throw new IllegalArgumentException("invalid permission: " + actions);
  }
  i--;
}
i-=matchlen;
}
return mask;
}
/** 
 * Return the canonical string representation of the actions.
 * Always returns present actions in the following order:
 * read, write.
 * @return the canonical string representation of the actions.
 */
static String getActions(int mask){
StringBuilder sb=new StringBuilder();
boolean comma=false;
if ((mask & READ) == READ) {
comma=true;
sb.append("read");
}
if ((mask & WRITE) == WRITE) {
if (comma) sb.append(',');
 else comma=true;
sb.append("write");
}
return sb.toString();
}
/** 
 * Returns the "canonical string representation" of the actions.
 * That is, this method always returns present actions in the following order:
 * read, write. For example, if this PropertyPermission object
 * allows both write and read actions, a call to <code>getActions</code>
 * will return the string "read,write".
 * @return the canonical string representation of the actions.
 */
public String getActions(){
if (actions == null) actions=getActions(this.mask);
return actions;
}
/** 
 * Return the current action mask.
 * Used by the PropertyPermissionCollection
 * @return the actions mask.
 */
int getMask(){
return mask;
}
/** 
 * Returns a new PermissionCollection object for storing
 * PropertyPermission objects.
 * <p>
 * @return a new PermissionCollection object suitable for storing
 * PropertyPermissions.
 */
public PermissionCollection newPermissionCollection(){
return new PropertyPermissionCollection();
}
private static final long serialVersionUID=885438825399942851L;
/** 
 * WriteObject is called to save the state of the PropertyPermission
 * to a stream. The actions are serialized, and the superclass
 * takes care of the name.
 */
private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
if (actions == null) getActions();
s.defaultWriteObject();
}
/** 
 * readObject is called to restore the state of the PropertyPermission from
 * a stream.
 */
private synchronized void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
s.defaultReadObject();
init(getMask(actions));
}
}
/** 
 * A PropertyPermissionCollection stores a set of PropertyPermission
 * permissions.
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @author Roland Schemers
 * @serial include
 */
final class PropertyPermissionCollection extends PermissionCollection implements Serializable {
/** 
 * Key is property name; value is PropertyPermission.
 * Not serialized; see serialization section at end of class.
 */
private transient Map perms;
/** 
 * Boolean saying if "*" is in the collection.
 * @see #serialPersistentFields
 */
private boolean all_allowed;
/** 
 * Create an empty PropertyPermissions object.
 */
public PropertyPermissionCollection(){
perms=new HashMap(32);
all_allowed=false;
}
/** 
 * Adds a permission to the PropertyPermissions. The key for the hash is
 * the name.
 * @param permission the Permission object to add.
 * @exception IllegalArgumentException - if the permission is not a
 * PropertyPermission
 * @exception SecurityException - if this PropertyPermissionCollection
 * object has been marked readonly
 */
public void add(Permission permission){
if (!(permission instanceof PropertyPermission)) throw new IllegalArgumentException("invalid permission: " + permission);
if (isReadOnly()) throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
PropertyPermission pp=(PropertyPermission)permission;
String propName=pp.getName();
synchronized (this) {
PropertyPermission existing=(PropertyPermission)perms.get(propName);
if (existing != null) {
  int oldMask=existing.getMask();
  int newMask=pp.getMask();
  if (oldMask != newMask) {
    int effective=oldMask | newMask;
    String actions=PropertyPermission.getActions(effective);
    perms.put(propName,new PropertyPermission(propName,actions));
  }
}
 else {
  perms.put(propName,permission);
}
}
if (!all_allowed) {
if (propName.equals("*")) all_allowed=true;
}
}
/** 
 * Check and see if this set of permissions implies the permissions
 * expressed in "permission".
 * @param p the Permission object to compare
 * @return true if "permission" is a proper subset of a permission in
 * the set, false if not.
 */
public boolean implies(Permission permission){
if (!(permission instanceof PropertyPermission)) return false;
PropertyPermission pp=(PropertyPermission)permission;
PropertyPermission x;
int desired=pp.getMask();
int effective=0;
if (all_allowed) {
synchronized (this) {
  x=(PropertyPermission)perms.get("*");
}
if (x != null) {
  effective|=x.getMask();
  if ((effective & desired) == desired)   return true;
}
}
String name=pp.getName();
synchronized (this) {
x=(PropertyPermission)perms.get(name);
}
if (x != null) {
effective|=x.getMask();
if ((effective & desired) == desired) return true;
}
int last, offset;
offset=name.length() - 1;
while ((last=name.lastIndexOf(".",offset)) != -1) {
name=name.substring(0,last + 1) + "*";
synchronized (this) {
  x=(PropertyPermission)perms.get(name);
}
if (x != null) {
  effective|=x.getMask();
  if ((effective & desired) == desired)   return true;
}
offset=last - 1;
}
return false;
}
/** 
 * Returns an enumeration of all the PropertyPermission objects in the
 * container.
 * @return an enumeration of all the PropertyPermission objects.
 */
public Enumeration elements(){
synchronized (this) {
return Collections.enumeration(perms.values());
}
}
private static final long serialVersionUID=7015263904581634791L;
/** 
 * @serialField permissions java.util.Hashtable
 * A table of the PropertyPermissions.
 * @serialField all_allowed boolean
 * boolean saying if "*" is in the collection.
 */
private static final ObjectStreamField[] serialPersistentFields={new ObjectStreamField("permissions",Hashtable.class),new ObjectStreamField("all_allowed",Boolean.TYPE)};
/** 
 * @serialData Default fields.
 */
private void writeObject(ObjectOutputStream out) throws IOException {
Hashtable permissions=new Hashtable(perms.size() * 2);
synchronized (this) {
permissions.putAll(perms);
}
ObjectOutputStream.PutField pfields=out.putFields();
pfields.put("all_allowed",all_allowed);
pfields.put("permissions",permissions);
out.writeFields();
}
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
ObjectInputStream.GetField gfields=in.readFields();
all_allowed=gfields.get("all_allowed",false);
Hashtable permissions=(Hashtable)gfields.get("permissions",null);
perms=new HashMap(permissions.size() * 2);
perms.putAll(permissions);
}
}