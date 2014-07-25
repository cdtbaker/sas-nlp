package org.apache.commons.math3.exception;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.util.Localizable;
/** 
 * Exception to be thrown when two dimensions differ.
 * @since 2.2
 * @version $Id: DimensionMismatchException.java 1364378 2012-07-22 17:42:38Z tn $
 */
public class DimensionMismatchException extends MathIllegalNumberException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-8415396756375798143L;
  /** 
 * Correct dimension. 
 */
  private final int dimension;
  /** 
 * Construct an exception from the mismatched dimensions.
 * @param specific Specific context information pattern.
 * @param wrong Wrong dimension.
 * @param expected Expected dimension.
 */
  public DimensionMismatchException(  Localizable specific,  int wrong,  int expected){
    super(specific,wrong,expected);
    dimension=expected;
  }
  /** 
 * Construct an exception from the mismatched dimensions.
 * @param wrong Wrong dimension.
 * @param expected Expected dimension.
 */
  public DimensionMismatchException(  int wrong,  int expected){
    this(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE,wrong,expected);
  }
  /** 
 * @return the expected dimension.
 */
  public int getDimension(){
    return dimension;
  }
}
