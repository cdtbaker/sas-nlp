package org.apache.commons.math3.optim;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Test;
import org.junit.Assert;
public class SimpleValueCheckerTest {
  @Test(expected=NotStrictlyPositiveException.class) public void testIterationCheckPrecondition(){
    new SimpleValueChecker(1e-1,1e-2,0);
  }
  @Test public void testIterationCheck(){
    final int max=10;
    final SimpleValueChecker checker=new SimpleValueChecker(1e-1,1e-2,max);
    Assert.assertTrue(checker.converged(max,null,null));
    Assert.assertTrue(checker.converged(max + 1,null,null));
  }
  @Test public void testIterationCheckDisabled(){
    final SimpleValueChecker checker=new SimpleValueChecker(1e-8,1e-8);
    final PointValuePair a=new PointValuePair(new double[]{1d},1d);
    final PointValuePair b=new PointValuePair(new double[]{10d},10d);
    Assert.assertFalse(checker.converged(-1,a,b));
    Assert.assertFalse(checker.converged(0,a,b));
    Assert.assertFalse(checker.converged(1000000,a,b));
    Assert.assertTrue(checker.converged(-1,a,a));
    Assert.assertTrue(checker.converged(-1,b,b));
  }
}
