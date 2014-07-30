package java.rmi.server;
/** 
 * An obsolete subclass of {@link ExportException}.
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class SocketSecurityException extends ExportException {
  private static final long serialVersionUID=-7622072999407781979L;
  /** 
 * Constructs an <code>SocketSecurityException</code> with the specified
 * detail message.
 * @param s the detail message.
 * @since JDK1.1
 */
  public SocketSecurityException(  String s){
    super(s);
  }
  /** 
 * Constructs an <code>SocketSecurityException</code> with the specified
 * detail message and nested exception.
 * @param s the detail message.
 * @param ex the nested exception
 * @since JDK1.1
 */
  public SocketSecurityException(  String s,  Exception ex){
    super(s,ex);
  }
}