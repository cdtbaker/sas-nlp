package java.security;
import java.util.ArrayList;
import java.util.List;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
/** 
 * An AccessControlContext is used to make system resource access decisions
 * based on the context it encapsulates.
 * <p>More specifically, it encapsulates a context and
 * has a single method, <code>checkPermission</code>,
 * that is equivalent to the <code>checkPermission</code> method
 * in the AccessController class, with one difference: The AccessControlContext
 * <code>checkPermission</code> method makes access decisions based on the
 * context it encapsulates,
 * rather than that of the current execution thread.
 * <p>Thus, the purpose of AccessControlContext is for those situations where
 * a security check that should be made within a given context
 * actually needs to be done from within a
 * <i>different</i> context (for example, from within a worker thread).
 * <p> An AccessControlContext is created by calling the
 * <code>AccessController.getContext</code> method.
 * The <code>getContext</code> method takes a "snapshot"
 * of the current calling context, and places
 * it in an AccessControlContext object, which it returns. A sample call is
 * the following:
 * <pre>
 * AccessControlContext acc = AccessController.getContext()
 * </pre>
 * <p>
 * Code within a different context can subsequently call the
 * <code>checkPermission</code> method on the
 * previously-saved AccessControlContext object. A sample call is the
 * following:
 * <pre>
 * acc.checkPermission(permission)
 * </pre>
 * @see AccessController
 * @author Roland Schemers
 */
public final class AccessControlContext {
  private ProtectionDomain context[];
  private boolean isPrivileged;
  private AccessControlContext privilegedContext;
  private DomainCombiner combiner=null;
  private static boolean debugInit=false;
  private static Debug debug=null;
  static Debug getDebug(){
    if (debugInit)     return debug;
 else {
      if (Policy.isSet()) {
        debug=Debug.getInstance("access");
        debugInit=true;
      }
      return debug;
    }
  }
  /** 
 * Create an AccessControlContext with the given array of ProtectionDomains.
 * Context must not be null. Duplicate domains will be removed from the
 * context.
 * @param context the ProtectionDomains associated with this context.
 * The non-duplicate domains are copied from the array. Subsequent
 * changes to the array will not affect this AccessControlContext.
 * @throws NullPointerException if <code>context</code> is <code>null</code>
 */
  public AccessControlContext(  ProtectionDomain context[]){
    if (context.length == 0) {
      this.context=null;
    }
 else     if (context.length == 1) {
      if (context[0] != null) {
        this.context=context.clone();
      }
 else {
        this.context=null;
      }
    }
 else {
      List<ProtectionDomain> v=new ArrayList<>(context.length);
      for (int i=0; i < context.length; i++) {
        if ((context[i] != null) && (!v.contains(context[i])))         v.add(context[i]);
      }
      if (!v.isEmpty()) {
        this.context=new ProtectionDomain[v.size()];
        this.context=v.toArray(this.context);
      }
    }
  }
  /** 
 * Create a new <code>AccessControlContext</code> with the given
 * <code>AccessControlContext</code> and <code>DomainCombiner</code>.
 * This constructor associates the provided
 * <code>DomainCombiner</code> with the provided
 * <code>AccessControlContext</code>.
 * <p>
 * @param acc the <code>AccessControlContext</code> associated
 * with the provided <code>DomainCombiner</code>.
 * @param combiner the <code>DomainCombiner</code> to be associated
 * with the provided <code>AccessControlContext</code>.
 * @exception NullPointerException if the provided
 * <code>context</code> is <code>null</code>.
 * @exception SecurityException if a security manager is installed and the
 * caller does not have the "createAccessControlContext"{@link SecurityPermission}
 * @since 1.3
 */
  public AccessControlContext(  AccessControlContext acc,  DomainCombiner combiner){
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(SecurityConstants.CREATE_ACC_PERMISSION);
    }
    this.context=acc.context;
    this.combiner=combiner;
  }
  /** 
 * package private for AccessController
 */
  AccessControlContext(  ProtectionDomain context[],  DomainCombiner combiner){
    if (context != null) {
      this.context=context.clone();
    }
    this.combiner=combiner;
  }
  /** 
 * package private constructor for AccessController.getContext()
 */
  AccessControlContext(  ProtectionDomain context[],  boolean isPrivileged){
    this.context=context;
    this.isPrivileged=isPrivileged;
  }
  /** 
 * Constructor for JavaSecurityAccess.doIntersectionPrivilege()
 */
  AccessControlContext(  ProtectionDomain[] context,  AccessControlContext privilegedContext){
    this.context=context;
    this.privilegedContext=privilegedContext;
    this.isPrivileged=true;
  }
  /** 
 * Returns this context's context.
 */
  ProtectionDomain[] getContext(){
    return context;
  }
  /** 
 * Returns true if this context is privileged.
 */
  boolean isPrivileged(){
    return isPrivileged;
  }
  /** 
 * get the assigned combiner from the privileged or inherited context
 */
  DomainCombiner getAssignedCombiner(){
    AccessControlContext acc;
    if (isPrivileged) {
      acc=privilegedContext;
    }
 else {
      acc=AccessController.getInheritedAccessControlContext();
    }
    if (acc != null) {
      return acc.combiner;
    }
    return null;
  }
  /** 
 * Get the <code>DomainCombiner</code> associated with this
 * <code>AccessControlContext</code>.
 * <p>
 * @return the <code>DomainCombiner</code> associated with this
 * <code>AccessControlContext</code>, or <code>null</code>
 * if there is none.
 * @exception SecurityException if a security manager is installed and
 * the caller does not have the "getDomainCombiner"{@link SecurityPermission}
 * @since 1.3
 */
  public DomainCombiner getDomainCombiner(){
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(SecurityConstants.GET_COMBINER_PERMISSION);
    }
    return combiner;
  }
  /** 
 * Determines whether the access request indicated by the
 * specified permission should be allowed or denied, based on
 * the security policy currently in effect, and the context in
 * this object. The request is allowed only if every ProtectionDomain
 * in the context implies the permission. Otherwise the request is
 * denied.
 * <p>
 * This method quietly returns if the access request
 * is permitted, or throws a suitable AccessControlException otherwise.
 * @param perm the requested permission.
 * @exception AccessControlException if the specified permission
 * is not permitted, based on the current security policy and the
 * context encapsulated by this object.
 * @exception NullPointerException if the permission to check for is null.
 */
  public void checkPermission(  Permission perm) throws AccessControlException {
    boolean dumpDebug=false;
    if (perm == null) {
      throw new NullPointerException("permission can't be null");
    }
    if (getDebug() != null) {
      dumpDebug=!Debug.isOn("codebase=");
      if (!dumpDebug) {
        for (int i=0; context != null && i < context.length; i++) {
          if (context[i].getCodeSource() != null && context[i].getCodeSource().getLocation() != null && Debug.isOn("codebase=" + context[i].getCodeSource().getLocation().toString())) {
            dumpDebug=true;
            break;
          }
        }
      }
      dumpDebug&=!Debug.isOn("permission=") || Debug.isOn("permission=" + perm.getClass().getCanonicalName());
      if (dumpDebug && Debug.isOn("stack")) {
        Thread.currentThread().dumpStack();
      }
      if (dumpDebug && Debug.isOn("domain")) {
        if (context == null) {
          debug.println("domain (context is null)");
        }
 else {
          for (int i=0; i < context.length; i++) {
            debug.println("domain " + i + " "+ context[i]);
          }
        }
      }
    }
    if (context == null)     return;
    for (int i=0; i < context.length; i++) {
      if (context[i] != null && !context[i].implies(perm)) {
        if (dumpDebug) {
          debug.println("access denied " + perm);
        }
        if (Debug.isOn("failure") && debug != null) {
          if (!dumpDebug) {
            debug.println("access denied " + perm);
          }
          Thread.currentThread().dumpStack();
          final ProtectionDomain pd=context[i];
          final Debug db=debug;
          AccessController.doPrivileged(new PrivilegedAction<Void>(){
            public Void run(){
              db.println("domain that failed " + pd);
              return null;
            }
          }
);
        }
        throw new AccessControlException("access denied " + perm,perm);
      }
    }
    if (dumpDebug) {
      debug.println("access allowed " + perm);
    }
    return;
  }
  /** 
 * Take the stack-based context (this) and combine it with the
 * privileged or inherited context, if need be.
 */
  AccessControlContext optimize(){
    AccessControlContext acc;
    if (isPrivileged) {
      acc=privilegedContext;
    }
 else {
      acc=AccessController.getInheritedAccessControlContext();
    }
    boolean skipStack=(context == null);
    boolean skipAssigned=(acc == null || acc.context == null);
    if (acc != null && acc.combiner != null) {
      return goCombiner(context,acc);
    }
    if (skipAssigned && skipStack) {
      return this;
    }
    if (skipStack) {
      return acc;
    }
    int slen=context.length;
    if (skipAssigned && slen <= 2) {
      return this;
    }
    if ((slen == 1) && (context[0] == acc.context[0])) {
      return acc;
    }
    int n=(skipAssigned) ? 0 : acc.context.length;
    ProtectionDomain pd[]=new ProtectionDomain[slen + n];
    if (!skipAssigned) {
      System.arraycopy(acc.context,0,pd,0,n);
    }
    outer:     for (int i=0; i < context.length; i++) {
      ProtectionDomain sd=context[i];
      if (sd != null) {
        for (int j=0; j < n; j++) {
          if (sd == pd[j]) {
            continue outer;
          }
        }
        pd[n++]=sd;
      }
    }
    if (n != pd.length) {
      if (!skipAssigned && n == acc.context.length) {
        return acc;
      }
 else       if (skipAssigned && n == slen) {
        return this;
      }
      ProtectionDomain tmp[]=new ProtectionDomain[n];
      System.arraycopy(pd,0,tmp,0,n);
      pd=tmp;
    }
    this.context=pd;
    this.combiner=null;
    this.isPrivileged=false;
    return this;
  }
  private AccessControlContext goCombiner(  ProtectionDomain[] current,  AccessControlContext assigned){
    if (getDebug() != null) {
      debug.println("AccessControlContext invoking the Combiner");
    }
    ProtectionDomain[] combinedPds=assigned.combiner.combine(current,assigned.context);
    this.context=combinedPds;
    this.combiner=assigned.combiner;
    this.isPrivileged=false;
    return this;
  }
  /** 
 * Checks two AccessControlContext objects for equality.
 * Checks that <i>obj</i> is
 * an AccessControlContext and has the same set of ProtectionDomains
 * as this context.
 * <P>
 * @param obj the object we are testing for equality with this object.
 * @return true if <i>obj</i> is an AccessControlContext, and has the
 * same set of ProtectionDomains as this context, false otherwise.
 */
  public boolean equals(  Object obj){
    if (obj == this)     return true;
    if (!(obj instanceof AccessControlContext))     return false;
    AccessControlContext that=(AccessControlContext)obj;
    if (context == null) {
      return (that.context == null);
    }
    if (that.context == null)     return false;
    if (!(this.containsAllPDs(that) && that.containsAllPDs(this)))     return false;
    if (this.combiner == null)     return (that.combiner == null);
    if (that.combiner == null)     return false;
    if (!this.combiner.equals(that.combiner))     return false;
    return true;
  }
  private boolean containsAllPDs(  AccessControlContext that){
    boolean match=false;
    ProtectionDomain thisPd;
    for (int i=0; i < context.length; i++) {
      match=false;
      if ((thisPd=context[i]) == null) {
        for (int j=0; (j < that.context.length) && !match; j++) {
          match=(that.context[j] == null);
        }
      }
 else {
        Class thisPdClass=thisPd.getClass();
        ProtectionDomain thatPd;
        for (int j=0; (j < that.context.length) && !match; j++) {
          thatPd=that.context[j];
          match=(thatPd != null && thisPdClass == thatPd.getClass() && thisPd.equals(thatPd));
        }
      }
      if (!match)       return false;
    }
    return match;
  }
  /** 
 * Returns the hash code value for this context. The hash code
 * is computed by exclusive or-ing the hash code of all the protection
 * domains in the context together.
 * @return a hash code value for this context.
 */
  public int hashCode(){
    int hashCode=0;
    if (context == null)     return hashCode;
    for (int i=0; i < context.length; i++) {
      if (context[i] != null)       hashCode^=context[i].hashCode();
    }
    return hashCode;
  }
}
