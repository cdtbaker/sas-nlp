package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test case for the bicubic interpolator.
 * @version $Id$
 */
public final class BicubicSplineInterpolatorTest {
  /** 
 * Test preconditions.
 */
  @Test public void testPreconditions(){
    double[] xval=new double[]{3,4,5,6.5};
    double[] yval=new double[]{-4,-3,-1,2.5};
    double[][] zval=new double[xval.length][yval.length];
    BivariateGridInterpolator interpolator=new BicubicSplineInterpolator();
    @SuppressWarnings("unused") BivariateFunction p=interpolator.interpolate(xval,yval,zval);
    double[] wxval=new double[]{3,2,5,6.5};
    try {
      p=interpolator.interpolate(wxval,yval,zval);
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException e) {
    }
    double[] wyval=new double[]{-4,-3,-1,-1};
    try {
      p=interpolator.interpolate(xval,wyval,zval);
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException e) {
    }
    double[][] wzval=new double[xval.length][yval.length + 1];
    try {
      p=interpolator.interpolate(xval,yval,wzval);
      Assert.fail("an exception should have been thrown");
    }
 catch (    DimensionMismatchException e) {
    }
    wzval=new double[xval.length - 1][yval.length];
    try {
      p=interpolator.interpolate(xval,yval,wzval);
      Assert.fail("an exception should have been thrown");
    }
 catch (    DimensionMismatchException e) {
    }
  }
  /** 
 * Test of interpolator for a plane.
 * <p>
 * z = 2 x - 3 y + 5
 */
  @Test public void testPlane(){
    BivariateFunction f=new BivariateFunction(){
      public double value(      double x,      double y){
        return 2 * x - 3 * y + 5;
      }
    }
;
    BivariateGridInterpolator interpolator=new BicubicSplineInterpolator();
    double[] xval=new double[]{3,4,5,6.5};
    double[] yval=new double[]{-4,-3,-1,2,2.5};
    double[][] zval=new double[xval.length][yval.length];
    for (int i=0; i < xval.length; i++) {
      for (int j=0; j < yval.length; j++) {
        zval[i][j]=f.value(xval[i],yval[j]);
      }
    }
    BivariateFunction p=interpolator.interpolate(xval,yval,zval);
    double x, y;
    double expected, result;
    x=4;
    y=-3;
    expected=f.value(x,y);
    result=p.value(x,y);
    Assert.assertEquals("On sample point",expected,result,1e-15);
    x=4.5;
    y=-1.5;
    expected=f.value(x,y);
    result=p.value(x,y);
    Assert.assertEquals("half-way between sample points (middle of the patch)",expected,result,0.3);
    x=3.5;
    y=-3.5;
    expected=f.value(x,y);
    result=p.value(x,y);
    Assert.assertEquals("half-way between sample points (border of the patch)",expected,result,0.3);
  }
  /** 
 * Test of interpolator for a paraboloid.
 * <p>
 * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
 */
  @Test public void testParaboloid(){
    BivariateFunction f=new BivariateFunction(){
      public double value(      double x,      double y){
        return 2 * x * x - 3 * y * y + 4 * x * y - 5;
      }
    }
;
    BivariateGridInterpolator interpolator=new BicubicSplineInterpolator();
    double[] xval=new double[]{3,4,5,6.5};
    double[] yval=new double[]{-4,-3,-2,-1,0.5,2.5};
    double[][] zval=new double[xval.length][yval.length];
    for (int i=0; i < xval.length; i++) {
      for (int j=0; j < yval.length; j++) {
        zval[i][j]=f.value(xval[i],yval[j]);
      }
    }
    BivariateFunction p=interpolator.interpolate(xval,yval,zval);
    double x, y;
    double expected, result;
    x=5;
    y=0.5;
    expected=f.value(x,y);
    result=p.value(x,y);
    Assert.assertEquals("On sample point",expected,result,1e-13);
    x=4.5;
    y=-1.5;
    expected=f.value(x,y);
    result=p.value(x,y);
    Assert.assertEquals("half-way between sample points (middle of the patch)",expected,result,0.2);
    x=3.5;
    y=-3.5;
    expected=f.value(x,y);
    result=p.value(x,y);
    Assert.assertEquals("half-way between sample points (border of the patch)",expected,result,0.2);
  }
}