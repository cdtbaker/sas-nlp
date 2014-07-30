package javax.management;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.StringTokenizer;
/** 
 * A Permission to perform actions related to MBeanServers.
 * The <em>name</em> of the permission specifies the operation requested
 * or granted by the permission.  For a granted permission, it can be
 * <code>*</code> to allow all of the MBeanServer operations specified below.
 * Otherwise, for a granted or requested permission, it must be one of the
 * following:
 * <dl>
 * <dt>createMBeanServer</dt>
 * <dd>Create a new MBeanServer object using the method{@link MBeanServerFactory#createMBeanServer()} or{@link MBeanServerFactory#createMBeanServer(java.lang.String)}.
 * <dt>findMBeanServer</dt>
 * <dd>Find an MBeanServer with a given name, or all MBeanServers in this
 * JVM, using the method {@link MBeanServerFactory#findMBeanServer}.
 * <dt>newMBeanServer</dt>
 * <dd>Create a new MBeanServer object without keeping a reference to it,
 * using the method {@link MBeanServerFactory#newMBeanServer()} or{@link MBeanServerFactory#newMBeanServer(java.lang.String)}.
 * <dt>releaseMBeanServer</dt>
 * <dd>Remove the MBeanServerFactory's reference to an MBeanServer,
 * using the method {@link MBeanServerFactory#releaseMBeanServer}.
 * </dl>
 * The <em>name</em> of the permission can also denote a list of one or more
 * comma-separated operations.  Spaces are allowed at the beginning and
 * end of the <em>name</em> and before and after commas.
 * <p>
 * <code>MBeanServerPermission("createMBeanServer")</code> implies
 * <code>MBeanServerPermission("newMBeanServer")</code>.
 * @since 1.5
 */
public class MBeanServerPermission extends BasicPermission {
  private static final long serialVersionUID=-5661980843569388590L;
  private final static int CREATE=0, FIND=1, NEW=2, RELEASE=3, N_NAMES=4;
  private final static String[] names={"createMBeanServer","findMBeanServer","newMBeanServer","releaseMBeanServer"};
  private final static int CREATE_MASK=1 << CREATE, FIND_MASK=1 << FIND, NEW_MASK=1 << NEW, RELEASE_MASK=1 << RELEASE, ALL_MASK=CREATE_MASK | FIND_MASK | NEW_MASK| RELEASE_MASK;
  private final static String[] canonicalNames=new String[1 << N_NAMES];
  transient int mask;
  /** 
 * <p>Create a new MBeanServerPermission with the given name.</p>
 * <p>This constructor is equivalent to
 * <code>MBeanServerPermission(name,null)</code>.</p>
 * @param name the name of the granted permission.  It must
 * respect the constraints spelt out in the description of the{@link MBeanServerPermission} class.
 * @exception NullPointerException if the name is null.
 * @exception IllegalArgumentException if the name is not
 * <code>*</code> or one of the allowed names or a comma-separated
 * list of the allowed names.
 */
  public MBeanServerPermission(  String name){
    this(name,null);
  }
  /** 
 * <p>Create a new MBeanServerPermission with the given name.</p>
 * @param name the name of the granted permission.  It must
 * respect the constraints spelt out in the description of the{@link MBeanServerPermission} class.
 * @param actions the associated actions.  This parameter is not
 * currently used and must be null or the empty string.
 * @exception NullPointerException if the name is null.
 * @exception IllegalArgumentException if the name is not
 * <code>*</code> or one of the allowed names or a comma-separated
 * list of the allowed names, or if <code>actions</code> is a non-null
 * non-empty string.
 * @throws NullPointerException if <code>name</code> is <code>null</code>.
 * @throws IllegalArgumentException if <code>name</code> is empty or
 * if arguments are invalid.
 */
  public MBeanServerPermission(  String name,  String actions){
    super(getCanonicalName(parseMask(name)),actions);
    mask=parseMask(name);
    if (actions != null && actions.length() > 0)     throw new IllegalArgumentException("MBeanServerPermission " + "actions must be null: " + actions);
  }
  MBeanServerPermission(  int mask){
    super(getCanonicalName(mask));
    this.mask=impliedMask(mask);
  }
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    mask=parseMask(getName());
  }
  static int simplifyMask(  int mask){
    if ((mask & CREATE_MASK) != 0)     mask&=~NEW_MASK;
    return mask;
  }
  static int impliedMask(  int mask){
    if ((mask & CREATE_MASK) != 0)     mask|=NEW_MASK;
    return mask;
  }
  static String getCanonicalName(  int mask){
    if (mask == ALL_MASK)     return "*";
    mask=simplifyMask(mask);
synchronized (canonicalNames) {
      if (canonicalNames[mask] == null)       canonicalNames[mask]=makeCanonicalName(mask);
    }
    return canonicalNames[mask];
  }
  private static String makeCanonicalName(  int mask){
    final StringBuilder buf=new StringBuilder();
    for (int i=0; i < N_NAMES; i++) {
      if ((mask & (1 << i)) != 0) {
        if (buf.length() > 0)         buf.append(',');
        buf.append(names[i]);
      }
    }
    return buf.toString().intern();
  }
  private static int parseMask(  String name){
    if (name == null) {
      throw new NullPointerException("MBeanServerPermission: " + "target name can't be null");
    }
    name=name.trim();
    if (name.equals("*"))     return ALL_MASK;
    if (name.indexOf(',') < 0)     return impliedMask(1 << nameIndex(name.trim()));
    int mask=0;
    StringTokenizer tok=new StringTokenizer(name,",");
    while (tok.hasMoreTokens()) {
      String action=tok.nextToken();
      int i=nameIndex(action.trim());
      mask|=(1 << i);
    }
    return impliedMask(mask);
  }
  private static int nameIndex(  String name) throws IllegalArgumentException {
    for (int i=0; i < N_NAMES; i++) {
      if (names[i].equals(name))       return i;
    }
    final String msg="Invalid MBeanServerPermission name: \"" + name + "\"";
    throw new IllegalArgumentException(msg);
  }
  public int hashCode(){
    return mask;
  }
  /** 
 * <p>Checks if this MBeanServerPermission object "implies" the specified
 * permission.</p>
 * <p>More specifically, this method returns true if:</p>
 * <ul>
 * <li> <i>p</i> is an instance of MBeanServerPermission,</li>
 * <li> <i>p</i>'s target names are a subset of this object's target
 * names</li>
 * </ul>
 * <p>The <code>createMBeanServer</code> permission implies the
 * <code>newMBeanServer</code> permission.</p>
 * @param p the permission to check against.
 * @return true if the specified permission is implied by this object,
 * false if not.
 */
  public boolean implies(  Permission p){
    if (!(p instanceof MBeanServerPermission))     return false;
    MBeanServerPermission that=(MBeanServerPermission)p;
    return ((this.mask & that.mask) == that.mask);
  }
  /** 
 * Checks two MBeanServerPermission objects for equality. Checks that
 * <i>obj</i> is an MBeanServerPermission, and represents the same
 * list of allowable actions as this object.
 * <P>
 * @param obj the object we are testing for equality with this object.
 * @return true if the objects are equal.
 */
  public boolean equals(  Object obj){
    if (obj == this)     return true;
    if (!(obj instanceof MBeanServerPermission))     return false;
    MBeanServerPermission that=(MBeanServerPermission)obj;
    return (this.mask == that.mask);
  }
  public PermissionCollection newPermissionCollection(){
    return new MBeanServerPermissionCollection();
  }
}
/** 
 * Class returned by {@link MBeanServerPermission#newPermissionCollection()}.
 * @serial include
 */
class MBeanServerPermissionCollection extends PermissionCollection {
  /** 
 * @serial Null if no permissions in collection, otherwise a
 * single permission that is the union of all permissions that
 * have been added.  
 */
  private MBeanServerPermission collectionPermission;
  private static final long serialVersionUID=-5661980843569388590L;
  public synchronized void add(  Permission permission){
    if (!(permission instanceof MBeanServerPermission)) {
      final String msg="Permission not an MBeanServerPermission: " + permission;
      throw new IllegalArgumentException(msg);
    }
    if (isReadOnly())     throw new SecurityException("Read-only permission collection");
    MBeanServerPermission mbsp=(MBeanServerPermission)permission;
    if (collectionPermission == null)     collectionPermission=mbsp;
 else     if (!collectionPermission.implies(permission)) {
      int newmask=collectionPermission.mask | mbsp.mask;
      collectionPermission=new MBeanServerPermission(newmask);
    }
  }
  public synchronized boolean implies(  Permission permission){
    return (collectionPermission != null && collectionPermission.implies(permission));
  }
  public synchronized Enumeration<Permission> elements(){
    Set<Permission> set;
    if (collectionPermission == null)     set=Collections.emptySet();
 else     set=Collections.singleton((Permission)collectionPermission);
    return Collections.enumeration(set);
  }
}