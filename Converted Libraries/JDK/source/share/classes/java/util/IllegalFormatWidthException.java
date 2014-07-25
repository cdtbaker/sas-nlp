package java.util;
/** 
 * Unchecked exception thrown when the format width is a negative value other
 * than <tt>-1</tt> or is otherwise unsupported.
 * @since 1.5
 */
public class IllegalFormatWidthException extends IllegalFormatException {
  private static final long serialVersionUID=16660902L;
  private int w;
  /** 
 * Constructs an instance of this class with the specified width.
 * @param wThe width
 */
  public IllegalFormatWidthException(  int w){
    this.w=w;
  }
  /** 
 * Returns the width
 * @return  The width
 */
  public int getWidth(){
    return w;
  }
  public String getMessage(){
    return Integer.toString(w);
  }
}
