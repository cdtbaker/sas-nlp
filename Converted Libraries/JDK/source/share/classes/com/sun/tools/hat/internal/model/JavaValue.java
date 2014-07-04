package com.sun.tools.hat.internal.model;
/** 
 * Abstract base class for all value types (ints, longs, floats, etc.)
 * @author      Bill Foote
 */
public abstract class JavaValue extends JavaThing {
  protected JavaValue(){
  }
  public boolean isHeapAllocated(){
    return false;
  }
  abstract public String toString();
  public int getSize(){
    return 0;
  }
}
