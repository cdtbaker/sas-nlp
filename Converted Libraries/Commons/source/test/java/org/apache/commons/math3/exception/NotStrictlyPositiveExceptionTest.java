package org.apache.commons.math3.exception;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link NotStrictlyPositiveException}.
 * @version $Id$
 */
public class NotStrictlyPositiveExceptionTest {
  @Test public void testAccessors(){
    final NotStrictlyPositiveException e=new NotStrictlyPositiveException(0);
    Assert.assertEquals(0,e.getArgument());
    Assert.assertEquals(0,e.getMin());
    Assert.assertFalse(e.getBoundIsAllowed());
  }
}
