package org.apache.commons.math3.util;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
public class BigRealFieldTest {
  @Test public void testZero(){
    Assert.assertEquals(BigReal.ZERO,BigRealField.getInstance().getZero());
  }
  @Test public void testOne(){
    Assert.assertEquals(BigReal.ONE,BigRealField.getInstance().getOne());
  }
  @Test public void testSerial(){
    BigRealField field=BigRealField.getInstance();
    Assert.assertTrue(field == TestUtils.serializeAndRecover(field));
  }
}
