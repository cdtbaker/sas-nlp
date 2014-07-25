package java.nio.charset;
/** 
 * Checked exception thrown when an input byte sequence is not legal for given
 * charset, or an input character sequence is not a legal sixteen-bit Unicode
 * sequence.
 * @since 1.4
 */
public class MalformedInputException extends CharacterCodingException {
  private static final long serialVersionUID=-3438823399834806194L;
  private int inputLength;
  public MalformedInputException(  int inputLength){
    this.inputLength=inputLength;
  }
  public int getInputLength(){
    return inputLength;
  }
  public String getMessage(){
    return "Input length = " + inputLength;
  }
}
