package edu.umd.cs.piccolo.activities;
import edu.umd.cs.piccolo.util.PUtil;
import junit.framework.TestCase;
/** 
 * Unit test for PInterpolatingActivity.
 */
public class PInterpolatingActivityTest extends TestCase {
  public void testConstructorLong(){
    PInterpolatingActivity activity=new PInterpolatingActivity(1L);
    assertNotNull(activity);
    assertEquals(1L,activity.getDuration());
    assertEquals(1,activity.getLoopCount());
    assertEquals(PUtil.DEFAULT_ACTIVITY_STEP_RATE,activity.getStepRate());
    assertEquals(PInterpolatingActivity.SOURCE_TO_DESTINATION,activity.getMode());
  }
}
