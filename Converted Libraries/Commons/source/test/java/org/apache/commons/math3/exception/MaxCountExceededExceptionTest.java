package org.apache.commons.math3.exception;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link MaxCountExceededException}.
 * @version $Id$
 */
public class MaxCountExceededExceptionTest {
  @Test public void testAccessors(){
    final MaxCountExceededException e=new MaxCountExceededException(10);
    Assert.assertEquals(10,e.getMax());
  }
}
