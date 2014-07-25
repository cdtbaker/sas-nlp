package java.nio.file;
/** 
 * Checked exception thrown when a file system operation fails because a file
 * is not a symbolic link.
 * @since 1.7
 */
public class NotLinkException extends FileSystemException {
  static final long serialVersionUID=-388655596416518021L;
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known
 */
  public NotLinkException(  String file){
    super(file);
  }
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known
 * @param othera string identifying the other file or {@code null} if not known
 * @param reasona reason message with additional information or {@code null}
 */
  public NotLinkException(  String file,  String other,  String reason){
    super(file,other,reason);
  }
}
