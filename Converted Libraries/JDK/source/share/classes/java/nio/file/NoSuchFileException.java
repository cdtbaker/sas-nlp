package java.nio.file;
/** 
 * Checked exception thrown when an attempt is made to access a file that does
 * not exist.
 * @since 1.7
 */
public class NoSuchFileException extends FileSystemException {
  static final long serialVersionUID=-1390291775875351931L;
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known.
 */
  public NoSuchFileException(  String file){
    super(file);
  }
  /** 
 * Constructs an instance of this class.
 * @param filea string identifying the file or {@code null} if not known.
 * @param othera string identifying the other file or {@code null} if not known.
 * @param reasona reason message with additional information or {@code null}
 */
  public NoSuchFileException(  String file,  String other,  String reason){
    super(file,other,reason);
  }
}
