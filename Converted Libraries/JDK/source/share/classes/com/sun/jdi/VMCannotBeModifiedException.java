package com.sun.jdi;
/** 
 * Thrown to indicate that the operation is invalid because it would
 * modify the VM and the VM is read-only.  See {@link VirtualMachine#canBeModified()}.
 * @author Jim Holmlund
 * @since  1.5
 */
public class VMCannotBeModifiedException extends UnsupportedOperationException {
  public VMCannotBeModifiedException(){
    super();
  }
  public VMCannotBeModifiedException(  String s){
    super(s);
  }
}
