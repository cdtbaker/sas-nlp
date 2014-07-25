package org.apache.commons.math3.linear;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link MatrixDimensionMismatchException}.
 * @version $Id$
 */
public class MatrixDimensionMismatchExceptionTest {
  @Test public void testAccessors(){
    final MatrixDimensionMismatchException e=new MatrixDimensionMismatchException(1,2,3,4);
    Assert.assertEquals(1,e.getWrongRowDimension());
    Assert.assertEquals(2,e.getWrongColumnDimension());
    Assert.assertEquals(3,e.getExpectedRowDimension());
    Assert.assertEquals(4,e.getExpectedColumnDimension());
  }
}
