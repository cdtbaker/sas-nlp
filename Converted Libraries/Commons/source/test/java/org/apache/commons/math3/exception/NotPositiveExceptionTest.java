package org.apache.commons.math3.exception;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link NotPositiveException}.
 * @version $Id$
 */
public class NotPositiveExceptionTest {
  @Test public void testAccessors(){
    final NotPositiveException e=new NotPositiveException(-1);
    Assert.assertEquals(-1,e.getArgument());
    Assert.assertEquals(0,e.getMin());
    Assert.assertTrue(e.getBoundIsAllowed());
  }
}
