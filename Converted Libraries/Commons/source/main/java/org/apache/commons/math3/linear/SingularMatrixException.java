package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a non-singular matrix is expected.
 * @since 3.0
 * @version $Id: SingularMatrixException.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class SingularMatrixException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-4206514844735401070L;
  /** 
 * Construct an exception.
 */
  public SingularMatrixException(){
    super(LocalizedFormats.SINGULAR_MATRIX);
  }
}
