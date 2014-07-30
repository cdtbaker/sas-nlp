package java.nio.file;
/** 
 * Checked exception thrown when a file system operation is denied, typically
 * due to a file permission or other access check.
 * <p> This exception is not related to the {@link java.security.AccessControlException AccessControlException} or {@link SecurityException} thrown by access controllers or security managers when
 * access to a file is denied.
 * @since 1.7
 */
public class AccessDeniedException extends FileSystemException {
  private static final long serialVersionUID=4943049599949219617L;
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known
 */
  public AccessDeniedException(  String file){
    super(file);
  }
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known
 * @param othera string identifying the other file or {@code null} if not known
 * @param reasona reason message with additional information or {@code null}
 */
  public AccessDeniedException(  String file,  String other,  String reason){
    super(file,other,reason);
  }
}