package com.sun.jdi;
/** 
 * Thrown to indicate that the requested operation cannot be
 * completed because the specified object has been garbage collected.
 * @author Gordon Hirsch
 * @since  1.3
 */
public class ObjectCollectedException extends RuntimeException {
  public ObjectCollectedException(){
    super();
  }
  public ObjectCollectedException(  String s){
    super(s);
  }
}
