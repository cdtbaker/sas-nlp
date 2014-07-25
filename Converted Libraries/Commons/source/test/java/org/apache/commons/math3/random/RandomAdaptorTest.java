package org.apache.commons.math3.random;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test cases for the RandomAdaptor class
 * @version $Id: RandomAdaptorTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class RandomAdaptorTest {
  @Test public void testAdaptor(){
    ConstantGenerator generator=new ConstantGenerator();
    Random random=RandomAdaptor.createAdaptor(generator);
    checkConstant(random);
    RandomAdaptor randomAdaptor=new RandomAdaptor(generator);
    checkConstant(randomAdaptor);
  }
  private void checkConstant(  Random random){
    byte[] bytes=new byte[]{0};
    random.nextBytes(bytes);
    Assert.assertEquals(0,bytes[0]);
    Assert.assertEquals(false,random.nextBoolean());
    Assert.assertEquals(0,random.nextDouble(),0);
    Assert.assertEquals(0,random.nextFloat(),0);
    Assert.assertEquals(0,random.nextGaussian(),0);
    Assert.assertEquals(0,random.nextInt());
    Assert.assertEquals(0,random.nextInt(1));
    Assert.assertEquals(0,random.nextLong());
    random.setSeed(100);
    Assert.assertEquals(0,random.nextDouble(),0);
  }
public static class ConstantGenerator implements RandomGenerator {
    private final double value;
    public ConstantGenerator(){
      value=0;
    }
    public ConstantGenerator(    double value){
      this.value=value;
    }
    public boolean nextBoolean(){
      return false;
    }
    public void nextBytes(    byte[] bytes){
    }
    public double nextDouble(){
      return value;
    }
    public float nextFloat(){
      return (float)value;
    }
    public double nextGaussian(){
      return value;
    }
    public int nextInt(){
      return (int)value;
    }
    public int nextInt(    int n){
      return (int)value;
    }
    public long nextLong(){
      return (int)value;
    }
    public void setSeed(    int seed){
    }
    public void setSeed(    int[] seed){
    }
    public void setSeed(    long seed){
    }
  }
}
