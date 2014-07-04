package org.apache.commons.math3.util;
import org.junit.Assert;
import org.junit.Test;
/** 
 * @version $Id: ContinuedFractionTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class ContinuedFractionTest {
  @Test public void testGoldenRatio() throws Exception {
    ContinuedFraction cf=new ContinuedFraction(){
      @Override public double getA(      int n,      double x){
        return 1.0;
      }
      @Override public double getB(      int n,      double x){
        return 1.0;
      }
    }
;
    double gr=cf.evaluate(0.0,10e-9);
    Assert.assertEquals(1.61803399,gr,10e-9);
  }
}
