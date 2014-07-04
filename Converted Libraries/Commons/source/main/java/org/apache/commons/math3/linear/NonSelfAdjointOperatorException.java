package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a self-adjoint {@link RealLinearOperator}is expected.
 * Since the coefficients of the matrix are not accessible, the most
 * general definition is used to check that A is not self-adjoint, i.e.
 * there exist x and y such as {@code | x' A y - y' A x | >= eps},
 * where {@code eps} is a user-specified tolerance, and {@code x'}denotes the transpose of {@code x}.
 * In the terminology of this exception, {@code A} is the "offending"
 * linear operator, {@code x} and {@code y} are the first and second
 * "offending" vectors, respectively.
 * @version $Id: NonSelfAdjointOperatorException.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class NonSelfAdjointOperatorException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=1784999305030258247L;
  /** 
 * Creates a new instance of this class. 
 */
  public NonSelfAdjointOperatorException(){
    super(LocalizedFormats.NON_SELF_ADJOINT_OPERATOR);
  }
}
