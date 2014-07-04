package org.apache.commons.math3.genetics;
import org.junit.Assert;
import org.junit.Test;
public class OnePointCrossoverTest {
  @Test public void testCrossover(){
    Integer[] p1=new Integer[]{1,0,1,0,0,1,0,1,1};
    Integer[] p2=new Integer[]{0,1,1,0,1,0,1,1,1};
    BinaryChromosome p1c=new DummyBinaryChromosome(p1);
    BinaryChromosome p2c=new DummyBinaryChromosome(p2);
    OnePointCrossover<Integer> opc=new OnePointCrossover<Integer>();
    for (int i=0; i < 20; i++) {
      ChromosomePair pair=opc.crossover(p1c,p2c);
      Integer[] c1=new Integer[p1.length];
      Integer[] c2=new Integer[p2.length];
      c1=((BinaryChromosome)pair.getFirst()).getRepresentation().toArray(c1);
      c2=((BinaryChromosome)pair.getSecond()).getRepresentation().toArray(c2);
      Assert.assertEquals((int)p1[0],(int)c1[0]);
      Assert.assertEquals((int)p2[0],(int)c2[0]);
      Assert.assertEquals((int)p1[p1.length - 1],(int)c1[c1.length - 1]);
      Assert.assertEquals((int)p2[p2.length - 1],(int)c2[c2.length - 1]);
      Assert.assertEquals((int)p1[2],(int)c1[2]);
      Assert.assertEquals((int)p2[2],(int)c2[2]);
      Assert.assertEquals((int)p1[3],(int)c1[3]);
      Assert.assertEquals((int)p2[3],(int)c2[3]);
      Assert.assertEquals((int)p1[7],(int)c1[7]);
      Assert.assertEquals((int)p2[7],(int)c2[7]);
    }
  }
}
