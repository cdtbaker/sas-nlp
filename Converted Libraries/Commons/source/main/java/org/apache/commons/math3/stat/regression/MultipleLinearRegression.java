package org.apache.commons.math3.stat.regression;
/** 
 * The multiple linear regression can be represented in matrix-notation.
 * <pre>
 * y=X*b+u
 * </pre>
 * where y is an <code>n-vector</code> <b>regressand</b>, X is a <code>[n,k]</code> matrix whose <code>k</code> columns are called
 * <b>regressors</b>, b is <code>k-vector</code> of <b>regression parameters</b> and <code>u</code> is an <code>n-vector</code>
 * of <b>error terms</b> or <b>residuals</b>.
 * The notation is quite standard in literature,
 * cf eg <a href="http://www.econ.queensu.ca/ETM">Davidson and MacKinnon, Econometrics Theory and Methods, 2004</a>.
 * @version $Id: MultipleLinearRegression.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public interface MultipleLinearRegression {
  /** 
 * Estimates the regression parameters b.
 * @return The [k,1] array representing b
 */
  double[] estimateRegressionParameters();
  /** 
 * Estimates the variance of the regression parameters, ie Var(b).
 * @return The [k,k] array representing the variance of b
 */
  double[][] estimateRegressionParametersVariance();
  /** 
 * Estimates the residuals, ie u = y - X*b.
 * @return The [n,1] array representing the residuals
 */
  double[] estimateResiduals();
  /** 
 * Returns the variance of the regressand, ie Var(y).
 * @return The double representing the variance of y
 */
  double estimateRegressandVariance();
  /** 
 * Returns the standard errors of the regression parameters.
 * @return standard errors of estimated regression parameters
 */
  double[] estimateRegressionParametersStandardErrors();
}
