package org.apache.commons.math3.exception;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a number is not finite.
 * @since 3.0
 * @version $Id: NotFiniteNumberException.java 1364378 2012-07-22 17:42:38Z tn $
 */
public class NotFiniteNumberException extends MathIllegalNumberException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-6100997100383932834L;
  /** 
 * Construct the exception.
 * @param wrong Value that is infinite or NaN.
 * @param args Optional arguments.
 */
  public NotFiniteNumberException(  Number wrong,  Object... args){
    this(LocalizedFormats.NOT_FINITE_NUMBER,wrong,args);
  }
  /** 
 * Construct the exception with a specific context.
 * @param specific Specific context pattern.
 * @param wrong Value that is infinite or NaN.
 * @param args Optional arguments.
 */
  public NotFiniteNumberException(  Localizable specific,  Number wrong,  Object... args){
    super(specific,wrong,args);
  }
}
