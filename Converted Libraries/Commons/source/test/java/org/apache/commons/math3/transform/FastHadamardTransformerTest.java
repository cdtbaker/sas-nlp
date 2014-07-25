package org.apache.commons.math3.transform;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;
/** 
 * JUnit Test for HadamardTransformerTest
 * @see org.apache.commons.math3.transform.FastHadamardTransformer
 */
public final class FastHadamardTransformerTest {
  /** 
 * Test of transformer for the a 8-point FHT (means n=8)
 */
  @Test public void test8Points(){
    checkAllTransforms(new int[]{1,4,-2,3,0,1,4,-1},new int[]{10,-4,2,-4,2,-12,6,8});
  }
  /** 
 * Test of transformer for the a 4-points FHT (means n=4)
 */
  @Test public void test4Points(){
    checkAllTransforms(new int[]{1,2,3,4},new int[]{10,-2,-4,0});
  }
  /** 
 * Test the inverse transform of an integer vector is not always an integer vector
 */
  @Test public void testNoIntInverse(){
    FastHadamardTransformer transformer=new FastHadamardTransformer();
    double[] x=transformer.transform(new double[]{0,1,0,1},TransformType.INVERSE);
    Assert.assertEquals(0.5,x[0],0);
    Assert.assertEquals(-0.5,x[1],0);
    Assert.assertEquals(0.0,x[2],0);
    Assert.assertEquals(0.0,x[3],0);
  }
  /** 
 * Test of transformer for wrong number of points
 */
  @Test public void test3Points(){
    try {
      new FastHadamardTransformer().transform(new double[3],TransformType.FORWARD);
      Assert.fail("an exception should have been thrown");
    }
 catch (    IllegalArgumentException iae) {
    }
  }
  private void checkAllTransforms(  int[] x,  int[] y){
    checkDoubleTransform(x,y);
    checkInverseDoubleTransform(x,y);
    checkIntTransform(x,y);
  }
  private void checkDoubleTransform(  int[] x,  int[] y){
    FastHadamardTransformer transformer=new FastHadamardTransformer();
    double[] dX=new double[x.length];
    for (int i=0; i < dX.length; ++i) {
      dX[i]=x[i];
    }
    double dResult[]=transformer.transform(dX,TransformType.FORWARD);
    for (int i=0; i < dResult.length; i++) {
      Assert.assertTrue(Precision.equals(y[i],dResult[i],1));
    }
  }
  private void checkIntTransform(  int[] x,  int[] y){
    FastHadamardTransformer transformer=new FastHadamardTransformer();
    int iResult[]=transformer.transform(x);
    for (int i=0; i < iResult.length; i++) {
      Assert.assertEquals(y[i],iResult[i]);
    }
  }
  private void checkInverseDoubleTransform(  int[] x,  int[] y){
    FastHadamardTransformer transformer=new FastHadamardTransformer();
    double[] dY=new double[y.length];
    for (int i=0; i < dY.length; ++i) {
      dY[i]=y[i];
    }
    double dResult[]=transformer.transform(dY,TransformType.INVERSE);
    for (int i=0; i < dResult.length; i++) {
      Assert.assertTrue(Precision.equals(x[i],dResult[i],1));
    }
  }
}
