package javax.management;
/** 
 * Represents exceptions thrown in the MBean server when performing operations
 * on MBeans.
 * @since 1.5
 */
public class OperationsException extends JMException {
  private static final long serialVersionUID=-4967597595580536216L;
  /** 
 * Default constructor.
 */
  public OperationsException(){
    super();
  }
  /** 
 * Constructor that allows a specific error message to be specified.
 * @param message the detail message.
 */
  public OperationsException(  String message){
    super(message);
  }
}
