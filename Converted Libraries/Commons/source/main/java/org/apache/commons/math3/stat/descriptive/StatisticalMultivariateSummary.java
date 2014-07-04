package org.apache.commons.math3.stat.descriptive;
import org.apache.commons.math3.linear.RealMatrix;
/** 
 * Reporting interface for basic multivariate statistics.
 * @since 1.2
 * @version $Id: StatisticalMultivariateSummary.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface StatisticalMultivariateSummary {
  /** 
 * Returns the dimension of the data
 * @return The dimension of the data
 */
  int getDimension();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * mean of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component means
 */
  double[] getMean();
  /** 
 * Returns the covariance of the available values.
 * @return The covariance, null if no multivariate sample
 * have been added or a zeroed matrix for a single value set.
 */
  RealMatrix getCovariance();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * standard deviation of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component standard deviations
 */
  double[] getStandardDeviation();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * maximum of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component maxima
 */
  double[] getMax();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * minimum of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component minima
 */
  double[] getMin();
  /** 
 * Returns the number of available values
 * @return The number of available values
 */
  long getN();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * geometric mean of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component geometric means
 */
  double[] getGeometricMean();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * sum of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component sums
 */
  double[] getSum();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * sum of squares of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component sums of squares
 */
  double[] getSumSq();
  /** 
 * Returns an array whose i<sup>th</sup> entry is the
 * sum of logs of the i<sup>th</sup> entries of the arrays
 * that correspond to each multivariate sample
 * @return the array of component log sums
 */
  double[] getSumLog();
}
