package org.apache.commons.math3.random;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.math3.Retry;
import org.apache.commons.math3.RetryRunner;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.BinomialDistributionTest;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.HypergeometricDistributionTest;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.distribution.PascalDistributionTest;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.distribution.ZipfDistributionTest;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
/** 
 * Test cases for the RandomDataGenerator class.
 * @version $Id: RandomDataGeneratorTest.java 1457491 2013-03-17 17:15:31Z psteitz $
 */
@RunWith(RetryRunner.class) public class RandomDataGeneratorTest {
  public RandomDataGeneratorTest(){
    randomData=new RandomDataGenerator();
    randomData.reSeed(1000);
  }
  protected final long smallSampleSize=1000;
  protected final double[] expected={250,250,250,250};
  protected final int largeSampleSize=10000;
  private final String[] hex={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
  protected RandomDataGenerator randomData=null;
  protected final ChiSquareTest testStatistic=new ChiSquareTest();
  @Test public void testNextIntExtremeValues(){
    int x=randomData.nextInt(Integer.MIN_VALUE,Integer.MAX_VALUE);
    int y=randomData.nextInt(Integer.MIN_VALUE,Integer.MAX_VALUE);
    Assert.assertFalse(x == y);
  }
  @Test public void testNextLongExtremeValues(){
    long x=randomData.nextLong(Long.MIN_VALUE,Long.MAX_VALUE);
    long y=randomData.nextLong(Long.MIN_VALUE,Long.MAX_VALUE);
    Assert.assertFalse(x == y);
  }
  @Test public void testNextUniformExtremeValues(){
    double x=randomData.nextUniform(-Double.MAX_VALUE,Double.MAX_VALUE);
    double y=randomData.nextUniform(-Double.MAX_VALUE,Double.MAX_VALUE);
    Assert.assertFalse(x == y);
    Assert.assertFalse(Double.isNaN(x));
    Assert.assertFalse(Double.isNaN(y));
    Assert.assertFalse(Double.isInfinite(x));
    Assert.assertFalse(Double.isInfinite(y));
  }
  @Test public void testNextIntIAE(){
    try {
      randomData.nextInt(4,3);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test public void testNextIntNegativeToPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextIntUniform(-3,5);
      checkNextIntUniform(-3,6);
    }
  }
  @Test public void testNextIntNegativeRange(){
    for (int i=0; i < 5; i++) {
      checkNextIntUniform(-7,-4);
      checkNextIntUniform(-15,-2);
      checkNextIntUniform(Integer.MIN_VALUE + 1,Integer.MIN_VALUE + 12);
    }
  }
  @Test public void testNextIntPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextIntUniform(0,3);
      checkNextIntUniform(2,12);
      checkNextIntUniform(1,2);
      checkNextIntUniform(Integer.MAX_VALUE - 12,Integer.MAX_VALUE - 1);
    }
  }
  private void checkNextIntUniform(  int min,  int max){
    final Frequency freq=new Frequency();
    for (int i=0; i < smallSampleSize; i++) {
      final int value=randomData.nextInt(min,max);
      Assert.assertTrue("nextInt range",(value >= min) && (value <= max));
      freq.addValue(value);
    }
    final int len=max - min + 1;
    final long[] observed=new long[len];
    for (int i=0; i < len; i++) {
      observed[i]=freq.getCount(min + i);
    }
    final double[] expected=new double[len];
    for (int i=0; i < len; i++) {
      expected[i]=1d / len;
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.001);
  }
  @Test public void testNextIntWideRange(){
    int lower=-0x6543210F;
    int upper=0x456789AB;
    int max=Integer.MIN_VALUE;
    int min=Integer.MAX_VALUE;
    for (int i=0; i < 1000000; ++i) {
      int r=randomData.nextInt(lower,upper);
      max=FastMath.max(max,r);
      min=FastMath.min(min,r);
      Assert.assertTrue(r >= lower);
      Assert.assertTrue(r <= upper);
    }
    double ratio=(((double)max) - ((double)min)) / (((double)upper) - ((double)lower));
    Assert.assertTrue(ratio > 0.99999);
  }
  @Test public void testNextLongIAE(){
    try {
      randomData.nextLong(4,3);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test public void testNextLongNegativeToPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextLongUniform(-3,5);
      checkNextLongUniform(-3,6);
    }
  }
  @Test public void testNextLongNegativeRange(){
    for (int i=0; i < 5; i++) {
      checkNextLongUniform(-7,-4);
      checkNextLongUniform(-15,-2);
      checkNextLongUniform(Long.MIN_VALUE + 1,Long.MIN_VALUE + 12);
    }
  }
  @Test public void testNextLongPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextLongUniform(0,3);
      checkNextLongUniform(2,12);
      checkNextLongUniform(Long.MAX_VALUE - 12,Long.MAX_VALUE - 1);
    }
  }
  private void checkNextLongUniform(  long min,  long max){
    final Frequency freq=new Frequency();
    for (int i=0; i < smallSampleSize; i++) {
      final long value=randomData.nextLong(min,max);
      Assert.assertTrue("nextLong range: " + value + " "+ min+ " "+ max,(value >= min) && (value <= max));
      freq.addValue(value);
    }
    final int len=((int)(max - min)) + 1;
    final long[] observed=new long[len];
    for (int i=0; i < len; i++) {
      observed[i]=freq.getCount(min + i);
    }
    final double[] expected=new double[len];
    for (int i=0; i < len; i++) {
      expected[i]=1d / len;
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.01);
  }
  @Test public void testNextLongWideRange(){
    long lower=-0x6543210FEDCBA987L;
    long upper=0x456789ABCDEF0123L;
    long max=Long.MIN_VALUE;
    long min=Long.MAX_VALUE;
    for (int i=0; i < 10000000; ++i) {
      long r=randomData.nextLong(lower,upper);
      max=FastMath.max(max,r);
      min=FastMath.min(min,r);
      Assert.assertTrue(r >= lower);
      Assert.assertTrue(r <= upper);
    }
    double ratio=(((double)max) - ((double)min)) / (((double)upper) - ((double)lower));
    Assert.assertTrue(ratio > 0.99999);
  }
  @Test public void testNextSecureLongIAE(){
    try {
      randomData.nextSecureLong(4,3);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test @Retry(3) public void testNextSecureLongNegativeToPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextSecureLongUniform(-3,5);
      checkNextSecureLongUniform(-3,6);
    }
  }
  @Test @Retry(3) public void testNextSecureLongNegativeRange(){
    for (int i=0; i < 5; i++) {
      checkNextSecureLongUniform(-7,-4);
      checkNextSecureLongUniform(-15,-2);
    }
  }
  @Test @Retry(3) public void testNextSecureLongPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextSecureLongUniform(0,3);
      checkNextSecureLongUniform(2,12);
    }
  }
  private void checkNextSecureLongUniform(  int min,  int max){
    final Frequency freq=new Frequency();
    for (int i=0; i < smallSampleSize; i++) {
      final long value=randomData.nextSecureLong(min,max);
      Assert.assertTrue("nextLong range",(value >= min) && (value <= max));
      freq.addValue(value);
    }
    final int len=max - min + 1;
    final long[] observed=new long[len];
    for (int i=0; i < len; i++) {
      observed[i]=freq.getCount(min + i);
    }
    final double[] expected=new double[len];
    for (int i=0; i < len; i++) {
      expected[i]=1d / len;
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.0001);
  }
  @Test public void testNextSecureIntIAE(){
    try {
      randomData.nextSecureInt(4,3);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test @Retry(3) public void testNextSecureIntNegativeToPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextSecureIntUniform(-3,5);
      checkNextSecureIntUniform(-3,6);
    }
  }
  @Test @Retry(3) public void testNextSecureIntNegativeRange(){
    for (int i=0; i < 5; i++) {
      checkNextSecureIntUniform(-7,-4);
      checkNextSecureIntUniform(-15,-2);
    }
  }
  @Test @Retry(3) public void testNextSecureIntPositiveRange(){
    for (int i=0; i < 5; i++) {
      checkNextSecureIntUniform(0,3);
      checkNextSecureIntUniform(2,12);
    }
  }
  private void checkNextSecureIntUniform(  int min,  int max){
    final Frequency freq=new Frequency();
    for (int i=0; i < smallSampleSize; i++) {
      final int value=randomData.nextSecureInt(min,max);
      Assert.assertTrue("nextInt range",(value >= min) && (value <= max));
      freq.addValue(value);
    }
    final int len=max - min + 1;
    final long[] observed=new long[len];
    for (int i=0; i < len; i++) {
      observed[i]=freq.getCount(min + i);
    }
    final double[] expected=new double[len];
    for (int i=0; i < len; i++) {
      expected[i]=1d / len;
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.0001);
  }
  /** 
 * Make sure that empirical distribution of random Poisson(4)'s has P(X <=
 * 5) close to actual cumulative Poisson probability and that nextPoisson
 * fails when mean is non-positive.
 */
  @Test public void testNextPoisson(){
    try {
      randomData.nextPoisson(0);
      Assert.fail("zero mean -- expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextPoisson(-1);
      Assert.fail("negative mean supplied -- MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextPoisson(0);
      Assert.fail("0 mean supplied -- MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    final double mean=4.0d;
    final int len=5;
    PoissonDistribution poissonDistribution=new PoissonDistribution(mean);
    Frequency f=new Frequency();
    randomData.reSeed(1000);
    for (int i=0; i < largeSampleSize; i++) {
      f.addValue(randomData.nextPoisson(mean));
    }
    final long[] observed=new long[len];
    for (int i=0; i < len; i++) {
      observed[i]=f.getCount(i + 1);
    }
    final double[] expected=new double[len];
    for (int i=0; i < len; i++) {
      expected[i]=poissonDistribution.probability(i + 1) * largeSampleSize;
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.0001);
  }
  @Test public void testNextPoissonConsistency(){
    for (int i=1; i < 100; i++) {
      checkNextPoissonConsistency(i);
    }
    for (int i=1; i < 10; i++) {
      checkNextPoissonConsistency(randomData.nextUniform(1,1000));
    }
    for (int i=1; i < 10; i++) {
      checkNextPoissonConsistency(randomData.nextUniform(1000,3000));
    }
  }
  /** 
 * Verifies that nextPoisson(mean) generates an empirical distribution of values
 * consistent with PoissonDistributionImpl by generating 1000 values, computing a
 * grouped frequency distribution of the observed values and comparing this distribution
 * to the corresponding expected distribution computed using PoissonDistributionImpl.
 * Uses ChiSquare test of goodness of fit to evaluate the null hypothesis that the
 * distributions are the same. If the null hypothesis can be rejected with confidence
 * 1 - alpha, the check fails.
 */
  public void checkNextPoissonConsistency(  double mean){
    final int sampleSize=1000;
    final int minExpectedCount=7;
    long maxObservedValue=0;
    final double alpha=0.001;
    Frequency frequency=new Frequency();
    for (int i=0; i < sampleSize; i++) {
      long value=randomData.nextPoisson(mean);
      if (value > maxObservedValue) {
        maxObservedValue=value;
      }
      frequency.addValue(value);
    }
    PoissonDistribution poissonDistribution=new PoissonDistribution(mean);
    int lower=1;
    while (poissonDistribution.cumulativeProbability(lower - 1) * sampleSize < minExpectedCount) {
      lower++;
    }
    int upper=(int)(5 * mean);
    while ((1 - poissonDistribution.cumulativeProbability(upper - 1)) * sampleSize < minExpectedCount) {
      upper--;
    }
    int binWidth=0;
    boolean widthSufficient=false;
    double lowerBinMass=0;
    double upperBinMass=0;
    while (!widthSufficient) {
      binWidth++;
      lowerBinMass=poissonDistribution.cumulativeProbability(lower - 1,lower + binWidth - 1);
      upperBinMass=poissonDistribution.cumulativeProbability(upper - binWidth - 1,upper - 1);
      widthSufficient=FastMath.min(lowerBinMass,upperBinMass) * sampleSize >= minExpectedCount;
    }
    List<Integer> binBounds=new ArrayList<Integer>();
    binBounds.add(lower);
    int bound=lower + binWidth;
    while (bound < upper - binWidth) {
      binBounds.add(bound);
      bound+=binWidth;
    }
    binBounds.add(upper);
    final int binCount=binBounds.size() + 1;
    long[] observed=new long[binCount];
    double[] expected=new double[binCount];
    observed[0]=0;
    for (int i=0; i < lower; i++) {
      observed[0]+=frequency.getCount(i);
    }
    expected[0]=poissonDistribution.cumulativeProbability(lower - 1) * sampleSize;
    observed[binCount - 1]=0;
    for (int i=upper; i <= maxObservedValue; i++) {
      observed[binCount - 1]+=frequency.getCount(i);
    }
    expected[binCount - 1]=(1 - poissonDistribution.cumulativeProbability(upper - 1)) * sampleSize;
    for (int i=1; i < binCount - 1; i++) {
      observed[i]=0;
      for (int j=binBounds.get(i - 1); j < binBounds.get(i); j++) {
        observed[i]+=frequency.getCount(j);
      }
      expected[i]=(poissonDistribution.cumulativeProbability(binBounds.get(i) - 1) - poissonDistribution.cumulativeProbability(binBounds.get(i - 1) - 1)) * sampleSize;
    }
    ChiSquareTest chiSquareTest=new ChiSquareTest();
    if (chiSquareTest.chiSquareTest(expected,observed,alpha)) {
      StringBuilder msgBuffer=new StringBuilder();
      DecimalFormat df=new DecimalFormat("#.##");
      msgBuffer.append("Chisquare test failed for mean = ");
      msgBuffer.append(mean);
      msgBuffer.append(" p-value = ");
      msgBuffer.append(chiSquareTest.chiSquareTest(expected,observed));
      msgBuffer.append(" chisquare statistic = ");
      msgBuffer.append(chiSquareTest.chiSquare(expected,observed));
      msgBuffer.append(". \n");
      msgBuffer.append("bin\t\texpected\tobserved\n");
      for (int i=0; i < expected.length; i++) {
        msgBuffer.append("[");
        msgBuffer.append(i == 0 ? 1 : binBounds.get(i - 1));
        msgBuffer.append(",");
        msgBuffer.append(i == binBounds.size() ? "inf" : binBounds.get(i));
        msgBuffer.append(")");
        msgBuffer.append("\t\t");
        msgBuffer.append(df.format(expected[i]));
        msgBuffer.append("\t\t");
        msgBuffer.append(observed[i]);
        msgBuffer.append("\n");
      }
      msgBuffer.append("This test can fail randomly due to sampling error with probability ");
      msgBuffer.append(alpha);
      msgBuffer.append(".");
      Assert.fail(msgBuffer.toString());
    }
  }
  /** 
 * test dispersion and failure modes for nextHex() 
 */
  @Test public void testNextHex(){
    try {
      randomData.nextHexString(-1);
      Assert.fail("negative length supplied -- MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextHexString(0);
      Assert.fail("zero length supplied -- MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    String hexString=randomData.nextHexString(3);
    if (hexString.length() != 3) {
      Assert.fail("incorrect length for generated string");
    }
    hexString=randomData.nextHexString(1);
    if (hexString.length() != 1) {
      Assert.fail("incorrect length for generated string");
    }
    try {
      hexString=randomData.nextHexString(0);
      Assert.fail("zero length requested -- expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    Frequency f=new Frequency();
    for (int i=0; i < smallSampleSize; i++) {
      hexString=randomData.nextHexString(100);
      if (hexString.length() != 100) {
        Assert.fail("incorrect length for generated string");
      }
      for (int j=0; j < hexString.length(); j++) {
        f.addValue(hexString.substring(j,j + 1));
      }
    }
    double[] expected=new double[16];
    long[] observed=new long[16];
    for (int i=0; i < 16; i++) {
      expected[i]=(double)smallSampleSize * 100 / 16;
      observed[i]=f.getCount(hex[i]);
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.001);
  }
  /** 
 * test dispersion and failure modes for nextHex() 
 */
  @Test @Retry(3) public void testNextSecureHex(){
    try {
      randomData.nextSecureHexString(-1);
      Assert.fail("negative length -- MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextSecureHexString(0);
      Assert.fail("zero length -- MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    String hexString=randomData.nextSecureHexString(3);
    if (hexString.length() != 3) {
      Assert.fail("incorrect length for generated string");
    }
    hexString=randomData.nextSecureHexString(1);
    if (hexString.length() != 1) {
      Assert.fail("incorrect length for generated string");
    }
    try {
      hexString=randomData.nextSecureHexString(0);
      Assert.fail("zero length requested -- expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    Frequency f=new Frequency();
    for (int i=0; i < smallSampleSize; i++) {
      hexString=randomData.nextSecureHexString(100);
      if (hexString.length() != 100) {
        Assert.fail("incorrect length for generated string");
      }
      for (int j=0; j < hexString.length(); j++) {
        f.addValue(hexString.substring(j,j + 1));
      }
    }
    double[] expected=new double[16];
    long[] observed=new long[16];
    for (int i=0; i < 16; i++) {
      expected[i]=(double)smallSampleSize * 100 / 16;
      observed[i]=f.getCount(hex[i]);
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.001);
  }
  @Test public void testNextUniformIAE(){
    try {
      randomData.nextUniform(4,3);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextUniform(0,Double.POSITIVE_INFINITY);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextUniform(Double.NEGATIVE_INFINITY,0);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextUniform(0,Double.NaN);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextUniform(Double.NaN,0);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test public void testNextUniformUniformPositiveBounds(){
    for (int i=0; i < 5; i++) {
      checkNextUniformUniform(0,10);
    }
  }
  @Test public void testNextUniformUniformNegativeToPositiveBounds(){
    for (int i=0; i < 5; i++) {
      checkNextUniformUniform(-3,5);
    }
  }
  @Test public void testNextUniformUniformNegaiveBounds(){
    for (int i=0; i < 5; i++) {
      checkNextUniformUniform(-7,-3);
    }
  }
  @Test public void testNextUniformUniformMaximalInterval(){
    for (int i=0; i < 5; i++) {
      checkNextUniformUniform(-Double.MAX_VALUE,Double.MAX_VALUE);
    }
  }
  private void checkNextUniformUniform(  double min,  double max){
    final int binCount=5;
    final double binSize=max / binCount - min / binCount;
    final double[] binBounds=new double[binCount - 1];
    binBounds[0]=min + binSize;
    for (int i=1; i < binCount - 1; i++) {
      binBounds[i]=binBounds[i - 1] + binSize;
    }
    final Frequency freq=new Frequency();
    for (int i=0; i < smallSampleSize; i++) {
      final double value=randomData.nextUniform(min,max);
      Assert.assertTrue("nextUniform range",(value > min) && (value < max));
      int j=0;
      while (j < binCount - 1 && value > binBounds[j]) {
        j++;
      }
      freq.addValue(j);
    }
    final long[] observed=new long[binCount];
    for (int i=0; i < binCount; i++) {
      observed[i]=freq.getCount(i);
    }
    final double[] expected=new double[binCount];
    for (int i=0; i < binCount; i++) {
      expected[i]=1d / binCount;
    }
    TestUtils.assertChiSquareAccept(expected,observed,0.01);
  }
  /** 
 * test exclusive endpoints of nextUniform 
 */
  @Test public void testNextUniformExclusiveEndpoints(){
    for (int i=0; i < 1000; i++) {
      double u=randomData.nextUniform(0.99,1);
      Assert.assertTrue(u > 0.99 && u < 1);
    }
  }
  /** 
 * test failure modes and distribution of nextGaussian() 
 */
  @Test public void testNextGaussian(){
    try {
      randomData.nextGaussian(0,0);
      Assert.fail("zero sigma -- MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    double[] quartiles=TestUtils.getDistributionQuartiles(new NormalDistribution(0,1));
    long[] counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextGaussian(0,1);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  /** 
 * test failure modes and distribution of nextExponential() 
 */
  @Test public void testNextExponential(){
    try {
      randomData.nextExponential(-1);
      Assert.fail("negative mean -- expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      randomData.nextExponential(0);
      Assert.fail("zero mean -- expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    double[] quartiles;
    long[] counts;
    quartiles=TestUtils.getDistributionQuartiles(new ExponentialDistribution(1));
    counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextExponential(1);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
    quartiles=TestUtils.getDistributionQuartiles(new ExponentialDistribution(5));
    counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextExponential(5);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  /** 
 * test reseeding, algorithm/provider games 
 */
  @Test public void testConfig(){
    randomData.reSeed(1000);
    double v=randomData.nextUniform(0,1);
    randomData.reSeed();
    Assert.assertTrue("different seeds",Math.abs(v - randomData.nextUniform(0,1)) > 10E-12);
    randomData.reSeed(1000);
    Assert.assertEquals("same seeds",v,randomData.nextUniform(0,1),10E-12);
    randomData.reSeedSecure(1000);
    String hex=randomData.nextSecureHexString(40);
    randomData.reSeedSecure();
    Assert.assertTrue("different seeds",!hex.equals(randomData.nextSecureHexString(40)));
    randomData.reSeedSecure(1000);
    Assert.assertTrue("same seeds",!hex.equals(randomData.nextSecureHexString(40)));
    RandomDataGenerator rd=new RandomDataGenerator();
    rd.reSeed(100);
    rd.nextLong(1,2);
    RandomDataGenerator rd2=new RandomDataGenerator();
    rd2.reSeedSecure(2000);
    rd2.nextSecureLong(1,2);
    rd=new RandomDataGenerator();
    rd.reSeed();
    rd.nextLong(1,2);
    rd2=new RandomDataGenerator();
    rd2.reSeedSecure();
    rd2.nextSecureLong(1,2);
  }
  /** 
 * tests for nextSample() sampling from Collection 
 */
  @Test public void testNextSample(){
    Object[][] c={{"0","1"},{"0","2"},{"0","3"},{"0","4"},{"1","2"},{"1","3"},{"1","4"},{"2","3"},{"2","4"},{"3","4"}};
    long[] observed={0,0,0,0,0,0,0,0,0,0};
    double[] expected={100,100,100,100,100,100,100,100,100,100};
    HashSet<Object> cPop=new HashSet<Object>();
    for (int i=0; i < 5; i++) {
      cPop.add(Integer.toString(i));
    }
    Object[] sets=new Object[10];
    for (int i=0; i < 10; i++) {
      HashSet<Object> hs=new HashSet<Object>();
      hs.add(c[i][0]);
      hs.add(c[i][1]);
      sets[i]=hs;
    }
    for (int i=0; i < 1000; i++) {
      Object[] cSamp=randomData.nextSample(cPop,2);
      observed[findSample(sets,cSamp)]++;
    }
    Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",testStatistic.chiSquare(expected,observed) < 27.88);
    HashSet<Object> hs=new HashSet<Object>();
    hs.add("one");
    Object[] one=randomData.nextSample(hs,1);
    String oneString=(String)one[0];
    if ((one.length != 1) || !oneString.equals("one")) {
      Assert.fail("bad sample for set size = 1, sample size = 1");
    }
    try {
      one=randomData.nextSample(hs,2);
      Assert.fail("sample size > set size, expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      hs=new HashSet<Object>();
      one=randomData.nextSample(hs,0);
      Assert.fail("n = k = 0, expecting MathIllegalArgumentException");
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @SuppressWarnings("unchecked") private int findSample(  Object[] u,  Object[] samp){
    for (int i=0; i < u.length; i++) {
      HashSet<Object> set=(HashSet<Object>)u[i];
      HashSet<Object> sampSet=new HashSet<Object>();
      for (int j=0; j < samp.length; j++) {
        sampSet.add(samp[j]);
      }
      if (set.equals(sampSet)) {
        return i;
      }
    }
    Assert.fail("sample not found:{" + samp[0] + ","+ samp[1]+ "}");
    return -1;
  }
  /** 
 * tests for nextPermutation 
 */
  @Test public void testNextPermutation(){
    int[][] p={{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
    long[] observed={0,0,0,0,0,0};
    double[] expected={100,100,100,100,100,100};
    for (int i=0; i < 600; i++) {
      int[] perm=randomData.nextPermutation(3,3);
      observed[findPerm(p,perm)]++;
    }
    String[] labels={"{0, 1, 2}","{ 0, 2, 1 }","{ 1, 0, 2 }","{ 1, 2, 0 }","{ 2, 0, 1 }","{ 2, 1, 0 }"};
    TestUtils.assertChiSquareAccept(labels,expected,observed,0.001);
    int[] perm=randomData.nextPermutation(1,1);
    if ((perm.length != 1) || (perm[0] != 0)) {
      Assert.fail("bad permutation for n = 1, sample k = 1");
      try {
        perm=randomData.nextPermutation(2,3);
        Assert.fail("permutation k > n, expecting MathIllegalArgumentException");
      }
 catch (      MathIllegalArgumentException ex) {
      }
      try {
        perm=randomData.nextPermutation(0,0);
        Assert.fail("permutation k = n = 0, expecting MathIllegalArgumentException");
      }
 catch (      MathIllegalArgumentException ex) {
      }
      try {
        perm=randomData.nextPermutation(-1,-3);
        Assert.fail("permutation k < n < 0, expecting MathIllegalArgumentException");
      }
 catch (      MathIllegalArgumentException ex) {
      }
    }
  }
  private int findPerm(  int[][] p,  int[] samp){
    for (int i=0; i < p.length; i++) {
      boolean good=true;
      for (int j=0; j < samp.length; j++) {
        if (samp[j] != p[i][j]) {
          good=false;
        }
      }
      if (good) {
        return i;
      }
    }
    Assert.fail("permutation not found");
    return -1;
  }
  @Test public void testNextInversionDeviate(){
    RandomGenerator rg=new Well19937c(100);
    RandomDataGenerator rdg=new RandomDataGenerator(rg);
    double[] quantiles=new double[10];
    for (int i=0; i < 10; i++) {
      quantiles[i]=rdg.nextUniform(0,1);
    }
    rg.setSeed(100);
    BetaDistribution betaDistribution=new BetaDistribution(rg,2,4,BetaDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    for (int i=0; i < 10; i++) {
      double value=betaDistribution.sample();
      Assert.assertEquals(betaDistribution.cumulativeProbability(value),quantiles[i],10E-9);
    }
  }
  @Test public void testNextBeta(){
    double[] quartiles=TestUtils.getDistributionQuartiles(new BetaDistribution(2,5));
    long[] counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextBeta(2,5);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  @Test public void testNextCauchy(){
    double[] quartiles=TestUtils.getDistributionQuartiles(new CauchyDistribution(1.2,2.1));
    long[] counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextCauchy(1.2,2.1);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  @Test public void testNextChiSquare(){
    double[] quartiles=TestUtils.getDistributionQuartiles(new ChiSquaredDistribution(12));
    long[] counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextChiSquare(12);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  @Test public void testNextF(){
    double[] quartiles=TestUtils.getDistributionQuartiles(new FDistribution(12,5));
    long[] counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextF(12,5);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  @Test public void testNextGamma(){
    double[] quartiles;
    long[] counts;
    quartiles=TestUtils.getDistributionQuartiles(new GammaDistribution(4,2));
    counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextGamma(4,2);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
    quartiles=TestUtils.getDistributionQuartiles(new GammaDistribution(0.3,3));
    counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextGamma(0.3,3);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  @Test public void testNextT(){
    double[] quartiles=TestUtils.getDistributionQuartiles(new TDistribution(10));
    long[] counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextT(10);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  @Test public void testNextWeibull(){
    double[] quartiles=TestUtils.getDistributionQuartiles(new WeibullDistribution(1.2,2.1));
    long[] counts=new long[4];
    randomData.reSeed(1000);
    for (int i=0; i < 1000; i++) {
      double value=randomData.nextWeibull(1.2,2.1);
      TestUtils.updateCounts(value,counts,quartiles);
    }
    TestUtils.assertChiSquareAccept(expected,counts,0.001);
  }
  @Test public void testNextBinomial(){
    BinomialDistributionTest testInstance=new BinomialDistributionTest();
    int[] densityPoints=testInstance.makeDensityTestPoints();
    double[] densityValues=testInstance.makeDensityTestValues();
    int sampleSize=1000;
    int length=TestUtils.eliminateZeroMassPoints(densityPoints,densityValues);
    BinomialDistribution distribution=(BinomialDistribution)testInstance.makeDistribution();
    double[] expectedCounts=new double[length];
    long[] observedCounts=new long[length];
    for (int i=0; i < length; i++) {
      expectedCounts[i]=sampleSize * densityValues[i];
    }
    randomData.reSeed(1000);
    for (int i=0; i < sampleSize; i++) {
      int value=randomData.nextBinomial(distribution.getNumberOfTrials(),distribution.getProbabilityOfSuccess());
      for (int j=0; j < length; j++) {
        if (value == densityPoints[j]) {
          observedCounts[j]++;
        }
      }
    }
    TestUtils.assertChiSquareAccept(densityPoints,expectedCounts,observedCounts,.001);
  }
  @Test public void testNextHypergeometric(){
    HypergeometricDistributionTest testInstance=new HypergeometricDistributionTest();
    int[] densityPoints=testInstance.makeDensityTestPoints();
    double[] densityValues=testInstance.makeDensityTestValues();
    int sampleSize=1000;
    int length=TestUtils.eliminateZeroMassPoints(densityPoints,densityValues);
    HypergeometricDistribution distribution=(HypergeometricDistribution)testInstance.makeDistribution();
    double[] expectedCounts=new double[length];
    long[] observedCounts=new long[length];
    for (int i=0; i < length; i++) {
      expectedCounts[i]=sampleSize * densityValues[i];
    }
    randomData.reSeed(1000);
    for (int i=0; i < sampleSize; i++) {
      int value=randomData.nextHypergeometric(distribution.getPopulationSize(),distribution.getNumberOfSuccesses(),distribution.getSampleSize());
      for (int j=0; j < length; j++) {
        if (value == densityPoints[j]) {
          observedCounts[j]++;
        }
      }
    }
    TestUtils.assertChiSquareAccept(densityPoints,expectedCounts,observedCounts,.001);
  }
  @Test public void testNextPascal(){
    PascalDistributionTest testInstance=new PascalDistributionTest();
    int[] densityPoints=testInstance.makeDensityTestPoints();
    double[] densityValues=testInstance.makeDensityTestValues();
    int sampleSize=1000;
    int length=TestUtils.eliminateZeroMassPoints(densityPoints,densityValues);
    PascalDistribution distribution=(PascalDistribution)testInstance.makeDistribution();
    double[] expectedCounts=new double[length];
    long[] observedCounts=new long[length];
    for (int i=0; i < length; i++) {
      expectedCounts[i]=sampleSize * densityValues[i];
    }
    randomData.reSeed(1000);
    for (int i=0; i < sampleSize; i++) {
      int value=randomData.nextPascal(distribution.getNumberOfSuccesses(),distribution.getProbabilityOfSuccess());
      for (int j=0; j < length; j++) {
        if (value == densityPoints[j]) {
          observedCounts[j]++;
        }
      }
    }
    TestUtils.assertChiSquareAccept(densityPoints,expectedCounts,observedCounts,.001);
  }
  @Test public void testNextZipf(){
    ZipfDistributionTest testInstance=new ZipfDistributionTest();
    int[] densityPoints=testInstance.makeDensityTestPoints();
    double[] densityValues=testInstance.makeDensityTestValues();
    int sampleSize=1000;
    int length=TestUtils.eliminateZeroMassPoints(densityPoints,densityValues);
    ZipfDistribution distribution=(ZipfDistribution)testInstance.makeDistribution();
    double[] expectedCounts=new double[length];
    long[] observedCounts=new long[length];
    for (int i=0; i < length; i++) {
      expectedCounts[i]=sampleSize * densityValues[i];
    }
    randomData.reSeed(1000);
    for (int i=0; i < sampleSize; i++) {
      int value=randomData.nextZipf(distribution.getNumberOfElements(),distribution.getExponent());
      for (int j=0; j < length; j++) {
        if (value == densityPoints[j]) {
          observedCounts[j]++;
        }
      }
    }
    TestUtils.assertChiSquareAccept(densityPoints,expectedCounts,observedCounts,.001);
  }
  @Test public void testReseed(){
    PoissonDistribution x=new PoissonDistribution(3.0);
    x.reseedRandomGenerator(0);
    final double u=x.sample();
    PoissonDistribution y=new PoissonDistribution(3.0);
    y.reseedRandomGenerator(0);
    Assert.assertEquals(u,y.sample(),0);
  }
}