package org.apache.commons.math3.random;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.Assert;
import org.junit.Test;
public class UniformRandomGeneratorTest {
  @Test public void testMeanAndStandardDeviation(){
    RandomGenerator rg=new JDKRandomGenerator();
    rg.setSeed(17399225432l);
    UniformRandomGenerator generator=new UniformRandomGenerator(rg);
    double[] sample=new double[10000];
    for (int i=0; i < sample.length; ++i) {
      sample[i]=generator.nextNormalizedDouble();
    }
    Assert.assertEquals(0.0,StatUtils.mean(sample),0.07);
    Assert.assertEquals(1.0,StatUtils.variance(sample),0.02);
  }
}
