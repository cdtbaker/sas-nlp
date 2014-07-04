package sun.awt.X11;
/** 
 * Signals that some Xlib routine failed.
 * @since 1.5
 */
public class XException extends RuntimeException {
  public XException(){
    super();
  }
  public XException(  String message){
    super(message);
  }
}
