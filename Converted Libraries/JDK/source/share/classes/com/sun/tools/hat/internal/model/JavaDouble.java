package com.sun.tools.hat.internal.model;
/** 
 * Represents a double (i.e. a double field in an instance).
 * @author      Bill Foote
 */
public class JavaDouble extends JavaValue {
  double value;
  public JavaDouble(  double value){
    this.value=value;
  }
  public String toString(){
    return Double.toString(value);
  }
}
