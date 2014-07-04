package com.sun.naming.internal;
/** 
 * A NamedWeakReference is a WeakReference with an immutable string name.
 * @author Scott Seligman
 */
class NamedWeakReference extends java.lang.ref.WeakReference {
  private final String name;
  NamedWeakReference(  Object referent,  String name){
    super(referent);
    this.name=name;
  }
  String getName(){
    return name;
  }
}
