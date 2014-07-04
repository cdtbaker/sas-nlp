package org.apache.commons.math3.genetics;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
public class ChromosomeTest {
  @Test public void testCompareTo(){
    Chromosome c1=new Chromosome(){
      public double fitness(){
        return 0;
      }
    }
;
    Chromosome c2=new Chromosome(){
      public double fitness(){
        return 10;
      }
    }
;
    Chromosome c3=new Chromosome(){
      public double fitness(){
        return 10;
      }
    }
;
    Assert.assertTrue(c1.compareTo(c2) < 0);
    Assert.assertTrue(c2.compareTo(c1) > 0);
    Assert.assertEquals(0,c3.compareTo(c2));
    Assert.assertEquals(0,c2.compareTo(c3));
  }
private abstract static class DummyChromosome extends Chromosome {
    private final int repr;
    public DummyChromosome(    final int repr){
      this.repr=repr;
    }
    @Override protected boolean isSame(    Chromosome another){
      return ((DummyChromosome)another).repr == repr;
    }
  }
  @Test public void testFindSameChromosome(){
    Chromosome c1=new DummyChromosome(1){
      public double fitness(){
        return 1;
      }
    }
;
    Chromosome c2=new DummyChromosome(2){
      public double fitness(){
        return 2;
      }
    }
;
    Chromosome c3=new DummyChromosome(3){
      public double fitness(){
        return 3;
      }
    }
;
    Chromosome c4=new DummyChromosome(1){
      public double fitness(){
        return 5;
      }
    }
;
    Chromosome c5=new DummyChromosome(15){
      public double fitness(){
        return 15;
      }
    }
;
    List<Chromosome> popChr=new ArrayList<Chromosome>();
    popChr.add(c1);
    popChr.add(c2);
    popChr.add(c3);
    Population pop=new ListPopulation(popChr,3){
      public Population nextGeneration(){
        return null;
      }
    }
;
    Assert.assertNull(c5.findSameChromosome(pop));
    Assert.assertEquals(c1,c4.findSameChromosome(pop));
    c4.searchForFitnessUpdate(pop);
    Assert.assertEquals(1,c4.getFitness(),0);
  }
}
