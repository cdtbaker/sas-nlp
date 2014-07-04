package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * An exception to be thrown when the condition number of a{@link RealLinearOperator} is too high.
 * @version $Id: IllConditionedOperatorException.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class IllConditionedOperatorException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-7883263944530490135L;
  /** 
 * Creates a new instance of this class.
 * @param cond An estimate of the condition number of the offending linear
 * operator.
 */
  public IllConditionedOperatorException(  final double cond){
    super(LocalizedFormats.ILL_CONDITIONED_OPERATOR,cond);
  }
}
