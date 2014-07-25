package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.Test;
public final class OpenMapRealMatrixTest {
  @Test(expected=NumberIsTooLargeException.class) public void testMath679(){
    new OpenMapRealMatrix(3,Integer.MAX_VALUE);
  }
}
