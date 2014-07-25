package org.apache.commons.math3.genetics;
import org.junit.Assert;
import org.junit.Test;
public class RandomKeyMutationTest {
  @Test public void testMutate(){
    MutationPolicy mutation=new RandomKeyMutation();
    int l=10;
    for (int i=0; i < 20; i++) {
      DummyRandomKey origRk=new DummyRandomKey(RandomKey.randomPermutation(l));
      Chromosome mutated=mutation.mutate(origRk);
      DummyRandomKey mutatedRk=(DummyRandomKey)mutated;
      int changes=0;
      for (int j=0; j < origRk.getLength(); j++) {
        if (origRk.getRepresentation().get(j) != mutatedRk.getRepresentation().get(j)) {
          changes++;
        }
      }
      Assert.assertEquals(1,changes);
    }
  }
}
