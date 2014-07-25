package org.apache.commons.math3.genetics;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
public class FixedGenerationCountTest {
  @Test public void testIsSatisfied(){
    FixedGenerationCount fgc=new FixedGenerationCount(20);
    int cnt=0;
    Population pop=new Population(){
      public void addChromosome(      Chromosome chromosome){
      }
      public Chromosome getFittestChromosome(){
        return null;
      }
      public int getPopulationLimit(){
        return 0;
      }
      public int getPopulationSize(){
        return 0;
      }
      public Population nextGeneration(){
        return null;
      }
      public Iterator<Chromosome> iterator(){
        return null;
      }
    }
;
    while (!fgc.isSatisfied(pop)) {
      cnt++;
    }
    Assert.assertEquals(20,cnt);
  }
}
