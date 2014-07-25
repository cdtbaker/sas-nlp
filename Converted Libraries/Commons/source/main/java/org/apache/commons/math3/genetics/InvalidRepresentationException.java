package org.apache.commons.math3.genetics;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
/** 
 * Exception indicating that the representation of a chromosome is not valid.
 * @version $Id: InvalidRepresentationException.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public class InvalidRepresentationException extends MathIllegalArgumentException {
  /** 
 * Serialization version id 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Construct an InvalidRepresentationException with a specialized message.
 * @param pattern Message pattern.
 * @param args Arguments.
 */
  public InvalidRepresentationException(  Localizable pattern,  Object... args){
    super(pattern,args);
  }
}
