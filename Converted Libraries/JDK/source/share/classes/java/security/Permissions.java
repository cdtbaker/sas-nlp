package java.security;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.io.Serializable;
import java.io.ObjectStreamField;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
/** 
 * This class represents a heterogeneous collection of Permissions. That is,
 * it contains different types of Permission objects, organized into
 * PermissionCollections. For example, if any
 * <code>java.io.FilePermission</code> objects are added to an instance of
 * this class, they are all stored in a single
 * PermissionCollection. It is the PermissionCollection returned by a call to
 * the <code>newPermissionCollection</code> method in the FilePermission class.
 * Similarly, any <code>java.lang.RuntimePermission</code> objects are
 * stored in the PermissionCollection returned by a call to the
 * <code>newPermissionCollection</code> method in the
 * RuntimePermission class. Thus, this class represents a collection of
 * PermissionCollections.
 * <p>When the <code>add</code> method is called to add a Permission, the
 * Permission is stored in the appropriate PermissionCollection. If no such
 * collection exists yet, the Permission object's class is determined and the
 * <code>newPermissionCollection</code> method is called on that class to create
 * the PermissionCollection and add it to the Permissions object. If
 * <code>newPermissionCollection</code> returns null, then a default
 * PermissionCollection that uses a hashtable will be created and used. Each
 * hashtable entry stores a Permission object as both the key and the value.
 * <p> Enumerations returned via the <code>elements</code> method are
 * not <em>fail-fast</em>.  Modifications to a collection should not be
 * performed while enumerating over that collection.
 * @see Permission
 * @see PermissionCollection
 * @see AllPermission
 * @author Marianne Mueller
 * @author Roland Schemers
 * @serial exclude
 */
public final class Permissions extends PermissionCollection implements Serializable {
  /** 
 * Key is permissions Class, value is PermissionCollection for that class.
 * Not serialized; see serialization section at end of class.
 */
  private transient Map<Class<?>,PermissionCollection> permsMap;
  private transient boolean hasUnresolved=false;
  PermissionCollection allPermission;
  /** 
 * Creates a new Permissions object containing no PermissionCollections.
 */
  public Permissions(){
    permsMap=new HashMap<Class<?>,PermissionCollection>(11);
    allPermission=null;
  }
  /** 
 * Adds a permission object to the PermissionCollection for the class the
 * permission belongs to. For example, if <i>permission</i> is a
 * FilePermission, it is added to the FilePermissionCollection stored
 * in this Permissions object.
 * This method creates
 * a new PermissionCollection object (and adds the permission to it)
 * if an appropriate collection does not yet exist. <p>
 * @param permission the Permission object to add.
 * @exception SecurityException if this Permissions object is
 * marked as readonly.
 * @see PermissionCollection#isReadOnly()
 */
  public void add(  Permission permission){
    if (isReadOnly())     throw new SecurityException("attempt to add a Permission to a readonly Permissions object");
    PermissionCollection pc;
synchronized (this) {
      pc=getPermissionCollection(permission,true);
      pc.add(permission);
    }
    if (permission instanceof AllPermission) {
      allPermission=pc;
    }
    if (permission instanceof UnresolvedPermission) {
      hasUnresolved=true;
    }
  }
  /** 
 * Checks to see if this object's PermissionCollection for permissions of
 * the specified permission's class implies the permissions
 * expressed in the <i>permission</i> object. Returns true if the
 * combination of permissions in the appropriate PermissionCollection
 * (e.g., a FilePermissionCollection for a FilePermission) together
 * imply the specified permission.
 * <p>For example, suppose there is a FilePermissionCollection in this
 * Permissions object, and it contains one FilePermission that specifies
 * "read" access for  all files in all subdirectories of the "/tmp"
 * directory, and another FilePermission that specifies "write" access
 * for all files in the "/tmp/scratch/foo" directory.
 * Then if the <code>implies</code> method
 * is called with a permission specifying both "read" and "write" access
 * to files in the "/tmp/scratch/foo" directory, <code>true</code> is
 * returned.
 * <p>Additionally, if this PermissionCollection contains the
 * AllPermission, this method will always return true.
 * <p>
 * @param permission the Permission object to check.
 * @return true if "permission" is implied by the permissions in the
 * PermissionCollection it
 * belongs to, false if not.
 */
  public boolean implies(  Permission permission){
    if (allPermission != null) {
      return true;
    }
 else {
synchronized (this) {
        PermissionCollection pc=getPermissionCollection(permission,false);
        if (pc != null) {
          return pc.implies(permission);
        }
 else {
          return false;
        }
      }
    }
  }
  /** 
 * Returns an enumeration of all the Permission objects in all the
 * PermissionCollections in this Permissions object.
 * @return an enumeration of all the Permissions.
 */
  public Enumeration<Permission> elements(){
synchronized (this) {
      return new PermissionsEnumerator(permsMap.values().iterator());
    }
  }
  /** 
 * Gets the PermissionCollection in this Permissions object for
 * permissions whose type is the same as that of <i>p</i>.
 * For example, if <i>p</i> is a FilePermission,
 * the FilePermissionCollection
 * stored in this Permissions object will be returned.
 * If createEmpty is true,
 * this method creates a new PermissionCollection object for the specified
 * type of permission objects if one does not yet exist.
 * To do so, it first calls the <code>newPermissionCollection</code> method
 * on <i>p</i>.  Subclasses of class Permission
 * override that method if they need to store their permissions in a
 * particular PermissionCollection object in order to provide the
 * correct semantics when the <code>PermissionCollection.implies</code>
 * method is called.
 * If the call returns a PermissionCollection, that collection is stored
 * in this Permissions object. If the call returns null and createEmpty
 * is true, then
 * this method instantiates and stores a default PermissionCollection
 * that uses a hashtable to store its permission objects.
 * createEmpty is ignored when creating empty PermissionCollection
 * for unresolved permissions because of the overhead of determining the
 * PermissionCollection to use.
 * createEmpty should be set to false when this method is invoked from
 * implies() because it incurs the additional overhead of creating and
 * adding an empty PermissionCollection that will just return false.
 * It should be set to true when invoked from add().
 */
  private PermissionCollection getPermissionCollection(  Permission p,  boolean createEmpty){
    Class c=p.getClass();
    PermissionCollection pc=permsMap.get(c);
    if (!hasUnresolved && !createEmpty) {
      return pc;
    }
 else     if (pc == null) {
      pc=(hasUnresolved ? getUnresolvedPermissions(p) : null);
      if (pc == null && createEmpty) {
        pc=p.newPermissionCollection();
        if (pc == null)         pc=new PermissionsHash();
      }
      if (pc != null) {
        permsMap.put(c,pc);
      }
    }
    return pc;
  }
  /** 
 * Resolves any unresolved permissions of type p.
 * @param p the type of unresolved permission to resolve
 * @return PermissionCollection containing the unresolved permissions,
 * or null if there were no unresolved permissions of type p.
 */
  private PermissionCollection getUnresolvedPermissions(  Permission p){
    UnresolvedPermissionCollection uc=(UnresolvedPermissionCollection)permsMap.get(UnresolvedPermission.class);
    if (uc == null)     return null;
    List<UnresolvedPermission> unresolvedPerms=uc.getUnresolvedPermissions(p);
    if (unresolvedPerms == null)     return null;
    java.security.cert.Certificate certs[]=null;
    Object signers[]=p.getClass().getSigners();
    int n=0;
    if (signers != null) {
      for (int j=0; j < signers.length; j++) {
        if (signers[j] instanceof java.security.cert.Certificate) {
          n++;
        }
      }
      certs=new java.security.cert.Certificate[n];
      n=0;
      for (int j=0; j < signers.length; j++) {
        if (signers[j] instanceof java.security.cert.Certificate) {
          certs[n++]=(java.security.cert.Certificate)signers[j];
        }
      }
    }
    PermissionCollection pc=null;
synchronized (unresolvedPerms) {
      int len=unresolvedPerms.size();
      for (int i=0; i < len; i++) {
        UnresolvedPermission up=unresolvedPerms.get(i);
        Permission perm=up.resolve(p,certs);
        if (perm != null) {
          if (pc == null) {
            pc=p.newPermissionCollection();
            if (pc == null)             pc=new PermissionsHash();
          }
          pc.add(perm);
        }
      }
    }
    return pc;
  }
  private static final long serialVersionUID=4858622370623524688L;
  /** 
 * @serialField perms java.util.Hashtable
 * A table of the Permission classes and PermissionCollections.
 * @serialField allPermission java.security.PermissionCollection
 */
  private static final ObjectStreamField[] serialPersistentFields={new ObjectStreamField("perms",Hashtable.class),new ObjectStreamField("allPermission",PermissionCollection.class)};
  /** 
 * @serialData Default fields.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    Hashtable<Class<?>,PermissionCollection> perms=new Hashtable<>(permsMap.size() * 2);
synchronized (this) {
      perms.putAll(permsMap);
    }
    ObjectOutputStream.PutField pfields=out.putFields();
    pfields.put("allPermission",allPermission);
    pfields.put("perms",perms);
    out.writeFields();
  }
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField gfields=in.readFields();
    allPermission=(PermissionCollection)gfields.get("allPermission",null);
    Hashtable<Class<?>,PermissionCollection> perms=(Hashtable<Class<?>,PermissionCollection>)gfields.get("perms",null);
    permsMap=new HashMap<Class<?>,PermissionCollection>(perms.size() * 2);
    permsMap.putAll(perms);
    UnresolvedPermissionCollection uc=(UnresolvedPermissionCollection)permsMap.get(UnresolvedPermission.class);
    hasUnresolved=(uc != null && uc.elements().hasMoreElements());
  }
}
final class PermissionsEnumerator implements Enumeration<Permission> {
  private Iterator<PermissionCollection> perms;
  private Enumeration<Permission> permset;
  PermissionsEnumerator(  Iterator<PermissionCollection> e){
    perms=e;
    permset=getNextEnumWithMore();
  }
  public boolean hasMoreElements(){
    if (permset == null)     return false;
    if (permset.hasMoreElements())     return true;
    permset=getNextEnumWithMore();
    return (permset != null);
  }
  public Permission nextElement(){
    if (hasMoreElements()) {
      return permset.nextElement();
    }
 else {
      throw new NoSuchElementException("PermissionsEnumerator");
    }
  }
  private Enumeration<Permission> getNextEnumWithMore(){
    while (perms.hasNext()) {
      PermissionCollection pc=perms.next();
      Enumeration<Permission> next=pc.elements();
      if (next.hasMoreElements())       return next;
    }
    return null;
  }
}
/** 
 * A PermissionsHash stores a homogeneous set of permissions in a hashtable.
 * @see Permission
 * @see Permissions
 * @author Roland Schemers
 * @serial include
 */
final class PermissionsHash extends PermissionCollection implements Serializable {
  /** 
 * Key and value are (same) permissions objects.
 * Not serialized; see serialization section at end of class.
 */
  private transient Map<Permission,Permission> permsMap;
  /** 
 * Create an empty PermissionsHash object.
 */
  PermissionsHash(){
    permsMap=new HashMap<Permission,Permission>(11);
  }
  /** 
 * Adds a permission to the PermissionsHash.
 * @param permission the Permission object to add.
 */
  public void add(  Permission permission){
synchronized (this) {
      permsMap.put(permission,permission);
    }
  }
  /** 
 * Check and see if this set of permissions implies the permissions
 * expressed in "permission".
 * @param permission the Permission object to compare
 * @return true if "permission" is a proper subset of a permission in
 * the set, false if not.
 */
  public boolean implies(  Permission permission){
synchronized (this) {
      Permission p=permsMap.get(permission);
      if (p == null) {
        for (        Permission p_ : permsMap.values()) {
          if (p_.implies(permission))           return true;
        }
        return false;
      }
 else {
        return true;
      }
    }
  }
  /** 
 * Returns an enumeration of all the Permission objects in the container.
 * @return an enumeration of all the Permissions.
 */
  public Enumeration<Permission> elements(){
synchronized (this) {
      return Collections.enumeration(permsMap.values());
    }
  }
  private static final long serialVersionUID=-8491988220802933440L;
  /** 
 * @serialField perms java.util.Hashtable
 * A table of the Permissions (both key and value are same).
 */
  private static final ObjectStreamField[] serialPersistentFields={new ObjectStreamField("perms",Hashtable.class)};
  /** 
 * @serialData Default fields.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    Hashtable<Permission,Permission> perms=new Hashtable<>(permsMap.size() * 2);
synchronized (this) {
      perms.putAll(permsMap);
    }
    ObjectOutputStream.PutField pfields=out.putFields();
    pfields.put("perms",perms);
    out.writeFields();
  }
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField gfields=in.readFields();
    Hashtable<Permission,Permission> perms=(Hashtable<Permission,Permission>)gfields.get("perms",null);
    permsMap=new HashMap<Permission,Permission>(perms.size() * 2);
    permsMap.putAll(perms);
  }
}
