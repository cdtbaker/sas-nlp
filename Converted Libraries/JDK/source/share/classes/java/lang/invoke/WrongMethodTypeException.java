package java.lang.invoke;
/** 
 * Thrown to indicate that code has attempted to call a method handle
 * via the wrong method type.  As with the bytecode representation of
 * normal Java method calls, method handle calls are strongly typed
 * to a specific type descriptor associated with a call site.
 * <p>
 * This exception may also be thrown when two method handles are
 * composed, and the system detects that their types cannot be
 * matched up correctly.  This amounts to an early evaluation
 * of the type mismatch, at method handle construction time,
 * instead of when the mismatched method handle is called.
 * @author John Rose, JSR 292 EG
 * @since 1.7
 */
public class WrongMethodTypeException extends RuntimeException {
  private static final long serialVersionUID=292L;
  /** 
 * Constructs a {@code WrongMethodTypeException} with no detail message.
 */
  public WrongMethodTypeException(){
    super();
  }
  /** 
 * Constructs a {@code WrongMethodTypeException} with the specified
 * detail message.
 * @param s the detail message.
 */
  public WrongMethodTypeException(  String s){
    super(s);
  }
}