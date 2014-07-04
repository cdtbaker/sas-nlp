package sun.tools.java;
import java.util.*;
/** 
 * The MethodSet structure is used to store methods for a class.
 * It maintains the invariant that it never stores two methods
 * with the same signature.  MethodSets are able to lookup
 * all methods with a given name and the unique method with a given
 * signature (name, args).
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class MethodSet {
  /** 
 * A Map containing Lists of MemberDefinitions.  The Lists
 * contain methods which share the same name.
 */
  private final Map lookupMap;
  /** 
 * The number of methods stored in the MethodSet.
 */
  private int count;
  /** 
 * Is this MethodSet currently frozen?  See freeze() for more details.
 */
  private boolean frozen;
  /** 
 * Creates a brand new MethodSet
 */
  public MethodSet(){
    frozen=false;
    lookupMap=new HashMap();
    count=0;
  }
  /** 
 * Returns the number of distinct methods stored in the MethodSet.
 */
  public int size(){
    return count;
  }
  /** 
 * Adds `method' to the MethodSet.  No method of the same signature
 * should be already defined.
 */
  public void add(  MemberDefinition method){
    if (frozen) {
      throw new CompilerError("add()");
    }
    Identifier name=method.getName();
    List methodList=(List)lookupMap.get(name);
    if (methodList == null) {
      methodList=new ArrayList();
      lookupMap.put(name,methodList);
    }
    int size=methodList.size();
    for (int i=0; i < size; i++) {
      if (((MemberDefinition)methodList.get(i)).getType().equalArguments(method.getType())) {
        throw new CompilerError("duplicate addition");
      }
    }
    methodList.add(method);
    count++;
  }
  /** 
 * Adds `method' to the MethodSet, replacing any previous definition
 * with the same signature.
 */
  public void replace(  MemberDefinition method){
    if (frozen) {
      throw new CompilerError("replace()");
    }
    Identifier name=method.getName();
    List methodList=(List)lookupMap.get(name);
    if (methodList == null) {
      methodList=new ArrayList();
      lookupMap.put(name,methodList);
    }
    int size=methodList.size();
    for (int i=0; i < size; i++) {
      if (((MemberDefinition)methodList.get(i)).getType().equalArguments(method.getType())) {
        methodList.set(i,method);
        return;
      }
    }
    methodList.add(method);
    count++;
  }
  /** 
 * If the MethodSet contains a method with the same signature
 * then lookup() returns it.  Otherwise, this method returns null.
 */
  public MemberDefinition lookupSig(  Identifier name,  Type type){
    Iterator matches=lookupName(name);
    MemberDefinition candidate;
    while (matches.hasNext()) {
      candidate=(MemberDefinition)matches.next();
      if (candidate.getType().equalArguments(type)) {
        return candidate;
      }
    }
    return null;
  }
  /** 
 * Returns an Iterator of all methods contained in the
 * MethodSet which have a given name.
 */
  public Iterator lookupName(  Identifier name){
    List methodList=(List)lookupMap.get(name);
    if (methodList == null) {
      return Collections.emptyIterator();
    }
    return methodList.iterator();
  }
  /** 
 * Returns an Iterator of all methods in the MethodSet
 */
  public Iterator iterator(){
class MethodIterator implements Iterator {
      Iterator hashIter=lookupMap.values().iterator();
      Iterator listIter=Collections.emptyIterator();
      public boolean hasNext(){
        if (listIter.hasNext()) {
          return true;
        }
 else {
          if (hashIter.hasNext()) {
            listIter=((List)hashIter.next()).iterator();
            if (listIter.hasNext()) {
              return true;
            }
 else {
              throw new CompilerError("iterator() in MethodSet");
            }
          }
        }
        return false;
      }
      public Object next(){
        return listIter.next();
      }
      public void remove(){
        throw new UnsupportedOperationException();
      }
    }
    return new MethodIterator();
  }
  /** 
 * After freeze() is called, the MethodSet becomes (mostly)
 * immutable.  Any calls to add() or addMeet() lead to
 * CompilerErrors.  Note that the entries themselves are still
 * (unfortunately) open for mischievous and wanton modification.
 */
  public void freeze(){
    frozen=true;
  }
  /** 
 * Tells whether freeze() has been called on this MethodSet.
 */
  public boolean isFrozen(){
    return frozen;
  }
  /** 
 * Returns a (big) string representation of this MethodSet
 */
  public String toString(){
    int len=size();
    StringBuffer buf=new StringBuffer();
    Iterator all=iterator();
    buf.append("{");
    while (all.hasNext()) {
      buf.append(all.next().toString());
      len--;
      if (len > 0) {
        buf.append(", ");
      }
    }
    buf.append("}");
    return buf.toString();
  }
}
