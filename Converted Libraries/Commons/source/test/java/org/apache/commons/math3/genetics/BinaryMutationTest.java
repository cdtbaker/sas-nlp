package org.apache.commons.math3.genetics;
import org.junit.Assert;
import org.junit.Test;
public class BinaryMutationTest {
  @Test public void testMutate(){
    BinaryMutation mutation=new BinaryMutation();
    for (int i=0; i < 20; i++) {
      DummyBinaryChromosome original=new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(10));
      DummyBinaryChromosome mutated=(DummyBinaryChromosome)mutation.mutate(original);
      int numDifferent=0;
      for (int j=0; j < original.getRepresentation().size(); j++) {
        if (original.getRepresentation().get(j) != mutated.getRepresentation().get(j))         numDifferent++;
      }
      Assert.assertEquals(1,numDifferent);
    }
  }
}
