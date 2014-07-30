package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test the LinearInterpolator.
 */
public class LinearInterpolatorTest {
  /** 
 * error tolerance for spline interpolator value at knot points 
 */
  protected double knotTolerance=1E-12;
  /** 
 * error tolerance for interpolating polynomial coefficients 
 */
  protected double coefficientTolerance=1E-6;
  /** 
 * error tolerance for interpolated values 
 */
  protected double interpolationTolerance=1E-12;
  @Test public void testInterpolateLinearDegenerateTwoSegment(){
    double x[]={0.0,0.5,1.0};
    double y[]={0.0,0.5,1.0};
    UnivariateInterpolator i=new LinearInterpolator();
    UnivariateFunction f=i.interpolate(x,y);
    verifyInterpolation(f,x,y);
    PolynomialFunction polynomials[]=((PolynomialSplineFunction)f).getPolynomials();
    double target[]={y[0],1d};
    TestUtils.assertEquals(polynomials[0].getCoefficients(),target,coefficientTolerance);
    target=new double[]{y[1],1d};
    TestUtils.assertEquals(polynomials[1].getCoefficients(),target,coefficientTolerance);
    Assert.assertEquals(0.0,f.value(0.0),interpolationTolerance);
    Assert.assertEquals(0.4,f.value(0.4),interpolationTolerance);
    Assert.assertEquals(1.0,f.value(1.0),interpolationTolerance);
  }
  @Test public void testInterpolateLinearDegenerateThreeSegment(){
    double x[]={0.0,0.5,1.0,1.5};
    double y[]={0.0,0.5,1.0,1.5};
    UnivariateInterpolator i=new LinearInterpolator();
    UnivariateFunction f=i.interpolate(x,y);
    verifyInterpolation(f,x,y);
    PolynomialFunction polynomials[]=((PolynomialSplineFunction)f).getPolynomials();
    double target[]={y[0],1d};
    TestUtils.assertEquals(polynomials[0].getCoefficients(),target,coefficientTolerance);
    target=new double[]{y[1],1d};
    TestUtils.assertEquals(polynomials[1].getCoefficients(),target,coefficientTolerance);
    target=new double[]{y[2],1d};
    TestUtils.assertEquals(polynomials[2].getCoefficients(),target,coefficientTolerance);
    Assert.assertEquals(0,f.value(0),interpolationTolerance);
    Assert.assertEquals(1.4,f.value(1.4),interpolationTolerance);
    Assert.assertEquals(1.5,f.value(1.5),interpolationTolerance);
  }
  @Test public void testInterpolateLinear(){
    double x[]={0.0,0.5,1.0};
    double y[]={0.0,0.5,0.0};
    UnivariateInterpolator i=new LinearInterpolator();
    UnivariateFunction f=i.interpolate(x,y);
    verifyInterpolation(f,x,y);
    PolynomialFunction polynomials[]=((PolynomialSplineFunction)f).getPolynomials();
    double target[]={y[0],1d};
    TestUtils.assertEquals(polynomials[0].getCoefficients(),target,coefficientTolerance);
    target=new double[]{y[1],-1d};
    TestUtils.assertEquals(polynomials[1].getCoefficients(),target,coefficientTolerance);
  }
  @Test public void testIllegalArguments(){
    UnivariateInterpolator i=new LinearInterpolator();
    try {
      double xval[]={0.0,1.0};
      double yval[]={0.0,1.0,2.0};
      i.interpolate(xval,yval);
      Assert.fail("Failed to detect data set array with different sizes.");
    }
 catch (    DimensionMismatchException iae) {
    }
    try {
      double xval[]={0.0,1.0,0.5};
      double yval[]={0.0,1.0,2.0};
      i.interpolate(xval,yval);
      Assert.fail("Failed to detect unsorted arguments.");
    }
 catch (    NonMonotonicSequenceException iae) {
    }
    try {
      double xval[]={0.0};
      double yval[]={0.0};
      i.interpolate(xval,yval);
      Assert.fail("Failed to detect unsorted arguments.");
    }
 catch (    NumberIsTooSmallException iae) {
    }
  }
  /** 
 * verifies that f(x[i]) = y[i] for i = 0..n-1 where n is common length.
 */
  protected void verifyInterpolation(  UnivariateFunction f,  double x[],  double y[]){
    for (int i=0; i < x.length; i++) {
      Assert.assertEquals(f.value(x[i]),y[i],knotTolerance);
    }
  }
}