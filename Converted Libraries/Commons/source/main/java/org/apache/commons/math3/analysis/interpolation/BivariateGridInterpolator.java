package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
/** 
 * Interface representing a bivariate real interpolating function where the
 * sample points must be specified on a regular grid.
 * @version $Id: BivariateGridInterpolator.java 1455194 2013-03-11 15:45:54Z luc $
 */
public interface BivariateGridInterpolator {
  /** 
 * Compute an interpolating function for the dataset.
 * @param xval All the x-coordinates of the interpolation points, sorted
 * in increasing order.
 * @param yval All the y-coordinates of the interpolation points, sorted
 * in increasing order.
 * @param fval The values of the interpolation points on all the grid knots:{@code fval[i][j] = f(xval[i], yval[j])}.
 * @return a function which interpolates the dataset.
 * @throws NoDataException if any of the arrays has zero length.
 * @throws DimensionMismatchException if the array lengths are inconsistent.
 * @throws NonMonotonicSequenceException if the array is not sorted.
 * @throws NumberIsTooSmallException if the number of points is too small for
 * the order of the interpolation
 */
  BivariateFunction interpolate(  double[] xval,  double[] yval,  double[][] fval) throws NoDataException, DimensionMismatchException, NonMonotonicSequenceException, NumberIsTooSmallException ;
}
