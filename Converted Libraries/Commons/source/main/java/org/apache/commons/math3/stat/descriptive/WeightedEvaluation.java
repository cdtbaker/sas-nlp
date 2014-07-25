package org.apache.commons.math3.stat.descriptive;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Weighted evaluation for statistics.
 * @since 2.1
 * @version $Id: WeightedEvaluation.java 1382332 2012-09-08 17:27:47Z psteitz $
 */
public interface WeightedEvaluation {
  /** 
 * Returns the result of evaluating the statistic over the input array,
 * using the supplied weights.
 * @param values input array
 * @param weights array of weights
 * @return the value of the weighted statistic applied to the input array
 * @throws MathIllegalArgumentException if either array is null, lengths
 * do not match, weights contain NaN, negative or infinite values, or
 * weights does not include at least on positive value
 */
  double evaluate(  double[] values,  double[] weights) throws MathIllegalArgumentException ;
  /** 
 * Returns the result of evaluating the statistic over the specified entries
 * in the input array, using corresponding entries in the supplied weights array.
 * @param values the input array
 * @param weights array of weights
 * @param begin the index of the first element to include
 * @param length the number of elements to include
 * @return the value of the weighted statistic applied to the included array entries
 * @throws MathIllegalArgumentException if either array is null, lengths
 * do not match, indices are invalid, weights contain NaN, negative or
 * infinite values, or weights does not include at least on positive value
 */
  double evaluate(  double[] values,  double[] weights,  int begin,  int length) throws MathIllegalArgumentException ;
}
