package org.apache.commons.math3.filter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Tests for {@link KalmanFilter}.
 * @version $Id$
 */
public class KalmanFilterTest {
  @Test(expected=MatrixDimensionMismatchException.class) public void testTransitionMeasurementMatrixMismatch(){
    RealMatrix A=new Array2DRowRealMatrix(new double[]{1d});
    RealMatrix B=null;
    RealMatrix H=new Array2DRowRealMatrix(new double[]{1d,1d});
    RealMatrix Q=new Array2DRowRealMatrix(new double[]{0});
    RealMatrix R=new Array2DRowRealMatrix(new double[]{0});
    ProcessModel pm=new DefaultProcessModel(A,B,Q,new ArrayRealVector(new double[]{0}),null);
    MeasurementModel mm=new DefaultMeasurementModel(H,R);
    new KalmanFilter(pm,mm);
    Assert.fail("transition and measurement matrix should not be compatible");
  }
  @Test(expected=MatrixDimensionMismatchException.class) public void testTransitionControlMatrixMismatch(){
    RealMatrix A=new Array2DRowRealMatrix(new double[]{1d});
    RealMatrix B=new Array2DRowRealMatrix(new double[]{1d,1d});
    RealMatrix H=new Array2DRowRealMatrix(new double[]{1d});
    RealMatrix Q=new Array2DRowRealMatrix(new double[]{0});
    RealMatrix R=new Array2DRowRealMatrix(new double[]{0});
    ProcessModel pm=new DefaultProcessModel(A,B,Q,new ArrayRealVector(new double[]{0}),null);
    MeasurementModel mm=new DefaultMeasurementModel(H,R);
    new KalmanFilter(pm,mm);
    Assert.fail("transition and control matrix should not be compatible");
  }
  @Test public void testConstant(){
    double constantValue=10d;
    double measurementNoise=0.1d;
    double processNoise=1e-5d;
    RealMatrix A=new Array2DRowRealMatrix(new double[]{1d});
    RealMatrix B=null;
    RealMatrix H=new Array2DRowRealMatrix(new double[]{1d});
    RealVector x=new ArrayRealVector(new double[]{constantValue});
    RealMatrix Q=new Array2DRowRealMatrix(new double[]{processNoise});
    RealMatrix R=new Array2DRowRealMatrix(new double[]{measurementNoise});
    ProcessModel pm=new DefaultProcessModel(A,B,Q,new ArrayRealVector(new double[]{constantValue}),null);
    MeasurementModel mm=new DefaultMeasurementModel(H,R);
    KalmanFilter filter=new KalmanFilter(pm,mm);
    Assert.assertEquals(1,filter.getMeasurementDimension());
    Assert.assertEquals(1,filter.getStateDimension());
    assertMatrixEquals(Q.getData(),filter.getErrorCovariance());
    double[] expectedInitialState=new double[]{constantValue};
    assertVectorEquals(expectedInitialState,filter.getStateEstimation());
    RealVector pNoise=new ArrayRealVector(1);
    RealVector mNoise=new ArrayRealVector(1);
    RandomGenerator rand=new JDKRandomGenerator();
    for (int i=0; i < 60; i++) {
      filter.predict();
      pNoise.setEntry(0,processNoise * rand.nextGaussian());
      x=A.operate(x).add(pNoise);
      mNoise.setEntry(0,measurementNoise * rand.nextGaussian());
      RealVector z=H.operate(x).add(mNoise);
      filter.correct(z);
      double diff=Math.abs(constantValue - filter.getStateEstimation()[0]);
      Assert.assertTrue(Precision.compareTo(diff,measurementNoise,1e-6) < 0);
    }
    Assert.assertTrue(Precision.compareTo(filter.getErrorCovariance()[0][0],0.02d,1e-6) < 0);
  }
  @Test public void testConstantAcceleration(){
    double dt=0.1d;
    double measurementNoise=10d;
    double accelNoise=0.2d;
    RealMatrix A=new Array2DRowRealMatrix(new double[][]{{1,dt},{0,1}});
    RealMatrix B=new Array2DRowRealMatrix(new double[][]{{Math.pow(dt,2d) / 2d},{dt}});
    RealMatrix H=new Array2DRowRealMatrix(new double[][]{{1d,0d}});
    RealVector x=new ArrayRealVector(new double[]{0,0});
    RealMatrix tmp=new Array2DRowRealMatrix(new double[][]{{Math.pow(dt,4d) / 4d,Math.pow(dt,3d) / 2d},{Math.pow(dt,3d) / 2d,Math.pow(dt,2d)}});
    RealMatrix Q=tmp.scalarMultiply(Math.pow(accelNoise,2));
    RealMatrix P0=new Array2DRowRealMatrix(new double[][]{{1,1},{1,1}});
    RealMatrix R=new Array2DRowRealMatrix(new double[]{Math.pow(measurementNoise,2)});
    RealVector u=new ArrayRealVector(new double[]{0.1d});
    ProcessModel pm=new DefaultProcessModel(A,B,Q,x,P0);
    MeasurementModel mm=new DefaultMeasurementModel(H,R);
    KalmanFilter filter=new KalmanFilter(pm,mm);
    Assert.assertEquals(1,filter.getMeasurementDimension());
    Assert.assertEquals(2,filter.getStateDimension());
    assertMatrixEquals(P0.getData(),filter.getErrorCovariance());
    double[] expectedInitialState=new double[]{0.0,0.0};
    assertVectorEquals(expectedInitialState,filter.getStateEstimation());
    RandomGenerator rand=new JDKRandomGenerator();
    RealVector tmpPNoise=new ArrayRealVector(new double[]{Math.pow(dt,2d) / 2d,dt});
    RealVector mNoise=new ArrayRealVector(1);
    for (int i=0; i < 60; i++) {
      filter.predict(u);
      RealVector pNoise=tmpPNoise.mapMultiply(accelNoise * rand.nextGaussian());
      x=A.operate(x).add(B.operate(u)).add(pNoise);
      mNoise.setEntry(0,measurementNoise * rand.nextGaussian());
      RealVector z=H.operate(x).add(mNoise);
      filter.correct(z);
      double diff=Math.abs(x.getEntry(0) - filter.getStateEstimation()[0]);
      Assert.assertTrue(Precision.compareTo(diff,measurementNoise,1e-6) < 0);
    }
    Assert.assertTrue(Precision.compareTo(filter.getErrorCovariance()[1][1],0.1d,1e-6) < 0);
  }
  private void assertVectorEquals(  double[] expected,  double[] result){
    Assert.assertEquals("Wrong number of rows.",expected.length,result.length);
    for (int i=0; i < expected.length; i++) {
      Assert.assertEquals("Wrong value at position [" + i + "]",expected[i],result[i],1.0e-6);
    }
  }
  private void assertMatrixEquals(  double[][] expected,  double[][] result){
    Assert.assertEquals("Wrong number of rows.",expected.length,result.length);
    for (int i=0; i < expected.length; i++) {
      Assert.assertEquals("Wrong number of columns.",expected[i].length,result[i].length);
      for (int j=0; j < expected[i].length; j++) {
        Assert.assertEquals("Wrong value at position [" + i + ","+ j+ "]",expected[i][j],result[i][j],1.0e-6);
      }
    }
  }
}
