package javax.management;
/** 
 * The specified attribute does not exist or cannot be retrieved.
 * @since 1.5
 */
public class AttributeNotFoundException extends OperationsException {
  private static final long serialVersionUID=6511584241791106926L;
  /** 
 * Default constructor.
 */
  public AttributeNotFoundException(){
    super();
  }
  /** 
 * Constructor that allows a specific error message to be specified.
 * @param message detail message.
 */
  public AttributeNotFoundException(  String message){
    super(message);
  }
}