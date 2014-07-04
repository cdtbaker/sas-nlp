package com.sun.jdi;
/** 
 * Thrown to indicate line number or variable information is not available.
 * @author Gordon Hirsch
 * @since  1.3
 */
public class AbsentInformationException extends Exception {
  public AbsentInformationException(){
    super();
  }
  public AbsentInformationException(  String s){
    super(s);
  }
}
