package org.apache.commons.math3.exception;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link NonMonotonicSequenceException}.
 * @version $Id$
 */
public class NonMonotonicSequenceExceptionTest {
  @Test public void testAccessors(){
    NonMonotonicSequenceException e=new NonMonotonicSequenceException(0,-1,1,MathArrays.OrderDirection.DECREASING,false);
    Assert.assertEquals(0,e.getArgument());
    Assert.assertEquals(-1,e.getPrevious());
    Assert.assertEquals(1,e.getIndex());
    Assert.assertTrue(e.getDirection() == MathArrays.OrderDirection.DECREASING);
    Assert.assertFalse(e.getStrict());
    e=new NonMonotonicSequenceException(-1,0,1);
    Assert.assertEquals(-1,e.getArgument());
    Assert.assertEquals(0,e.getPrevious());
    Assert.assertEquals(1,e.getIndex());
    Assert.assertTrue(e.getDirection() == MathArrays.OrderDirection.INCREASING);
    Assert.assertTrue(e.getStrict());
  }
}
