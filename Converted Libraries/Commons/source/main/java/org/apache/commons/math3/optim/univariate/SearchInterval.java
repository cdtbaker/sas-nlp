package org.apache.commons.math3.optim.univariate;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
/** 
 * Search interval and (optional) start value.
 * <br/>
 * Immutable class.
 * @version $Id: SearchInterval.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class SearchInterval implements OptimizationData {
  /** 
 * Lower bound. 
 */
  private final double lower;
  /** 
 * Upper bound. 
 */
  private final double upper;
  /** 
 * Start value. 
 */
  private final double start;
  /** 
 * @param lo Lower bound.
 * @param hi Upper bound.
 * @param init Start value.
 * @throws NumberIsTooLargeException if {@code lo >= hi}.
 * @throws OutOfRangeException if {@code init < lo} or {@code init > hi}.
 */
  public SearchInterval(  double lo,  double hi,  double init){
    if (lo >= hi) {
      throw new NumberIsTooLargeException(lo,hi,false);
    }
    if (init < lo || init > hi) {
      throw new OutOfRangeException(init,lo,hi);
    }
    lower=lo;
    upper=hi;
    start=init;
  }
  /** 
 * @param lo Lower bound.
 * @param hi Upper bound.
 * @throws NumberIsTooLargeException if {@code lo >= hi}.
 */
  public SearchInterval(  double lo,  double hi){
    this(lo,hi,0.5 * (lo + hi));
  }
  /** 
 * Gets the lower bound.
 * @return the lower bound.
 */
  public double getMin(){
    return lower;
  }
  /** 
 * Gets the upper bound.
 * @return the upper bound.
 */
  public double getMax(){
    return upper;
  }
  /** 
 * Gets the start value.
 * @return the start value.
 */
  public double getStartValue(){
    return start;
  }
}
