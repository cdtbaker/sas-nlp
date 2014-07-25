package org.apache.commons.math3.util;
import java.math.BigDecimal;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * @version $Id: DefaultTransformerTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class DefaultTransformerTest {
  /** 
 */
  @Test public void testTransformDouble() throws Exception {
    double expected=1.0;
    Double input=Double.valueOf(expected);
    DefaultTransformer t=new DefaultTransformer();
    Assert.assertEquals(expected,t.transform(input),1.0e-4);
  }
  /** 
 */
  @Test public void testTransformNull() throws Exception {
    DefaultTransformer t=new DefaultTransformer();
    try {
      t.transform(null);
      Assert.fail("Expecting NullArgumentException");
    }
 catch (    NullArgumentException e) {
    }
  }
  /** 
 */
  @Test public void testTransformInteger() throws Exception {
    double expected=1.0;
    Integer input=Integer.valueOf(1);
    DefaultTransformer t=new DefaultTransformer();
    Assert.assertEquals(expected,t.transform(input),1.0e-4);
  }
  /** 
 */
  @Test public void testTransformBigDecimal() throws Exception {
    double expected=1.0;
    BigDecimal input=new BigDecimal("1.0");
    DefaultTransformer t=new DefaultTransformer();
    Assert.assertEquals(expected,t.transform(input),1.0e-4);
  }
  /** 
 */
  @Test public void testTransformString() throws Exception {
    double expected=1.0;
    String input="1.0";
    DefaultTransformer t=new DefaultTransformer();
    Assert.assertEquals(expected,t.transform(input),1.0e-4);
  }
  /** 
 */
  @Test(expected=MathIllegalArgumentException.class) public void testTransformObject(){
    Boolean input=Boolean.TRUE;
    DefaultTransformer t=new DefaultTransformer();
    t.transform(input);
  }
  @Test public void testSerial(){
    Assert.assertEquals(new DefaultTransformer(),TestUtils.serializeAndRecover(new DefaultTransformer()));
  }
}
