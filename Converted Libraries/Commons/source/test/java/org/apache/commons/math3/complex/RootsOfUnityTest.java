package org.apache.commons.math3.complex;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Unit tests for the {@link RootsOfUnity} class.
 * @version $Id: RootsOfUnityTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class RootsOfUnityTest {
  @Test(expected=MathIllegalStateException.class) public void testMathIllegalState1(){
    final RootsOfUnity roots=new RootsOfUnity();
    roots.getReal(0);
  }
  @Test(expected=MathIllegalStateException.class) public void testMathIllegalState2(){
    final RootsOfUnity roots=new RootsOfUnity();
    roots.getImaginary(0);
  }
  @Test(expected=MathIllegalStateException.class) public void testMathIllegalState3(){
    final RootsOfUnity roots=new RootsOfUnity();
    roots.isCounterClockWise();
  }
  @Test(expected=ZeroException.class) public void testZeroNumberOfRoots(){
    final RootsOfUnity roots=new RootsOfUnity();
    roots.computeRoots(0);
  }
  @Test public void testGetNumberOfRoots(){
    final RootsOfUnity roots=new RootsOfUnity();
    Assert.assertEquals("",0,roots.getNumberOfRoots());
    roots.computeRoots(5);
    Assert.assertEquals("",5,roots.getNumberOfRoots());
    roots.computeRoots(-5);
    Assert.assertEquals("",5,roots.getNumberOfRoots());
    roots.computeRoots(6);
    Assert.assertEquals("",6,roots.getNumberOfRoots());
  }
  @Test public void testComputeRoots(){
    final RootsOfUnity roots=new RootsOfUnity();
    for (int n=-10; n < 11; n++) {
      if (n != 0) {
        roots.computeRoots(n);
        doTestComputeRoots(roots);
        roots.computeRoots(-n);
        doTestComputeRoots(roots);
      }
    }
  }
  private void doTestComputeRoots(  final RootsOfUnity roots){
    final int n=roots.isCounterClockWise() ? roots.getNumberOfRoots() : -roots.getNumberOfRoots();
    final double tol=10 * Math.ulp(1.0);
    for (int k=0; k < n; k++) {
      final double t=2.0 * FastMath.PI * k / n;
      final String msg=String.format("n = %d, k = %d",n,k);
      Assert.assertEquals(msg,FastMath.cos(t),roots.getReal(k),tol);
      Assert.assertEquals(msg,FastMath.sin(t),roots.getImaginary(k),tol);
    }
  }
}
