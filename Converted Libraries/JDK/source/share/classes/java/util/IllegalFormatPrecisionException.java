package java.util;
/** 
 * Unchecked exception thrown when the precision is a negative value other than
 * <tt>-1</tt>, the conversion does not support a precision, or the value is
 * otherwise unsupported.
 * @since 1.5
 */
public class IllegalFormatPrecisionException extends IllegalFormatException {
  private static final long serialVersionUID=18711008L;
  private int p;
  /** 
 * Constructs an instance of this class with the specified precision.
 * @param pThe precision
 */
  public IllegalFormatPrecisionException(  int p){
    this.p=p;
  }
  /** 
 * Returns the precision
 * @return  The precision
 */
  public int getPrecision(){
    return p;
  }
  public String getMessage(){
    return Integer.toString(p);
  }
}