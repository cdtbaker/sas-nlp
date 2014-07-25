package edu.umd.cs.piccolox.nodes;
import junit.framework.TestCase;
/** 
 * Unit test for PClip.
 */
public class PClipTest extends TestCase {
  public void testClone(){
    PClip clip=new PClip();
    PClip cloned=(PClip)clip.clone();
    assertNotNull(cloned);
  }
}
