package org.apache.commons.math3.random;
import java.util.Random;
/** 
 * Test cases for the BitStreamGenerator class
 * @version $Id: BitsStreamGeneratorTest.java 1454847 2013-03-10 13:07:08Z luc $
 */
public class BitsStreamGeneratorTest extends RandomGeneratorAbstractTest {
  public BitsStreamGeneratorTest(){
    super();
  }
  @Override protected RandomGenerator makeGenerator(){
    RandomGenerator generator=new TestBitStreamGenerator();
    generator.setSeed(1000);
    return generator;
  }
  /** 
 * Test BitStreamGenerator using a Random as bit source.
 */
static class TestBitStreamGenerator extends BitsStreamGenerator {
    private static final long serialVersionUID=1L;
    private BitRandom ran=new BitRandom();
    @Override public void setSeed(    int seed){
      ran.setSeed(seed);
      clear();
    }
    @Override public void setSeed(    int[] seed){
      ran.setSeed(seed[0]);
    }
    @Override public void setSeed(    long seed){
      ran.setSeed((int)seed);
    }
    @Override protected int next(    int bits){
      return ran.nextBits(bits);
    }
  }
  /** 
 * Extend Random to expose next(bits)
 */
@SuppressWarnings("serial") static class BitRandom extends Random {
    public BitRandom(){
      super();
    }
    public int nextBits(    int bits){
      return next(bits);
    }
  }
}
