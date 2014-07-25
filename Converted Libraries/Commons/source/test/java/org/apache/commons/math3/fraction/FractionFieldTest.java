package org.apache.commons.math3.fraction;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
public class FractionFieldTest {
  @Test public void testZero(){
    Assert.assertEquals(Fraction.ZERO,FractionField.getInstance().getZero());
  }
  @Test public void testOne(){
    Assert.assertEquals(Fraction.ONE,FractionField.getInstance().getOne());
  }
  @Test public void testSerial(){
    FractionField field=FractionField.getInstance();
    Assert.assertTrue(field == TestUtils.serializeAndRecover(field));
  }
}
