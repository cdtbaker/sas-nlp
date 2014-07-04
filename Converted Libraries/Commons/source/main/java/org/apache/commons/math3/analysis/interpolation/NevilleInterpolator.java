package org.apache.commons.math3.analysis.interpolation;
import java.io.Serializable;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
/** 
 * Implements the <a href="http://mathworld.wolfram.com/NevillesAlgorithm.html">
 * Neville's Algorithm</a> for interpolation of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 2.
 * <p>
 * The actual code of Neville's algorithm is in PolynomialFunctionLagrangeForm,
 * this class provides an easy-to-use interface to it.</p>
 * @version $Id: NevilleInterpolator.java 1379904 2012-09-01 23:54:52Z erans $
 * @since 1.2
 */
public class NevilleInterpolator implements UnivariateInterpolator, Serializable {
  /** 
 * serializable version identifier 
 */
  static final long serialVersionUID=3003707660147873733L;
  /** 
 * Computes an interpolating function for the data set.
 * @param x Interpolating points.
 * @param y Interpolating values.
 * @return a function which interpolates the data set
 * @throws DimensionMismatchException if the array lengths are different.
 * @throws NumberIsTooSmallException if the number of points is less than 2.
 * @throws NonMonotonicSequenceException if two abscissae have the same
 * value.
 */
  public PolynomialFunctionLagrangeForm interpolate(  double x[],  double y[]) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
    return new PolynomialFunctionLagrangeForm(x,y);
  }
}
