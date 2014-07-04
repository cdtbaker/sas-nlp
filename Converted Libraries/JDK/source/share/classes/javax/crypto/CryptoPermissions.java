package javax.crypto;
import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.io.Serializable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
/** 
 * This class contains CryptoPermission objects, organized into
 * PermissionCollections according to algorithm names.
 * <p>When the <code>add</code> method is called to add a
 * CryptoPermission, the CryptoPermission is stored in the
 * appropriate PermissionCollection. If no such
 * collection exists yet, the algorithm name associated with
 * the CryptoPermission object is
 * determined and the <code>newPermissionCollection</code> method
 * is called on the CryptoPermission or CryptoAllPermission class to
 * create the PermissionCollection and add it to the Permissions object.
 * @see javax.crypto.CryptoPermission
 * @see java.security.PermissionCollection
 * @see java.security.Permissions
 * @author Sharon Liu
 * @since 1.4
 */
final class CryptoPermissions extends PermissionCollection implements Serializable {
  private static final long serialVersionUID=4946547168093391015L;
  private Hashtable perms;
  /** 
 * Creates a new CryptoPermissions object containing
 * no CryptoPermissionCollections.
 */
  CryptoPermissions(){
    perms=new Hashtable(7);
  }
  /** 
 * Populates the crypto policy from the specified
 * InputStream into this CryptoPermissions object.
 * @param in the InputStream to load from.
 * @exception SecurityException if cannot load
 * successfully.
 */
  void load(  InputStream in) throws IOException, CryptoPolicyParser.ParsingException {
    CryptoPolicyParser parser=new CryptoPolicyParser();
    parser.read(new BufferedReader(new InputStreamReader(in,"UTF-8")));
    CryptoPermission[] parsingResult=parser.getPermissions();
    for (int i=0; i < parsingResult.length; i++) {
      this.add(parsingResult[i]);
    }
  }
  /** 
 * Returns true if this CryptoPermissions object doesn't
 * contain any CryptoPermission objects; otherwise, returns
 * false.
 */
  boolean isEmpty(){
    return perms.isEmpty();
  }
  /** 
 * Adds a permission object to the PermissionCollection for the
 * algorithm returned by
 * <code>(CryptoPermission)permission.getAlgorithm()</code>.
 * This method creates
 * a new PermissionCollection object (and adds the permission to it)
 * if an appropriate collection does not yet exist. <p>
 * @param permission the Permission object to add.
 * @exception SecurityException if this CryptoPermissions object is
 * marked as readonly.
 * @see isReadOnly
 */
  public void add(  Permission permission){
    if (isReadOnly())     throw new SecurityException("Attempt to add a Permission " + "to a readonly CryptoPermissions " + "object");
    if (!(permission instanceof CryptoPermission))     return;
    CryptoPermission cryptoPerm=(CryptoPermission)permission;
    PermissionCollection pc=getPermissionCollection(cryptoPerm);
    pc.add(cryptoPerm);
    String alg=cryptoPerm.getAlgorithm();
    if (!perms.containsKey(alg)) {
      perms.put(alg,pc);
    }
  }
  /** 
 * Checks if this object's PermissionCollection for permissons
 * of the specified permission's algorithm implies the specified
 * permission. Returns true if the checking succeeded.
 * @param permission the Permission object to check.
 * @return true if "permission" is implied by the permissions
 * in the PermissionCollection it belongs to, false if not.
 */
  public boolean implies(  Permission permission){
    if (!(permission instanceof CryptoPermission)) {
      return false;
    }
    CryptoPermission cryptoPerm=(CryptoPermission)permission;
    PermissionCollection pc=getPermissionCollection(cryptoPerm.getAlgorithm());
    return pc.implies(cryptoPerm);
  }
  /** 
 * Returns an enumeration of all the Permission objects in all the
 * PermissionCollections in this CryptoPermissions object.
 * @return an enumeration of all the Permissions.
 */
  public Enumeration elements(){
    return new PermissionsEnumerator(perms.elements());
  }
  /** 
 * Returns a CryptoPermissions object which
 * represents the minimum of the specified
 * CryptoPermissions object and this
 * CryptoPermissions object.
 * @param other the CryptoPermission
 * object to compare with this object.
 */
  CryptoPermissions getMinimum(  CryptoPermissions other){
    if (other == null) {
      return null;
    }
    if (this.perms.containsKey(CryptoAllPermission.ALG_NAME)) {
      return other;
    }
    if (other.perms.containsKey(CryptoAllPermission.ALG_NAME)) {
      return this;
    }
    CryptoPermissions ret=new CryptoPermissions();
    PermissionCollection thatWildcard=(PermissionCollection)other.perms.get(CryptoPermission.ALG_NAME_WILDCARD);
    int maxKeySize=0;
    if (thatWildcard != null) {
      maxKeySize=((CryptoPermission)thatWildcard.elements().nextElement()).getMaxKeySize();
    }
    Enumeration thisKeys=this.perms.keys();
    while (thisKeys.hasMoreElements()) {
      String alg=(String)thisKeys.nextElement();
      PermissionCollection thisPc=(PermissionCollection)this.perms.get(alg);
      PermissionCollection thatPc=(PermissionCollection)other.perms.get(alg);
      CryptoPermission[] partialResult;
      if (thatPc == null) {
        if (thatWildcard == null) {
          continue;
        }
        partialResult=getMinimum(maxKeySize,thisPc);
      }
 else {
        partialResult=getMinimum(thisPc,thatPc);
      }
      for (int i=0; i < partialResult.length; i++) {
        ret.add(partialResult[i]);
      }
    }
    PermissionCollection thisWildcard=(PermissionCollection)this.perms.get(CryptoPermission.ALG_NAME_WILDCARD);
    if (thisWildcard == null) {
      return ret;
    }
    maxKeySize=((CryptoPermission)thisWildcard.elements().nextElement()).getMaxKeySize();
    Enumeration thatKeys=other.perms.keys();
    while (thatKeys.hasMoreElements()) {
      String alg=(String)thatKeys.nextElement();
      if (this.perms.containsKey(alg)) {
        continue;
      }
      PermissionCollection thatPc=(PermissionCollection)other.perms.get(alg);
      CryptoPermission[] partialResult;
      partialResult=getMinimum(maxKeySize,thatPc);
      for (int i=0; i < partialResult.length; i++) {
        ret.add(partialResult[i]);
      }
    }
    return ret;
  }
  /** 
 * Get the minimum of the two given PermissionCollection
 * <code>thisPc</code> and <code>thatPc</code>.
 * @param thisPc the first given PermissionColloection
 * object.
 * @param thatPc the second given PermissionCollection
 * object.
 */
  private CryptoPermission[] getMinimum(  PermissionCollection thisPc,  PermissionCollection thatPc){
    Vector permVector=new Vector(2);
    Enumeration thisPcPermissions=thisPc.elements();
    while (thisPcPermissions.hasMoreElements()) {
      CryptoPermission thisCp=(CryptoPermission)thisPcPermissions.nextElement();
      Enumeration thatPcPermissions=thatPc.elements();
      while (thatPcPermissions.hasMoreElements()) {
        CryptoPermission thatCp=(CryptoPermission)thatPcPermissions.nextElement();
        if (thatCp.implies(thisCp)) {
          permVector.addElement(thisCp);
          break;
        }
        if (thisCp.implies(thatCp)) {
          permVector.addElement(thatCp);
        }
      }
    }
    CryptoPermission[] ret=new CryptoPermission[permVector.size()];
    permVector.copyInto(ret);
    return ret;
  }
  /** 
 * Returns all the CryptoPermission objects in the given
 * PermissionCollection object
 * whose maximum keysize no greater than <code>maxKeySize</code>.
 * For all CryptoPermission objects with a maximum keysize greater
 * than <code>maxKeySize</code>, this method constructs a
 * corresponding CryptoPermission object whose maximum keysize is
 * set to <code>maxKeySize</code>, and includes that in the result.
 * @param maxKeySize the given maximum key size.
 * @param pc the given PermissionCollection object.
 */
  private CryptoPermission[] getMinimum(  int maxKeySize,  PermissionCollection pc){
    Vector permVector=new Vector(1);
    Enumeration enum_=pc.elements();
    while (enum_.hasMoreElements()) {
      CryptoPermission cp=(CryptoPermission)enum_.nextElement();
      if (cp.getMaxKeySize() <= maxKeySize) {
        permVector.addElement(cp);
      }
 else {
        if (cp.getCheckParam()) {
          permVector.addElement(new CryptoPermission(cp.getAlgorithm(),maxKeySize,cp.getAlgorithmParameterSpec(),cp.getExemptionMechanism()));
        }
 else {
          permVector.addElement(new CryptoPermission(cp.getAlgorithm(),maxKeySize,cp.getExemptionMechanism()));
        }
      }
    }
    CryptoPermission[] ret=new CryptoPermission[permVector.size()];
    permVector.copyInto(ret);
    return ret;
  }
  /** 
 * Returns the PermissionCollection for the
 * specified algorithm. Returns null if there
 * isn't such a PermissionCollection.
 * @param alg the algorithm name.
 */
  PermissionCollection getPermissionCollection(  String alg){
    if (perms.containsKey(CryptoAllPermission.ALG_NAME)) {
      return (PermissionCollection)(perms.get(CryptoAllPermission.ALG_NAME));
    }
    PermissionCollection pc=(PermissionCollection)perms.get(alg);
    if (pc == null) {
      pc=(PermissionCollection)perms.get(CryptoPermission.ALG_NAME_WILDCARD);
    }
    return pc;
  }
  /** 
 * Returns the PermissionCollection for the algorithm
 * associated with the specified CryptoPermission
 * object. Creates such a PermissionCollection
 * if such a PermissionCollection does not
 * exist yet.
 * @param cryptoPerm the CryptoPermission object.
 */
  private PermissionCollection getPermissionCollection(  CryptoPermission cryptoPerm){
    String alg=cryptoPerm.getAlgorithm();
    PermissionCollection pc=(PermissionCollection)perms.get(alg);
    if (pc == null) {
      pc=cryptoPerm.newPermissionCollection();
    }
    return pc;
  }
}
final class PermissionsEnumerator implements Enumeration {
  private Enumeration perms;
  private Enumeration permset;
  PermissionsEnumerator(  Enumeration e){
    perms=e;
    permset=getNextEnumWithMore();
  }
  public synchronized boolean hasMoreElements(){
    if (permset == null)     return false;
    if (permset.hasMoreElements())     return true;
    permset=getNextEnumWithMore();
    return (permset != null);
  }
  public synchronized Object nextElement(){
    if (hasMoreElements()) {
      return permset.nextElement();
    }
 else {
      throw new NoSuchElementException("PermissionsEnumerator");
    }
  }
  private Enumeration getNextEnumWithMore(){
    while (perms.hasMoreElements()) {
      PermissionCollection pc=(PermissionCollection)perms.nextElement();
      Enumeration next=pc.elements();
      if (next.hasMoreElements())       return next;
    }
    return null;
  }
}
