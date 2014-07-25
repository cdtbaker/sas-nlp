package org.apache.commons.math3.genetics;
import org.junit.Assert;
import org.junit.Test;
public class TournamentSelectionTest {
  private static int counter=0;
  @Test public void testSelect(){
    TournamentSelection ts=new TournamentSelection(2);
    ElitisticListPopulation pop=new ElitisticListPopulation(100,0.203);
    for (int i=0; i < pop.getPopulationLimit(); i++) {
      pop.addChromosome(new DummyChromosome());
    }
    for (int i=0; i < 20; i++) {
      ChromosomePair pair=ts.select(pop);
      Assert.assertTrue(pair.getFirst().getFitness() > 0);
      Assert.assertTrue(pair.getSecond().getFitness() > 0);
    }
  }
private static class DummyChromosome extends Chromosome {
    private final int fitness;
    public DummyChromosome(){
      this.fitness=counter;
      counter++;
    }
    public double fitness(){
      return this.fitness;
    }
  }
}
