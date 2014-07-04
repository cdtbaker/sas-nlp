package org.apache.commons.math3.genetics;
import org.junit.Assert;
import org.junit.Test;
public class BinaryChromosomeTest {
  @Test public void testInvalidConstructor(){
    Integer[][] reprs=new Integer[][]{new Integer[]{0,1,0,1,2},new Integer[]{0,1,0,1,-1}};
    for (    Integer[] repr : reprs) {
      try {
        new DummyBinaryChromosome(repr);
        Assert.fail("Exception not caught");
      }
 catch (      IllegalArgumentException e) {
      }
    }
  }
  @Test public void testRandomConstructor(){
    for (int i=0; i < 20; i++) {
      new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(10));
    }
  }
  @Test public void testIsSame(){
    Chromosome c1=new DummyBinaryChromosome(new Integer[]{0,1,0,1,0,1});
    Chromosome c2=new DummyBinaryChromosome(new Integer[]{0,1,1,0,1});
    Chromosome c3=new DummyBinaryChromosome(new Integer[]{0,1,0,1,0,1,1});
    Chromosome c4=new DummyBinaryChromosome(new Integer[]{1,1,0,1,0,1});
    Chromosome c5=new DummyBinaryChromosome(new Integer[]{0,1,0,1,0,0});
    Chromosome c6=new DummyBinaryChromosome(new Integer[]{0,1,0,1,0,1});
    Assert.assertFalse(c1.isSame(c2));
    Assert.assertFalse(c1.isSame(c3));
    Assert.assertFalse(c1.isSame(c4));
    Assert.assertFalse(c1.isSame(c5));
    Assert.assertTrue(c1.isSame(c6));
  }
}
