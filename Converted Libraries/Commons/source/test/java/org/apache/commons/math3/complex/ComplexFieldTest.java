package org.apache.commons.math3.complex;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
public class ComplexFieldTest {
  @Test public void testZero(){
    Assert.assertEquals(Complex.ZERO,ComplexField.getInstance().getZero());
  }
  @Test public void testOne(){
    Assert.assertEquals(Complex.ONE,ComplexField.getInstance().getOne());
  }
  @Test public void testSerial(){
    ComplexField field=ComplexField.getInstance();
    Assert.assertTrue(field == TestUtils.serializeAndRecover(field));
  }
}
