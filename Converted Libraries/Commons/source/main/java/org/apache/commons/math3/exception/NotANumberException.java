package org.apache.commons.math3.exception;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a number is not a number.
 * @since 3.1
 * @version $Id: NotANumberException.java 1422195 2012-12-15 06:45:18Z psteitz $
 */
public class NotANumberException extends MathIllegalNumberException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=20120906L;
  /** 
 * Construct the exception.
 */
  public NotANumberException(){
    super(LocalizedFormats.NAN_NOT_ALLOWED,Double.NaN);
  }
}
