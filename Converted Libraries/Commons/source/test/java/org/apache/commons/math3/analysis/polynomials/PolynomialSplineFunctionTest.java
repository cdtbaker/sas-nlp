package org.apache.commons.math3.analysis.polynomials;
import java.util.Arrays;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Tests the PolynomialSplineFunction implementation.
 * @version $Id: PolynomialSplineFunctionTest.java 1364030 2012-07-21 01:10:04Z erans $
 */
public class PolynomialSplineFunctionTest {
  /** 
 * Error tolerance for tests 
 */
  protected double tolerance=1.0e-12;
  /** 
 * Quadratic polynomials used in tests:
 * x^2 + x            [-1, 0)
 * x^2 + x + 2        [0, 1)
 * x^2 + x + 4        [1, 2)
 * Defined so that evaluation using PolynomialSplineFunction evaluation
 * algorithm agrees at knot point boundaries.
 */
  protected PolynomialFunction[] polynomials={new PolynomialFunction(new double[]{0d,1d,1d}),new PolynomialFunction(new double[]{2d,1d,1d}),new PolynomialFunction(new double[]{4d,1d,1d})};
  /** 
 * Knot points  
 */
  protected double[] knots={-1,0,1,2};
  /** 
 * Derivative of test polynomials -- 2x + 1  
 */
  protected PolynomialFunction dp=new PolynomialFunction(new double[]{1d,2d});
  @Test public void testConstructor(){
    PolynomialSplineFunction spline=new PolynomialSplineFunction(knots,polynomials);
    Assert.assertTrue(Arrays.equals(knots,spline.getKnots()));
    Assert.assertEquals(1d,spline.getPolynomials()[0].getCoefficients()[2],0);
    Assert.assertEquals(3,spline.getN());
    try {
      new PolynomialSplineFunction(new double[]{0},polynomials);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      new PolynomialSplineFunction(new double[]{0,1,2,3,4},polynomials);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      new PolynomialSplineFunction(new double[]{0,1,3,2},polynomials);
      Assert.fail("Expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test public void testValues(){
    PolynomialSplineFunction spline=new PolynomialSplineFunction(knots,polynomials);
    UnivariateFunction dSpline=spline.derivative();
    double x=-1;
    int index=0;
    for (int i=0; i < 10; i++) {
      x+=0.25;
      index=findKnot(knots,x);
      Assert.assertEquals("spline function evaluation failed for x=" + x,polynomials[index].value(x - knots[index]),spline.value(x),tolerance);
      Assert.assertEquals("spline derivative evaluation failed for x=" + x,dp.value(x - knots[index]),dSpline.value(x),tolerance);
    }
    for (int i=0; i < 3; i++) {
      Assert.assertEquals("spline function evaluation failed for knot=" + knots[i],polynomials[i].value(0),spline.value(knots[i]),tolerance);
      Assert.assertEquals("spline function evaluation failed for knot=" + knots[i],dp.value(0),dSpline.value(knots[i]),tolerance);
    }
    try {
      x=spline.value(-1.5);
      Assert.fail("Expecting OutOfRangeException");
    }
 catch (    OutOfRangeException ex) {
    }
    try {
      x=spline.value(2.5);
      Assert.fail("Expecting OutOfRangeException");
    }
 catch (    OutOfRangeException ex) {
    }
  }
  /** 
 * Do linear search to find largest knot point less than or equal to x.
 * Implementation does binary search.
 */
  protected int findKnot(  double[] knots,  double x){
    if (x < knots[0] || x >= knots[knots.length - 1]) {
      throw new OutOfRangeException(x,knots[0],knots[knots.length - 1]);
    }
    for (int i=0; i < knots.length; i++) {
      if (knots[i] > x) {
        return i - 1;
      }
    }
    throw new MathIllegalStateException();
  }
}
