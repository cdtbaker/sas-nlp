package org.apache.commons.math3.stat.descriptive.moment;
import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
/** 
 * Computes the Kurtosis of the available values.
 * <p>
 * We use the following (unbiased) formula to define kurtosis:</p>
 * <p>
 * kurtosis = { [n(n+1) / (n -1)(n - 2)(n-3)] sum[(x_i - mean)^4] / std^4 } - [3(n-1)^2 / (n-2)(n-3)]
 * </p><p>
 * where n is the number of values, mean is the {@link Mean} and std is the{@link StandardDeviation}</p>
 * <p>
 * Note that this statistic is undefined for n < 4.  <code>Double.Nan</code>
 * is returned when there is not sufficient data to compute the statistic.</p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 * @version $Id: Kurtosis.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class Kurtosis extends AbstractStorelessUnivariateStatistic implements Serializable {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=2784465764798260919L;
  /** 
 * Fourth Moment on which this statistic is based 
 */
  protected FourthMoment moment;
  /** 
 * Determines whether or not this statistic can be incremented or cleared.
 * <p>
 * Statistics based on (constructed from) external moments cannot
 * be incremented or cleared.</p>
 */
  protected boolean incMoment;
  /** 
 * Construct a Kurtosis
 */
  public Kurtosis(){
    incMoment=true;
    moment=new FourthMoment();
  }
  /** 
 * Construct a Kurtosis from an external moment
 * @param m4 external Moment
 */
  public Kurtosis(  final FourthMoment m4){
    incMoment=false;
    this.moment=m4;
  }
  /** 
 * Copy constructor, creates a new {@code Kurtosis} identical
 * to the {@code original}
 * @param original the {@code Kurtosis} instance to copy
 * @throws NullArgumentException if original is null
 */
  public Kurtosis(  Kurtosis original) throws NullArgumentException {
    copy(original,this);
  }
  /** 
 * {@inheritDoc}<p>Note that when {@link #Kurtosis(FourthMoment)} is used to
 * create a Variance, this method does nothing. In that case, the
 * FourthMoment should be incremented directly.</p>
 */
  @Override public void increment(  final double d){
    if (incMoment) {
      moment.increment(d);
    }
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double getResult(){
    double kurtosis=Double.NaN;
    if (moment.getN() > 3) {
      double variance=moment.m2 / (moment.n - 1);
      if (moment.n <= 3 || variance < 10E-20) {
        kurtosis=0.0;
      }
 else {
        double n=moment.n;
        kurtosis=(n * (n + 1) * moment.getResult() - 3 * moment.m2 * moment.m2* (n - 1)) / ((n - 1) * (n - 2) * (n - 3)* variance* variance);
      }
    }
    return kurtosis;
  }
  /** 
 * {@inheritDoc}
 */
  @Override public void clear(){
    if (incMoment) {
      moment.clear();
    }
  }
  /** 
 * {@inheritDoc}
 */
  public long getN(){
    return moment.getN();
  }
  /** 
 * Returns the kurtosis of the entries in the specified portion of the
 * input array.
 * <p>
 * See {@link Kurtosis} for details on the computing algorithm.</p>
 * <p>
 * Throws <code>IllegalArgumentException</code> if the array is null.</p>
 * @param values the input array
 * @param begin index of the first array element to include
 * @param length the number of elements to include
 * @return the kurtosis of the values or Double.NaN if length is less than 4
 * @throws MathIllegalArgumentException if the input array is null or the array
 * index parameters are not valid
 */
  @Override public double evaluate(  final double[] values,  final int begin,  final int length) throws MathIllegalArgumentException {
    double kurt=Double.NaN;
    if (test(values,begin,length) && length > 3) {
      Variance variance=new Variance();
      variance.incrementAll(values,begin,length);
      double mean=variance.moment.m1;
      double stdDev=FastMath.sqrt(variance.getResult());
      double accum3=0.0;
      for (int i=begin; i < begin + length; i++) {
        accum3+=FastMath.pow(values[i] - mean,4.0);
      }
      accum3/=FastMath.pow(stdDev,4.0d);
      double n0=length;
      double coefficientOne=(n0 * (n0 + 1)) / ((n0 - 1) * (n0 - 2) * (n0 - 3));
      double termTwo=(3 * FastMath.pow(n0 - 1,2.0)) / ((n0 - 2) * (n0 - 3));
      kurt=(coefficientOne * accum3) - termTwo;
    }
    return kurt;
  }
  /** 
 * {@inheritDoc}
 */
  @Override public Kurtosis copy(){
    Kurtosis result=new Kurtosis();
    copy(this,result);
    return result;
  }
  /** 
 * Copies source to dest.
 * <p>Neither source nor dest can be null.</p>
 * @param source Kurtosis to copy
 * @param dest Kurtosis to copy to
 * @throws NullArgumentException if either source or dest is null
 */
  public static void copy(  Kurtosis source,  Kurtosis dest) throws NullArgumentException {
    MathUtils.checkNotNull(source);
    MathUtils.checkNotNull(dest);
    dest.setData(source.getDataRef());
    dest.moment=source.moment.copy();
    dest.incMoment=source.incMoment;
  }
}
