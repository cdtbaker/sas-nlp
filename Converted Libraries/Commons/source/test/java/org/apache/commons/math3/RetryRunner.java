package org.apache.commons.math3;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
/** 
 * A test runner that retries tests when assertions fail.
 * @version $Id: RetryRunner.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class RetryRunner extends BlockJUnit4ClassRunner {
  /** 
 * Simple constructor.
 * @param testClass Class to test.
 * @throws InitializationError if default runner cannot be built.
 */
  public RetryRunner(  final Class<?> testClass) throws InitializationError {
    super(testClass);
  }
  @Override public Statement methodInvoker(  final FrameworkMethod method,  Object test){
    final Statement singleTryStatement=super.methodInvoker(method,test);
    return new Statement(){
      /** 
 * Evaluate the statement.
 * We attempt several runs for the test, at most MAX_ATTEMPTS.
 * if one attempt succeeds, we succeed, if all attempts fail, we
 * fail with the reason corresponding to the last attempt
 */
      @Override public void evaluate() throws Throwable {
        Throwable failureReason=null;
        final Retry retry=method.getAnnotation(Retry.class);
        if (retry == null) {
          singleTryStatement.evaluate();
        }
 else {
          final int numRetries=retry.value();
          for (int i=0; i < numRetries; ++i) {
            try {
              singleTryStatement.evaluate();
              return;
            }
 catch (            Throwable t) {
              failureReason=t;
            }
          }
          throw failureReason;
        }
      }
    }
;
  }
}
