package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
/** 
 * Interface representing a univariate real interpolating function.
 * @since 2.1
 * @version $Id: MultivariateInterpolator.java 1455194 2013-03-11 15:45:54Z luc $
 */
public interface MultivariateInterpolator {
  /** 
 * Computes an interpolating function for the data set.
 * @param xval the arguments for the interpolation points.{@code xval[i][0]} is the first component of interpolation point{@code i}, {@code xval[i][1]} is the second component, and so on
 * until {@code xval[i][d-1]}, the last component of that interpolation
 * point (where {@code d} is thus the dimension of the space).
 * @param yval the values for the interpolation points
 * @return a function which interpolates the data set
 * @throws MathIllegalArgumentException if the arguments violate assumptions
 * made by the interpolation algorithm.
 * @throws DimensionMismatchException when the array dimensions are not consistent.
 * @throws NoDataException if an array has zero-length.
 * @throws NullArgumentException if the arguments are {@code null}.
 */
  MultivariateFunction interpolate(  double[][] xval,  double[] yval) throws MathIllegalArgumentException, DimensionMismatchException, NoDataException, NullArgumentException ;
}
