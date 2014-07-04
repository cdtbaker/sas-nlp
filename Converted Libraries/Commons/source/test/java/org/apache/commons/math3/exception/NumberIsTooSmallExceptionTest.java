package org.apache.commons.math3.exception;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link NumberIsTooSmallException}.
 * @version $Id$
 */
public class NumberIsTooSmallExceptionTest {
  @Test public void testAccessors(){
    final NumberIsTooSmallException e=new NumberIsTooSmallException(0,0,false);
    Assert.assertEquals(0,e.getArgument());
    Assert.assertEquals(0,e.getMin());
    Assert.assertFalse(e.getBoundIsAllowed());
  }
}
