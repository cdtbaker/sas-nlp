package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when trying to invert a singular operator.
 * @version $Id: SingularOperatorException.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class SingularOperatorException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-476049978595245033L;
  /** 
 * Creates a new instance of this class.
 */
  public SingularOperatorException(){
    super(LocalizedFormats.SINGULAR_OPERATOR);
  }
}
