package org.apache.commons.math3.exception;
import java.text.MessageFormat;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link TooManyEvaluationsException}.
 * @version $Id$
 */
public class TooManyEvaluationsExceptionTest {
  @Test public void testMessage(){
    final int max=12345;
    final TooManyEvaluationsException e=new TooManyEvaluationsException(max);
    final String msg=e.getLocalizedMessage();
    Assert.assertTrue(msg,msg.matches(".*?" + MessageFormat.format("{0}",max) + ".*"));
  }
}
