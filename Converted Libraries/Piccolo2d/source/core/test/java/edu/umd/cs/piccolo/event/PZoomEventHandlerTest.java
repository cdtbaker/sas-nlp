package edu.umd.cs.piccolo.event;
import junit.framework.TestCase;
/** 
 * Unit test for PZoomEventHandler.
 */
public class PZoomEventHandlerTest extends TestCase {
  public PZoomEventHandlerTest(  final String name){
    super(name);
  }
  public void testToString(){
    final PZoomEventHandler zoomEventHandler=new PZoomEventHandler();
    assertNotNull(zoomEventHandler.toString());
  }
}
