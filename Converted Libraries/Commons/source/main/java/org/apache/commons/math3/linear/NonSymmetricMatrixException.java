package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when a symmetric matrix is expected.
 * @since 3.0
 * @version $Id: NonSymmetricMatrixException.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class NonSymmetricMatrixException extends MathIllegalArgumentException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-7518495577824189882L;
  /** 
 * Row. 
 */
  private final int row;
  /** 
 * Column. 
 */
  private final int column;
  /** 
 * Threshold. 
 */
  private final double threshold;
  /** 
 * Construct an exception.
 * @param row Row index.
 * @param column Column index.
 * @param threshold Relative symmetry threshold.
 */
  public NonSymmetricMatrixException(  int row,  int column,  double threshold){
    super(LocalizedFormats.NON_SYMMETRIC_MATRIX,row,column,threshold);
    this.row=row;
    this.column=column;
    this.threshold=threshold;
  }
  /** 
 * @return the row index of the entry.
 */
  public int getRow(){
    return row;
  }
  /** 
 * @return the column index of the entry.
 */
  public int getColumn(){
    return column;
  }
  /** 
 * @return the relative symmetry threshold.
 */
  public double getThreshold(){
    return threshold;
  }
}
