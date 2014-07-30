package java.lang;
/** 
 * Thrown to indicate that the Java Virtual Machine is broken or has
 * run out of resources necessary for it to continue operating.
 * @author  Frank Yellin
 * @since   JDK1.0
 */
abstract public class VirtualMachineError extends Error {
  /** 
 * Constructs a <code>VirtualMachineError</code> with no detail message.
 */
  public VirtualMachineError(){
    super();
  }
  /** 
 * Constructs a <code>VirtualMachineError</code> with the specified
 * detail message.
 * @param s   the detail message.
 */
  public VirtualMachineError(  String s){
    super(s);
  }
}