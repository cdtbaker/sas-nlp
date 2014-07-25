package org.apache.commons.math3.fitting;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
/** 
 * Polynomial fitting is a very simple case of {@link CurveFitter curve fitting}.
 * The estimated coefficients are the polynomial coefficients (see the{@link #fit(double[]) fit} method).
 * @version $Id: PolynomialFitter.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public class PolynomialFitter extends CurveFitter<PolynomialFunction.Parametric> {
  /** 
 * Simple constructor.
 * @param optimizer Optimizer to use for the fitting.
 */
  public PolynomialFitter(  MultivariateVectorOptimizer optimizer){
    super(optimizer);
  }
  /** 
 * Get the coefficients of the polynomial fitting the weighted data points.
 * The degree of the fitting polynomial is {@code guess.length - 1}.
 * @param guess First guess for the coefficients. They must be sorted in
 * increasing order of the polynomial's degree.
 * @param maxEval Maximum number of evaluations of the polynomial.
 * @return the coefficients of the polynomial that best fits the observed points.
 * @throws org.apache.commons.math3.exception.TooManyEvaluationsException if
 * the number of evaluations exceeds {@code maxEval}.
 * @throws org.apache.commons.math3.exception.ConvergenceExceptionif the algorithm failed to converge.
 */
  public double[] fit(  int maxEval,  double[] guess){
    return fit(maxEval,new PolynomialFunction.Parametric(),guess);
  }
  /** 
 * Get the coefficients of the polynomial fitting the weighted data points.
 * The degree of the fitting polynomial is {@code guess.length - 1}.
 * @param guess First guess for the coefficients. They must be sorted in
 * increasing order of the polynomial's degree.
 * @return the coefficients of the polynomial that best fits the observed points.
 * @throws org.apache.commons.math3.exception.ConvergenceExceptionif the algorithm failed to converge.
 */
  public double[] fit(  double[] guess){
    return fit(new PolynomialFunction.Parametric(),guess);
  }
}
