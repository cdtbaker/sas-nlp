package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test case for the "microsphere projection" interpolator.
 * @version $Id: MicrosphereInterpolatorTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public final class MicrosphereInterpolatorTest {
  /** 
 * Test of interpolator for a plane.
 * <p>
 * y = 2 x<sub>1</sub> - 3 x<sub>2</sub> + 5
 */
  @Test public void testLinearFunction2D(){
    MultivariateFunction f=new MultivariateFunction(){
      public double value(      double[] x){
        if (x.length != 2) {
          throw new IllegalArgumentException();
        }
        return 2 * x[0] - 3 * x[1] + 5;
      }
    }
;
    MultivariateInterpolator interpolator=new MicrosphereInterpolator();
    final int n=9;
    final int dim=2;
    double[][] x=new double[n][dim];
    double[] y=new double[n];
    int index=0;
    for (int i=-1; i <= 1; i++) {
      for (int j=-1; j <= 1; j++) {
        x[index][0]=i;
        x[index][1]=j;
        y[index]=f.value(x[index]);
        ++index;
      }
    }
    MultivariateFunction p=interpolator.interpolate(x,y);
    double[] c=new double[dim];
    double expected, result;
    c[0]=0;
    c[1]=0;
    expected=f.value(c);
    result=p.value(c);
    Assert.assertEquals("On sample point",expected,result,FastMath.ulp(1d));
    c[0]=0 + 1e-5;
    c[1]=1 - 1e-5;
    expected=f.value(c);
    result=p.value(c);
    Assert.assertEquals("1e-5 away from sample point",expected,result,1e-4);
  }
  /** 
 * Test of interpolator for a quadratic function.
 * <p>
 * y = 2 x<sub>1</sub><sup>2</sup> - 3 x<sub>2</sub><sup>2</sup>
 * + 4 x<sub>1</sub> x<sub>2</sub> - 5
 */
  @Test public void testParaboloid2D(){
    MultivariateFunction f=new MultivariateFunction(){
      public double value(      double[] x){
        if (x.length != 2) {
          throw new IllegalArgumentException();
        }
        return 2 * x[0] * x[0] - 3 * x[1] * x[1] + 4 * x[0] * x[1] - 5;
      }
    }
;
    MultivariateInterpolator interpolator=new MicrosphereInterpolator();
    final int n=121;
    final int dim=2;
    double[][] x=new double[n][dim];
    double[] y=new double[n];
    int index=0;
    for (int i=-10; i <= 10; i+=2) {
      for (int j=-10; j <= 10; j+=2) {
        x[index][0]=i;
        x[index][1]=j;
        y[index]=f.value(x[index]);
        ++index;
      }
    }
    MultivariateFunction p=interpolator.interpolate(x,y);
    double[] c=new double[dim];
    double expected, result;
    c[0]=0;
    c[1]=0;
    expected=f.value(c);
    result=p.value(c);
    Assert.assertEquals("On sample point",expected,result,FastMath.ulp(1d));
    c[0]=2 + 1e-5;
    c[1]=2 - 1e-5;
    expected=f.value(c);
    result=p.value(c);
    Assert.assertEquals("1e-5 away from sample point",expected,result,1e-3);
  }
}
