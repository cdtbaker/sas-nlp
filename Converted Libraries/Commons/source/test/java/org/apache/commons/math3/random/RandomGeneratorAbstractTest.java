package org.apache.commons.math3.random;
import java.util.Arrays;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/** 
 * Base class for RandomGenerator tests.
 * Tests RandomGenerator methods directly and also executes RandomDataTest
 * test cases against a RandomDataImpl created using the provided generator.
 * RandomGenerator test classes should extend this class, implementing
 * makeGenerator() to provide a concrete generator to test. The generator
 * returned by makeGenerator should be seeded with a fixed seed.
 * @version $Id: RandomGeneratorAbstractTest.java 1454846 2013-03-10 13:02:04Z luc $
 */
public abstract class RandomGeneratorAbstractTest extends RandomDataGeneratorTest {
  /** 
 * RandomGenerator under test 
 */
  protected RandomGenerator generator;
  /** 
 * Override this method in subclasses to provide a concrete generator to test.
 * Return a generator seeded with a fixed seed.
 */
  protected abstract RandomGenerator makeGenerator();
  /** 
 * Initialize generator and randomData instance in superclass.
 */
  public RandomGeneratorAbstractTest(){
    generator=makeGenerator();
    randomData=new RandomDataGenerator(generator);
  }
  /** 
 * Set a fixed seed for the tests
 */
  @Before public void setUp(){
    generator=makeGenerator();
  }
  @Override public void testNextSecureLongIAE(){
  }
  @Override public void testNextSecureLongNegativeToPositiveRange(){
  }
  @Override public void testNextSecureLongNegativeRange(){
  }
  @Override public void testNextSecureLongPositiveRange(){
  }
  @Override public void testNextSecureIntIAE(){
  }
  @Override public void testNextSecureIntNegativeToPositiveRange(){
  }
  @Override public void testNextSecureIntNegativeRange(){
  }
  @Override public void testNextSecureIntPositiveRange(){
  }
  @Override public void testNextSecureHex(){
  }
  @Test public void testNextIntDirect(){
    int[] testValues=new int[]{4,10,12,32,100,10000,0,0,0,0};
    for (int i=6; i < 10; i++) {
      final int val=generator.nextInt();
      testValues[i]=val < 0 ? -val : val + 1;
    }
    final int numTests=1000;
    for (int i=0; i < testValues.length; i++) {
      final int n=testValues[i];
      int[] binUpperBounds;
      if (n < 32) {
        binUpperBounds=new int[n];
        for (int k=0; k < n; k++) {
          binUpperBounds[k]=k;
        }
      }
 else {
        binUpperBounds=new int[10];
        final int step=n / 10;
        for (int k=0; k < 9; k++) {
          binUpperBounds[k]=(k + 1) * step;
        }
        binUpperBounds[9]=n - 1;
      }
      int numFailures=0;
      final int binCount=binUpperBounds.length;
      final long[] observed=new long[binCount];
      final double[] expected=new double[binCount];
      expected[0]=binUpperBounds[0] == 0 ? (double)smallSampleSize / (double)n : (double)((binUpperBounds[0] + 1) * smallSampleSize) / (double)n;
      for (int k=1; k < binCount; k++) {
        expected[k]=(double)smallSampleSize * (double)(binUpperBounds[k] - binUpperBounds[k - 1]) / (double)n;
      }
      for (int j=0; j < numTests; j++) {
        Arrays.fill(observed,0);
        for (int k=0; k < smallSampleSize; k++) {
          final int value=generator.nextInt(n);
          Assert.assertTrue("nextInt range",(value >= 0) && (value < n));
          for (int l=0; l < binCount; l++) {
            if (binUpperBounds[l] >= value) {
              observed[l]++;
              break;
            }
          }
        }
        if (testStatistic.chiSquareTest(expected,observed) < 0.01) {
          numFailures++;
        }
      }
      if ((double)numFailures / (double)numTests > 0.02) {
        Assert.fail("Too many failures for n = " + n + " "+ numFailures+ " out of "+ numTests+ " tests failed.");
      }
    }
  }
  @Test public void testNextIntIAE2(){
    try {
      generator.nextInt(-1);
      Assert.fail("MathIllegalArgumentException expected");
    }
 catch (    MathIllegalArgumentException ex) {
    }
    try {
      generator.nextInt(0);
    }
 catch (    MathIllegalArgumentException ex) {
    }
  }
  @Test public void testNextLongDirect(){
    long q1=Long.MAX_VALUE / 4;
    long q2=2 * q1;
    long q3=3 * q1;
    Frequency freq=new Frequency();
    long val=0;
    int value=0;
    for (int i=0; i < smallSampleSize; i++) {
      val=generator.nextLong();
      val=val < 0 ? -val : val;
      if (val < q1) {
        value=0;
      }
 else       if (val < q2) {
        value=1;
      }
 else       if (val < q3) {
        value=2;
      }
 else {
        value=3;
      }
      freq.addValue(value);
    }
    long[] observed=new long[4];
    for (int i=0; i < 4; i++) {
      observed[i]=freq.getCount(i);
    }
    Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",testStatistic.chiSquare(expected,observed) < 16.27);
  }
  @Test public void testNextBooleanDirect(){
    long halfSampleSize=smallSampleSize / 2;
    double[] expected={halfSampleSize,halfSampleSize};
    long[] observed=new long[2];
    for (int i=0; i < smallSampleSize; i++) {
      if (generator.nextBoolean()) {
        observed[0]++;
      }
 else {
        observed[1]++;
      }
    }
    Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",testStatistic.chiSquare(expected,observed) < 10.828);
  }
  @Test public void testNextFloatDirect(){
    Frequency freq=new Frequency();
    float val=0;
    int value=0;
    for (int i=0; i < smallSampleSize; i++) {
      val=generator.nextFloat();
      if (val < 0.25) {
        value=0;
      }
 else       if (val < 0.5) {
        value=1;
      }
 else       if (val < 0.75) {
        value=2;
      }
 else {
        value=3;
      }
      freq.addValue(value);
    }
    long[] observed=new long[4];
    for (int i=0; i < 4; i++) {
      observed[i]=freq.getCount(i);
    }
    Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times",testStatistic.chiSquare(expected,observed) < 16.27);
  }
  @Test public void testDoubleDirect(){
    SummaryStatistics sample=new SummaryStatistics();
    final int N=10000;
    for (int i=0; i < N; ++i) {
      sample.addValue(generator.nextDouble());
    }
    Assert.assertEquals("Note: This test will fail randomly about 1 in 100 times.",0.5,sample.getMean(),FastMath.sqrt(N / 12.0) * 2.576);
    Assert.assertEquals(1.0 / (2.0 * FastMath.sqrt(3.0)),sample.getStandardDeviation(),0.01);
  }
  @Test public void testFloatDirect(){
    SummaryStatistics sample=new SummaryStatistics();
    final int N=1000;
    for (int i=0; i < N; ++i) {
      sample.addValue(generator.nextFloat());
    }
    Assert.assertEquals("Note: This test will fail randomly about 1 in 100 times.",0.5,sample.getMean(),FastMath.sqrt(N / 12.0) * 2.576);
    Assert.assertEquals(1.0 / (2.0 * FastMath.sqrt(3.0)),sample.getStandardDeviation(),0.01);
  }
  @Test(expected=MathIllegalArgumentException.class) public void testNextIntNeg(){
    generator.nextInt(-1);
  }
  @Test public void testNextInt2(){
    int walk=0;
    final int N=10000;
    for (int k=0; k < N; ++k) {
      if (generator.nextInt() >= 0) {
        ++walk;
      }
 else {
        --walk;
      }
    }
    Assert.assertTrue("Walked too far astray: " + walk + "\nNote: This "+ "test will fail randomly about 1 in 100 times.",FastMath.abs(walk) < FastMath.sqrt(N) * 2.576);
  }
  @Test public void testNextLong2(){
    int walk=0;
    final int N=1000;
    for (int k=0; k < N; ++k) {
      if (generator.nextLong() >= 0) {
        ++walk;
      }
 else {
        --walk;
      }
    }
    Assert.assertTrue("Walked too far astray: " + walk + "\nNote: This "+ "test will fail randomly about 1 in 100 times.",FastMath.abs(walk) < FastMath.sqrt(N) * 2.576);
  }
  @Test public void testNexBoolean2(){
    int walk=0;
    final int N=10000;
    for (int k=0; k < N; ++k) {
      if (generator.nextBoolean()) {
        ++walk;
      }
 else {
        --walk;
      }
    }
    Assert.assertTrue("Walked too far astray: " + walk + "\nNote: This "+ "test will fail randomly about 1 in 100 times.",FastMath.abs(walk) < FastMath.sqrt(N) * 2.576);
  }
  @Test public void testNexBytes(){
    long[] count=new long[256];
    byte[] bytes=new byte[10];
    double[] expected=new double[256];
    final int sampleSize=100000;
    for (int i=0; i < 256; i++) {
      expected[i]=(double)sampleSize / 265f;
    }
    for (int k=0; k < sampleSize; ++k) {
      generator.nextBytes(bytes);
      for (      byte b : bytes) {
        ++count[b + 128];
      }
    }
    TestUtils.assertChiSquareAccept(expected,count,0.001);
  }
  @Test public void testSeeding(){
    RandomGenerator gen=makeGenerator();
    RandomGenerator gen1=makeGenerator();
    checkSameSequence(gen,gen1);
    gen.setSeed(100);
    gen1=makeGenerator();
    gen1.setSeed(100);
    checkSameSequence(gen,gen1);
  }
  private void checkSameSequence(  RandomGenerator gen1,  RandomGenerator gen2){
    final int len=11;
    final double[][] values=new double[2][len];
    for (int i=0; i < len; i++) {
      values[0][i]=gen1.nextDouble();
    }
    for (int i=0; i < len; i++) {
      values[1][i]=gen2.nextDouble();
    }
    Assert.assertTrue(Arrays.equals(values[0],values[1]));
    for (int i=0; i < len; i++) {
      values[0][i]=gen1.nextFloat();
    }
    for (int i=0; i < len; i++) {
      values[1][i]=gen2.nextFloat();
    }
    Assert.assertTrue(Arrays.equals(values[0],values[1]));
    for (int i=0; i < len; i++) {
      values[0][i]=gen1.nextInt();
    }
    for (int i=0; i < len; i++) {
      values[1][i]=gen2.nextInt();
    }
    Assert.assertTrue(Arrays.equals(values[0],values[1]));
    for (int i=0; i < len; i++) {
      values[0][i]=gen1.nextLong();
    }
    for (int i=0; i < len; i++) {
      values[1][i]=gen2.nextLong();
    }
    Assert.assertTrue(Arrays.equals(values[0],values[1]));
    for (int i=0; i < len; i++) {
      values[0][i]=gen1.nextInt(len);
    }
    for (int i=0; i < len; i++) {
      values[1][i]=gen2.nextInt(len);
    }
    Assert.assertTrue(Arrays.equals(values[0],values[1]));
    for (int i=0; i < len; i++) {
      values[0][i]=gen1.nextBoolean() ? 1 : 0;
    }
    for (int i=0; i < len; i++) {
      values[1][i]=gen2.nextBoolean() ? 1 : 0;
    }
    Assert.assertTrue(Arrays.equals(values[0],values[1]));
    for (int i=0; i < len; i++) {
      values[0][i]=gen1.nextGaussian();
    }
    for (int i=0; i < len; i++) {
      values[1][i]=gen2.nextGaussian();
    }
    Assert.assertTrue(Arrays.equals(values[0],values[1]));
  }
}