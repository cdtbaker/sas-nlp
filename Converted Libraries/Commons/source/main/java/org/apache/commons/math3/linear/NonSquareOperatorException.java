package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a square linear operator is expected.
 * @since 3.0
 * @version $Id: NonSquareOperatorException.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class NonSquareOperatorException extends DimensionMismatchException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-4145007524150846242L;
  /** 
 * Construct an exception from the mismatched dimensions.
 * @param wrong Row dimension.
 * @param expected Column dimension.
 */
  public NonSquareOperatorException(  int wrong,  int expected){
    super(LocalizedFormats.NON_SQUARE_OPERATOR,wrong,expected);
  }
}
