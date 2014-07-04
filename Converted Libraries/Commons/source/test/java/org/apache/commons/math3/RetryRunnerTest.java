package org.apache.commons.math3;
import java.util.Random;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.junit.Test;
import org.junit.runner.RunWith;
/** 
 * Test for the "Retry" functionality (retrying Junit test methods).
 */
@RunWith(RetryRunner.class) public class RetryRunnerTest {
  final Random rng=new Random();
  /** 
 * Shows that an always failing test will fail even if it is retried.
 */
  @Test(expected=MathIllegalStateException.class) @Retry public void testRetryFailAlways(){
    throw new MathIllegalStateException();
  }
  /** 
 * Shows that a test that sometimes fail might succeed if it is retried.
 * In this case the high number of retries makes it quite unlikely that
 * the exception will be thrown by all of the calls.
 */
  @Test @Retry(100) public void testRetryFailSometimes(){
    if (rng.nextBoolean()) {
      throw new MathIllegalStateException();
    }
  }
}
