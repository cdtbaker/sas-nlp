package org.apache.commons.math3.distribution;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for {@link KolmogorovSmirnovDistribution}.
 * @version $Id: KolmogorovSmirnovDistributionTest.java 1364028 2012-07-21 00:42:49Z erans $
 */
public class KolmogorovSmirnovDistributionTest {
  private static final double TOLERANCE=10e-10;
  @Test public void testCumulativeDensityFunction(){
    KolmogorovSmirnovDistribution dist;
    dist=new KolmogorovSmirnovDistribution(200);
    Assert.assertEquals(4.907829957616471622388047046469198862537e-86,dist.cdf(0.005,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(200);
    Assert.assertEquals(5.151982014280041957199687829849210629618e-06,dist.cdf(0.02,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(200);
    Assert.assertEquals(0.01291614648162886340443389343590752105229,dist.cdf(0.031111,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(200);
    Assert.assertEquals(0.1067137011362679355208626930107129737735,dist.cdf(0.04,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(341);
    Assert.assertEquals(1.914734701559404553985102395145063418825e-53,dist.cdf(0.005,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(341);
    Assert.assertEquals(0.001171328985781981343872182321774744195864,dist.cdf(0.02,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(341);
    Assert.assertEquals(0.1142955196267499418105728636874118819833,dist.cdf(0.031111,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(341);
    Assert.assertEquals(0.3685529520496805266915885113121476024389,dist.cdf(0.04,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(389);
    Assert.assertEquals(1.810657144595055888918455512707637574637e-47,dist.cdf(0.005,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(389);
    Assert.assertEquals(0.003068542559702356568168690742481885536108,dist.cdf(0.02,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(389);
    Assert.assertEquals(0.1658291700122746237244797384846606291831,dist.cdf(0.031111,false),TOLERANCE);
    dist=new KolmogorovSmirnovDistribution(389);
    Assert.assertEquals(0.4513143712128902529379104180407011881471,dist.cdf(0.04,false),TOLERANCE);
  }
}
