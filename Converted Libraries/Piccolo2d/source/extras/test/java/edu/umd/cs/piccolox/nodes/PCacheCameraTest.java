package edu.umd.cs.piccolox.nodes;
import junit.framework.TestCase;
/** 
 * Unit test for PCacheCamera.
 */
public class PCacheCameraTest extends TestCase {
  public void testClone(){
    PCacheCamera camera=new PCacheCamera();
    PCacheCamera cloned=(PCacheCamera)camera.clone();
    assertNotNull(cloned);
  }
}
