package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a square matrix is expected.
 * @since 3.0
 * @version $Id: NonSquareMatrixException.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class NonSquareMatrixException extends DimensionMismatchException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-660069396594485772L;
  /** 
 * Construct an exception from the mismatched dimensions.
 * @param wrong Row dimension.
 * @param expected Column dimension.
 */
  public NonSquareMatrixException(  int wrong,  int expected){
    super(LocalizedFormats.NON_SQUARE_MATRIX,wrong,expected);
  }
}
