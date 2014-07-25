package org.apache.commons.math3.stat.descriptive.summary;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id: ProductTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class ProductTest extends StorelessUnivariateStatisticAbstractTest {
  protected Product stat;
  /** 
 * {@inheritDoc}
 */
  @Override public UnivariateStatistic getUnivariateStatistic(){
    return new Product();
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double getTolerance(){
    return 10E8;
  }
  /** 
 * {@inheritDoc}
 */
  @Override public double expectedValue(){
    return this.product;
  }
  /** 
 * Expected value for  the testArray defined in UnivariateStatisticAbstractTest 
 */
  public double expectedWeightedValue(){
    return this.weightedProduct;
  }
  @Test public void testSpecialValues(){
    Product product=new Product();
    Assert.assertEquals(1,product.getResult(),0);
    product.increment(1);
    Assert.assertEquals(1,product.getResult(),0);
    product.increment(Double.POSITIVE_INFINITY);
    Assert.assertEquals(Double.POSITIVE_INFINITY,product.getResult(),0);
    product.increment(Double.NEGATIVE_INFINITY);
    Assert.assertEquals(Double.NEGATIVE_INFINITY,product.getResult(),0);
    product.increment(Double.NaN);
    Assert.assertTrue(Double.isNaN(product.getResult()));
    product.increment(1);
    Assert.assertTrue(Double.isNaN(product.getResult()));
  }
  @Test public void testWeightedProduct(){
    Product product=new Product();
    Assert.assertEquals(expectedWeightedValue(),product.evaluate(testArray,testWeightsArray,0,testArray.length),getTolerance());
    Assert.assertEquals(expectedValue(),product.evaluate(testArray,unitWeightsArray,0,testArray.length),getTolerance());
  }
  @Override protected void checkClearValue(  StorelessUnivariateStatistic statistic){
    Assert.assertEquals(1,statistic.getResult(),0);
  }
}
