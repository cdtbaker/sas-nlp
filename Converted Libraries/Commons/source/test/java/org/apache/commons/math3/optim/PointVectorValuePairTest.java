package org.apache.commons.math3.optim;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
public class PointVectorValuePairTest {
  @Test public void testSerial(){
    PointVectorValuePair pv1=new PointVectorValuePair(new double[]{1.0,2.0,3.0},new double[]{4.0,5.0});
    PointVectorValuePair pv2=(PointVectorValuePair)TestUtils.serializeAndRecover(pv1);
    Assert.assertEquals(pv1.getKey().length,pv2.getKey().length);
    for (int i=0; i < pv1.getKey().length; ++i) {
      Assert.assertEquals(pv1.getKey()[i],pv2.getKey()[i],1.0e-15);
    }
    Assert.assertEquals(pv1.getValue().length,pv2.getValue().length);
    for (int i=0; i < pv1.getValue().length; ++i) {
      Assert.assertEquals(pv1.getValue()[i],pv2.getValue()[i],1.0e-15);
    }
  }
}
