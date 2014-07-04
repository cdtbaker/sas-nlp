package org.apache.commons.math3.optimization.univariate;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link BracketFinder}.
 */
public class BracketFinderTest {
  @Test public void testCubicMin(){
    final BracketFinder bFind=new BracketFinder();
    final UnivariateFunction func=new UnivariateFunction(){
      public double value(      double x){
        if (x < -2) {
          return value(-2);
        }
 else {
          return (x - 1) * (x + 2) * (x + 3);
        }
      }
    }
;
    bFind.search(func,GoalType.MINIMIZE,-2,-1);
    final double tol=1e-15;
    Assert.assertEquals(-2,bFind.getLo(),tol);
    Assert.assertEquals(-1,bFind.getMid(),tol);
    Assert.assertEquals(0.61803399999999997,bFind.getHi(),tol);
  }
  @Test public void testCubicMax(){
    final BracketFinder bFind=new BracketFinder();
    final UnivariateFunction func=new UnivariateFunction(){
      public double value(      double x){
        if (x < -2) {
          return value(-2);
        }
 else {
          return -(x - 1) * (x + 2) * (x + 3);
        }
      }
    }
;
    bFind.search(func,GoalType.MAXIMIZE,-2,-1);
    final double tol=1e-15;
    Assert.assertEquals(-2,bFind.getLo(),tol);
    Assert.assertEquals(-1,bFind.getMid(),tol);
    Assert.assertEquals(0.61803399999999997,bFind.getHi(),tol);
  }
  @Test public void testMinimumIsOnIntervalBoundary(){
    final UnivariateFunction func=new UnivariateFunction(){
      public double value(      double x){
        return x * x;
      }
    }
;
    final BracketFinder bFind=new BracketFinder();
    bFind.search(func,GoalType.MINIMIZE,0,1);
    Assert.assertTrue(bFind.getLo() <= 0);
    Assert.assertTrue(0 <= bFind.getHi());
    bFind.search(func,GoalType.MINIMIZE,-1,0);
    Assert.assertTrue(bFind.getLo() <= 0);
    Assert.assertTrue(0 <= bFind.getHi());
  }
  @Test public void testIntervalBoundsOrdering(){
    final UnivariateFunction func=new UnivariateFunction(){
      public double value(      double x){
        return x * x;
      }
    }
;
    final BracketFinder bFind=new BracketFinder();
    bFind.search(func,GoalType.MINIMIZE,-1,1);
    Assert.assertTrue(bFind.getLo() <= 0);
    Assert.assertTrue(0 <= bFind.getHi());
    bFind.search(func,GoalType.MINIMIZE,1,-1);
    Assert.assertTrue(bFind.getLo() <= 0);
    Assert.assertTrue(0 <= bFind.getHi());
    bFind.search(func,GoalType.MINIMIZE,1,2);
    Assert.assertTrue(bFind.getLo() <= 0);
    Assert.assertTrue(0 <= bFind.getHi());
    bFind.search(func,GoalType.MINIMIZE,2,1);
    Assert.assertTrue(bFind.getLo() <= 0);
    Assert.assertTrue(0 <= bFind.getHi());
  }
}
