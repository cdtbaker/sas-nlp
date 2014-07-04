package org.apache.commons.math3.exception;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link DimensionMismatchException}.
 * @version $Id$
 */
public class DimensionMismatchExceptionTest {
  @Test public void testAccessors(){
    final DimensionMismatchException e=new DimensionMismatchException(1,2);
    Assert.assertEquals(1,e.getArgument());
    Assert.assertEquals(2,e.getDimension());
  }
}
