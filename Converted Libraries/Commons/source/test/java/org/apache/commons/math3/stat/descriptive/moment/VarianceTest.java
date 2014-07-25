package org.apache.commons.math3.stat.descriptive.moment;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: VarianceTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class VarianceTest extends StorelessUnivariateStatisticAbstractTest {
  protected Variance stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new Variance();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.var;
  }
  /** 
 * Expected value for  the testArray defined in UnivariateStatisticAbstractTest 
 */
  public double expectedWeightedValue(){
    return this.weightedVar;
  }
  /** 
 * Make sure Double.NaN is returned iff n = 0
 */
  @Test public void testNaN(){
    StandardDeviation std=new StandardDeviation();
    Assert.assertTrue(Double.isNaN(std.getResult()));
    std.increment(1d);
    Assert.assertEquals(0d,std.getResult(),0);
  }
  /** 
 * Test population version of variance
 */
  @Test public void testPopulation(){
    double[] values={-1.0d,3.1d,4.0d,-2.1d,22d,11.7d,3d,14d};
    SecondMoment m=new SecondMoment();
    m.evaluate(values);
    Variance v1=new Variance();
    v1.setBiasCorrected(false);
    Assert.assertEquals(populationVariance(values),v1.evaluate(values),1E-14);
    v1.incrementAll(values);
    Assert.assertEquals(populationVariance(values),v1.getResult(),1E-14);
    v1=new Variance(false,m);
    Assert.assertEquals(populationVariance(values),v1.getResult(),1E-14);
    v1=new Variance(false);
    Assert.assertEquals(populationVariance(values),v1.evaluate(values),1E-14);
    v1.incrementAll(values);
    Assert.assertEquals(populationVariance(values),v1.getResult(),1E-14);
  }
  /** 
 * Definitional formula for population variance
 */
  protected double populationVariance(  double[] v){
    double mean=new Mean().evaluate(v);
    double sum=0;
    for (int i=0; i < v.length; i++) {
      sum+=(v[i] - mean) * (v[i] - mean);
    }
    return sum / v.length;
  }
  @Test public void testWeightedVariance(){
    Variance variance=new Variance();
    Assert.assertEquals(expectedWeightedValue(),variance.evaluate(testArray,testWeightsArray,0,testArray.length),getTolerance());
    Assert.assertEquals(expectedValue(),variance.evaluate(testArray,unitWeightsArray,0,testArray.length),getTolerance());
    Assert.assertEquals(expectedValue(),variance.evaluate(testArray,MathArrays.normalizeArray(identicalWeightsArray,testArray.length),0,testArray.length),getTolerance());
  }
}
