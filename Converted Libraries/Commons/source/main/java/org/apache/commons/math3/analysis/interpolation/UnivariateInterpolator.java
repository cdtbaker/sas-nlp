package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
/** 
 * Interface representing a univariate real interpolating function.
 * @version $Id: UnivariateInterpolator.java 1455194 2013-03-11 15:45:54Z luc $
 */
public interface UnivariateInterpolator {
  /** 
 * Compute an interpolating function for the dataset.
 * @param xval Arguments for the interpolation points.
 * @param yval Values for the interpolation points.
 * @return a function which interpolates the dataset.
 * @throws MathIllegalArgumentExceptionif the arguments violate assumptions made by the interpolation
 * algorithm.
 * @throws DimensionMismatchException if arrays lengthes do not match
 */
  UnivariateFunction interpolate(  double xval[],  double yval[]) throws MathIllegalArgumentException, DimensionMismatchException ;
}
