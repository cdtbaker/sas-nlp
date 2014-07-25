package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.analysis.TrivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
/** 
 * Interface representing a trivariate real interpolating function where the
 * sample points must be specified on a regular grid.
 * @since 2.2
 * @version $Id: TrivariateGridInterpolator.java 1455194 2013-03-11 15:45:54Z luc $
 */
public interface TrivariateGridInterpolator {
  /** 
 * Compute an interpolating function for the dataset.
 * @param xval All the x-coordinates of the interpolation points, sorted
 * in increasing order.
 * @param yval All the y-coordinates of the interpolation points, sorted
 * in increasing order.
 * @param zval All the z-coordinates of the interpolation points, sorted
 * in increasing order.
 * @param fval the values of the interpolation points on all the grid knots:{@code fval[i][j][k] = f(xval[i], yval[j], zval[k])}.
 * @return a function that interpolates the data set.
 * @throws NoDataException if any of the arrays has zero length.
 * @throws DimensionMismatchException if the array lengths are inconsistent.
 * @throws NonMonotonicSequenceException if arrays are not sorted
 * @throws NumberIsTooSmallException if the number of points is too small for
 * the order of the interpolation
 */
  TrivariateFunction interpolate(  double[] xval,  double[] yval,  double[] zval,  double[][][] fval) throws NoDataException, NumberIsTooSmallException, DimensionMismatchException, NonMonotonicSequenceException ;
}
