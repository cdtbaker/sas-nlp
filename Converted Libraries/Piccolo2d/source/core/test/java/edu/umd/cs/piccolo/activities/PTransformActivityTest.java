package edu.umd.cs.piccolo.activities;
import junit.framework.TestCase;
/** 
 * Unit test for PTransformActivity.
 */
public class PTransformActivityTest extends TestCase {
  public PTransformActivityTest(  final String name){
    super(name);
  }
  public void testToString(){
    final PTransformActivity transformActivity=new PTransformActivity(1000,0,null);
    assertNotNull(transformActivity.toString());
  }
}
