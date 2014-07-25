package org.apache.commons.math3.exception;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link NumberIsTooLargeException}.
 * @version $Id$
 */
public class NumberIsTooLargeExceptionTest {
  @Test public void testAccessors(){
    final NumberIsTooLargeException e=new NumberIsTooLargeException(1,0,true);
    Assert.assertEquals(1,e.getArgument());
    Assert.assertEquals(0,e.getMax());
    Assert.assertTrue(e.getBoundIsAllowed());
  }
}
