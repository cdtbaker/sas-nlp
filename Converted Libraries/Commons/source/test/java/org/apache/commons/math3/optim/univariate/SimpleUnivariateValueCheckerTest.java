package org.apache.commons.math3.optim.univariate;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Test;
import org.junit.Assert;
public class SimpleUnivariateValueCheckerTest {
  @Test(expected=NotStrictlyPositiveException.class) public void testIterationCheckPrecondition(){
    new SimpleUnivariateValueChecker(1e-1,1e-2,0);
  }
  @Test public void testIterationCheck(){
    final int max=10;
    final SimpleUnivariateValueChecker checker=new SimpleUnivariateValueChecker(1e-1,1e-2,max);
    Assert.assertTrue(checker.converged(max,null,null));
    Assert.assertTrue(checker.converged(max + 1,null,null));
  }
  @Test public void testIterationCheckDisabled(){
    final SimpleUnivariateValueChecker checker=new SimpleUnivariateValueChecker(1e-8,1e-8);
    final UnivariatePointValuePair a=new UnivariatePointValuePair(1d,1d);
    final UnivariatePointValuePair b=new UnivariatePointValuePair(10d,10d);
    Assert.assertFalse(checker.converged(-1,a,b));
    Assert.assertFalse(checker.converged(0,a,b));
    Assert.assertFalse(checker.converged(1000000,a,b));
    Assert.assertTrue(checker.converged(-1,a,a));
    Assert.assertTrue(checker.converged(-1,b,b));
  }
}