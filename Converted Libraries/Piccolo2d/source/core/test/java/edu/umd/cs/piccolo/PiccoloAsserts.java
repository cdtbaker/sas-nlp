package edu.umd.cs.piccolo;
import java.awt.geom.Dimension2D;
import junit.framework.Assert;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
/** 
 * This class provides helper methods to help with testing.
 * It's implemented this way, as opposed to as a subclass, because when we move
 * to JUnit4, inheritance is not the preferred way of importing asserts.
 */
public final class PiccoloAsserts {
  private PiccoloAsserts(){
  }
  public static final void assertEquals(  final PBounds expected,  final PBounds actual,  final double errorRate){
    assertEquals("Expected " + expected + " but was "+ actual,expected,actual,errorRate);
  }
  public static final void assertEquals(  final String message,  final PBounds expected,  final PBounds actual,  final double errorRate){
    Assert.assertEquals(message,expected.getX(),actual.getX(),errorRate);
    Assert.assertEquals(message,expected.getY(),actual.getY(),errorRate);
    Assert.assertEquals(message,expected.getWidth(),actual.getWidth(),errorRate);
    Assert.assertEquals(message,expected.getHeight(),actual.getHeight(),errorRate);
  }
  public static void assertEquals(  final PDimension expected,  final Dimension2D actual,  final double errorRate){
    assertEquals("Expected " + expected + " but was "+ actual,expected,actual,errorRate);
  }
  public static void assertEquals(  final String message,  final PDimension expected,  final Dimension2D actual,  final double errorRate){
    Assert.assertEquals(message,expected.getWidth(),actual.getWidth(),errorRate);
    Assert.assertEquals(message,expected.getHeight(),actual.getHeight(),errorRate);
  }
  public static void assertEquals(  final String[] expected,  final String[] actual){
    Assert.assertEquals("arrays are not same size",expected.length,actual.length);
    for (int i=0; i < expected.length; i++) {
      Assert.assertEquals(expected[i],expected[i]);
    }
  }
}
