/** 
 * Generic PKCS Parsing exception.
 * @author Benjamin Renaud
 */
package sun.security.pkcs;
import java.io.IOException;
public class ParsingException extends IOException {
  private static final long serialVersionUID=-6316569918966181883L;
  public ParsingException(){
    super();
  }
  public ParsingException(  String s){
    super(s);
  }
}
