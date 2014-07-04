package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.GaussNewtonOptimizer;
import org.apache.commons.math3.fitting.PolynomialFitter;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
/** 
 * Generates a bicubic interpolation function.
 * Prior to generating the interpolating function, the input is smoothed using
 * polynomial fitting.
 * @version $Id: SmoothingPolynomialBicubicSplineInterpolator.java 1455194 2013-03-11 15:45:54Z luc $
 * @since 2.2
 */
public class SmoothingPolynomialBicubicSplineInterpolator extends BicubicSplineInterpolator {
  /** 
 * Fitter for x. 
 */
  private final PolynomialFitter xFitter;
  /** 
 * Degree of the fitting polynomial. 
 */
  private final int xDegree;
  /** 
 * Fitter for y. 
 */
  private final PolynomialFitter yFitter;
  /** 
 * Degree of the fitting polynomial. 
 */
  private final int yDegree;
  /** 
 * Default constructor. The degree of the fitting polynomials is set to 3.
 */
  public SmoothingPolynomialBicubicSplineInterpolator(){
    this(3);
  }
  /** 
 * @param degree Degree of the polynomial fitting functions.
 * @exception NotPositiveException if degree is not positive
 */
  public SmoothingPolynomialBicubicSplineInterpolator(  int degree) throws NotPositiveException {
    this(degree,degree);
  }
  /** 
 * @param xDegree Degree of the polynomial fitting functions along the
 * x-dimension.
 * @param yDegree Degree of the polynomial fitting functions along the
 * y-dimension.
 * @exception NotPositiveException if degrees are not positive
 */
  public SmoothingPolynomialBicubicSplineInterpolator(  int xDegree,  int yDegree) throws NotPositiveException {
    if (xDegree < 0) {
      throw new NotPositiveException(xDegree);
    }
    if (yDegree < 0) {
      throw new NotPositiveException(yDegree);
    }
    this.xDegree=xDegree;
    this.yDegree=yDegree;
    final double safeFactor=1e2;
    final SimpleVectorValueChecker checker=new SimpleVectorValueChecker(safeFactor * Precision.EPSILON,safeFactor * Precision.SAFE_MIN);
    xFitter=new PolynomialFitter(new GaussNewtonOptimizer(false,checker));
    yFitter=new PolynomialFitter(new GaussNewtonOptimizer(false,checker));
  }
  /** 
 * {@inheritDoc}
 */
  @Override public BicubicSplineInterpolatingFunction interpolate(  final double[] xval,  final double[] yval,  final double[][] fval) throws NoDataException, NullArgumentException, DimensionMismatchException, NonMonotonicSequenceException {
    if (xval.length == 0 || yval.length == 0 || fval.length == 0) {
      throw new NoDataException();
    }
    if (xval.length != fval.length) {
      throw new DimensionMismatchException(xval.length,fval.length);
    }
    final int xLen=xval.length;
    final int yLen=yval.length;
    for (int i=0; i < xLen; i++) {
      if (fval[i].length != yLen) {
        throw new DimensionMismatchException(fval[i].length,yLen);
      }
    }
    MathArrays.checkOrder(xval);
    MathArrays.checkOrder(yval);
    final PolynomialFunction[] yPolyX=new PolynomialFunction[yLen];
    for (int j=0; j < yLen; j++) {
      xFitter.clearObservations();
      for (int i=0; i < xLen; i++) {
        xFitter.addObservedPoint(1,xval[i],fval[i][j]);
      }
      yPolyX[j]=new PolynomialFunction(xFitter.fit(new double[xDegree + 1]));
    }
    final double[][] fval_1=new double[xLen][yLen];
    for (int j=0; j < yLen; j++) {
      final PolynomialFunction f=yPolyX[j];
      for (int i=0; i < xLen; i++) {
        fval_1[i][j]=f.value(xval[i]);
      }
    }
    final PolynomialFunction[] xPolyY=new PolynomialFunction[xLen];
    for (int i=0; i < xLen; i++) {
      yFitter.clearObservations();
      for (int j=0; j < yLen; j++) {
        yFitter.addObservedPoint(1,yval[j],fval_1[i][j]);
      }
      xPolyY[i]=new PolynomialFunction(yFitter.fit(new double[yDegree + 1]));
    }
    final double[][] fval_2=new double[xLen][yLen];
    for (int i=0; i < xLen; i++) {
      final PolynomialFunction f=xPolyY[i];
      for (int j=0; j < yLen; j++) {
        fval_2[i][j]=f.value(yval[j]);
      }
    }
    return super.interpolate(xval,yval,fval_2);
  }
}
