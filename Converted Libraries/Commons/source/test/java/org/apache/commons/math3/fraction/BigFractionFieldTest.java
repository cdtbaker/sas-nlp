package org.apache.commons.math3.fraction;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
public class BigFractionFieldTest {
  @Test public void testZero(){
    Assert.assertEquals(BigFraction.ZERO,BigFractionField.getInstance().getZero());
  }
  @Test public void testOne(){
    Assert.assertEquals(BigFraction.ONE,BigFractionField.getInstance().getOne());
  }
  @Test public void testSerial(){
    BigFractionField field=BigFractionField.getInstance();
    Assert.assertTrue(field == TestUtils.serializeAndRecover(field));
  }
}
