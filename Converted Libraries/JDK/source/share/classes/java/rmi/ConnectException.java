package java.rmi;
/** 
 * A <code>ConnectException</code> is thrown if a connection is refused
 * to the remote host for a remote method call.
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class ConnectException extends RemoteException {
  private static final long serialVersionUID=4863550261346652506L;
  /** 
 * Constructs a <code>ConnectException</code> with the specified
 * detail message.
 * @param s the detail message
 * @since JDK1.1
 */
  public ConnectException(  String s){
    super(s);
  }
  /** 
 * Constructs a <code>ConnectException</code> with the specified
 * detail message and nested exception.
 * @param s the detail message
 * @param ex the nested exception
 * @since JDK1.1
 */
  public ConnectException(  String s,  Exception ex){
    super(s,ex);
  }
}
