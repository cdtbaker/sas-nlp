package javax.management;
/** 
 * The value specified is not valid for the attribute.
 * @since 1.5
 */
public class InvalidAttributeValueException extends OperationsException {
  private static final long serialVersionUID=2164571879317142449L;
  /** 
 * Default constructor.
 */
  public InvalidAttributeValueException(){
    super();
  }
  /** 
 * Constructor that allows a specific error message to be specified.
 * @param message the detail message.
 */
  public InvalidAttributeValueException(  String message){
    super(message);
  }
}
