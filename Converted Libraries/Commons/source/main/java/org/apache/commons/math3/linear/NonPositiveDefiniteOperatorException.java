package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a symmetric, definite positive{@link RealLinearOperator} is expected.
 * Since the coefficients of the matrix are not accessible, the most
 * general definition is used to check that {@code A} is not positive
 * definite, i.e.  there exists {@code x} such that {@code x' A x <= 0}.
 * In the terminology of this exception, {@code A} is the "offending"
 * linear operator and {@code x} the "offending" vector.
 * @version $Id: NonPositiveDefiniteOperatorException.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class NonPositiveDefiniteOperatorException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=917034489420549847L;
  /** 
 * Creates a new instance of this class. 
 */
  public NonPositiveDefiniteOperatorException(){
    super(LocalizedFormats.NON_POSITIVE_DEFINITE_OPERATOR);
  }
}
