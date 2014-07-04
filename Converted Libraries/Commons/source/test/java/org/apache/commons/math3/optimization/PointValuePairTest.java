package org.apache.commons.math3.optimization;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
public class PointValuePairTest {
  @Test public void testSerial(){
    PointValuePair pv1=new PointValuePair(new double[]{1.0,2.0,3.0},4.0);
    PointValuePair pv2=(PointValuePair)TestUtils.serializeAndRecover(pv1);
    Assert.assertEquals(pv1.getKey().length,pv2.getKey().length);
    for (int i=0; i < pv1.getKey().length; ++i) {
      Assert.assertEquals(pv1.getKey()[i],pv2.getKey()[i],1.0e-15);
    }
    Assert.assertEquals(pv1.getValue(),pv2.getValue(),1.0e-15);
  }
}
