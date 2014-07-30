package org.apache.commons.math3.optimization.fitting;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
/** 
 * Polynomial fitting is a very simple case of {@link CurveFitter curve fitting}.
 * The estimated coefficients are the polynomial coefficients (see the{@link #fit(double[]) fit} method).
 * @version $Id: PolynomialFitter.java 1422313 2012-12-15 18:53:41Z psteitz $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public class PolynomialFitter extends CurveFitter<PolynomialFunction.Parametric> {
  /** 
 * Polynomial degree.
 * @deprecated
 */
  @Deprecated private final int degree;
  /** 
 * Simple constructor.
 * <p>The polynomial fitter built this way are complete polynomials,
 * ie. a n-degree polynomial has n+1 coefficients.</p>
 * @param degree Maximal degree of the polynomial.
 * @param optimizer Optimizer to use for the fitting.
 * @deprecated Since 3.1 (to be removed in 4.0). Please use{@link #PolynomialFitter(DifferentiableMultivariateVectorOptimizer)} instead.
 */
  @Deprecated public PolynomialFitter(  int degree,  final DifferentiableMultivariateVectorOptimizer optimizer){
    super(optimizer);
    this.degree=degree;
  }
  /** 
 * Simple constructor.
 * @param optimizer Optimizer to use for the fitting.
 * @since 3.1
 */
  public PolynomialFitter(  DifferentiableMultivariateVectorOptimizer optimizer){
    super(optimizer);
    degree=-1;
  }
  /** 
 * Get the polynomial fitting the weighted (x, y) points.
 * @return the coefficients of the polynomial that best fits the observed points.
 * @throws org.apache.commons.math3.exception.ConvergenceExceptionif the algorithm failed to converge.
 * @deprecated Since 3.1 (to be removed in 4.0). Please use {@link #fit(double[])} instead.
 */
  @Deprecated public double[] fit(){
    return fit(new PolynomialFunction.Parametric(),new double[degree + 1]);
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
 * @since 3.1
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
 * @since 3.1
 */
  public double[] fit(  double[] guess){
    return fit(new PolynomialFunction.Parametric(),guess);
  }
}