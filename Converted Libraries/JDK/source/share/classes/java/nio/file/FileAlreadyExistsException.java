package java.nio.file;
/** 
 * Checked exception thrown when an attempt is made to create a file or
 * directory and a file of that name already exists.
 * @since 1.7
 */
public class FileAlreadyExistsException extends FileSystemException {
  static final long serialVersionUID=7579540934498831181L;
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known
 */
  public FileAlreadyExistsException(  String file){
    super(file);
  }
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known
 * @param othera string identifying the other file or {@code null} if not known
 * @param reasona reason message with additional information or {@code null}
 */
  public FileAlreadyExistsException(  String file,  String other,  String reason){
    super(file,other,reason);
  }
}
