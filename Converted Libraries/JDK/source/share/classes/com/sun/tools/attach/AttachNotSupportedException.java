package com.sun.tools.attach;
import com.sun.tools.attach.spi.AttachProvider;
/** 
 * Thrown by {@link com.sun.tools.attach.VirtualMachine#attachVirtalMachine.attach} when attempting to attach to a Java virtual machine
 * for which a compatible {@link com.sun.tools.attach.spi.AttachProviderAttachProvider} does not exist. It is also thrown by {@link com.sun.tools.attach.spi.AttachProvider#attachVirtualMachineAttachProvider.attachVirtualMachine} if the provider attempts to
 * attach to a Java virtual machine with which it not comptatible.
 */
public class AttachNotSupportedException extends Exception {
  /** 
 * use serialVersionUID for interoperability 
 */
  static final long serialVersionUID=3391824968260177264L;
  /** 
 * Constructs an <code>AttachNotSupportedException</code> with
 * no detail message.
 */
  public AttachNotSupportedException(){
    super();
  }
  /** 
 * Constructs an <code>AttachNotSupportedException</code> with
 * the specified detail message.
 * @param s   the detail message.
 */
  public AttachNotSupportedException(  String s){
    super(s);
  }
}
