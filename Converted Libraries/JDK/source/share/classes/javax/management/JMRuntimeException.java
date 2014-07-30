package javax.management;
/** 
 * Runtime exceptions emitted by JMX implementations.
 * @since 1.5
 */
public class JMRuntimeException extends RuntimeException {
  private static final long serialVersionUID=6573344628407841861L;
  /** 
 * Default constructor.
 */
  public JMRuntimeException(){
    super();
  }
  /** 
 * Constructor that allows a specific error message to be specified.
 * @param message the detail message.
 */
  public JMRuntimeException(  String message){
    super(message);
  }
  /** 
 * Constructor with a nested exception.  This constructor is
 * package-private because it arrived too late for the JMX 1.2
 * specification.  A later version may make it public.
 */
  JMRuntimeException(  String message,  Throwable cause){
    super(message,cause);
  }
}