package javax.management.relation;
/** 
 * This exception is raised when a role in a relation does not exist, or is not
 * readable, or is not settable.
 * @since 1.5
 */
public class RoleNotFoundException extends RelationException {
  private static final long serialVersionUID=-2986406101364031481L;
  /** 
 * Default constructor, no message put in exception.
 */
  public RoleNotFoundException(){
    super();
  }
  /** 
 * Constructor with given message put in exception.
 * @param message the detail message.
 */
  public RoleNotFoundException(  String message){
    super(message);
  }
}