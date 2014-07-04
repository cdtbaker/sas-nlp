package org.apache.commons.math3.distribution;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for AbstractIntegerDistribution default implementations.
 * @version $Id$
 */
public class AbstractIntegerDistributionTest {
  protected final DiceDistribution diceDistribution=new DiceDistribution();
  protected final double p=diceDistribution.probability(1);
  @Test public void testCumulativeProbabilitiesSingleArguments(){
    for (int i=1; i < 7; i++) {
      Assert.assertEquals(p * i,diceDistribution.cumulativeProbability(i),Double.MIN_VALUE);
    }
    Assert.assertEquals(0.0,diceDistribution.cumulativeProbability(0),Double.MIN_VALUE);
    Assert.assertEquals(1.0,diceDistribution.cumulativeProbability(7),Double.MIN_VALUE);
  }
  @Test public void testCumulativeProbabilitiesRangeArguments(){
    int lower=0;
    int upper=6;
    for (int i=0; i < 2; i++) {
      Assert.assertEquals(1 - p * 2 * i,diceDistribution.cumulativeProbability(lower,upper),1E-12);
      lower++;
      upper--;
    }
    for (int i=0; i < 6; i++) {
      Assert.assertEquals(p,diceDistribution.cumulativeProbability(i,i + 1),1E-12);
    }
  }
  /** 
 * Simple distribution modeling a 6-sided die
 */
class DiceDistribution extends AbstractIntegerDistribution {
    public static final long serialVersionUID=23734213;
    private final double p=1d / 6d;
    public DiceDistribution(){
      super(null);
    }
    public double probability(    int x){
      if (x < 1 || x > 6) {
        return 0;
      }
 else {
        return p;
      }
    }
    public double cumulativeProbability(    int x){
      if (x < 1) {
        return 0;
      }
 else       if (x >= 6) {
        return 1;
      }
 else {
        return p * x;
      }
    }
    public double getNumericalMean(){
      return 3.5;
    }
    public double getNumericalVariance(){
      return 12.5 - 3.5 * 3.5;
    }
    public int getSupportLowerBound(){
      return 1;
    }
    public int getSupportUpperBound(){
      return 6;
    }
    public final boolean isSupportConnected(){
      return true;
    }
  }
}
