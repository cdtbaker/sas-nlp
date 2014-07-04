package org.apache.commons.math3.analysis.interpolation;
import java.io.Serializable;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
/** 
 * Implements the <a href="
 * http://mathworld.wolfram.com/NewtonsDividedDifferenceInterpolationFormula.html">
 * Divided Difference Algorithm</a> for interpolation of real univariate
 * functions. For reference, see <b>Introduction to Numerical Analysis</b>,
 * ISBN 038795452X, chapter 2.
 * <p>
 * The actual code of Neville's evaluation is in PolynomialFunctionLagrangeForm,
 * this class provides an easy-to-use interface to it.</p>
 * @version $Id: DividedDifferenceInterpolator.java 1385313 2012-09-16 16:35:23Z tn $
 * @since 1.2
 */
public class DividedDifferenceInterpolator implements UnivariateInterpolator, Serializable {
  /** 
 * serializable version identifier 
 */
  private static final long serialVersionUID=107049519551235069L;
  /** 
 * Compute an interpolating function for the dataset.
 * @param x Interpolating points array.
 * @param y Interpolating values array.
 * @return a function which interpolates the dataset.
 * @throws DimensionMismatchException if the array lengths are different.
 * @throws NumberIsTooSmallException if the number of points is less than 2.
 * @throws NonMonotonicSequenceException if {@code x} is not sorted in
 * strictly increasing order.
 */
  public PolynomialFunctionNewtonForm interpolate(  double x[],  double y[]) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
    PolynomialFunctionLagrangeForm.verifyInterpolationArray(x,y,true);
    final double[] c=new double[x.length - 1];
    System.arraycopy(x,0,c,0,c.length);
    final double[] a=computeDividedDifference(x,y);
    return new PolynomialFunctionNewtonForm(a,c);
  }
  /** 
 * Return a copy of the divided difference array.
 * <p>
 * The divided difference array is defined recursively by <pre>
 * f[x0] = f(x0)
 * f[x0,x1,...,xk] = (f[x1,...,xk] - f[x0,...,x[k-1]]) / (xk - x0)
 * </pre></p>
 * <p>
 * The computational complexity is O(N^2).</p>
 * @param x Interpolating points array.
 * @param y Interpolating values array.
 * @return a fresh copy of the divided difference array.
 * @throws DimensionMismatchException if the array lengths are different.
 * @throws NumberIsTooSmallException if the number of points is less than 2.
 * @throws NonMonotonicSequenceExceptionif {@code x} is not sorted in strictly increasing order.
 */
  protected static double[] computeDividedDifference(  final double x[],  final double y[]) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
    PolynomialFunctionLagrangeForm.verifyInterpolationArray(x,y,true);
    final double[] divdiff=y.clone();
    final int n=x.length;
    final double[] a=new double[n];
    a[0]=divdiff[0];
    for (int i=1; i < n; i++) {
      for (int j=0; j < n - i; j++) {
        final double denominator=x[j + i] - x[j];
        divdiff[j]=(divdiff[j + 1] - divdiff[j]) / denominator;
      }
      a[i]=divdiff[0];
    }
    return a;
  }
}
