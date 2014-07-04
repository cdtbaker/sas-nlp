package org.apache.commons.math3.stat.descriptive.rank;
import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
/** 
 * Returns the minimum of the available values.
 * <p>
 * <ul>
 * <li>The result is <code>NaN</code> iff all values are <code>NaN</code>
 * (i.e. <code>NaN</code> values have no impact on the value of the statistic).</li>
 * <li>If any of the values equals <code>Double.NEGATIVE_INFINITY</code>,
 * the result is <code>Double.NEGATIVE_INFINITY.</code></li>
 * </ul></p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 * @version $Id: Min.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class Min extends AbstractStorelessUnivariateStatistic implements Serializable {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=-2941995784909003131L;
  /** 
 * Number of values that have been added 
 */
  private long n;
  /** 
 * Current value of the statistic 
 */
  private double value;
  /** 
 * Create a Min instance
 */
  public Min(){
    n=0;
    value=Double.NaN;
  }
  /** 
 * Copy constructor, creates a new {@code Min} identical
 * to the {@code original}
 * @param original the {@code Min} instance to copy
 * @throws NullArgumentException if original is null
 */
  public Min(  Min original) throws NullArgumentException {
    copy(original,this);
  }
  /** 
 * {@inheritDoc}
 */
  @Override public void increment(  final double d){
    if (d < value || Double.isNaN(value)) {
      value=d;
    }
    n++;
  }
  /** 
 * {@inheritDoc}
 */
  @Override public void clear(){
    value=Double.NaN;
    n=0;
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double getResult(){
    return value;
  }
  /** 
 * {@inheritDoc}
 */
  public long getN(){
    return n;
  }
  /** 
 * Returns the minimum of the entries in the specified portion of
 * the input array, or <code>Double.NaN</code> if the designated subarray
 * is empty.
 * <p>
 * Throws <code>MathIllegalArgumentException</code> if the array is null or
 * the array index parameters are not valid.</p>
 * <p>
 * <ul>
 * <li>The result is <code>NaN</code> iff all values are <code>NaN</code>
 * (i.e. <code>NaN</code> values have no impact on the value of the statistic).</li>
 * <li>If any of the values equals <code>Double.NEGATIVE_INFINITY</code>,
 * the result is <code>Double.NEGATIVE_INFINITY.</code></li>
 * </ul> </p>
 * @param values the input array
 * @param begin index of the first array element to include
 * @param length the number of elements to include
 * @return the minimum of the values or Double.NaN if length = 0
 * @throws MathIllegalArgumentException if the array is null or the array index
 * parameters are not valid
 */
  @Override public double evaluate(  final double[] values,  final int begin,  final int length) throws MathIllegalArgumentException {
    double min=Double.NaN;
    if (test(values,begin,length)) {
      min=values[begin];
      for (int i=begin; i < begin + length; i++) {
        if (!Double.isNaN(values[i])) {
          min=(min < values[i]) ? min : values[i];
        }
      }
    }
    return min;
  }
  /** 
 * {@inheritDoc}
 */
  @Override public Min copy(){
    Min result=new Min();
    copy(this,result);
    return result;
  }
  /** 
 * Copies source to dest.
 * <p>Neither source nor dest can be null.</p>
 * @param source Min to copy
 * @param dest Min to copy to
 * @throws NullArgumentException if either source or dest is null
 */
  public static void copy(  Min source,  Min dest) throws NullArgumentException {
    MathUtils.checkNotNull(source);
    MathUtils.checkNotNull(dest);
    dest.setData(source.getDataRef());
    dest.n=source.n;
    dest.value=source.value;
  }
}
