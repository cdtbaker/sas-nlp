package org.apache.commons.math3.stat.descriptive;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.MathArrays;
/** 
 * Base interface implemented by all statistics.
 * @version $Id: UnivariateStatistic.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface UnivariateStatistic extends MathArrays.Function {
  /** 
 * Returns the result of evaluating the statistic over the input array.
 * @param values input array
 * @return the value of the statistic applied to the input array
 * @throws MathIllegalArgumentException  if values is null
 */
  double evaluate(  double[] values) throws MathIllegalArgumentException ;
  /** 
 * Returns the result of evaluating the statistic over the specified entries
 * in the input array.
 * @param values the input array
 * @param begin the index of the first element to include
 * @param length the number of elements to include
 * @return the value of the statistic applied to the included array entries
 * @throws MathIllegalArgumentException if values is null or the indices are invalid
 */
  double evaluate(  double[] values,  int begin,  int length) throws MathIllegalArgumentException ;
  /** 
 * Returns a copy of the statistic with the same internal state.
 * @return a copy of the statistic
 */
  UnivariateStatistic copy();
}
