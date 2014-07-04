package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.util.ExceptionContext;
/** 
 * Exception to be thrown when a positive definite matrix is expected.
 * @since 3.0
 * @version $Id: NonPositiveDefiniteMatrixException.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class NonPositiveDefiniteMatrixException extends NumberIsTooSmallException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=1641613838113738061L;
  /** 
 * Index (diagonal element). 
 */
  private final int index;
  /** 
 * Threshold. 
 */
  private final double threshold;
  /** 
 * Construct an exception.
 * @param wrong Value that fails the positivity check.
 * @param index Row (and column) index.
 * @param threshold Absolute positivity threshold.
 */
  public NonPositiveDefiniteMatrixException(  double wrong,  int index,  double threshold){
    super(wrong,threshold,false);
    this.index=index;
    this.threshold=threshold;
    final ExceptionContext context=getContext();
    context.addMessage(LocalizedFormats.NOT_POSITIVE_DEFINITE_MATRIX);
    context.addMessage(LocalizedFormats.ARRAY_ELEMENT,wrong,index);
  }
  /** 
 * @return the row index.
 */
  public int getRow(){
    return index;
  }
  /** 
 * @return the column index.
 */
  public int getColumn(){
    return index;
  }
  /** 
 * @return the absolute positivity threshold.
 */
  public double getThreshold(){
    return threshold;
  }
}
