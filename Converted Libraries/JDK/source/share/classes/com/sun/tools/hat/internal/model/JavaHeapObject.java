package com.sun.tools.hat.internal.model;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import com.sun.tools.hat.internal.util.Misc;
/** 
 * Represents an object that's allocated out of the Java heap.  It occupies
 * memory in the VM, and is the sort of thing that in a JDK 1.1 VM had
 * a handle.  It can be a
 * JavaClass, a JavaObjectArray, a JavaValueArray or a JavaObject.
 */
public abstract class JavaHeapObject extends JavaThing {
  private JavaThing[] referers=null;
  private int referersLen=0;
  public abstract JavaClass getClazz();
  public abstract int getSize();
  public abstract long getId();
  /** 
 * Do any initialization this thing needs after its data is read in.
 * Subclasses that override this should call super.resolve().
 */
  public void resolve(  Snapshot snapshot){
    StackTrace trace=snapshot.getSiteTrace(this);
    if (trace != null) {
      trace.resolve(snapshot);
    }
  }
  void setupReferers(){
    if (referersLen > 1) {
      Map<JavaThing,JavaThing> map=new HashMap<JavaThing,JavaThing>();
      for (int i=0; i < referersLen; i++) {
        if (map.get(referers[i]) == null) {
          map.put(referers[i],referers[i]);
        }
      }
      referers=new JavaThing[map.size()];
      map.keySet().toArray(referers);
    }
    referersLen=-1;
  }
  /** 
 * @return the id of this thing as hex string
 */
  public String getIdString(){
    return Misc.toHex(getId());
  }
  public String toString(){
    return getClazz().getName() + "@" + getIdString();
  }
  /** 
 * @return the StackTrace of the point of allocation of this object,
 * or null if unknown
 */
  public StackTrace getAllocatedFrom(){
    return getClazz().getSiteTrace(this);
  }
  public boolean isNew(){
    return getClazz().isNew(this);
  }
  void setNew(  boolean flag){
    getClazz().setNew(this,flag);
  }
  /** 
 * Tell the visitor about all of the objects we refer to
 */
  public void visitReferencedObjects(  JavaHeapObjectVisitor v){
    v.visit(getClazz());
  }
  void addReferenceFrom(  JavaHeapObject other){
    if (referersLen == 0) {
      referers=new JavaThing[1];
    }
 else     if (referersLen == referers.length) {
      JavaThing[] copy=new JavaThing[(3 * (referersLen + 1)) / 2];
      System.arraycopy(referers,0,copy,0,referersLen);
      referers=copy;
    }
    referers[referersLen++]=other;
  }
  void addReferenceFromRoot(  Root r){
    getClazz().addReferenceFromRoot(r,this);
  }
  /** 
 * If the rootset includes this object, return a Root describing one
 * of the reasons why.
 */
  public Root getRoot(){
    return getClazz().getRoot(this);
  }
  /** 
 * Tell who refers to us.
 * @return an Enumeration of JavaHeapObject instances
 */
  public Enumeration getReferers(){
    if (referersLen != -1) {
      throw new RuntimeException("not resolved: " + getIdString());
    }
    return new Enumeration(){
      private int num=0;
      public boolean hasMoreElements(){
        return referers != null && num < referers.length;
      }
      public Object nextElement(){
        return referers[num++];
      }
    }
;
  }
  /** 
 * Given other, which the caller promises is in referers, determines if
 * the reference is only a weak reference.
 */
  public boolean refersOnlyWeaklyTo(  Snapshot ss,  JavaThing other){
    return false;
  }
  /** 
 * Describe the reference that this thing has to target.  This will only
 * be called if target is in the array returned by getChildrenForRootset.
 */
  public String describeReferenceTo(  JavaThing target,  Snapshot ss){
    return "??";
  }
  public boolean isHeapAllocated(){
    return true;
  }
}
