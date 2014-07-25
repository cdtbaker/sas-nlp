package edu.umd.cs.piccolox.nodes;
import junit.framework.TestCase;
/** 
 * Unit test for PStyledText.
 */
public final class PStyledTextTest extends TestCase {
  public void testClone(){
    PStyledText text=new PStyledText();
    PStyledText clone=(PStyledText)text.clone();
    assertNotNull(clone);
  }
}
