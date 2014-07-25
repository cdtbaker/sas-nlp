package org.apache.commons.math3.exception;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link OutOfRangeException}.
 * @version $Id$
 */
public class OutOfRangeExceptionTest {
  @Test public void testAccessors(){
    final OutOfRangeException e=new OutOfRangeException(-1,0,2);
    Assert.assertEquals(-1,e.getArgument());
    Assert.assertEquals(0,e.getLo());
    Assert.assertEquals(2,e.getHi());
  }
}
